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
import java.text.SimpleDateFormat
import java.util.Locale

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

            // 🔹 Converte a data de "dd/MM/yyyy" para "yyyy-MM-dd"
            val dataFormatada = try {
                val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formatoSaida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val data = formatoEntrada.parse(dataNascimento)
                formatoSaida.format(data!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Data de nascimento inválida!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cadastrarPaciente(nome, cpf, rg, dataFormatada, telefone, email, senha)
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
        DatabaseHelper.inserirPaciente(
            nome, cpf, rg, dataNascimento, telefone, email, senha
        ) { sucesso, erro ->
            runOnUiThread {
                if (sucesso) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PrincipalPacienteActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erro: $erro", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
