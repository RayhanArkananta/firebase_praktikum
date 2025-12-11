package com.rayhan.postest8

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.rayhan.postest8.databinding.UploadDialogBinding
import com.google.firebase.database.DatabaseReference
import java.util.Calendar

class TaskAddDialog(
    private val context: Context,
    private val ref: DatabaseReference,
    private val task: Task?
) {

    fun show() {
        val bind = UploadDialogBinding.inflate(LayoutInflater.from(context))
        val title = if (task == null) "Tambah Tugas Baru" else "Edit Tugas"

        bind.editTextTitleBook.setText(task?.title ?: "")
        bind.editTextDesc.setText(task?.description ?: "")
        bind.editTextRelease.setText(task?.deadline ?: "")
        bind.editTextRelease.setOnClickListener {

            val cal = Calendar.getInstance()
            val dp = DatePickerDialog(
                context,
                { _, y, m, d ->
                    bind.editTextRelease.setText("$d/${m + 1}/$y")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            // 🔥 BATASI TANGGAL MINIMAL HARI INI
            dp.datePicker.minDate = System.currentTimeMillis()

            dp.show()
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(bind.root)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newTitle = bind.editTextTitleBook.text.toString()
            val desc = bind.editTextDesc.text.toString()
            val date = bind.editTextRelease.text.toString()

            if (newTitle.isEmpty() || date.isEmpty()) {
                Toast.makeText(context, "Lengkapi data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (task == null)
                saveNewTask(newTitle, desc, date)
            else
                updateExistingTask(task.id!!, newTitle, desc, date)

            dialog.dismiss()
        }
    }


    private fun saveNewTask(title: String, desc: String, date: String) {
        val id = ref.push().key!!

        val data = Task(
            id = id,
            title = title,
            description = desc,
            deadline = date,
            done = false
        )

        ref.child(id).setValue(data)
        Toast.makeText(context, "Tugas ditambah", Toast.LENGTH_SHORT).show()
    }

    private fun updateExistingTask(id: String, title: String, desc: String, date: String) {
        val update = mapOf(
            "title" to title,
            "description" to desc,
            "deadline" to date
        )

        ref.child(id).updateChildren(update)
        Toast.makeText(context, "Tugas diperbarui", Toast.LENGTH_SHORT).show()
    }
}