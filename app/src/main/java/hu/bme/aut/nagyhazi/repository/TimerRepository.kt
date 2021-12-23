package hu.bme.aut.nagyhazi.repository

import androidx.lifecycle.LiveData
import hu.bme.aut.nagyhazi.data.Timer
import hu.bme.aut.nagyhazi.data.TimerDao


class TimerRepository(private val userDao: TimerDao) {

    val readAllData: LiveData<List<Timer>> = userDao.readAllData()

    suspend fun addTimer(timer: Timer){
        userDao.addTimer(timer)
    }

    suspend fun updateTimer(timer: Timer){
        userDao.updateTimer(timer)
    }

    suspend fun deleteTimer(timer: Timer){
        userDao.deleteTimer(timer)
    }

    suspend fun deleteAllTimers(){
        userDao.deleteAllTimers()
    }

}