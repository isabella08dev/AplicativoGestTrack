package com.example.gesttrack.medico

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView

class PrincipalMedicoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: AppCompatImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medico_principal)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view_medico)
        btnMenu = findViewById(R.id.btnMenuMedico)


        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Listener do menu
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> startActivity(
                    Intent(
                        this,
                        MeuPerfilMedicoActivity::class.java
                    )
                )

                R.id.nav_calendario -> startActivity(
                    Intent(
                        this,
                        CalendarioMedicoActivity::class.java
                    )
                )

                R.id.nav_meuspacientes-> startActivity(Intent(this, MeusPacientesMedicoActivity::class.java))
                R.id.nav_voltar -> startActivity(
                    Intent(
                        this,
                        PrincipalMedicoActivity::class.java
                    )
                )
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}

