package ru.samtakoy.listtest.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// https://raw.githubusercontent.com/samtakoy/data/master/employers/employers1.json

val BASE_URL: String = "https://raw.githubusercontent.com/samtakoy/data/master/employers/"


val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClientBuilder.build())
    .addConverterFactory(GsonConverterFactory.create())
    //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()


