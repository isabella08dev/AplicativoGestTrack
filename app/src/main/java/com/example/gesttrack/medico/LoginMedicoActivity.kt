package com.example.gesttrack.medico

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.DatabaseHelper
import com.example.gesttrack.R

class LoginMedicoActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editCrm: EditText
    private lateinit var btnEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_login)

        editEmail = findViewById(R.id.editEmailMedico)
        editCrm = findViewById(R.id.editSenhaMedico)
        btnEntrar = findViewById(R.id.btnEntrarMedico)

        btnEntrar.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val crm = editCrm.text.toString().trim()

            when {
                email.isEmpty() -> Toast.makeText(this, "Digite o email!", Toast.LENGTH_SHORT).show()
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(this, "Email inválido!", Toast.LENGTH_SHORT).show()
                crm.isEmpty() -> Toast.makeText(this, "Digite o CRM!", Toast.LENGTH_SHORT).show()
                else -> {
                    DatabaseHelper.verificarLoginMedico(email, crm) { valido, erro ->
                        runOnUiThread {
                            if (valido) {
                                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,PrincipalMedicoActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Email ou CRM incorretos!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
