package ru.samtakoy.listtest.presentation.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val NO_EMPLOYEE = -1

class SharedEmployeeViewModel : ViewModel() {

    private val readyEmployeeIdLiveData = MutableLiveData<Int>(NO_EMPLOYEE)
    fun onImageReady(id: Int) {
        readyEmployeeIdLiveData.value = id
    }
    fun getImageReadyEmployeeId(): LiveData<Int> = readyEmployeeIdLiveData
    fun resetReady() {
        readyEmployeeIdLiveData.value = NO_EMPLOYEE
    }

    var currentEmployeeId: Int = NO_EMPLOYEE
    fun isCurrentEmployeeSetted() = currentEmployeeId > 0

    val bigPictureLoadingAllowed = MutableLiveData<Boolean>(false)
}