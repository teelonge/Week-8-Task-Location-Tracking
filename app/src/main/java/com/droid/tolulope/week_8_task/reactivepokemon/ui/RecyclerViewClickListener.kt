package com.droid.tolulope.week_8_task.reactivepokemon.ui

import android.view.View
import com.droid.tolulope.week_8_task.reactivepokemon.model.Result


interface RecyclerViewClickListener {
    fun onRecyclerViewItemClicked(view : View, pokemonResult: Result)
}