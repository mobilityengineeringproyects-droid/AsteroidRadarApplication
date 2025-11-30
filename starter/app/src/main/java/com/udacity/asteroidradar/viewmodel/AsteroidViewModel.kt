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
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
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

    //TODO:Remove unnecessary comment
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

    var databaseFiltered = false


    init {
        refreshDailyImage()
        refreshAsteroids()
    }


    fun getAsteroidsFromPeriod(startDate: String, endDate: String) {

        var asteroidsPlaceholder: List<DatabaseAsteroid> = emptyList()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    asteroidsPlaceholder =
                        database.asteroidDao.getAsteroidsFromDate(startDate, endDate)
                    val got = true
                } catch (e: Exception) {
                    print(e.message)
                }
            }
//            Convert from DatabaseAsteroid to Asteroid type used in list of main view
            _databaseAsteroids.value = asteroidsPlaceholder
            _asteroids.value = asteroidsPlaceholder.asDomainModel()
        }
    }

    fun getAsteroids() {
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
            _asteroids.value = asteroidsPlaceholder.asDomainModel()
        }
    }

    fun clear() {
        var asteroidsPlaceholder: List<DatabaseAsteroid> = emptyList()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.asteroidDao.clear()
                try {
                    asteroidsPlaceholder = database.asteroidDao.getAsteroids()
                    val got = true
                } catch (e: Exception) {
                    print(e.message)
                }
            }
                _asteroids.value = asteroidsPlaceholder.asDomainModel()


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
                    viewModelScope.launch(Dispatchers.IO) {


                        database.asteroidDao.insertAll(*(_asteroids.value as ArrayList<Asteroid>).asDatabaseModel())
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
