package com.example.gesttrack.paciente

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat
import java.util.*
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class CalendarioPacienteActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var btnAddEvento: ImageButton
    private lateinit var layoutEventos: LinearLayout

    private val eventosPorData = HashMap<CalendarDay, MutableList<Evento>>()
    private var dataSelecionada: CalendarDay? = null

    enum class TipoEvento { SINTOMA, CONSULTA }

    data class Evento(val tipo: TipoEvento, val descricao: String)

    private val coresEvento = mapOf(
        TipoEvento.SINTOMA to R.color.purple_500,
        TipoEvento.CONSULTA to R.color.blue_500
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_calendario)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btnMenu)
        calendarView = findViewById(R.id.calendarViewPaciente)
        btnAddEvento = findViewById(R.id.btnAddEvento)
        layoutEventos = findViewById(R.id.layoutEventos)

        btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
            else drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_perfil -> { startActivity(Intent(this, MeuPerfilPacienteActivity::class.java)); true }
                R.id.nav_notas -> { startActivity(Intent(this, MinhasNotasPacienteActivity::class.java)); true }
                R.id.nav_chat -> { startActivity(Intent(this, ChatPacienteActivity::class.java)); true }
                R.id.nav_voltar -> { finish(); true }
                else -> false
            }
        }

        calendarView.setOnDateChangedListener { _, date, _ ->
            dataSelecionada = date
            mostrarEventos(date)
        }

        btnAddEvento.setOnClickListener {
            dataSelecionada?.let { adicionarEvento(it) }
                ?: Toast.makeText(this, "Selecione uma data primeiro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun adicionarEvento(date: CalendarDay) {
        val tipos = TipoEvento.values().map { it.name }
        var tipoSelecionado = TipoEvento.SINTOMA

        val input = EditText(this)
        input.hint = "Descrição do evento"

        AlertDialog.Builder(this)
            .setTitle("Adicionar evento")
            .setSingleChoiceItems(tipos.toTypedArray(), 0) { _, which ->
                tipoSelecionado = TipoEvento.values()[which]
            }
            .setView(input)
            .setPositiveButton("Salvar") { dialog, _ ->
                val texto = input.text.toString().trim()
                if (texto.isNotEmpty()) {
                    val lista = eventosPorData.getOrPut(date) { mutableListOf() }
                    lista.add(Evento(tipoSelecionado, texto))
                    marcarData(date)
                    mostrarEventos(date)
                    Toast.makeText(this, "Evento salvo!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun mostrarEventos(date: CalendarDay) {
        layoutEventos.removeAllViews()
        val lista = eventosPorData[date]
        if (lista.isNullOrEmpty()) return

        lista.forEach { evento ->
            val icon = ImageView(this)
            icon.layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                setMargins(16, 0, 16, 0)
            }
            icon.background = getDrawable(R.drawable.botao_bege_borda_azul)
            icon.setPadding(24, 24, 24, 24)

            when (evento.tipo) {
                TipoEvento.SINTOMA -> icon.setImageResource(R.drawable.notas_icon) // coloque seu ícone
                TipoEvento.CONSULTA -> icon.setImageResource(R.drawable.notas_icon)
            }

            icon.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Evento")
                    .setMessage("${evento.tipo.name}: ${evento.descricao}")
                    .setPositiveButton("OK", null)
                    .show()
            }

            layoutEventos.addView(icon)
        }
    }
    private fun marcarData(date: CalendarDay) {
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay) = day == date
            override fun decorate(view: DayViewFacade) {
                eventosPorData[date]?.forEach { evento ->
                    val color = coresEvento[evento.tipo] ?: R.color.black
                    view.addSpan(DotSpan(10f, getColor(color)))
                }
            }
        })
    }

    private fun formatarData(date: CalendarDay): String {
        val calendar = Calendar.getInstance()
        calendar.set(date.year, date.month - 1, date.day)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}
