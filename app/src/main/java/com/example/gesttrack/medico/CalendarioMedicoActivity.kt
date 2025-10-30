package com.example.gesttrack.medico

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

class CalendarioMedicoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var btnAddEvento: ImageButton
    private lateinit var layoutEventos: LinearLayout

    private val eventosPorData = HashMap<CalendarDay, MutableList<Evento>>()
    private var dataSelecionada: CalendarDay? = null

    // 🔹 Agora com PARTO incluído
    enum class TipoEvento { CONSULTA, PARTO }

    data class Evento(val tipo: TipoEvento, val descricao: String)

    private val coresEvento = mapOf(
        TipoEvento.CONSULTA to R.color.blue_500,
        TipoEvento.PARTO to R.color.purple_500
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_calendario)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btnMenu)
        calendarView = findViewById(R.id.calendarViewMedico)
        btnAddEvento = findViewById(R.id.btnAddEvento)
        layoutEventos = findViewById(R.id.layoutEventos)

        btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_perfil -> true
                R.id.nav_calendario -> {
                    startActivity(Intent(this, CalendarioMedicoActivity::class.java))
                    true
                }
                R.id.nav_meuspacientes -> {
                    startActivity(Intent(this, MeusPacientesMedicoActivity::class.java))
                    true
                }
                R.id.nav_voltar -> {
                    startActivity(Intent(this, PrincipalMedicoActivity::class.java))
                    true
                }
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
        val tipos = TipoEvento.values().map {
            when (it) {
                TipoEvento.CONSULTA -> "Consulta"
                TipoEvento.PARTO -> "Parto"
            }
        }

        var tipoSelecionado = TipoEvento.CONSULTA
        val input = EditText(this)
        input.hint = "Descrição (ex: local, observações...)"

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
                TipoEvento.CONSULTA -> icon.setImageResource(R.drawable.notas_icon)
                TipoEvento.PARTO -> icon.setImageResource(R.drawable.notas_icon)
            }

            icon.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(evento.tipo.name)
                    .setMessage(evento.descricao)
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
