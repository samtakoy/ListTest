package ru.samtakoy.listtest.domain.model.cache

import android.util.Log
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository
import ru.samtakoy.listtest.extensions.CloseableCoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.model.dto.EmployeePack
import javax.inject.Inject

class CacheModelImpl @Inject constructor(

    var cacheRepository: EmployeeCacheRepository,
    var remoteRepository: RemoteEmployeeRepository,

) : CacheModel {

    private val TAG = "CacheModelImpl"

    private val CACHE_PAGE_SIZE = 10
    private var cachedPageCount = -1

    private var cacheStatus: CacheStatus = CacheStatus.NOT_INITIALIZED
    private var cacheStatusChannel = ConflatedBroadcastChannel<CacheStatus>()

    private val modelScope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun init() {
        if(cacheStatus == CacheStatus.NOT_INITIALIZED){
            modelScope.launch {
                cacheStatusChannel.send(cacheStatus)
            }
            retrieveInitialData()
        }
    }

    override fun observeCacheStatus(): Flow<CacheStatus> = cacheStatusChannel.asFlow()

    private fun retrieveInitialData() {
        modelScope.launch {
            cacheRepository.getEmployeesCount()
                .flowOn(Dispatchers.IO)
                .collect {onInitialData(it)}
        }
    }

    private suspend fun changeCacheStatus(newStatus: CacheStatus){
        cacheStatus = newStatus
        cacheStatusChannel.send(newStatus)
    }

    private suspend fun onInitialData(cacheSize: Int){

        if(cacheSize == 0){
            changeCacheStatus(CacheStatus.UNCOMPLETED)
            cachedPageCount = 0
            retrieveMoreEmployees()
        } else {
            // подсчитать количество уже загруженных в кеш страниц
            cachedPageCount = Math.round(cacheSize.toDouble() / CACHE_PAGE_SIZE).toInt()
            changeCacheStatus(CacheStatus.UNCOMPLETED)
        }
    }

    override fun getEmployees(): Flow<List<Employee>> = cacheRepository.getEmployees()

    override fun retrieveMoreEmployees() {

        if(cacheStatus != CacheStatus.UNCOMPLETED){
            return
        }

        modelScope.launch {

            changeCacheStatus(CacheStatus.DATA_RETRIEVING)

            var resultPack: EmployeePack? = null

            withContext(Dispatchers.IO) {
                resultPack = remoteRepository.retrieveMoreEmployees(cachedPageCount + 1)
            }

            if(resultPack != null) {
                onRetrieveEmployeesComplete(resultPack!!)
                changeCacheStatus(if(resultPack!!.isEmpty()) CacheStatus.SYNCHRONIZED else CacheStatus.UNCOMPLETED)
            } else {
                onRetrieveEmployeesError()
                changeCacheStatus(CacheStatus.UNCOMPLETED)
            }
        }
    }

    private suspend fun onRetrieveEmployeesComplete(resultPack: EmployeePack) {
        if(!resultPack.isEmpty()){
            cachedPageCount++;
            withContext(Dispatchers.IO) {
                cacheRepository.addData(resultPack.employees)
            }
        }
    }

    private fun onRetrieveEmployeesError() {
        Log.e(TAG, "onRetrieveEmployeesError")
    }
}