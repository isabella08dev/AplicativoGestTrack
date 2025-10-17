package com.example.gesttrack.medico

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.R

class CalendarioMedicoActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var btnMarcarConsulta: Button
    private var selectedDateInMillis: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_calendario)

        calendarView = findViewById(R.id.calendarViewMedico)
        btnMarcarConsulta = findViewById(R.id.btnMarcarConsulta)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDateInMillis = getDateInMillis(year, month, dayOfMonth)
            btnMarcarConsulta.isEnabled = true
        }

        btnMarcarConsulta.setOnClickListener {
            if (selectedDateInMillis != 0L) {
                abrirDialogMarcarConsulta()
            }
        }
    }

    private fun getDateInMillis(year: Int, month: Int, day: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun abrirDialogMarcarConsulta() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Consulta")
            .setMessage("Deseja marcar uma consulta para a data selecionada?")
            .setPositiveButton("Sim") { dialog, _ ->
                dialog.dismiss()
                btnMarcarConsulta.isEnabled = false
                // Aqui coloque a lógica para salvar a consulta na agenda
            }
            .setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
