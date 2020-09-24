package ru.samtakoy.listtest.data.remote.api

import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.model.EmployeeStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EmployeePojo {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("firstname")
    @Expose
    var firstname: String? = null

    @SerializedName("lastname")
    @Expose
    var lastname: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("avatar")
    @Expose
    var avatar: String? = null

    @SerializedName("position")
    @Expose
    var position: String? = null

    @SerializedName("tel")
    @Expose
    var tel: String? = null

    @SerializedName("desc")
    @Expose
    var desc: String? = null


    fun toDomainModel(): Employee {
        return Employee(
            id!!,
            firstname ?: "",
            lastname ?: "",
            url ?: "",
            avatar ?: "",
            position ?: "",
            tel ?: "",
            desc ?: "",
            EmployeeStatus.getRandomly()
            )
    }

}