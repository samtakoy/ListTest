package ru.samtakoy.listtest.data.local.cache.database

import androidx.room.*
import ru.samtakoy.listtest.domain.model.Employee
import ru.samtakoy.listtest.domain.model.EmployeeStatus

@Entity(
    tableName = "employee"
)
class EmployeeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "avatar") val avatar: String,
    @ColumnInfo(name = "position") val position: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "description") val description: String,
    @field:TypeConverters(EmployeeStatusIntConverter::class)
    @ColumnInfo(name = "status") val status: EmployeeStatus
) {

    companion object{

        fun fromDomainEntity(domainEntity: Employee) = EmployeeEntity(
            domainEntity.id,
            domainEntity.firstName,
            domainEntity.lastName,
            domainEntity.url,
            domainEntity.avatar,
            domainEntity.position,
            domainEntity.phone,
            domainEntity.description,
            domainEntity.status
        )

    }

    fun toDomainEntity()= Employee(
        id, firstName, lastName, url, avatar, position, phone, description, status
    )

}