package com.example.gesttrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.paciente.CadastroPacienteActivity
import com.example.gesttrack.paciente.LoginPacienteActivity

class PacienteActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_paciente)

        val btnLogin = findViewById<Button>(R.id.btnLoginPaciente)
        val btnEntrar = findViewById<Button>(R.id.btnCadastroPaciente)

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginPacienteActivity::class.java)
            startActivity(intent)
        }

        btnEntrar.setOnClickListener {
            val intent = Intent(this, CadastroPacienteActivity::class.java)
            startActivity(intent)
        }
    }
}