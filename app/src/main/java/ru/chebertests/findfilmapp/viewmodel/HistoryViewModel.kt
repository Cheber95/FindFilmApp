package ru.chebertests.findfilmapp.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.chebertests.findfilmapp.app.App.Companion.getHistoryDao
import ru.chebertests.findfilmapp.extensions.AppState
import ru.chebertests.findfilmapp.model.repository.LocalRepository
import ru.chebertests.findfilmapp.model.repository.LocalRepositoryImpl

class HistoryViewModel(
    private val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val filmLocalRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao())
) : ViewModel() {

    fun getLiveData() = historyLiveData

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        val handler = Handler(Looper.getMainLooper())
        Thread {
            val history = filmLocalRepository.getAllHistory()
            handler.post(Runnable {
                historyLiveData.value = AppState.SuccessOnHistory(history)
            })
        }.start()
    }

    fun deleteAllHistory() {
        historyLiveData.value = AppState.Loading
        val handler = Handler(Looper.getMainLooper())
        Thread {
            filmLocalRepository.clearHistory()
            val history = filmLocalRepository.getAllHistory()
            handler.post(Runnable {
                historyLiveData.value = AppState.SuccessOnHistory(history)
            })
        }.start()
    }

}