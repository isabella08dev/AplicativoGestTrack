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

class CadastroMedicoActivity : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editCRM: EditText
    private lateinit var editSenha: EditText
    private lateinit var editTelefone: EditText
    private lateinit var editEmail: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_cadastro)

        editNome = findViewById(R.id.editNomeMedico)
        editCRM = findViewById(R.id.editCRM)
        editSenha = findViewById(R.id.editSenhaMedico)
        editTelefone = findViewById(R.id.editTelefoneMedico)
        editEmail = findViewById(R.id.editEmailMedico)
        btnCadastrar = findViewById(R.id.btnCadastrarMedico)

        btnCadastrar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val crm = editCRM.text.toString().trim()
            val senha = editSenha.text.toString().trim()
            val telefone = editTelefone.text.toString().trim()
            val email = editEmail.text.toString().trim()

            if (nome.isEmpty() || crm.isEmpty() || senha.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
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

            cadastrarMedico(nome, crm, senha, telefone, email)
        }
    }

    private fun cadastrarMedico(
        nome: String,
        crm: String,
        senha: String,
        telefone: String,
        email: String
    ) {
        DatabaseHelper.inserirMedico(
            nome, crm, senha, telefone, email
        ) { sucesso, erro ->
            runOnUiThread {
                if (sucesso) {
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PrincipalMedicoActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erro: $erro", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
