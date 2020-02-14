package com.cac.cacmobilescanner.http.repo.remote

import com.cac.cacmobilescanner.http.entity.BaseModel
import com.cac.cacmobilescanner.http.entity.Message
import com.cac.cacmobilescanner.http.service.DeviceService
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *  Created by ac.hsu on 2020/2/14.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */
class DeviceRepo {
    enum class Type(val type : String) {
        LOAN("1"), BACK("0");

        companion object {
            var key = "Type"
        }
    }

    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var deviceService : DeviceService? = null

        fun getService(retrofit : Retrofit) : Companion {
            deviceService = retrofit.create(DeviceService::class.java)
            return this
        }

        fun borrowDevice(userName : String, propertyUp: String) : Observable<BaseModel<Message>>? {
            var queryMap = mutableMapOf<String, String>()
            queryMap[Type.key] = Type.LOAN.type
            queryMap["Username"] = userName
            queryMap["Propertyup"] = propertyUp
            queryMap["Borrowtime"] = dateFormat.format(Date())

            return deviceService?.borrowDevice(queryMap)
        }

        fun checkDeviceStatus(propertyUp: String) : Observable<BaseModel<Message>>? {
            var queryMap = mutableMapOf<String, String>()
            queryMap["PropertyCheck"] = propertyUp

            return deviceService?.checkDeviceStatus(queryMap)
        }

        fun returnDevice(propertyUp: String) : Observable<BaseModel<Message>>? {
            var queryMap = mutableMapOf<String, String>()
            queryMap[Type.key] = Type.BACK.type
            queryMap["Propertyup"] = propertyUp
            queryMap["Borrowtime"] = dateFormat.format(Date())

            return deviceService?.borrowDevice(queryMap)
        }
    }
}