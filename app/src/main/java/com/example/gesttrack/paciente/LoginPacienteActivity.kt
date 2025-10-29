package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.DatabaseHelper
import com.example.gesttrack.R

class LoginPacienteActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_login)

        editEmail = findViewById(R.id.editEmailPaciente)
        editSenha = findViewById(R.id.editSenhaPaciente)
        btnEntrar = findViewById(R.id.btnEntrarPaciente)


        btnEntrar.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "Digite o email!", Toast.LENGTH_SHORT).show()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Email inválido!", Toast.LENGTH_SHORT).show()
                }

                senha.isEmpty() -> {
                    Toast.makeText(this, "Digite a senha!", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    verificarLogin(email, senha)
                }
            }
        }
    }

    private fun verificarLogin(email: String, senha: String) {
        DatabaseHelper.obterPacientePorEmailESenha(email, senha) { pacienteJson ->
            runOnUiThread {
                if (pacienteJson != null) {
                    // 🔥 MUDE DE optInt para optString
                    val pacienteId = pacienteJson.optString("id_paciente", "")

                    println("📍 JSON recebido: $pacienteJson")
                    println("📍 ID extraído: $pacienteId")

                    if (pacienteId.isNotEmpty()) {
                        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("id_paciente", pacienteId) // 🔥 putString ao invés de putInt
                            commit()
                        }

                        val idSalvo = sharedPref.getString("id_paciente", "")
                        println("📍 ID salvo no SharedPrefs: $idSalvo")

                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, PrincipalPacienteActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Erro ao obter ID do paciente.", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Email ou senha incorretos!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


