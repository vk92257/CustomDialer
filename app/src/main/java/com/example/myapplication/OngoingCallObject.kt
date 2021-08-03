package com.example.myapplication

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myapplication.listener.CallEventListener


object OngoingCallObject {

    const val STATE_NEW = "STATE_NEW"
    const val STATE_CONNECTING = "STATE_CONNECTING"
    const val STATE_RINGING = "STATE_RINGING"
    const val STATE_ACTIVE = "STATE_ACTIVE"
    const val STATE_DISCONNECTED = "CALL_DISCONNECTED"
    const val STATE_HOLDING = "STATE_HOLDING"
    const val STATE_DIALING = "STATE_DIALING"
    const val STATE_DISCONNECTING = "STATE_DISCONNECTING"
    const val REJECT_REASON_DECLINED = "REJECT_REASON_DECLINED"
    const val CALL_DISCONNECTED = "CALL_DISCONNECTED"

    const val CALLING = "Is Calling…"
    const val DIALING = "Dialing…"
    const val CLL_ENDED = "Call Ended"
    const val CALL_ENDING = "Call Ending"
    const val ONGOING = "Ongoing Call"


    var time: String = "00:00"
    var call: Call? = null
    private var callEventListener: CallEventListener? = null
    val callback: Call.Callback = @RequiresApi(Build.VERSION_CODES.M)
    object : Call.Callback() {
        override fun onDetailsChanged(call: Call?, details: Call.Details?) {
            super.onDetailsChanged(call, details)
            when (call?.state) {
                Call.STATE_CONNECTING -> {
                    sendCallStatus(STATE_CONNECTING)
                }
                Call.STATE_RINGING -> {
                    sendCallStatus(STATE_RINGING)
                }
                Call.STATE_ACTIVE -> {
                    startTimer()
                    sendCallStatus(STATE_ACTIVE)
                }
                Call.STATE_DISCONNECTED -> {
                    sendCallStatus(STATE_DISCONNECTED)
                }
                Call.STATE_HOLDING -> {
                    sendCallStatus(STATE_HOLDING)
                }
                Call.REJECT_REASON_DECLINED -> {
                    sendCallStatus(REJECT_REASON_DECLINED)
                }
                Call.STATE_DISCONNECTING -> {
                    sendCallStatus(STATE_DISCONNECTING)
                }
                Call.STATE_NEW -> {
                    sendCallStatus(STATE_NEW)
                }

            }
        }

        override fun onCallDestroyed(call: Call?) {
            super.onCallDestroyed(call)
            callEventListener?.onGoingCallEvent(CALL_DISCONNECTED)
            isRunning = false
        }


    }


    fun registerCallEventListener(callEventListener: CallEventListener) {
        this.callEventListener = callEventListener
    }

    fun unRegisterCallEventListener() {
        this.callEventListener = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun registerCallBack(call: Call?) {
        this.call = call
        this.call?.registerCallback(callback)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun unRegisterCallBack() {
        call?.unregisterCallback(callback)
    }


    fun sendCallStatus(status: String) {
        callEventListener?.onGoingCallEvent(status)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun sendCallInfo(time: String, number: String) {
        NotificationCreator.updateNotification(time)
        callEventListener?.onGoingCallInfo(time, number)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun answer() {
        call?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun hangup() {
        call?.disconnect()
    }


    private var isRunning: Boolean = true

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startTimer() {
        isRunning = true
        val handler = Handler(Looper.getMainLooper())
        val runnable: Runnable = object : Runnable {
            private val startTime = System.currentTimeMillis()

            override fun run() {
                while (isRunning) {
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    handler.post {
                        val millis = (System.currentTimeMillis() - startTime).toInt() / 1000
                        val min = millis / 60
                        val sec = millis % 60
                        time =
                            (if (min < 10) "0$min" else min).toString() + ":" + if (sec < 10) "0$sec" else sec

                        call?.details?.handle?.schemeSpecificPart?.let {
                            sendCallInfo(
                                time,
                                it
                            )
                        }

                    }
                }
            }
        }
        Thread(runnable).start()
    }

}