package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {


    @GET("neo/rest/v1/feed")
    fun getNearEarthAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Deferred<List<Asteroid>>


    /*
   @GET("neo/rest/v1/feed")
   fun getNearEarthAsteroidsAsync(
       @Query("start_date") startDate: String,
       @Query("end_date") endDate: String,
       @Query("api_key") apiKey: String
   ): //Deferred<NetworkAsteroidContainer>
           //Deferred<List<Asteroid>>
           //Deferred<JSONObject>
           Deferred<String>

     */
    @GET("neo/rest/v1/feed")
    fun getNearEarthAsteroidsAsync(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): Deferred<String> //Deferred<NetworkAsteroidContainer>
    //Deferred<List<Asteroid>>
    //Deferred<JSONObject>

/*
    @GET("watch?v=c-NFv0HFVD4")
    fun youtube(): Deferred<Unit>

 */

    @GET("planetary/apod")
    fun getDailyImage(
        @Query("api_key") apiKey: String
    ): Deferred<AsteroidMedia>

}

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Main entry point for network access. Call like `Network.devbytes.getPlaylist()`
 */
object Network {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroid: AsteroidService by lazy { retrofit.create(AsteroidService::class.java) }
}
