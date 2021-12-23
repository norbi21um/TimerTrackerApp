package hu.bme.aut.nagyhazi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import hu.bme.aut.nagyhazi.data.Timer
import hu.bme.aut.nagyhazi.data.TimerDatabase
import hu.bme.aut.nagyhazi.repository.TimerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TimerViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Timer>>
    private val repository: TimerRepository

    init {
        val userDao = TimerDatabase.getDatabase(
            application
        ).timerDao()
        repository = TimerRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addTimer(timer: Timer){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTimer(timer)
        }
    }

    fun updateTimer(timer: Timer){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTimer(timer)
        }
    }

    fun deleteTimer(timer: Timer){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTimer(timer)
        }
    }

    fun deleteAllTimers(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTimers()
        }
    }
}