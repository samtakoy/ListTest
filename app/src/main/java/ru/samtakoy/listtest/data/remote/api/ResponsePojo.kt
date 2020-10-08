package ru.samtakoy.listtest.data.remote.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.samtakoy.listtest.domain.model.dto.EmployeePack


class ResponsePojo {

    @SerializedName("version")
    @Expose
    val version: Int? = null

    @SerializedName("page")
    @Expose
    val page: Int? = null

    @SerializedName("total_pages")
    @Expose
    val total_pages: Int? = null

    @SerializedName("employees")
    @Expose
    val employees: List<EmployeePojo>? = null

    fun toDomainModel() = EmployeePack(
        version!!,
        page!!,
        total_pages!!,
        employees!!.map { it.toDomainModel() }
    )
}