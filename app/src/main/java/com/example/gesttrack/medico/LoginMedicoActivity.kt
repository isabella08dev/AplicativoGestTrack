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
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_login)


        editEmail = findViewById(R.id.editEmailMedico)
        editSenha = findViewById(R.id.editSenhaMedico)
        btnEntrar = findViewById(R.id.btnEntrarMedico)

        btnEntrar.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            if (!validarCampos(email, senha)) return@setOnClickListener
            verificarLogin(email, senha)
        }
    }

    private fun validarCampos(email: String, senha: String): Boolean {
        return when {
            email.isEmpty() -> {
                exibirMensagem("Digite o email!")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                exibirMensagem("Email inválido!")
                false
            }
            senha.isEmpty() -> {
                exibirMensagem("Digite a senha!")
                false
            }
            else -> true
        }
    }
    private fun verificarLogin(email: String, senha: String) {
        DatabaseHelper.obterMedicoPorEmailESenha(email, senha) { medicoJson ->
            runOnUiThread {
                if (medicoJson == null) {
                    exibirMensagem("Email ou senha incorretos!")
                    return@runOnUiThread
                }

                val medicoId = medicoJson.optString("id_medico", "")
                println("| JSON recebido: $medicoJson")
                println("| ID extraído: $medicoId")

                if (medicoId.isNotEmpty()) {
                    salvarIdMedico(medicoId)
                    exibirMensagem("Login realizado com sucesso!")

                    startActivity(Intent(this, PrincipalMedicoActivity::class.java))
                    finish()
                } else {
                    exibirMensagem("Erro ao obter ID do médico.")
                }
            }
        }
    }
    private fun salvarIdMedico(id: String) {
        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("id_medico", id)
            apply()
        }

        val idSalvo = sharedPref.getString("id_medico", "")
        println("| ID salvo no SharedPrefs: $idSalvo")
    }
    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}
