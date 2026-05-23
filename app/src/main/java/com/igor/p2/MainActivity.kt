package com.igor.p2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNome = findViewById<EditText>(R.id.etNome)
        val btnJogar = findViewById<Button>(R.id.btnJogar)
        val btnHistorico = findViewById<Button>(R.id.btnHistorico)

        btnJogar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite seu nome para continuar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("PLAYER_NAME", nome)
            startActivity(intent)
        }

        btnHistorico.setOnClickListener {
            val nome = etNome.text.toString().trim()
            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite seu nome para continuar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("PLAYER_NAME", nome)
            startActivity(intent)
        }
    }
}