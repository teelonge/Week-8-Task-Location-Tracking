package com.droid.tolulope.week_8_task.reactivepokemon.network

import com.droid.tolulope.week_8_task.reactivepokemon.model.PokemonDetail
import com.droid.tolulope.week_8_task.reactivepokemon.model.Pokemons
import retrofit2.http.GET
import retrofit2.http.Url
import rx.Observable

/**
 * This interface contains methods that returns observables used to make network calls
 * to the API
 */
interface PokemonEndPoint {

 // Gets initial pokemons limit of 20
 @GET("pokemon?limit=20&offset=0")
 fun getPokemonsList() : Observable<Pokemons>

 // Gets a particular pokemon passed as the url
 @GET
 fun getPokemonDetail(@Url url: String) : Observable<PokemonDetail>

 // Gets a specified limit of pokemons, next or previous pokemons
 @GET
 fun getCustomPokemon(@Url url : String) : Observable<Pokemons>

}