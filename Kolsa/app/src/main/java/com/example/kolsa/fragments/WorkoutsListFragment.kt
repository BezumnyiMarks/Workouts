package com.example.kolsa.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kolsa.viewmodels.MainViewModel
import com.example.kolsa.R
import com.example.kolsa.data.Workout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.kolsa.databinding.FragmentWorkoutsListBinding
import com.example.kolsa.adapters.WorkoutsAdapter
import com.example.kolsa.databinding.TypePickerBsDialogLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.collections.sort

@AndroidEntryPoint
class WorkoutsListFragment : Fragment() {

    private var _binding: FragmentWorkoutsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private val adapter = WorkoutsAdapter { workout -> onWorkoutClick(workout) }

    private var _typePickerDialog: BottomSheetDialog? = null
    private val typePickerDialog get() = _typePickerDialog!!
    private var _typePickerDialogBinding: TypePickerBsDialogLayoutBinding? = null
    private val typePickerDialogBinding get() = _typePickerDialogBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeNetworkState()
        initStatusPickerDialog()
        bindAdapter()
        bindTopBar()
        bindStatusPickerDialog()
        observeFiltersState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onWorkoutClick(workout: Workout){
        val bundle = bundleOf(
            "id" to workout.id,
            "desc" to workout.description
            )
        findNavController().navigate(R.id.action_workoutsListFragment_to_videoFragment, bundle)
    }

    private fun bindAdapter(){
        binding.listWorkouts.adapter = adapter
        lifecycleScope.launch {
            viewModel.filteredWorkoutsStateFlow.collect { workouts ->
                adapter.submitList(workouts)
            }
        }
    }

    private fun bindTopBar(){
        with(binding) {
            etSearch.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(keyword: Editable?) {
                        viewModel.setKeywordFilter(keyword.toString())
                    }
                }
            )

            imgType.setOnClickListener {
                typePickerDialog.show()
            }
        }
    }

    private fun bindStatusPickerDialog(){
        with(typePickerDialogBinding){
            radioGroup.setOnCheckedChangeListener { group, id ->
                when(id){
                    R.id.all -> {
                        viewModel.setTypeFilter(MainViewModel.FiltersApplied.Type(0))
                    }
                    R.id.type1 -> {
                        viewModel.setTypeFilter(MainViewModel.FiltersApplied.Type(1))
                    }
                    R.id.type2 -> {
                        viewModel.setTypeFilter(MainViewModel.FiltersApplied.Type(2))
                    }
                    R.id.type3 -> {
                        viewModel.setTypeFilter(MainViewModel.FiltersApplied.Type(3))
                    }
                }
                typePickerDialog.dismiss()
            }
        }
    }

    private fun observeFiltersState(){
        lifecycleScope.launch {
            viewModel.filters.collect { filters ->
                filters.forEach { filter ->
                    when(filter){
                        is MainViewModel.FiltersApplied.Keyword -> {
                        }
                        is MainViewModel.FiltersApplied.Type -> {
                            with (typePickerDialogBinding){
                                when(filter.type){
                                    0L -> {
                                        radioGroup.check(R.id.all)
                                    }
                                    1L -> {
                                        radioGroup.check(R.id.type1)
                                    }
                                    2L -> {
                                        radioGroup.check(R.id.type2)
                                    }
                                    3L -> {
                                        radioGroup.check(R.id.type3)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeNetworkState(){
        lifecycleScope.launch {
            viewModel.networkState.collect { networkState ->
                with(binding){
                    when(networkState){
                        MainViewModel.NetworkState.Empty -> {
                            viewModel.getWorkouts()
                            contentView.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                        }
                        MainViewModel.NetworkState.Error -> {
                            contentView.visibility = View.GONE
                            progressBar.visibility = View.GONE
                        }
                        MainViewModel.NetworkState.Loading -> {
                            contentView.visibility = View.GONE
                            progressBar.visibility = View.VISIBLE
                        }
                        MainViewModel.NetworkState.Success -> {
                            contentView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun initStatusPickerDialog(){
        _typePickerDialog = BottomSheetDialog(requireActivity())
        typePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        typePickerDialog.setContentView(R.layout.type_picker_bs_dialog_layout)
        _typePickerDialogBinding = TypePickerBsDialogLayoutBinding.inflate(layoutInflater)
        typePickerDialog.setContentView(typePickerDialogBinding.root)
        typePickerDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        typePickerDialog.window?.setGravity(Gravity.BOTTOM)
        typePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}