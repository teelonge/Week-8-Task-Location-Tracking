package com.droid.tolulope.week_8_task.reactivepokemon.model
data class PokemonDetail(
    val id: Int,
    val abilities: List<Ability>,
    val base_experience: Int,
    val height: Int,
    val held_items: List<Any>,
    val is_default: Boolean,
    val location_area_encounters: String,
    val moves: List<Move>,
    val name: String,
    val order: Int,
    val sprites: Sprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val weight: Int
)

data class Ability(
    val ability: AbilityX,
    val is_hidden: Boolean,
    val slot: Int
)


data class Move(
    val move: MoveX
)

data class Sprites(
    val back_default: String?,
    val back_female: Any?,
    val back_shiny: String?,
    val back_shiny_female: Any?,
    val front_default: String?,
    val front_female: Any?,
    val front_shiny: String?,
    val front_shiny_female: Any?,
)

data class Stat(
    val base_stat: Int,
    val effort: Int,
    val stat: StatX
)

data class Type(
    val slot: Int,
    val type: TypeX
)

data class AbilityX(
    val name: String,
    val url: String
)


data class MoveX(
    val name: String,
    val url: String
)

data class StatX(
    val name: String,
    val url: String
)

data class TypeX(
    val name: String,
    val url: String
)
