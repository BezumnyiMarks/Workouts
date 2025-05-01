package com.example.todolist.fragments

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.example.kolsa.viewmodels.MainViewModel
import com.example.kolsa.Player
import com.example.kolsa.data.Workout
import com.example.kolsa.databinding.FragmentVideoBinding
import com.example.kolsa.viewmodels.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VideoViewModel by viewModels()
    private var id: Long ?= null
    private var description: String ?= null
    @Inject
    lateinit var player: Player

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
        observeNetworkState()
        bindPlayer()
        observeNetworkState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showVideo() {
        lifecycleScope.launch {
            viewModel.videoStateFlow.collect {
                player.setMedia(it.link)
                binding.tvDescription.text = description
                playVideo()
            }
        }
    }

    private fun bindPlayer(){
        with(binding){
            playerView.player = player.player
        }
    }

    private fun getArgs(){
        id = arguments?.getLong("id")
        description = arguments?.getString("desc")
    }

    private fun observeNetworkState(){
        lifecycleScope.launch {
            viewModel.networkState.collect { networkState ->
                with(binding){
                    when(networkState){
                        VideoViewModel.NetworkState.Empty -> {
                            id?.let { viewModel.getVideo(it.toInt()) }
                            contentView.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                            pauseVideo()
                        }
                        VideoViewModel.NetworkState.Error -> {
                            contentView.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            pauseVideo()
                        }
                        VideoViewModel.NetworkState.Loading -> {
                            contentView.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                            pauseVideo()
                        }
                        VideoViewModel.NetworkState.Success -> {
                            contentView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            showVideo()
                        }
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun playVideo(){
        with(binding){
            playerView.player?.play()
            playerView.controllerAutoShow = false
            playerView.hideController()
        }
        player.player.play()
    }

    private fun pauseVideo(){
        binding.playerView.player?.pause()
        player.player.pause()
    }
}