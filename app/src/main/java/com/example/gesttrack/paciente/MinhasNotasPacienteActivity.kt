package com.example.gesttrack.paciente

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R

class MinhasNotasPacienteActivity : AppCompatActivity() {

    private lateinit var etNovaNota: EditText
    private lateinit var btnAdd: Button
    private lateinit var rvNotas: RecyclerView
    private lateinit var notasAdapter: NotasAdapter
    private val listaNotas = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_minhasnotas)

        etNovaNota = findViewById(R.id.etNovaNota)
        btnAdd = findViewById(R.id.btnAdd)
        rvNotas = findViewById(R.id.rvNotas)

        notasAdapter = NotasAdapter(listaNotas)
        rvNotas.layoutManager = LinearLayoutManager(this)
        rvNotas.adapter = notasAdapter

        btnAdd.setOnClickListener {
            val texto = etNovaNota.text.toString().trim()
            if (texto.isNotEmpty()) {
                listaNotas.add(texto)
                notasAdapter.notifyItemInserted(listaNotas.size - 1)
                etNovaNota.text.clear()
            } else {
                Toast.makeText(this, "Digite uma nota!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class NotasAdapter(private val notas: MutableList<String>) :
        RecyclerView.Adapter<NotasAdapter.NoteViewHolder>() {

        inner class NoteViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvNota: TextView = view.findViewById(R.id.tvNota)
            val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
            val btnDeletar: ImageButton = view.findViewById(R.id.btnDeletar)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): NoteViewHolder {
            val view = layoutInflater.inflate(R.layout.item_nota, parent, false)
            return NoteViewHolder(view)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            val nota = notas[position]
            holder.tvNota.text = nota

            holder.btnEditar.setOnClickListener {
                val editText = EditText(this@MinhasNotasPacienteActivity)
                editText.setText(nota)

                val alert = android.app.AlertDialog.Builder(this@MinhasNotasPacienteActivity)
                    .setTitle("Editar Nota")
                    .setView(editText)
                    .setPositiveButton("Salvar") { _, _ ->
                        val novaNota = editText.text.toString().trim()
                        if (novaNota.isNotEmpty()) {
                            notas[position] = novaNota
                            notifyItemChanged(position)
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .create()
                alert.show()
            }

            holder.btnDeletar.setOnClickListener {
                notas.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        override fun getItemCount() = notas.size
    }
}
