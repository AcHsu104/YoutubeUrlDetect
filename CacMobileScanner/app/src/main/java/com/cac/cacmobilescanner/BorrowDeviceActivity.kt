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

/**
 *  Created by ac.hsu on 2020/2/4.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */
class BorrowDeviceActivity : AppCompatActivity() {
    var pendingIntent : PendingIntent? = null
    var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow_device)
        initNfc()
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

}
