package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gesttrack.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import com.example.gesttrack.R

class CadastroPacienteActivity : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editCPF: EditText
    private lateinit var editRG: EditText
    private lateinit var editDataNascimento: EditText
    private lateinit var editTelefone: EditText
    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_cadastro)

        // Referência dos campos
        editNome = findViewById(R.id.editNomePaciente)
        editCPF = findViewById(R.id.editCPFPaciente)
        editRG = findViewById(R.id.editRGPaciente)
        editDataNascimento = findViewById(R.id.editDataNascimentoPaciente)
        editTelefone = findViewById(R.id.editTelefonePaciente)
        editEmail = findViewById(R.id.editEmailPaciente)
        editSenha = findViewById(R.id.editSenhaPaciente)
        btnCadastrar = findViewById(R.id.btnCadastrarPaciente)

        btnCadastrar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val cpf = editCPF.text.toString().trim()
            val rg = editRG.text.toString().trim()
            val dataNascimento = editDataNascimento.text.toString().trim()
            val telefone = editTelefone.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            // Validações básicas
            if (nome.isEmpty() || cpf.isEmpty() || rg.isEmpty() ||
                dataNascimento.isEmpty() || telefone.isEmpty() ||
                email.isEmpty() || senha.isEmpty()
            ) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Digite um email válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cadastrarPaciente(nome, cpf, rg, dataNascimento, telefone, email, senha)
        }
    }

    private fun cadastrarPaciente(
        nome: String,
        cpf: String,
        rg: String,
        dataNascimento: String,
        telefone: String,
        email: String,
        senha: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = SupabaseClient.client


                val session = client.auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
                    this.email = email
                    this.password = senha
                }


                // 2️⃣ Insere dados no Realtime (tabela Pacientes)
                val data = mapOf(
                    "nome" to nome,
                    "cpf" to cpf,
                    "rg" to rg,
                    "data_nascimento" to dataNascimento,
                    "telefone" to telefone,
                    "email" to email,
                    "senha" to senha,
                    "data_criacao" to LocalDateTime.now().toString()
                )

                client.from("Pacientes").insert(data)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CadastroPacienteActivity,
                        "Cadastro realizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this@CadastroPacienteActivity,
                        LoginPacienteActivity::class.java
                    )
                    startActivity(intent)
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CadastroPacienteActivity,
                        "Erro ao cadastrar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
