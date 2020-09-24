package ru.samtakoy.listtest.data.remote.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.samtakoy.listtest.domain.model.dto.EmployeePack


class ResponsePojo {

    @SerializedName("version")
    @Expose
    var version: Int? = null

    @SerializedName("employees")
    @Expose
    var employees: List<EmployeePojo>? = null

    fun toDomainModel() = EmployeePack(
        version!!,
        employees!!.map { it.toDomainModel() }
    )
}