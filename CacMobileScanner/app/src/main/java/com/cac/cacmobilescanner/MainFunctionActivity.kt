package com.cac.cacmobilescanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main_function.*
/**
 *  Created by ac.hsu on 2020/2/3.
 *  Mail: ac.hsu@104.com.tw
 *  Copyright (c) 2020 104 Corporation
 */

class MainFunctionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_function)
        scan_button.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        intentResult?.let {
            val result = it.contents ?: "Cancelled"
            Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
            entryBorrowDevicePage()
        }
    }

    fun entryBorrowDevicePage() {
        var intent = Intent(this, BorrowDeviceActivity::class.java)
        startActivity(intent)
    }
}