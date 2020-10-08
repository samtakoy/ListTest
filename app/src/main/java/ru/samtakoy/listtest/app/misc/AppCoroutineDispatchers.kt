package ru.samtakoy.listtest.app.misc

import kotlinx.coroutines.CoroutineDispatcher

data class AppCoroutineDispatchers(
    val database: CoroutineDispatcher,
    val disk: CoroutineDispatcher,
    val network: CoroutineDispatcher,
    val main: CoroutineDispatcher
)