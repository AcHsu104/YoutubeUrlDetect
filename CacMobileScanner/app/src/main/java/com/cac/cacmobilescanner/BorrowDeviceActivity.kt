package com.cac.cacmobilescanner

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import kotlin.experimental.and
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.cac.cacmobilescanner.http.RetrofitManager
import com.cac.cacmobilescanner.http.repo.remote.DeviceRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_borrow_device.*

/**
 *  Created by ac.hsu on 2020/2/4.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */
class BorrowDeviceActivity : AppCompatActivity() {
    val compositeDisposable = CompositeDisposable()

    companion object {
        val KEY_DEVICE_ID = "KEY_DEVICE_ID"
    }

    var pendingIntent : PendingIntent? = null
    var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_device)

        initNfc()

        initView()
        setListener()
    }

    fun getDeviceId() : String {
        return intent.getStringExtra(KEY_DEVICE_ID) ?: ""
    }

    fun initView() {
        device_id_edit.setText(getDeviceId())
    }

    fun initNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this) ?: return
        nfcAdapter?.let {
            if (!it.isEnabled) {
                showToast("Please open NFC!")
                return
            }

            val nfcIntent = Intent(this, javaClass)
            nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0)
        }
    }

    fun setListener() {
        borrow_device_button.setOnClickListener {
            callBorrowDevice(device_id_edit.text.toString(), employee_name_edit.text.toString())
        }
    }

    fun callBorrowDevice(deviceId : String, borrowUserName : String) {
        DeviceRepo.getService(RetrofitManager.getInstance(AppConfig.apiBaseUrl))
            .borrowDevice(borrowUserName , deviceId)?.let {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        it?.let {
                            if ("1" == it.status && it.message != null) {
                                when(it.message) {
                                    "BORROWOK" -> borrowSuccess()
                                    "NOPROPERTYUP" -> borrowFailure()
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

    fun borrowSuccess() {
        showToast("借出成功")
        finish()
    }

    fun borrowFailure() {
        showToast("借出失敗, 請再試一次")
    }

    fun showToast(message : String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
                Log.d("NFCTest", "Tag Id : " + encodeHexString(intent
                    .getByteArrayExtra(NfcAdapter.EXTRA_ID)))
            }
        }
    }

    fun processIntent(intent: Intent?) {
        if (intent == null) {
            return
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.action)) {
            showToast("Invalid action")
            return
        }


    }

    private fun encodeHexString(byteArray: ByteArray): String {
        val hexStringBuffer = StringBuilder()
        for (aByteArray in byteArray) {
            hexStringBuffer.append(byteToHex(aByteArray))
        }
        return hexStringBuffer.toString()
    }

    private fun byteToHex(num : Byte) : String {
        var hexDigits = CharArray(2)
        hexDigits[0] = Character.forDigit((num.toInt() shr 4) and 0xF, 16)
        hexDigits[1] = Character.forDigit((num.toInt() and 0xF), 16);
        return String(hexDigits);
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
