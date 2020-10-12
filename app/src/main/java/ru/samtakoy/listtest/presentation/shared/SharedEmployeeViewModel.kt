package ru.samtakoy.listtest.presentation.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedEmployeeViewModel : ViewModel() {
    private val employeeIdLiveData = MutableLiveData<Int>()
    fun onImageReady(id: Int) {
        employeeIdLiveData.value = id
    }
    fun getImageReadyEmployeeId(): LiveData<Int> = employeeIdLiveData
    val readyEmployeeId: Int?
        get() = employeeIdLiveData.value

    var currentEmployeeId: Int = -1
}