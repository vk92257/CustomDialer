package com.example.myapplication

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SET_DEFAULT_DIALER = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkDefaultDialer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SET_DEFAULT_DIALER -> checkSetDefaultDialerResult(resultCode)
        }
    }

    private fun checkDefaultDialer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return


        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(
            intent,
            REQUEST_CODE_SET_DEFAULT_DIALER
        )

        // you need to define CHANGE_DEFAULT_DIALER as a static final int

//        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
//        val isAlreadyDefaultDialer = packageName == telecomManager.defaultDialerPackage
//        if (isAlreadyDefaultDialer) return
//
//        val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
//            .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
//        startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else -> "Unexpected result code $resultCode"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}