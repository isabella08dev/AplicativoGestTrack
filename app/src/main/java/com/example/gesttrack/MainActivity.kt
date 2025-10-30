package com.example.gesttrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val btnMedico = findViewById<Button>(R.id.btnSouMedico)
        val btnPaciente = findViewById<Button>(R.id.btnSouPaciente)

        btnMedico.setOnClickListener {
            val intent = Intent(this, MedicoActivity::class.java)
            startActivity(intent)
        }

        btnPaciente.setOnClickListener {
            val intent = Intent(this, PacienteActivity::class.java)
            startActivity(intent)
        }
    }
}