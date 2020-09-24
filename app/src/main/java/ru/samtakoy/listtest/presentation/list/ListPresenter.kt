package ru.samtakoy.listtest.presentation.list

import android.util.Log
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.extensions.CloseableCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import ru.samtakoy.listtest.domain.model.cache.CacheStatus
import javax.inject.Inject


@InjectViewState
class ListPresenter @Inject constructor(
    val cache: CacheModel
) : MvpPresenter<ListView>(){

    private val TAG = "ListPresenter"

    private val presenterScope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {

        Log.e(TAG, "debug")

        viewState.showMessage(R.string.hello)


        cache.init()
        observeUpdates()
    }

    override fun onDestroy() {

        presenterScope.close()
        super.onDestroy()
    }

    private fun observeUpdates() {
        presenterScope.launch {
            cache.getEmployees()
                .flowOn(Dispatchers.IO)
                .collect {onCachedDataUpdated(it)}

            cache.observeCacheStatus()
                .collect{updateLoadingView(it) }
        }
    }

    private fun updateLoadingView(cacheStatus: CacheStatus) {
        if(cacheStatus == CacheStatus.DATA_RETRIEVING){
            viewState.showDataLoading()
        } else{
            viewState.hideDataLoading()
        }
    }

    fun onCachedDataUpdated(employees: List<Employee>){
        Log.d("ListPresenter", "applyDataToView:"+Thread.currentThread().name)
        viewState.setData(employees)
    }

    fun getMoreEmployees() {
        cache.retrieveMoreEmployees()
    }
}