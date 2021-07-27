package com.example.myapplication

import android.content.ContentValues
import android.os.Bundle
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import android.util.Log
import com.example.myapplication.listener.CallEventListener


object OngoingCallObject {

    val STATE_NEW = "STATE_NEW"
    val LOCAL_BROADCAST_CALL_EVENT = "LOCAL_BROADCAST_CALL_EVENT"
    val STATE_CONNECTING = "STATE_CONNECTING"
    val STATE_RINGING = "STATE_RINGING"
    val STATE_ACTIVE = "STATE_ACTIVE"
    val STATE_DISCONNECTED = "CALL_DISCONNECTED"
    val STATE_HOLDING = "STATE_HOLDING"
    val STATE_DIALING = "STATE_DIALING"
    val CONTAIN_NUMBER = "NUMBER"
    val STATE_DISCONNECTING = "STATE_DISCONNECTING"
    val REJECT_REASON_DECLINED = "REJECT_REASON_DECLINED"

    val EVENT = "event"
    val CALL_DISCONNECTED = "CALL_DISCONNECTED"


    var call: Call? = null
    private var callEventListener: CallEventListener? = null
    val callback: Call.Callback = object : Call.Callback() {
        override fun onStateChanged(call: Call?, state: Int) {
            super.onStateChanged(call, state)
            Log.i("debinf OngoingObj", "state is $state")
        }

        override fun onParentChanged(call: Call?, parent: Call?) {
            super.onParentChanged(call, parent)
            Log.e(ContentValues.TAG, "onParentChanged: ")
        }

        override fun onChildrenChanged(call: Call?, children: MutableList<Call>?) {
            super.onChildrenChanged(call, children)
            Log.e(ContentValues.TAG, "onChildrenChanged: ")
        }

        override fun onDetailsChanged(call: Call?, details: Call.Details?) {
            super.onDetailsChanged(call, details)
            Log.e(ContentValues.TAG, "onDetailsChanged: " + call?.state)


            when (call?.state) {
                Call.STATE_CONNECTING -> {
                    sendCallStatus(OngoingCallObject.STATE_CONNECTING)
                }
                Call.STATE_RINGING -> {
                    sendCallStatus(OngoingCallObject.STATE_RINGING)
                }
                Call.STATE_ACTIVE -> {
                    sendCallStatus(OngoingCallObject.STATE_ACTIVE)
                }
                Call.STATE_DISCONNECTED -> {
                    sendCallStatus(OngoingCallObject.STATE_DISCONNECTED)
                }
                Call.STATE_HOLDING -> {
                    sendCallStatus(OngoingCallObject.STATE_HOLDING)
                }
                Call.REJECT_REASON_DECLINED -> {
                    sendCallStatus(OngoingCallObject.REJECT_REASON_DECLINED)
                }
                Call.STATE_DISCONNECTING -> {
                    sendCallStatus(OngoingCallObject.STATE_DISCONNECTING)
                }
                Call.STATE_DIALING -> {
                    sendCallStatus(OngoingCallObject.STATE_DIALING)
                }
                Call.STATE_NEW -> {
                    sendCallStatus(OngoingCallObject.STATE_NEW)
                }

            }
        }

        override fun onCannedTextResponsesLoaded(
            call: Call?,
            cannedTextResponses: MutableList<String>?
        ) {
            super.onCannedTextResponsesLoaded(call, cannedTextResponses)
            Log.e(ContentValues.TAG, "onCannedTextResponsesLoaded: ")
        }

        override fun onPostDialWait(call: Call?, remainingPostDialSequence: String?) {
            super.onPostDialWait(call, remainingPostDialSequence)
            Log.e(ContentValues.TAG, "onPostDialWait: ")
        }

        override fun onVideoCallChanged(call: Call?, videoCall: InCallService.VideoCall?) {
            super.onVideoCallChanged(call, videoCall)

            Log.e(ContentValues.TAG, "onVideoCallChanged: ")
        }

        override fun onCallDestroyed(call: Call?) {
            super.onCallDestroyed(call)
//                finish the call activity
            callEventListener?.onGoingCallEvent(CALL_DISCONNECTED)
            Log.e(ContentValues.TAG, "onCallDestroyed: ")
        }

        override fun onConferenceableCallsChanged(
            call: Call?,
            conferenceableCalls: MutableList<Call>?
        ) {
            super.onConferenceableCallsChanged(call, conferenceableCalls)
            Log.e(ContentValues.TAG, "onConferenceableCallsChanged: ")
        }

        override fun onConnectionEvent(call: Call?, event: String?, extras: Bundle?) {
            super.onConnectionEvent(call, event, extras)
            Log.e(ContentValues.TAG, "onConnectionEvent: ")
        }

        override fun onRttModeChanged(call: Call?, mode: Int) {
            super.onRttModeChanged(call, mode)
            Log.e(ContentValues.TAG, "onRttModeChanged: ")
        }

        override fun onRttStatusChanged(call: Call?, enabled: Boolean, rttCall: Call.RttCall?) {
            super.onRttStatusChanged(call, enabled, rttCall)
            Log.e(ContentValues.TAG, "onRttStatusChanged: ")
        }

        override fun onRttRequest(call: Call?, id: Int) {
            super.onRttRequest(call, id)
            Log.e(ContentValues.TAG, "onRttRequest: ")
        }

        override fun onRttInitiationFailure(call: Call?, reason: Int) {
            super.onRttInitiationFailure(call, reason)
            Log.e(ContentValues.TAG, "onRttInitiationFailure: ")
        }

        override fun onHandoverComplete(call: Call?) {
            super.onHandoverComplete(call)
            Log.e(ContentValues.TAG, "onHandoverComplete: ")
        }

        override fun onHandoverFailed(call: Call?, failureReason: Int) {
            super.onHandoverFailed(call, failureReason)
            Log.e(ContentValues.TAG, "onHandoverFailed: ")
        }

    }

    //     Custom methods for handling the callEvents

    fun registerCallEventListener(callEventListener: CallEventListener) {
        this.callEventListener = callEventListener
    }

    fun unRegisterCallEventListener() {
        this.callEventListener = null
    }

    fun registerCallBack(call: Call?) {
        this.call = call
        call?.registerCallback(callback)
    }

    fun unRegisterCallBack() {
        call?.unregisterCallback(callback)
    }


    fun sendCallStatus(status: String) {
        callEventListener?.onGoingCallEvent(status)
    }

    fun answer() {
        //assert this.call != null;
        call?.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun hangup() {
        //assert this.call != null;
        call?.disconnect()
    }
}