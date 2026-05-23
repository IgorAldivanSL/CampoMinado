package com.igor.p2.api

import com.igor.p2.model.Partida
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("partidas.php")
    fun listarPartidas(): Call<List<Partida>>

    @POST("partidas.php")
    fun salvarPartida(@Body partida: Partida): Call<ResponseBody>
}
