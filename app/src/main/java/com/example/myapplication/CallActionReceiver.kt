package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
@RequiresApi(Build.VERSION_CODES.M)
class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            OngoingCallObject.STATE_ACTIVE -> OngoingCallObject.answer()
            OngoingCallObject.STATE_DISCONNECTED -> OngoingCallObject.hangup()
        }
    }
}