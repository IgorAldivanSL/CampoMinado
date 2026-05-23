package com.igor.p2

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.igor.p2.adapter.HistoryAdapter
import com.igor.p2.api.RetrofitClient
import com.igor.p2.model.Partida
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val playerName = intent.getStringExtra("PLAYER_NAME") ?: "Jogador"

        val tvPlayerName = findViewById<TextView>(R.id.tvPlayerName)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        tvPlayerName.text = "Jogador: $playerName"
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch history from API
        RetrofitClient.apiService.listarPartidas().enqueue(object : Callback<List<Partida>> {
            override fun onResponse(call: Call<List<Partida>>, response: Response<List<Partida>>) {
                val lista = response.body() ?: emptyList()
                if (lista.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.adapter = HistoryAdapter(lista)
                }
            }
            override fun onFailure(call: Call<List<Partida>>, t: Throwable) {
                Toast.makeText(this@HistoryActivity, "Erro ao carregar histórico", Toast.LENGTH_SHORT).show()
                tvEmpty.visibility = View.VISIBLE
            }
        })
    }
}
