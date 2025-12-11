package com.rayhan.postest8

import androidx.recyclerview.widget.RecyclerView
import com.rayhan.postest8.databinding.ItemTaksBinding
import android.view.LayoutInflater
import android.view.ViewGroup

class TaskAdapter(
    private val tasks: List<Task>,
    private val onCheckClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit,
    private val onEditClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaksBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTitle.text = task.title
            binding.tvRelease.text = task.deadline
            binding.tvDesc.text = task.description
            binding.checkBox.isChecked = task.done
            binding.cardView.alpha = if (task.done) 0.5f else 1f
            binding.tvTitle.paint.isStrikeThruText = task.done
            binding.checkBox.setOnCheckedChangeListener(null)
            binding.checkBox.setOnCheckedChangeListener { _, _ ->
                onCheckClicked(task)
            }
            binding.btnDelete.setOnClickListener { onDeleteClicked(task) }
            binding.root.setOnClickListener { onEditClicked(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }
}