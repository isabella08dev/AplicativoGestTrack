package com.example.gesttrack.paciente

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONObject

data class Nota(val id: Long, var texto: String, var cor: Int)

class MinhasNotasPacienteActivity : AppCompatActivity() {

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton

    // Notas
    private lateinit var etNovaNota: EditText
    private lateinit var btnAdd: MaterialButton
    private lateinit var rvNotas: RecyclerView
    private lateinit var notasAdapter: NotasAdapter
    private val listaNotas = mutableListOf<Nota>()
    private lateinit var rgCores: RadioGroup
    private var corSelecionada: Int = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_minhasnotas)

        // ===== Drawer =====
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Lógica de navegação CORRIGIDA
        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_perfil -> {
                    startActivity(Intent(this, MeuPerfilPacienteActivity::class.java))
                    true
                }
                R.id.nav_calendario -> {
                    startActivity(Intent(this, CalendarioPacienteActivity::class.java))
                    true
                }
                R.id.nav_notas -> {
                    true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatPacienteActivity::class.java))
                    true
                }
                R.id.nav_voltar -> {
                    startActivity(Intent(this, PrincipalPacienteActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // ===== UI Notas =====
        etNovaNota = findViewById(R.id.etNovaNota)
        btnAdd = findViewById(R.id.btnAdd)
        rvNotas = findViewById(R.id.rvNotas)
        rgCores = findViewById(R.id.rgCores)

        notasAdapter = NotasAdapter(listaNotas)
        rvNotas.layoutManager = LinearLayoutManager(this)
        rvNotas.adapter = notasAdapter

        setupColorSelection()
        setupAddButton()
        carregarNotas()
    }

    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupColorSelection() {
        corSelecionada = ContextCompat.getColor(this, R.color.postit_amarelo)
        rgCores.check(R.id.rbCor1)

        rgCores.setOnCheckedChangeListener { _, checkedId ->
            corSelecionada = when (checkedId) {
                R.id.rbCor1 -> ContextCompat.getColor(this, R.color.postit_amarelo)
                R.id.rbCor2 -> ContextCompat.getColor(this, R.color.postit_rosa)
                R.id.rbCor3 -> ContextCompat.getColor(this, R.color.postit_azul)
                R.id.rbCor4 -> ContextCompat.getColor(this, R.color.postit_verde)
                else -> ContextCompat.getColor(this, R.color.postit_amarelo)
            }
        }
    }

    private fun setupAddButton() {
        btnAdd.setOnClickListener {
            val texto = etNovaNota.text.toString().trim()
            if (texto.isNotEmpty()) {
                val novaNota = Nota(System.currentTimeMillis(), texto, corSelecionada)
                listaNotas.add(0, novaNota)
                notasAdapter.notifyItemInserted(0)
                rvNotas.scrollToPosition(0)
                etNovaNota.text.clear()
                salvarNotas()
            } else {
                Toast.makeText(this, "Digite uma nota!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarNotas() {
        val prefs = getSharedPreferences("GestTrackNotas", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val jsonArray = JSONArray()
        listaNotas.forEach { nota ->
            jsonArray.put(
                JSONObject().apply {
                    put("id", nota.id)
                    put("texto", nota.texto)
                    put("cor", nota.cor)
                }
            )
        }
        editor.putString("lista_notas", jsonArray.toString())
        editor.apply()
    }

    private fun carregarNotas() {
        val prefs = getSharedPreferences("GestTrackNotas", Context.MODE_PRIVATE)
        val jsonString = prefs.getString("lista_notas", null) ?: return
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                listaNotas.add(
                    Nota(
                        obj.getLong("id"),
                        obj.getString("texto"),
                        obj.getInt("cor")
                    )
                )
            }
            notasAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            getSharedPreferences("GestTrackNotas", Context.MODE_PRIVATE).edit().clear().apply()
        }
    }

    inner class NotasAdapter(private val notas: MutableList<Nota>) :
        RecyclerView.Adapter<NotasAdapter.NoteViewHolder>() {

        inner class NoteViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvNota: TextView = view.findViewById(R.id.tvNota)
            val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
            val btnDeletar: ImageButton = view.findViewById(R.id.btnDeletar)
            val cardView: CardView = view as CardView
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): NoteViewHolder {
            val view = layoutInflater.inflate(R.layout.item_nota, parent, false)
            return NoteViewHolder(view)
        }

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            val nota = notas[position]
            holder.tvNota.text = nota.texto
            holder.cardView.setCardBackgroundColor(nota.cor)

            holder.btnEditar.setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                val editText = EditText(this@MinhasNotasPacienteActivity).apply {
                    setText(notas[pos].texto)
                    setSelection(text.length)
                }

                android.app.AlertDialog.Builder(this@MinhasNotasPacienteActivity)
                    .setTitle("Editar Nota")
                    .setView(editText)
                    .setPositiveButton("Salvar") { _, _ ->
                        val novoTexto = editText.text.toString().trim()
                        if (novoTexto.isNotEmpty()) {
                            notas[pos].texto = novoTexto
                            notifyItemChanged(pos)
                            salvarNotas()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            holder.btnDeletar.setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                notas.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, notas.size - pos)
                salvarNotas()
            }
        }

        override fun getItemCount() = notas.size
    }
}