package ru.samtakoy.listtest.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Path

interface RequestApi {

    @GET("employers{num}.json")
    suspend fun getEmployeers(@Path("num") pageNum: Int): ResponsePojo

}