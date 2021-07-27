package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class CallService : InCallService() {


    override fun onCreate() {
        super.onCreate()


    }

    var call: Call? = null
    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)


        if (call != null) {
            this.call = call
            Log.e(TAG, "onCallAdded: ${call.getDetails().getHandle()}")
            OngoingCallObject.registerCallBack(call)
            OngoingCallObject.sendCallStatus(call.getDetails().getHandle().toString())
            Log.e(TAG, "onCallAdded: " + call?.state)
            CallActivity.start(this, call)
        }
        when (call?.state) {
            Call.STATE_CONNECTING -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_CONNECTING)
            }
            Call.STATE_RINGING -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_RINGING)
            }
            Call.STATE_ACTIVE -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_ACTIVE)
            }
            Call.STATE_DISCONNECTED -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_DISCONNECTED)
            }
            Call.STATE_HOLDING -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_HOLDING)
            }
            Call.REJECT_REASON_DECLINED -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.REJECT_REASON_DECLINED)
            }
            Call.STATE_DISCONNECTING -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_DISCONNECTING)
            }
            Call.STATE_DIALING -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_DIALING)
            }
            Call.STATE_NEW -> {
                OngoingCallObject.sendCallStatus(OngoingCallObject.STATE_NEW)
            }

        }
    }

    override fun onConnectionEvent(call: Call?, event: String?, extras: Bundle?) {
        super.onConnectionEvent(call, event, extras)
        Log.e(TAG, "onConnectionEvent: " + event)
    }

    override fun onCallRemoved(call: Call?) {
        super.onCallRemoved(call)
        OngoingCallObject.unRegisterCallBack()
        Log.e(TAG, "onCallRemoved: $call")
    }

}