package ru.samtakoy.listtest.domain.model.cache

import android.util.Log
import kotlinx.coroutines.*
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.domain.Locals
import ru.samtakoy.listtest.domain.TimestampHolder
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.model.dto.EmployeePack
import ru.samtakoy.listtest.utils.extensions.CloseableCoroutineScope
import javax.inject.Inject

private const val TAG = "CacheModelImpl"

class CacheModelImpl @Inject constructor(

    cacheSettings: CacheSettings,
    val cacheRepository: EmployeeCacheRepository,
    val remoteRepository: RemoteEmployeeRepository,
    locals: Locals,
    val timestampHolder: TimestampHolder
) : CacheModel {

    private var cacheStatus = CacheStatus.NOT_INITIALIZED
    private var networkBusyStatus = MutableStateFlow<Boolean>(false)
    private var errors = BroadcastChannel<CacheError>(1)
    private val cacheValidator = CacheValidator(cacheSettings.expireIntervalSeconds, locals)

    private val scopeExceptionHandler = CoroutineExceptionHandler{context, throwable ->
        Log.e(TAG, "Exception in CacheModelImpl", throwable)
    }
    private val modelScope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main + scopeExceptionHandler)

    override fun checkForInitialization() {

        modelScope.launch {
            // TODO запрашивать буду, если (cacheStatus == CacheStatus.UNCOMPLETED)
            // и нет данных в базе совсем (пометка в кеше, что данных нет)
            // и т.к. корутины, запрошу количество элементов в базе и все валидирую (предварительно)
            if (cacheStatus == CacheStatus.NOT_INITIALIZED) {
                cacheValidator.validateByTimestamp(timestampHolder.timestampSeconds)
                retrieveInitialData()
            }
        }
    }

    private fun retrieveInitialData() {

        modelScope.launch {

            if(cacheValidator.isSynchronized()){
                changeCacheStatus(CacheStatus.SYNCHRONIZED)
            } else {
                changeCacheStatus(CacheStatus.UNCOMPLETED)
                retrieveMoreEmployees()
            }
        }
    }

    override fun observeNetworkBusyStatus(): StateFlow<Boolean>{
        return networkBusyStatus
    }

    override fun observeErrors(): Flow<CacheError> {
        return errors.asFlow()
    }

    override fun observeEmployees(): Flow<List<Employee>> = cacheRepository.getEmployees()

    private fun changeCacheStatus(newStatus: CacheStatus){

        if(cacheStatus != newStatus) {
            Log.d(TAG, "* changeCacheStatus:${newStatus.name}")
            cacheStatus = newStatus
            networkBusyStatus.value = newStatus.isNetworkBusy
        }
    }

    override fun retrieveMoreEmployees() {

        modelScope.launch {
            if (cacheStatus == CacheStatus.UNCOMPLETED) {

                changeCacheStatus(CacheStatus.DATA_RETRIEVING)

                val pageNum =
                    if (cacheValidator.hasCacheRecord) cacheValidator.pagesLoaded + 1 else 1
                Log.d(TAG, "*** data retrieving, pageNum:${pageNum}")

                val emplPackResult: Result<EmployeePack> =
                    remoteRepository.retrieveMoreEmployees(pageNum)

                if (emplPackResult.isFailure) {
                    onRetrieveEmployeesError(
                        emplPackResult.exceptionOrNull(),
                        R.string.cache_err_cant_reuest
                    )
                } else {
                    onRetrieveEmployeesComplete(emplPackResult.getOrNull()!!)
                }
            }
        }
    }

    private suspend fun onRetrieveEmployeesComplete(resultPage: EmployeePack) {

        Log.d(TAG, "*** page loaded, ${resultPage.page}/${resultPage.totalPages}")

        if(!cacheValidator.verifyPageNumbers(resultPage.page, resultPage.totalPages)){
            cacheValidator.invalidate()
        }

        if(resultPage.page > 1 && !cacheValidator.hasCacheRecord){
            // все перезапросить заново
            changeCacheStatus(CacheStatus.UNCOMPLETED)
            retrieveInitialData()
            return
        }

        if(!cacheValidator.hasCacheRecord){
            cacheRepository.clearEmployees()
        }

        val dbResult: Result<Unit> = kotlin.runCatching {
            cacheRepository.addData(resultPage.employees)
        }
        when {
            (dbResult.isSuccess)->{
                cacheValidator.onNewData(resultPage.page, resultPage.totalPages, timestampHolder.timestampSeconds)
                changeCacheStatus(if(resultPage.isLast()) CacheStatus.SYNCHRONIZED else CacheStatus.UNCOMPLETED)
            }
            else -> {
                cacheValidator.invalidate()
                onRetrieveEmployeesError(dbResult.exceptionOrNull(), R.string.cache_err_cant_handle_request)
            }
        }
    }

    private suspend fun onRetrieveEmployeesError(throwable: Throwable?, errStringId: Int) {
        Log.e(TAG, "onRetrieveEmployeesError", throwable)

        errors.send(CacheError(errStringId))
        processStatusAfterEmployeeGettingError()
    }

    private suspend fun processStatusAfterEmployeeGettingError() {
        if (cacheValidator.hasCacheRecord) {
            changeCacheStatus(CacheStatus.UNCOMPLETED)
        } else {
            changeCacheStatus(CacheStatus.NOT_INITIALIZED)
        }
    }

    override fun invalidateDbCache() {
        modelScope.launch {
            cacheValidator.invalidate()
        }
    }

    override fun clearDbCache(): Deferred<Boolean> =
        modelScope.async {

            if (cacheStatus.isNetworkBusy) {
                errors.send(CacheError(R.string.cache_err_loading_in_progress))
                false
            } else {
                invalidateDbCache()
                changeCacheStatus(CacheStatus.NOT_INITIALIZED)
                cacheRepository.clearEmployees()
                true
            }
        }


}


