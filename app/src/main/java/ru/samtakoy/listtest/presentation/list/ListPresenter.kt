package ru.samtakoy.listtest.presentation.list

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import ru.samtakoy.listtest.utils.extensions.CloseableCoroutineScope
import javax.inject.Inject


@InjectViewState
class ListPresenter @Inject constructor(
    val cache: CacheModel
) : MvpPresenter<ListView>(){

    private val TAG = "ListPresenter"

    private val presenterScope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        cache.checkForInitialization()
        observeUpdates()
    }

    override fun onDestroy() {

        presenterScope.close()
        super.onDestroy()
    }

    private fun observeUpdates() {

        presenterScope.launch {
            cache.observeNetworkBusyStatus()
                .collect{
                    updateLoadingView(it)
                }
        }
        presenterScope.launch {
            cache.observeEmployees()
                .collect {onCachedDataUpdated(it)}
        }
        presenterScope.launch {
            cache.observeErrors()
                //.consumeEach {
                .collect{
                    viewState.showMessage(it.errorTextId)
                }
        }
    }

    private fun updateLoadingView(isLoading: Boolean) {
        if(isLoading){
            viewState.showDataLoading()
        } else{
            viewState.hideDataLoading()
        }
    }

    private fun onCachedDataUpdated(employees: List<Employee>){
        Log.d("ListPresenter", "applyDataToView:"+Thread.currentThread().name)
        viewState.setData(employees)
    }

    fun onUiGetMoreEmployees() {
        cache.retrieveMoreEmployees()
    }

    fun onUiSettingsClick() {
        viewState.navigateToSettings()
    }

    fun onUiCheckCacheStatus() {
        cache.checkForInitialization()
    }

    fun onUiEmployeeClick(employeeId: Int) {
        viewState.navigateToEmployeeDetails(employeeId)
    }


}