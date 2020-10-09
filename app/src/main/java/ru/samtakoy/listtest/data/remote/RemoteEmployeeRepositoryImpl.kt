package ru.samtakoy.listtest.data.remote

import android.util.Log
import ru.samtakoy.listtest.app.misc.AppCoroutineDispatchers
import ru.samtakoy.listtest.data.remote.api.RequestApi
import ru.samtakoy.listtest.domain.reps.RemoteEmployeeRepository
import ru.samtakoy.listtest.domain.model.dto.EmployeePack
import javax.inject.Inject

class RemoteEmployeeRepositoryImpl @Inject constructor(
    val dispatchers: AppCoroutineDispatchers
): RemoteEmployeeRepository {

    @Inject lateinit var api: RequestApi

    override suspend fun retrieveMoreEmployees(nextPageNum: Int): Result<EmployeePack?>? =
        with(dispatchers.network) {
            return kotlin.runCatching {
                api.getEmployeers(nextPageNum).toDomainModel()
            }
        }

}