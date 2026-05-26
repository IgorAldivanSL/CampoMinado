package com.igor.p2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.igor.p2.api.RetrofitClient
import com.igor.p2.model.Partida
import okhttp3.ResponseBody // Importado para corrigir o erro de tipo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val playerName = intent.getStringExtra("PLAYER_NAME") ?: "Jogador"
        val score = intent.getIntExtra("SCORE", 0)
        val elapsed = intent.getIntExtra("ELAPSED", 0)
        val won = intent.getBooleanExtra("WON", false)

        val tvPlayerName = findViewById<TextView>(R.id.tvPlayerName)
        val tvResultTitle = findViewById<TextView>(R.id.tvResultTitle)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val btnPlayAgain = findViewById<Button>(R.id.btnPlayAgain)
        val btnHistorico = findViewById<Button>(R.id.btnHistorico)
        val btnMenu = findViewById<Button>(R.id.btnMenu)

        tvPlayerName.text = "Jogador: $playerName"
        tvResultTitle.text = if (won) "6Você Ganhou!" else "Game Over!"
        tvScore.text = "Pontuação: $score"
        tvTime.text = "Tempo: ${elapsed}s"

        // Save match to API
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val partida = Partida(nome = playerName, pontuacao = score, data = dateStr)
        salvarPartida(partida)

        btnPlayAgain.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("PLAYER_NAME", playerName)
            startActivity(intent)
            finish()
        }

        btnHistorico.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("PLAYER_NAME", playerName)
            startActivity(intent)
        }

        btnMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun salvarPartida(partida: Partida) {
        // Alterado de Callback<Void> para Callback<ResponseBody> para bater com o ApiService
        RetrofitClient.apiService.salvarPartida(partida).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ResultActivity, "Partida salva!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ResultActivity, "Erro do servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ResultActivity, "Sem conexão com servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}