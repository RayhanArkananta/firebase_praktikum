package com.rayhan.postest8

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rayhan.postest8.databinding.ActivityMainBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskRef: DatabaseReference
    private lateinit var adapter: TaskAdapter
    private var taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskRef = FirebaseDatabase.getInstance().getReference("tasks")

        adapter = TaskAdapter(
            taskList,
            onCheckClicked = { task -> toggleTaskDone(task) },
            onDeleteClicked = { task -> deleteTask(task) },
            onEditClicked = { task -> showAddEditDialog(task) }
        )

        binding.rvBooks.layoutManager = LinearLayoutManager(this)
        binding.rvBooks.adapter = adapter

        setupTodayDate()
        fetchTasks()

        binding.fabAddBooks.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun setupTodayDate() {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = formatter.format(Date())
        binding.tvTodayDate.text = "Hari ini - $today"
    }

    private fun fetchTasks() {
        taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()

                for (data in snapshot.children) {
                    val task = data.getValue(Task::class.java)
                    task?.let { taskList.add(it) }
                }

                adapter.notifyDataSetChanged()
                updateEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateEmptyState() {
        if (taskList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.rvBooks.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.rvBooks.visibility = View.VISIBLE
        }
    }

    private fun toggleTaskDone(task: Task) {
        taskRef.child(task.id!!).child("done").setValue(!task.done)
    }

    private fun deleteTask(task: Task) {
        taskRef.child(task.id!!).removeValue()
        Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
    }

    private fun showAddEditDialog(task: Task?) {
        val dialog = TaskAddDialog(this, taskRef, task)
        dialog.show()
    }
}