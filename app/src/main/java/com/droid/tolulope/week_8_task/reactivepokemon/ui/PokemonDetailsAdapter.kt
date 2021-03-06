package com.droid.tolulope.week_8_task.reactivepokemon.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.droid.tolulope.week_8_task.R

/**
 * Adapter for binding the views of each pokemon received from the API in the all pokemon fragment
 */
class PokemonDetailsAdapter(private val context : Context, private val pokemonSprites : MutableList<String>) : RecyclerView.Adapter<PokemonDetailsAdapter.ViewHolder>() {


    inner class ViewHolder(itemView : View) :
        RecyclerView.ViewHolder(itemView) {
        private val txtPokeName = itemView.findViewById<TextView>(R.id.txtPokemonName)
        private val txtPokeId = itemView.findViewById<TextView>(R.id.pokemonID)
        private val imageDetail = itemView.findViewById<ImageView>(R.id.imgDetail)
        private val pokeView = itemView.findViewById<CardView>(R.id.pokeView)

        fun bind(pokemonSprite: String){
            Log.d("CHecking","${pokemonSprites[0]}")

            val imageUrl = pokemonSprite
            val imageUri = imageUrl.toUri().buildUpon()?.scheme("https")?.build()
            Glide.with(context)
                .load(imageUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_status_animation)
                        .error(R.drawable.ic_error_image)
                )
                .into(imageDetail)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_detail,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pokemonSprites[position])

    }

    override fun getItemCount(): Int {
        return pokemonSprites.size
    }

}