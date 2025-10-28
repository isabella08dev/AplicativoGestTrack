package com.example.gesttrack.paciente

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView
import android.widget.Toast
class CalendarioPacienteActivity : AppCompatActivity() {

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton

    // Calendário
    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var addButton: ImageButton // MUDADO para ImageButton
    private var selectedDateInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_calendario)

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

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_perfil -> {
                    startActivity(Intent(this, MeuPerfilPacienteActivity::class.java))
                    true
                }
                R.id.nav_calendario -> {
                    // Já estamos aqui
                    true
                }
                R.id.nav_notas -> {
                    startActivity(Intent(this, MinhasNotasPacienteActivity::class.java))
                    true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatPacienteActivity::class.java))
                    true
                }
                R.id.nav_voltar -> {
                    finish()
                    true
                }
                else -> false
            }
        }

        // ===== Calendário =====
        calendarView = findViewById(R.id.calendarViewPaciente)
        addButton = findViewById(R.id.btnAddSintoma)
        // A view de texto foi removida do layout original, o código para ela foi comentado.
        // selectedDateText = findViewById(R.id.selectedDateText) 

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDateInMillis = getDateInMillis(year, month, dayOfMonth)
            // selectedDateText.text = "Data selecionada: $dayOfMonth/${month + 1}/$year"
            // selectedDateText.setTextColor(resources.getColor(android.R.color.holo_blue_dark))
        }

        addButton.setOnClickListener {
            if (selectedDateInMillis != 0L) {
                showAddEventDialog()
            } else {
                // selectedDateText.text = "Por favor, selecione uma data primeiro."
                // Como o texto não existe mais, podemos usar um Toast
                Toast.makeText(this, "Por favor, selecione uma data primeiro.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun showAddEventDialog() {
        AlertDialog.Builder(this)
            .setTitle("Adicionar Evento")
            .setMessage("Marcar sintomas ou eventos para a data selecionada.")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                // selectedDateText.text = "Evento adicionado para a data selecionada."
                Toast.makeText(this, "Evento adicionado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
