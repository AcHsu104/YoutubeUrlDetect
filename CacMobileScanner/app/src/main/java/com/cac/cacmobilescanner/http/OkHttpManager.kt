package com.cac.cacmobilescanner.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *  Created by ac.hsu on 2020/2/14.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */

class OkHttpManager {

    object Config {
        val mLoggingInterceptor = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()

                val t1 = System.nanoTime()
                Log.d("Interceptor",
                    String.format(
                        "Sending request %s on %s%n%s",
                        request.url,
                        chain.connection(),
                        request.headers
                    )
                )

                Log.d("Interceptor", "query params : " + request.url.query!!)
                if ("GET" != request.method) {
                    Log.d("Interceptor", "body params : " + bodyToString(request))
                }

                val response = chain.proceed(request)
                val t2 = System.nanoTime()
                Log.d("Interceptor",
                    String.format(
                        Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                        response.request.url, (t2 - t1) / 1e6, response.headers
                    )
                )
                // LogUtil.logDebug("response : " + responseToString(response));
                return response
            }
        }

        private fun bodyToString(request: Request): String {
            var bodyString = ""
            val buffer = Buffer()
            try {
                request.body!!.writeTo(buffer)
                bodyString = buffer.readUtf8()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return bodyString
        }
    }
    companion object {
        var okHttpClientMap = mutableMapOf<String, OkHttpClient>()
        var TIME_OUT : Long = 20
        var networkInterceptor = mutableListOf<Interceptor>()
        var interceptor = mutableListOf<Interceptor>()

        fun newInstance(baseUrl : String) : OkHttpClient {
            var okHttpClient = okHttpClientMap[baseUrl] ?: getInstance(baseUrl)
            okHttpClientMap[baseUrl] = okHttpClient

            return okHttpClient
        }

        fun getInstance(baseUrl : String) : OkHttpClient {
            var okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)

            addInterceptor(okHttpClientBuilder)
            addNetworkInterceptor(okHttpClientBuilder)

            return okHttpClientBuilder.build()
        }

        fun addNetworkInterceptor(builder: OkHttpClient.Builder) : OkHttpClient.Builder {
            networkInterceptor.add(Config.mLoggingInterceptor)
            networkInterceptor.forEach { builder.addNetworkInterceptor(it) }
            return builder
        }

        fun addInterceptor(builder: OkHttpClient.Builder) : OkHttpClient.Builder {
            interceptor.forEach { builder.addInterceptor(it) }
            return builder
        }

    }
}