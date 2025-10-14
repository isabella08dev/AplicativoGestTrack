package com.example.gesttrack.paciente

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R

class MinhasNotasPacienteActivity : AppCompatActivity() {

    private lateinit var notasPaciente: NotasPaciente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_minhasnotas)

        val etNote = findViewById<EditText>(R.id.etNote)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        notasPaciente = NotasPaciente(mutableListOf())
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = notasPaciente

        btnAdd.setOnClickListener {
            val note = etNote.text.toString()
            if (note.isNotBlank()) {
                notasPaciente.addNote(note)
                etNote.text.clear()
            }
        }
    }
}
