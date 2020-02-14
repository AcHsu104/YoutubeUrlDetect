package com.cac.cacmobilescanner.http.service

import com.cac.cacmobilescanner.http.entity.BaseModel
import com.cac.cacmobilescanner.http.entity.Message
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 *  Created by ac.hsu on 2020/2/14.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */
interface DeviceService {
    @GET("prod/cacphone")
    fun borrowDevice(@QueryMap queryMap: MutableMap<String, String>) : Observable<BaseModel<Message>>

    @GET("prod/cacphone?Check")
    fun checkDeviceStatus(@QueryMap queryMap: MutableMap<String, String>) : Observable<BaseModel<Message>>
}