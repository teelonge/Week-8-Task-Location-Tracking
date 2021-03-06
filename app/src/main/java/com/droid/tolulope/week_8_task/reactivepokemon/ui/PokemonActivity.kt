package com.droid.tolulope.week_8_task.reactivepokemon.ui

import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_8_task.BASE_URL
import com.droid.tolulope.week_8_task.R
import com.droid.tolulope.week_8_task.reactivepokemon.maptracking.MapsActivity
import com.droid.tolulope.week_8_task.reactivepokemon.model.Pokemons
import com.droid.tolulope.week_8_task.reactivepokemon.model.Result
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
 * This class makes a call to the network to get a particular number of pokemons and populates
 * this list in a recycler view. Also reactiveNetwork library to monitor network disconnection and
 * connection
 */
class PokemonActivity : AppCompatActivity(), RecyclerViewClickListener {
    private lateinit var allPokeRecycler: RecyclerView
    private lateinit var adapter: AllPokemonAdapter
    private lateinit var subscription: Subscription
    private lateinit var progressBar: ProgressBar
    private lateinit var navigation: ImageButton
    private lateinit var networkDisposable: Disposable
    private lateinit var edtLimit: EditText
    private lateinit var limit: String
    private lateinit var setLimit: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var next: String
    private lateinit var prev: String
    private var current: String? = null
    private val pokemons = arrayListOf<Result>()
    private val TAG = "PokemonActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon2)

        // Finds each view present in the layout
        navigation = findViewById(R.id.navigation)
        adapter = AllPokemonAdapter(this)
        progressBar = findViewById(R.id.progressBar)
        allPokeRecycler = findViewById(R.id.pokemonRecyclerView)
        edtLimit = findViewById(R.id.edtLimit)
        setLimit = findViewById(R.id.setLimit)
        btnNext = findViewById(R.id.btnNext)
        btnPrev = findViewById(R.id.btnPrev)

        allPokeRecycler.adapter = adapter
        adapter.listener = this

        // Checks the network state and loads pokemon based on the current state
        checkNetworkState()

        // Navigates to the Maps activity
        navigation.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        // Displays list of pokemons based on user's input
        setLimit.setOnClickListener {
            val number = edtLimit.text.toString().toInt()
            limit = BASE_URL + "pokemon?limit=$number&offset=0"
            if (limit.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                getPokemonAtLimit(limit)
            }
        }

        // Loads previous items based on the previous value received from the API
        btnPrev.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            getPokemonAtLimit(prev)
        }

        // Loads next items based on the next value received from the API
        btnNext.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            getPokemonAtLimit(next)
        }
    }


    /*
      Checks for the current network state, if connected, shows a snackbar, brings up the progressbar
      and on succesful loading, removes the progress bar and displays the pokemons delivered, if network
      is disconnected, a snackbar notifies user that network is disconnected
     */
    private fun checkNetworkState() {
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe { connectivity: Connectivity ->
                Log.d(TAG, connectivity.toString())
                val state = connectivity.state()
                if (state == NetworkInfo.State.CONNECTED) {
                    Snackbar.make(
                        allPokeRecycler,
                        "Connection Status: Network Connected",
                        Snackbar.LENGTH_LONG
                    )
                        .setTextColor(resources.getColor(R.color.green))
                        .show()
                    progressBar.visibility = View.VISIBLE
                    if (current != null) {
                        getPokemonAtLimit(current!!)
                    } else {
                        getAllPokemon()
                    }
                } else if (state == NetworkInfo.State.DISCONNECTED) {
                    Snackbar.make(
                        allPokeRecycler,
                        "Connection Status: Network Disconnected",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setTextColor(resources.getColor(R.color.red))
                        .show()
                    progressBar.visibility = View.GONE
                }
            }

    }

    /*
     Retrieves the list of pokemons
     */
    private fun getAllPokemon() {
        subscription = PokeApiClient.getPokemonEndPointApi().getPokemonsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Pokemons> {
                override fun onCompleted() {
                    progressBar.visibility = View.GONE
                    Log.d(TAG, "Fetching Successful")
                }

                override fun onError(e: Throwable?) {
                    progressBar.visibility = View.GONE
                    e?.printStackTrace()
                    Log.d(TAG, "An error occured No network")

                }

                override fun onNext(t: Pokemons?) {
                    pokemons.clear()
                    pokemons.addAll(t?.results as ArrayList<Result>)
                    adapter.setUpPokemon(pokemons)
                    next = t.next.toString()
                    prev = t.previous.toString()
                }

            })
    }

    override fun onDestroy() {
        if (!subscription.isUnsubscribed || !networkDisposable.isDisposed) {
            subscription.unsubscribe()
            networkDisposable.dispose()
        }
        super.onDestroy()
    }

    // Handles the clicking each pokemon and navigating to the details activity
    override fun onRecyclerViewItemClicked(view: View, pokemonResult: Result) {
        when (view.id) {
            R.id.pokeView -> {
                Toast.makeText(this, "He touched me ${pokemonResult.name}", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, PokemonDetails::class.java)
                intent.putExtra("PokemonUrl", pokemonResult.url)
                startActivity(intent)
            }
        }
    }

    // Retrieves pokemon based on a specified limit
    private fun getPokemonAtLimit(lim: String) {
        current = lim
        subscription = PokeApiClient.getPokemonEndPointApi().getCustomPokemon(lim)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Pokemons> {
                override fun onCompleted() {
                    progressBar.visibility = View.GONE
                    Log.d(TAG, "Fetching Successful")
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    progressBar.visibility = View.GONE
                    Log.d(TAG, "An error occured No network")

                }

                override fun onNext(t: Pokemons?) {
                    pokemons.clear()
                    pokemons.addAll(t?.results as ArrayList<Result>)
                    adapter.setUpPokemon(pokemons)
                    next = t.next.toString()
                    prev = t.previous.toString()
                }

            })
    }
}