package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView

class MeuPerfilPacienteActivity : AppCompatActivity() {

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_meuperfil)

        // ===== Drawer =====
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
                R.id.nav_perfil -> {
                    // Já estamos aqui
                    true
                }
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
    }

    override fun onBackPressed() {
        if (this::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}