package com.example.gesttrack.medico

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

class MeuPerfilMedicoActivity: AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton

    private lateinit var etNome: EditText
    private lateinit var etCRM: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btnAtualizar: AppCompatButton

    private var medicoId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_meuperfil)

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
                    startActivity(Intent(this, CalendarioMedicoActivity::class.java))
                    true
                }

                R.id.nav_meuspacientes -> {
                    startActivity(Intent(this, MeusPacientesMedicoActivity::class.java))
                    true
                }

                R.id.nav_voltar -> {
                    startActivity(Intent(this, PrincipalMedicoActivity::class.java))
                    true
                }

                else -> false
            }
        }

        etNome = findViewById(R.id.editNomeMedico)
        etCRM = findViewById(R.id.editCRM)
        etTelefone = findViewById(R.id.editTelefoneMedico)
        etEmail = findViewById(R.id.editEmailMedico)
        etSenha = findViewById(R.id.editSenhaMedico)
        btnAtualizar = findViewById(R.id.btnAtualizarMedico)

        val sharedPref = getSharedPreferences("usuario_prefs", MODE_PRIVATE)
        medicoId = sharedPref.getString("id_medico", "") ?: ""
        if (medicoId.isEmpty()) { // 🔥 isEmpty() ao invés de == 0
            Toast.makeText(this, "Erro: medico não logado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        carregarDadosMedico()

        btnAtualizar.setOnClickListener {
            atualizarDadosMedico()
        }
    }

    private fun carregarDadosMedico() {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseHelper.obterMedicoPorId(medicoId) { medicoJson ->
                runOnUiThread {
                    if (medicoJson != null) {
                        etNome.setText(medicoJson.optString("nome"))
                        etCRM.setText(medicoJson.optString("crm"))
                        etTelefone.setText(medicoJson.optString("telefone"))
                        etEmail.setText(medicoJson.optString("email"))
                        etSenha.setText(medicoJson.optString("senha"))
                    } else {
                        Toast.makeText(
                            this@MeuPerfilMedicoActivity,
                            "Erro ao carregar dados do medico.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun atualizarDadosMedico() {
        val nome = etNome.text.toString().trim()
        val crm = etCRM.text.toString().trim()
        val telefone = etTelefone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = JSONObject().apply {
                    put("nome", nome)
                    put("crm", crm)
                    put("telefone", telefone)
                    put("email", email)
                    put("senha", senha)
                }

                val url =
                    "${DatabaseHelper.SUPABASE_URL}/rest/v1/medicos?id_medico=eq.$medicoId"


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
                        Toast.makeText(
                            this@MeuPerfilMedicoActivity,
                            "Dados atualizados com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MeuPerfilMedicoActivity,
                            "Erro ao atualizar dados (${response.code})",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MeuPerfilMedicoActivity,
                        "Erro: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
