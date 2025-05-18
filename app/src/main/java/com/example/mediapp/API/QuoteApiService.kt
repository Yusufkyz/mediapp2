package com.example.mediapp.API

import retrofit2.Call
import retrofit2.http.GET

interface QuoteApiService {
    @GET("random")
    fun getRandomQuote(): Call<List<Quote>>
}