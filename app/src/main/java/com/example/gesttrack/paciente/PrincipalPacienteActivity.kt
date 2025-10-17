package com.example.gesttrack.paciente

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.gesttrack.R
import com.google.android.material.navigation.NavigationView

class PrincipalPacienteActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnMenu: AppCompatImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_principal)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btnMenu)


        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_perfil -> startActivity(
                    Intent(
                        this,
                        MeuPerfilPacienteActivity::class.java
                    )
                )

                R.id.nav_calendario -> startActivity(
                    Intent(
                        this,
                        CalendarioPacienteActivity::class.java
                    )
                )

                R.id.nav_notas -> startActivity(
                    Intent(
                        this,
                        MinhasNotasPacienteActivity::class.java
                    )
                )

                R.id.nav_chat -> startActivity(Intent(this, ChatPacienteActivity::class.java))
                R.id.nav_voltar -> startActivity(
                    Intent(
                        this,
                        PrincipalPacienteActivity::class.java
                    )
                )
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
