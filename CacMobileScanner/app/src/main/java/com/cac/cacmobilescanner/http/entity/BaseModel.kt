package com.cac.cacmobilescanner.http.entity

import com.google.gson.annotations.SerializedName

/**
 *  Created by ac.hsu on 2020/2/14.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */
/*data class BaseModel<T> (val data : T,
                         @SerializedName("STATUS") val status : String)*/

data class BaseModel<T> (val data : T,
                         @SerializedName("STATUS") val status : String,
                         @SerializedName("MESSAGE") val message : String)
