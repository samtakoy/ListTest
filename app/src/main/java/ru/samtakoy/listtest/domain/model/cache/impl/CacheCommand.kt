package ru.samtakoy.listtest.domain.model.cache.impl

import kotlinx.coroutines.CompletableDeferred
import ru.samtakoy.listtest.domain.model.cache.RequestResult
import java.util.concurrent.atomic.AtomicBoolean

sealed class CacheCommand(){

    protected abstract fun getCapturedFlag(): AtomicBoolean

    /**
     * Реализуем поведение: в очереди на выполнение может присутствовать только 1 команда
     * определенного типа
     *
     * @return true при успешном получении разрешения на выполнение
     * */
    fun capturePermission(): Boolean{
        return !getCapturedFlag().getAndSet(true)
    }

    /**
     * отпустить полученное разрешение на выполнение
     * */
    fun releasePermission() {
        getCapturedFlag().set(false)
    }
}

object CheckForInitialization: CacheCommand(){
    private val isPermissionCaptured = AtomicBoolean(false)
    override fun getCapturedFlag(): AtomicBoolean = isPermissionCaptured
}

object RetrieveMoreEmployees: CacheCommand(){
    private val isPermissionCaptured = AtomicBoolean(false)
    override fun getCapturedFlag(): AtomicBoolean = isPermissionCaptured
}

object InvalidateDbCache: CacheCommand(){
    private val isPermissionCaptured = AtomicBoolean(false)
    override fun getCapturedFlag(): AtomicBoolean = isPermissionCaptured
}

data class ClearDbCache(
    val deferred: CompletableDeferred<RequestResult>
): CacheCommand(){
    companion object {
        private val isPermissionCaptured = AtomicBoolean(false)
    }
    override fun getCapturedFlag(): AtomicBoolean = isPermissionCaptured
}