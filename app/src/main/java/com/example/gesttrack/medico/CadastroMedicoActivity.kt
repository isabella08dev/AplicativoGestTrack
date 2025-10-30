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

        inicializarViews()
        configurarBotaoCadastro()
    }
    private fun inicializarViews() {
        editNome = findViewById(R.id.editNomeMedico)
        editCRM = findViewById(R.id.editCRM)
        editSenha = findViewById(R.id.editSenhaMedico)
        editTelefone = findViewById(R.id.editTelefoneMedico)
        editEmail = findViewById(R.id.editEmailMedico)
        btnCadastrar = findViewById(R.id.btnCadastrarMedico)
    }
    private fun configurarBotaoCadastro() {
        btnCadastrar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val crm = editCRM.text.toString().trim()
            val senha = editSenha.text.toString().trim()
            val telefone = editTelefone.text.toString().trim()
            val email = editEmail.text.toString().trim()

            if (!validarCampos(nome, crm, senha, telefone, email)) return@setOnClickListener
            cadastrarMedico(nome, crm, senha, telefone, email)
        }
    }
    private fun validarCampos(
        nome: String,
        crm: String,
        senha: String,
        telefone: String,
        email: String
    ): Boolean {
        return when {
            nome.isEmpty() || crm.isEmpty() || senha.isEmpty() || telefone.isEmpty() || email.isEmpty() -> {
                exibirMensagem("Preencha todos os campos!")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                exibirMensagem("Digite um email válido!")
                false
            }
            senha.length < 6 -> {
                exibirMensagem("A senha deve ter pelo menos 6 caracteres!")
                false
            }
            else -> true
        }
    }
    private fun cadastrarMedico(
        nome: String,
        crm: String,
        senha: String,
        telefone: String,
        email: String
    ) {
        DatabaseHelper.inserirMedico(nome, crm, senha, telefone, email) { sucesso, erro ->
            runOnUiThread {
                if (sucesso) {
                    exibirMensagem("Cadastro realizado com sucesso!")
                    startActivity(Intent(this, PrincipalMedicoActivity::class.java))
                    finish()
                } else {
                    exibirMensagem("Erro: ${erro ?: "Erro desconhecido."}")
                }
            }
        }
    }
    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}
