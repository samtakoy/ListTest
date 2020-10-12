package ru.samtakoy.listtest.presentation.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedEmployeeViewModel : ViewModel() {
    private val readyEmployeeIdLiveData = MutableLiveData<Int>()
    fun onImageReady(id: Int) {
        readyEmployeeIdLiveData.value = id
    }
    fun getImageReadyEmployeeId(): LiveData<Int> = readyEmployeeIdLiveData
    fun resetReady() {
        readyEmployeeIdLiveData.value = -1
    }

    val readyEmployeeId: Int?
        get() = readyEmployeeIdLiveData.value

    var currentEmployeeId: Int = -1
}