package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView

class MinhasNotasPacienteActivity : AppCompatActivity() {

    private lateinit var etNote: EditText
    private lateinit var btnAdd: Button
    private lateinit var rvNotes: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: ImageButton

    private val notesList = mutableListOf<String>()
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_minhasnotas)

        etNote = findViewById(R.id.etNote)
        btnAdd = findViewById(R.id.btnAdd)
        rvNotes = findViewById(R.id.rvNotes)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btnMenu)

        adapter = NotesAdapter()
        rvNotes.adapter = adapter
        rvNotes.layoutManager = LinearLayoutManager(this)

        btnAdd.setOnClickListener {
            val noteText = etNote.text.toString().trim()
            if (noteText.isNotEmpty()) {
                notesList.add(noteText)
                adapter.notifyItemInserted(notesList.size - 1)
                etNote.text.clear()
            }
        }

        btnMenu.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.nav_perfil -> startActivity(Intent(this, MeuPerfilPacienteActivity::class.java))
                R.id.nav_calendario -> startActivity(Intent(this, CalendarioPacienteActivity::class.java))
                R.id.nav_notas -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.nav_chat -> startActivity(Intent(this, ChatPacienteActivity::class.java))
                R.id.nav_voltar -> startActivity(Intent(this, PrincipalPacienteActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    inner class NotesAdapter : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

        inner class NoteViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            val etNoteItem: EditText = itemView.findViewById(R.id.etNoteItem)
            val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
            val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): NoteViewHolder {
            val view = layoutInflater.inflate(R.layout.item_nota, parent, false)
            return NoteViewHolder(view)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            holder.etNoteItem.setText(notesList[position])

            // Editar: salva diretamente
            holder.btnEdit.setOnClickListener {
                val newText = holder.etNoteItem.text.toString().trim()
                if (newText.isNotEmpty()) {
                    notesList[position] = newText
                    notifyItemChanged(position)
                }
            }

            // Deletar
            holder.btnDelete.setOnClickListener {
                notesList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        override fun getItemCount(): Int = notesList.size
    }
}
