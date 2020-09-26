package ru.samtakoy.listtest.domain.model

data class Employee(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val url: String,
    val avatar: String,
    val position: String,
    val phone: String,
    val description: String,
    val status: EmployeeStatus
) {


    fun getVisibleFirstName() = "${firstName}_$id"
    fun getVisibleLastName() = "${lastName}_$id"
}