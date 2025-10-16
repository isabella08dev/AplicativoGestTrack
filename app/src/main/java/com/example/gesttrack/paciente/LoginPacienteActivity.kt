package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.R
import com.example.gesttrack.SupabaseClient

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class LoginPacienteActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var btnEsqueciSenha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_login)

        editEmail = findViewById(R.id.editEmailMedico)
        editSenha = findViewById(R.id.editSenhaMedico)
        btnEntrar = findViewById(R.id.btnEntrarMedico)
        btnEsqueciSenha = findViewById(R.id.btnEsqueciSenhaMedico)


        btnEntrar.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            // Validações locais
            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "O campo email não pode estar vazio.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Digite um email válido.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                senha.isEmpty() -> {
                    Toast.makeText(this, "O campo senha não pode estar vazio.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                senha.length < 6 -> {
                    Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                else -> {
                    verificarLogin(email, senha)
                }
            }
        }

        btnEsqueciSenha.setOnClickListener {
            enviarEmailRecuperacao()
        }
    }

    private fun verificarLogin(email: String, senha: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Tenta autenticar o usuário com Supabase
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = senha
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginPacienteActivity,
                        "Login realizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redireciona para tela principal
                    startActivity(Intent(this@LoginPacienteActivity, PrincipalPacienteActivity::class.java))
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Caso o login falhe (usuário não cadastrado)
                    Toast.makeText(
                        this@LoginPacienteActivity,
                        "Você não possui cadastro. Crie sua conta!",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this@LoginPacienteActivity, CadastroPacienteActivity::class.java)
                    intent.putExtra("email_pre_preenchido", email)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun enviarEmailRecuperacao() {
        val email = editEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Digite seu email para recuperar a senha.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Digite um email válido.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.auth.resetPasswordForEmail(email)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginPacienteActivity,
                        "Email de recuperação enviado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginPacienteActivity,
                        "Erro: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
