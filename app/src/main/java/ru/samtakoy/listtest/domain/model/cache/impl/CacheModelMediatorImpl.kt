package ru.samtakoy.listtest.domain.model.cache.impl

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import ru.samtakoy.listtest.domain.Locals
import ru.samtakoy.listtest.domain.TimestampHolder
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.model.cache.CacheError
import ru.samtakoy.listtest.domain.model.cache.CacheModel
import ru.samtakoy.listtest.domain.model.cache.CacheSettings
import ru.samtakoy.listtest.domain.model.cache.RequestResult
import ru.samtakoy.listtest.domain.reps.EmployeeCacheRepository
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository
import ru.samtakoy.listtest.utils.extensions.CloseableCoroutineScope
import javax.inject.Inject

private const val TAG = "CacheModelMediatorImpl"

class CacheModelMediatorImpl @Inject constructor(

    cacheSettings: CacheSettings,
    cacheRepository: EmployeeCacheRepository,
    remoteRepository: RemoteEmployeeRepository,
    locals: Locals,
    timestampHolder: TimestampHolder

): CacheModel {

    private val cacheModel: CacheModelImpl = CacheModelImpl(
        cacheSettings,
        cacheRepository,
        remoteRepository,
        locals,
        timestampHolder
    )

    private val scopeExceptionHandler = CoroutineExceptionHandler{context, throwable ->
        Log.e(TAG, "Exception in CacheModelImpl \n${throwable.stackTraceToString()}" )
    }
    private val modelScope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main + scopeExceptionHandler)
    private val actor = modelScope.cacheActor()

    private fun CoroutineScope.cacheActor() = actor<CacheCommand>(capacity = Channel.UNLIMITED){

        for(command in channel){

            try {
                when (command) {
                    is CheckForInitialization -> cacheModel.checkForInitialization()
                    is RetrieveMoreEmployees -> cacheModel.retrieveMoreEmployees()
                    is InvalidateDbCache -> cacheModel.invalidateDbCache()
                    is ClearDbCache -> {
                        try {
                            if(cacheModel.clearDbCache()){
                                command.deferred.complete(RequestResult.SUCCESS)
                            } else {
                                command.deferred.complete(RequestResult.FAILED)
                            }
                        } catch (ex: Throwable) {
                            command.deferred.completeExceptionally(ex)
                        }
                    }
                } // when
            } finally {

                command.releasePermission()
            }
        } // for
    }

    private fun tryToSend(command: CacheCommand){
        modelScope.launch {
            if(command.capturePermission()){
                actor.send(command)
            }
        }
    }

    private fun tryToSendWithResult(
        command: CacheCommand,
        result: CompletableDeferred<RequestResult>
    ) {
        modelScope.launch {
            try {
                if(command.capturePermission()) {
                    actor.send(command)
                } else{
                    result.complete(RequestResult.IGNORED)
                }
            } catch (t: Throwable) {
                result.complete(RequestResult.FAILED)
            }
        }
    }

    override fun checkForInitialization() {
        tryToSend(CheckForInitialization)
    }

    override fun retrieveMoreEmployees() {
        tryToSend(RetrieveMoreEmployees)
    }

    override fun observeNetworkBusyStatus(): StateFlow<Boolean> = cacheModel.observeNetworkBusyStatus()
    override fun observeErrors(): Flow<CacheError> = cacheModel.observeErrors().asFlow()
    override fun observeEmployees(): Flow<List<Employee>>  = cacheModel.observeEmployees()
    override fun observeEmployeeIds(): Flow<List<Int>>  = cacheModel.observeEmployeeIds()

    override fun invalidateDbCache() {
        tryToSend(InvalidateDbCache)
    }

    override fun clearDbCache(): Deferred<RequestResult> {
        val result = CompletableDeferred<RequestResult>()
        val command = ClearDbCache(result)

        tryToSendWithResult(command, result)

        return result
    }

}