package com.droid.tolulope.week_8_task.reactivepokemon.ui

import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.droid.tolulope.week_8_task.R
import com.droid.tolulope.week_8_task.reactivepokemon.model.*
import com.droid.tolulope.week_8_task.reactivepokemon.network.PokeApiClient
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * This class handles the details of each pokemon clicked from the all pokemon details
 * Makes a call to the network to get the details of the pokemon using observables from RxJava
 */
class PokemonDetails : AppCompatActivity() {
    private lateinit var subscription: Subscription
    private lateinit var url: String
    private lateinit var imgPokemonDetail: ImageView
    private lateinit var spritesRecyclerView: RecyclerView
    private lateinit var txtAbility1 : TextView
    private lateinit var txtAbility2 : TextView
    private lateinit var txtAbility3 : TextView
    private lateinit var moveRecyclerView: RecyclerView
    private lateinit var networkDisposable: Disposable

    val pokemonSprites = mutableListOf<String>()
    val pokemonMoves = mutableListOf<Move>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        txtAbility1 = findViewById(R.id.txtability1)
        txtAbility2 = findViewById(R.id.txtAbility2)
        txtAbility3 = findViewById(R.id.txtAbility3)

        moveRecyclerView = findViewById(R.id.movesRecycler)

        imgPokemonDetail = findViewById(R.id.imgPokeDetail)
        spritesRecyclerView = findViewById(R.id.spritesRecycler)
        url = intent.getStringExtra("PokemonUrl").toString()
       checkNetworkState()

    }

    /*
      Checks for the current network state, if connected, shows a snackbar showing network
      connected displays the pokemons delivered, if network
      is disconnected, a snackbar notifies user that network is disconnected
     */
    private fun checkNetworkState() {
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe { connectivity: Connectivity ->
                val state = connectivity.state()
                if (state == NetworkInfo.State.CONNECTED) {
                    Snackbar.make(
                        moveRecyclerView,
                        "Connection Status: Network Connected",
                        Snackbar.LENGTH_LONG
                    )
                        .setTextColor(resources.getColor(R.color.green))
                        .show()
                 getPokemonDetails()
                } else if (state == NetworkInfo.State.DISCONNECTED) {
                    Snackbar.make(
                        moveRecyclerView,
                        "Connection Status: Network Disconnected",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setTextColor(resources.getColor(R.color.red))
                        .show()
                }
            }

    }


    /**
     * Makes a call using the url coming from the all pokemons activity and
     * uses it to grab the pokemon detail from the API
     */
    private fun getPokemonDetails() {
        subscription = PokeApiClient.getPokemonEndPointApi().getPokemonDetail(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<PokemonDetail> {
                override fun onCompleted() {

                    spritesRecyclerView.adapter = PokemonDetailsAdapter(this@PokemonDetails,pokemonSprites)
                    moveRecyclerView.adapter = PokemonMoveAdapter(pokemonMoves)
                }

                override fun onError(e: Throwable?) {
                    Log.d("PokemonDetails","${e?.message}")
                }

                override fun onNext(t: PokemonDetail?) {
                    bindDetails(t)
                }

            })
    }

    // Binds the details of each view with the received value from the observable
    private fun bindDetails(t : PokemonDetail?){
        if (t?.sprites?.front_default != null){
            pokemonSprites.add(t.sprites.front_default)
        }
        if (t?.sprites?.back_default != null){
            pokemonSprites.add(t.sprites.back_default)
        }
        if (t?.sprites?.back_shiny != null){
            pokemonSprites.add(t.sprites.back_shiny)
        }
        if (t?.sprites?.back_shiny_female != null){
            pokemonSprites.add(t.sprites.back_shiny_female.toString())
        }
        if (t?.sprites?.front_shiny_female != null){
            pokemonSprites.add(t.sprites.front_shiny_female.toString())
        }
        if (t?.sprites?.front_female != null){
            pokemonSprites.add(t.sprites.front_female.toString())
        }
        if (t?.sprites?.back_female != null){
            pokemonSprites.add(t.sprites.back_female.toString())
        }
        if (t?.sprites?.front_shiny != null){
            pokemonSprites.add(t.sprites.front_shiny)
        }


        if (t != null){
            txtAbility1.text = t.abilities[0].ability.name
            if (t.abilities.size > 1){
                txtAbility2.text = t.abilities[1].ability.name
            }
            if (t.abilities.size > 2) {
                txtAbility3.text = t.abilities[2].ability.name
            }
            pokemonMoves.addAll(t.moves)

        }


        val imageUrl = t?.sprites?.front_default
        Glide.with(this@PokemonDetails)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_status_animation)
                    .error(R.drawable.ic_error_image)
            )
            .into(imgPokemonDetail)
    }
}