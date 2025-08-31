package com.udacity.asteroidradar.database

import android.content.Context
import android.os.Parcelable
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid
import retrofit2.http.GET
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/*
data class Asteroid(val id: Long, val codename: String, val closeApproachDate: String,
                    val absoluteMagnitude: Double, val estimatedDiameter: Double,
                    val relativeVelocity: Double, val distanceFromEarth: Double,
                    val isPotentiallyHazardous: Boolean) : Parcelable
                    */

@Entity(tableName = "asteroid_table")
data class DatabaseAsteroid constructor(


    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "codename")
    val codename: String,
    @ColumnInfo(name = "close_approach_date")
    val closeApproachDate: String,
    @ColumnInfo(name = "absolute_magnitude")
    val absoluteMagnitude: Double,
    @ColumnInfo(name = "estimated_diameter")
    val estimatedDiameter: Double,
    @ColumnInfo(name = "relative_velocity")
    val relativeVelocity: Double,
    @ColumnInfo(name = "distance_from_earth")
    val distanceFromEarth: Double,
    @ColumnInfo(name = "potentially_hazardous")
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

@Dao
interface AsteroidDao {
    @Query("SELECT * from asteroid_table")
    fun getAsteroids(): List<DatabaseAsteroid>

    //As mentioned in the comments, remove suspend. When a method returns an observable, there is no reason to make it suspend
    // since it just returns and object, does not run any query until it is observed.
    @Query("SELECT * from asteroid_table")
    fun getAsteroidsMod(): List<DatabaseAsteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asteroid: DatabaseAsteroid)

    @Query("DELETE FROM asteroid_table")
    fun clear()


    @Query("SELECT * FROM asteroid_table WHERE date(close_approach_date) >= date(:startDate) AND date(close_approach_date) <= date(:endDate)")
    fun getAsteroidsFromDate(startDate: String, endDate: String): List<DatabaseAsteroid>

    @Query("SELECT * FROM asteroid_table WHERE date(close_approach_date) >= date(:startDate) AND date(close_approach_date) <= date(:endDate)")
    //fun getAsteroidsFromDates(startDate:String, endDate:String): List<DatabaseAsteroid>
    fun getAsteroidsFromDateLive(
        startDate: String,
        endDate: String
    ): LiveData<List<DatabaseAsteroid>>
}

@Database(entities = [DatabaseAsteroid::class], version = 4)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao

    companion object {
        /* The value of a volatile variable will never be cached, and all writes and
        *  reads will be done to and from the main memory. It means that changes made by one
        *  thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE: AsteroidDatabase? = null

        fun getDatabase(context: Context): AsteroidDatabase {

            /* Multiple threads can ask for the database at the same time, ensure we only initialize
         * it once by using synchronized. Only one thread may enter a synchronized block at a
         * time.
         */
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidDatabase::class.java,
                        "database_asteroids"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}