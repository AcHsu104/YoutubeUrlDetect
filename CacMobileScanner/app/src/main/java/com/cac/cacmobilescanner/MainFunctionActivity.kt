package com.cac.cacmobilescanner

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cac.cacmobilescanner.http.OkHttpManager
import com.cac.cacmobilescanner.http.RetrofitManager
import com.cac.cacmobilescanner.http.repo.remote.DeviceRepo
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main_function.*
import okhttp3.OkHttpClient

/**
 *  Created by ac.hsu on 2020/2/3.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */

class MainFunctionActivity : AppCompatActivity() {
    val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_function)
        scan_button.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentResult?.let {
            val result = it.contents ?: Scanner.CANCELLED.type
            Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
            if (!TextUtils.equals(result, Scanner.CANCELLED.type)) {
                checkDeviceBorrowType(result)
            }
        }
    }

    fun entryBorrowDevicePage(deviceId : String) {
        var intent = Intent(this, BorrowDeviceActivity::class.java)
        intent.putExtra(BorrowDeviceActivity.KEY_DEVICE_ID, deviceId)
        startActivity(intent)
    }

    fun checkDeviceBorrowType(deviceId : String) {
        DeviceRepo.getService(RetrofitManager.getInstance(AppConfig.apiBaseUrl))
            .checkDeviceStatus(deviceId)?.let {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        it?.let {
                            if ("1" == it.status && it.message != null) {
                                when (it.message) {
                                    "ISJOIN" -> entryBorrowDevicePage(deviceId)
                                    "NOJOIN" -> callReturnDevice(deviceId)
                                    "NOPROPERTYUP" -> Toast.makeText(applicationContext, "裝置不存在", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(applicationContext, "api 發生錯誤, 請確認網路狀態", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

            }?.let {
                compositeDisposable.add(it)
            }
    }

    fun callReturnDevice (deviceId: String) {
        DeviceRepo.getService(RetrofitManager.getInstance(AppConfig.apiBaseUrl))
            .returnDevice(deviceId)?.let {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        it?.let {
                            if ("1" == it.status && it.message != null) {
                                var message = when (it.message) {
                                    "UPDATEOK" -> "歸還成功"
                                    "NOPROPERTYUP" -> "歸還失敗, 請再重試一次"
                                    else -> "歸還失敗, 請再重試一次"
                                }
                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "api 發生錯誤, 請確認網路狀態", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }?.let {
                compositeDisposable.add(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}