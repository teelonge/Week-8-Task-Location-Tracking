package com.droid.tolulope.week_8_task.reactivepokemon.model

data class Pokemons(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Result>
)