package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gesttrack.DatabaseHelper
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class MeuPerfilPacienteActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton

    private lateinit var etNome: EditText
    private lateinit var etCpf: EditText
    private lateinit var etRg: EditText
    private lateinit var etDataNascimento: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btnAtualizar: AppCompatButton

    private var pacienteId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_meuperfil)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_perfil -> true
                R.id.nav_calendario -> {
                    startActivity(Intent(this, CalendarioPacienteActivity::class.java))
                    true
                }
                R.id.nav_notas -> {
                    startActivity(Intent(this, MinhasNotasPacienteActivity::class.java))
                    true
                }
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatPacienteActivity::class.java))
                    true
                }
                R.id.nav_voltar -> {
                    finish()
                    true
                }
                else -> false
            }
        }

        etNome = findViewById(R.id.editNomePaciente)
        etCpf = findViewById(R.id.editCPFPaciente)
        etRg = findViewById(R.id.editRGPaciente)
        etDataNascimento = findViewById(R.id.editDataNascimentoPaciente)
        etTelefone = findViewById(R.id.editTelefonePaciente)
        etEmail = findViewById(R.id.editEmailPaciente)
        etSenha = findViewById(R.id.editSenhaPaciente)
        btnAtualizar = findViewById(R.id.btnAtualizarPaciente)

        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        pacienteId = sharedPref.getString("id_paciente", "") ?: ""
        if (pacienteId.isEmpty()) { // 🔥 isEmpty() ao invés de == 0
            Toast.makeText(this, "Erro: paciente não logado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        carregarDadosPaciente()

        btnAtualizar.setOnClickListener {
            atualizarDadosPaciente()
        }
    }

    private fun carregarDadosPaciente() {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper.obterPacientePorId(pacienteId) { pacienteJson ->
                runOnUiThread {
                    if (pacienteJson != null) {
                        etNome.setText(pacienteJson.optString("nome"))
                        etCpf.setText(pacienteJson.optString("cpf"))
                        etRg.setText(pacienteJson.optString("rg"))
                        etDataNascimento.setText(pacienteJson.optString("data_nascimento"))
                        etTelefone.setText(pacienteJson.optString("telefone"))
                        etEmail.setText(pacienteJson.optString("email"))
                        etSenha.setText(pacienteJson.optString("senha"))
                    } else {
                        Toast.makeText(this@MeuPerfilPacienteActivity, "Erro ao carregar dados do paciente.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun atualizarDadosPaciente() {
        val nome = etNome.text.toString().trim()
        val cpf = etCpf.text.toString().trim()
        val rg = etRg.text.toString().trim()
        val dataNascimento = etDataNascimento.text.toString().trim()
        val telefone = etTelefone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply {
                    put("nome", nome)
                    put("cpf", cpf)
                    put("rg", rg)
                    put("data_nascimento", dataNascimento)
                    put("telefone", telefone)
                    put("email", email)
                    put("senha", senha)
                }

                val url = "${DatabaseHelper.SUPABASE_URL}/rest/v1/pacientes?id_paciente=eq.$pacienteId"


                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = json.toString().toRequestBody(mediaType)

                val request = okhttp3.Request.Builder()
                    .url(url)
                    .patch(requestBody)
                    .addHeader("apikey", DatabaseHelper.SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer ${DatabaseHelper.SUPABASE_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = DatabaseHelper.client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MeuPerfilPacienteActivity, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MeuPerfilPacienteActivity, "Erro ao atualizar dados (${response.code})", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MeuPerfilPacienteActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
