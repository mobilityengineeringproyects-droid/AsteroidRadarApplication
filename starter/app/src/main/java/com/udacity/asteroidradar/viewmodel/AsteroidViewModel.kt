/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.udacity.asteroidradar.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.network.AsteroidMedia
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * DevByteViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
enum class Progress {
    LOADING, FINISH, ERROR
}

@RequiresApi(Build.VERSION_CODES.N)
class AsteroidViewModel(application: Application) : AndroidViewModel(application) {

    val imageUrl =
        "https://api.nasa.gov/planetary/apod?api_key=W7dsDgE9T2VAzCqHMhf2pzcbba6wkbsqffOptjgj"
    private val database = AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    //Indicates the state of the invoke for each of the different network calls
    private val _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress>
        get() = _progress
    //Updates its value from call to NASA Api for retrieving Asteroids
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids
    //Property to permit navigation to DetailView
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedAsteroid: MutableLiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid
    //Retrieves the results from applying a filter to the Database
    private val _databaseAsteroids = MutableLiveData<List<DatabaseAsteroid>>()
    val databaseAsteroids: LiveData<List<DatabaseAsteroid>>
        get() = _databaseAsteroids
    //Retrieves the media object from NASA Api
    private val _asteroidMedia = MutableLiveData<AsteroidMedia>()
    val asteroidMedia: LiveData<AsteroidMedia>
        get() = _asteroidMedia


    //Test property to validate use of repository currently cannot be initialized when used with no server calls
    val transformedDatabaseAsteroids = asteroidRepository.asteroids
    /*LiveData<List<Asteroid>> =
Transformations.map(database.asteroidDao.getAsteroids()) {
    it.asDomainModel()
}

     */

    var databaseFiltered = false


    init {
        //if (verifyAvailableNetwork())
        refreshDailyImage()
        refreshAsteroids()
        //getAsteroidsFromDates("2023-04-11", "2023-04-11")
        //clear()
    }


    fun getAsteroidsFromPeriod(startDate: String, endDate: String) {

        var asteroidsPlaceholder: List<DatabaseAsteroid> = emptyList()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    asteroidsPlaceholder = database.asteroidDao.getAsteroidsFromDate(startDate, endDate)
                    val got = true
                } catch (e: Exception) {
                    print(e.message)
                }
            }
            _databaseAsteroids.value = asteroidsPlaceholder
        }
    }
    fun getAsteroids(){
        var asteroidsPlaceholder: List<DatabaseAsteroid> = emptyList()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    asteroidsPlaceholder = database.asteroidDao.getAsteroids()
                    val got = true
                } catch (e: Exception) {
                    print(e.message)
                }
            }
            _databaseAsteroids.value = asteroidsPlaceholder
        }
    }
    fun clear() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.asteroidDao.clear()
            }

        }
    }

    private fun refreshDailyImage() {
        viewModelScope.launch {
            try {
                _asteroidMedia.value =
                    Network.asteroid.getDailyImage(apiKey = "W7dsDgE9T2VAzCqHMhf2pzcbba6wkbsqffOptjgj")
                        .await()
            } catch (e: Exception) {
                Timber.d(e.message)
            }

        }
    }

    private fun refreshAsteroids() {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date: String = simpleDateFormat.format(Date())

        viewModelScope.launch {
            try {
                _progress.value = Progress.LOADING
                _asteroids.value =
                    parseAsteroidsJsonResult(
                        JSONObject(
                            Network.asteroid.getNearEarthAsteroidsAsync(
                                startDate = date,
                                endDate = date,
                                apiKey = "W7dsDgE9T2VAzCqHMhf2pzcbba6wkbsqffOptjgj"
                            ).await()
                        )
                    )
                _progress.value = Progress.FINISH

                var asteroid = Asteroid(1L, "", "2023-04-14", 0.0, 0.0, 0.0, 0.0, false)

                try {
                    //https://stackoverflow.com/questions/63166046/why-do-i-get-cannot-access-database-on-the-main-thread-since-it-may-potentially
                    /*
                    Option A uses viewModelScope.launch. The default dispatcher for viewModelScope is Dispatchers.Main.immediate as per the documentation.
                    As add isn't a suspending method, it runs on that dispatcher directly - i.e., it runs on the main thread.

                    Option B uses viewModelScope.launch(Dispatchers.IO) which means the code runs on the IO dispatcher.
                    As this isn't the main thread, it succeeds.

                    Option C makes add a suspending function. As per the Async queries with Kotlin coroutines guide,
                    this automatically moves the database access off of the main thread for you, no matter what dispatcher you are using.
                    Option C is always the right technique to use when using Room + Coroutines
                     */

                    viewModelScope.launch(Dispatchers.IO) {

                        /*
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        asteroid = Asteroid(2L, "", "2023-04-13", 0.0, 0.0, 0.0, 0.0, false)
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        asteroid = Asteroid(3L, "", "2023-04-12", 0.0, 0.0, 0.0, 0.0, false)
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        asteroid = Asteroid(4L, "", "2023-04-11", 0.0, 0.0, 0.0, 0.0, false)
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        asteroid = Asteroid(5L, "", "2023-04-10", 0.0, 0.0, 0.0, 0.0, false)
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        asteroid = Asteroid(6L, "", "2023-04-09", 0.0, 0.0, 0.0, 0.0, false)
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        asteroid = Asteroid(7L, "", "2023-04-08", 0.0, 0.0, 0.0, 0.0, false)
                        database.asteroidDao.insert(listOf(asteroid).asDatabaseModel()[0])
                        //_databaseAsteroids.value = database.asteroidDao.getAsteroids().

                         */

                        database.asteroidDao.insertAll(*(_asteroids.value as ArrayList<Asteroid>).asDatabaseModel())

                        /*val testAsteroids = database.asteroidDao.getAsteroidsMod()
                        for (entity in testAsteroids){
                            print(entity.id)
                        }

                         */
                        /*
                        if (database.asteroidDao.getAsteroids().value != null)
                            for (entity in database.asteroidDao.getAsteroids().value!!) {
                                Timber.d(entity.codename.toString())
                                val itArrived = 0
                            }
       //                 database.asteroidDao.insertAll(*testAsteroids.toTypedArray())

                         */
                        //clear()
                    }
                } catch (e: Exception) {
                    Timber.e(e.message)
                }
                val gol = 0
            } catch (e: Exception) {
                _progress.value = Progress.ERROR
                val message = e.message
                _asteroids.value = ArrayList()
            }
        }

    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }



    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AsteroidViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AsteroidViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
