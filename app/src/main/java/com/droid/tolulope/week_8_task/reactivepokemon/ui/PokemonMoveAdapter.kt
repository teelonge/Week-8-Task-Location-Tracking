package com.droid.tolulope.week_8_task.reactivepokemon.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_8_task.R
import com.droid.tolulope.week_8_task.reactivepokemon.model.Move

/**
 * Adapter for binding the move of each pokemon received from the API in the all pokemon fragment
 */
class PokemonMoveAdapter(private val pokemonMoves : MutableList<Move>) : RecyclerView.Adapter<PokemonMoveAdapter.ViewHolder>() {


    inner class ViewHolder(itemView : View) :
        RecyclerView.ViewHolder(itemView) {
        private val txtPokeMove = itemView.findViewById<TextView>(R.id.txtPokeMove)

        fun bind(pokemonMove: String) {
            txtPokeMove.text = pokemonMove
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.poke_move,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pokemonMoves[position].move.name)

    }

    override fun getItemCount(): Int {
        return pokemonMoves.size
    }

}