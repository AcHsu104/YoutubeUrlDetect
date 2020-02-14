package com.cac.cacmobilescanner.http

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  Created by ac.hsu on 2020/2/14.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */
class RetrofitManager {

    companion object {
        var gson : Gson? = null
        init {
            gson = Gson()
        }

        var retrofitMap = mutableMapOf<String, Retrofit>()

        fun getInstance(baseUrl : String) : Retrofit {
            var retrofitManager = retrofitMap[baseUrl] ?: newInstance(baseUrl)
            retrofitMap[baseUrl] = retrofitManager
            return retrofitManager
        }

        fun newInstance(baseUrl : String) : Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpManager.getInstance(baseUrl))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    }
}