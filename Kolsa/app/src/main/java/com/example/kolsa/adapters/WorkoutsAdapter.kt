package com.example.kolsa.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kolsa.databinding.WorkoutViewBinding
import com.example.kolsa.data.Workout

class WorkoutsAdapter(
    private val onWorkoutClick:(Workout) -> Unit
) : ListAdapter<Workout, WorkoutsViewHolder>(WorkoutsAdapterDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutsViewHolder {
        return WorkoutsViewHolder(
            WorkoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WorkoutsViewHolder, position: Int) {
        val workout = getItem(position)
        val type = "Тип: ${workout.type}"
        val duration = "Длительность: ${workout.duration} минут"
        with(holder.binding){
            tvName.text = workout.title
            tvType.text = type
            tvDuration.text = duration
            tvDescription.text = workout.description
            root.setOnClickListener {
                onWorkoutClick(workout)
            }
        }
    }
}

class WorkoutsAdapterDiffUtilCallback : DiffUtil.ItemCallback<Workout>() {
    override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean =
        oldItem == newItem
}

class WorkoutsViewHolder (val binding: WorkoutViewBinding) : RecyclerView.ViewHolder(binding.root)
