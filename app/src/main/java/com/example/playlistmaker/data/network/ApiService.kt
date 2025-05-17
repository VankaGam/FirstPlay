package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<ApiResponse>
}