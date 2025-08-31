package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import android.util.Log.DEBUG
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.util.Utils.getFormattedDate
import com.udacity.asteroidradar.viewmodel.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */

    //It takes the advantage of allowing an initial filtering shall no network is available when database is updated the DatabaseAsteroid list it points to will
    //update as well
    val today = getFormattedDate("yyyy-MM-dd", Date())
    var asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsFromDateLive(today, today)) {
            //Transformations.map(database.asteroidDao.getAsteroids()) {
            it?.let {
                it.asDomainModel()
            }
        }
    var testAsteroids = MutableLiveData<List<Asteroid>>()
    // Applies the given function on the main thread to each value emitted by source LiveData and
    // returns LiveData, which emits resulting values.
    //WHAT -> { HOW }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [videos]
     */
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun refreshAsteroids() {
        var jsonObject: JSONObject
        var playlist: List<Asteroid>?

        withContext(Dispatchers.IO)
        {
            val pattern = "yyyy-MM-dd"
            val simpleDateFormat = SimpleDateFormat(pattern)
            val date: String = simpleDateFormat.format(Date())

            try {
                //_progress.value = Progress.LOADING
                val asteroids =
                    parseAsteroidsJsonResult(
                        JSONObject(
                            Network.asteroid.getNearEarthAsteroidsAsync(
                                startDate = date,
                                endDate = date,
                                apiKey = "W7dsDgE9T2VAzCqHMhf2pzcbba6wkbsqffOptjgj"
                            ).await()
                        )
                    )
                database.asteroidDao.insertAll(*(asteroids).asDatabaseModel())

                // On a basic level, Deferred is a future. It makes it possible for one coroutine to wait
                // for the result produced by another one, suspending itself until it's ready.
                //jsonObject =
                /*
            testAsteroids.value =
                parseAsteroidsJsonResult(JSONObject(
                Network.asteroid.getNearEarthAsteroidsAsync(
                    startDate = "2023-04-05",
                    endDate = "2023-04-05",
                    apiKey = "W7dsDgE9T2VAzCqHMhf2pzcbba6wkbsqffOptjgj"
                ).await()))

             */

            }catch(e:Exception){
                Timber.e(e.message)
            }
        }
        // Uncomment THIS and the comment above
        // playlist = parseAsteroidsJsonResult(jsonObject)

        // To make such a function "behave as a suspending function", the blocking has to be dispatched
        // onto another worker thread, which (by recommendation) should happen with withContext:

        /*
        withContext(Dispatchers.Default) {
            updateAsteroidList(playlist as ArrayList<Asteroid>)
        }

         */
        /*
            database.asteroidDao.insert((playlist as ArrayList).asDatabaseModel().get(0))
            val databaseItemsLiveData = (database.asteroidDao.getAsteroids()).value
            val check = true
            (asteroids.value?.get(0))

            try {
                database.asteroidDao.insertAll(
                    listOf(
                        Asteroid(
                            123L,
                            "",
                            "",
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            false
                        )
                    ).asDatabaseModel().get(0)
                )

                    //val newPlaylist = playlist as ArrayList<Asteroid>

                database.asteroidDao.insertAll(*((playlist as ArrayList<Asteroid>).asDatabaseModel()))

                print(databaseItemsLiveData.value?.size)
                print(asteroids.value.toString())
            } catch (e: Exception) {
                val message = e.message
                print(message)

             */

    }



    fun updateAsteroidList(asteroidList: ArrayList<Asteroid>) {
        testAsteroids.value = asteroidList
    }


}