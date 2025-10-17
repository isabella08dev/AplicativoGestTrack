package com.example.gesttrack.paciente

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.R

class CalendarioPacienteActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView
    private lateinit var addButton: Button
    private var selectedDateInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_calendario)

        calendarView = findViewById(R.id.calendarViewPaciente)

        addButton = findViewById(R.id.btnAddSintoma)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDateInMillis = getDateInMillis(year, month, dayOfMonth)
            selectedDateText.text = "Data selecionada: $dayOfMonth/${month + 1}/$year"
            selectedDateText.setTextColor(resources.getColor(android.R.color.holo_blue_dark))
        }

        addButton.setOnClickListener {
            if (selectedDateInMillis != 0L) {
                showAddEventDialog()
            } else {
                selectedDateText.text = "Por favor, selecione uma data primeiro."
            }
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
                selectedDateText.text = "Evento adicionado para a data selecionada."
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
