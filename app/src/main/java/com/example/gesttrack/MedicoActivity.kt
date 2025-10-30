package com.example.gesttrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.medico.CadastroMedicoActivity
import com.example.gesttrack.medico.LoginMedicoActivity
class MedicoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_medico)

        val btnLogin = findViewById<Button>(R.id.btnLoginMedico)
        val btnEntrar = findViewById<Button>(R.id.btnCadastroMedico)

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginMedicoActivity::class.java)
            startActivity(intent)
        }

        btnEntrar.setOnClickListener {
            val intent = Intent(this, CadastroMedicoActivity::class.java)
            startActivity(intent)
        }
    }
}