package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidsRepository (private val database: AsteroidsDatabase) {

        /**
         * A playlist of videos that can be shown on the screen.
         */
        val asteroids: LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids()) {
                it.asDomainModel()
            }

        /**
         * Refresh the videos stored in the offline cache.
         *
         * This function uses the IO dispatcher to ensure the database insert database operation
         * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
         * function is now safe to call from any thread including the Main thread.
         *
         * To actually load the videos for use, observe [videos]
         */
        suspend fun refreshVideos() {
            withContext(Dispatchers.IO) {
                val playlist = Network.devbytes.getPlaylist().await()
                database.asteroidDao.insertAll(*playlist.asDatabaseModel())
            }
        }
    }
