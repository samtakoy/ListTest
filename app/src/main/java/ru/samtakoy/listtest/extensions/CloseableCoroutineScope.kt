package ru.samtakoy.listtest.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(override val coroutineContext: CoroutineContext) : CoroutineScope, Closeable {

    override fun close() {
        coroutineContext.cancel()
    }
}