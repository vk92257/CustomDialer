package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi

class CallService : InCallService() {
    companion object {
        var instance: CallService? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        OngoingCallObject.registerCallBack(call)
        if (call != null) {
            call.details?.handle?.schemeSpecificPart?.let {
                NotificationCreator.setupNotification(
                    it,
                    call.state,
                    "",
                    this
                )
            }
            CallActivity.start(this, call)

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (instance != null) {
            instance = null
        }
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
        OngoingCallObject.unRegisterCallBack()
        OngoingCallObject.unRegisterCallEventListener()
        NotificationCreator.cancelNotification()
    }

}