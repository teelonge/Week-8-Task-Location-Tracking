package com.droid.tolulope.week_8_task.reactivepokemon.network

import com.droid.tolulope.week_8_task.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit builder that makes a network call through the Interface, adds a logging
 * interceptor, a gson converter factory, and RxJava call adapter factory
 */
object PokeApiClient {

    fun getPokemonEndPointApi() : PokemonEndPoint{
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(PokemonEndPoint::class.java)

    }


}