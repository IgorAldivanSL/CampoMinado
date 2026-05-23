package com.igor.p2.model

import com.google.gson.annotations.SerializedName

data class Partida(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nome") val nome: String,
    @SerializedName("pontuacao") val pontuacao: Int,
    @SerializedName("data") val data: String
)
