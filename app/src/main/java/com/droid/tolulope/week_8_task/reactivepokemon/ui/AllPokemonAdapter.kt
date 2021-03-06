package com.droid.tolulope.week_8_task.reactivepokemon.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.droid.tolulope.week_8_task.R
import com.droid.tolulope.week_8_task.reactivepokemon.model.Result


/**
 * Adapter for binding the views of each pokemon received from the API in the all pokemon fragment
 */
class AllPokemonAdapter(private val context: Context) :
    RecyclerView.Adapter<AllPokemonAdapter.ViewHolder>() {
    private val allpokemons = arrayListOf<Result>()

    var listener: RecyclerViewClickListener? = null

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Gets each view in the recycler view item
        private val txtPokeName = itemView.findViewById<TextView>(R.id.txtPokemonName)
        private val txtPokeId = itemView.findViewById<TextView>(R.id.pokemonID)
        private val pokeImage = itemView.findViewById<ImageView>(R.id.imgPokemonImage)
        private val pokeView = itemView.findViewById<CardView>(R.id.pokeView)

        // binds the views to their respective values based on the Result received
        fun bind(pokemon: Result) {
            val imageUrl =
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${
                    getPokemonId(pokemon.url)
                }.png"
            txtPokeName.text = pokemon.name

            txtPokeId.text = context.getString(R.string.poke_id, getPokemonId(pokemon.url))
            Glide.with(context)
                .load(imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_status_animation)
                        .error(R.drawable.ic_error_image)
                )
                .into(pokeImage)
            pokeView.setOnClickListener {
                listener?.onRecyclerViewItemClicked(it, pokemon)

            }
        }

    }

    // Creates each view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allpokemons[position])

    }

    override fun getItemCount(): Int {
        return allpokemons.size
    }

    // Adds pokemons coming from the pokemon activity and uses the allpokemons to perform actions in the adapter
    fun setUpPokemon(allPoke: ArrayList<Result>?) {
        allPoke?.let {
            allpokemons.clear()
            allpokemons.addAll(it)
            notifyDataSetChanged()
        }
    }

    // An operation to get each pokemon id
    private fun getPokemonId(url: String): Int {
        return url.split("pokemon/")[1].split("/")[0].toInt()
    }
}