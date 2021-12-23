package hu.bme.aut.nagyhazi.data

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTimer(user: Timer): Unit

    @Update
    fun updateTimer(user: Timer): Unit

    @Delete
    fun deleteTimer(user: Timer): Unit

    @Query("DELETE FROM timer_table")
    fun deleteAllTimers(): Unit

    @Query("SELECT * FROM timer_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Timer>>

}