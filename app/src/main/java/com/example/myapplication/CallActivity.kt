package com.example.myapplication

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.telecom.Call
import android.telecom.CallAudioState
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.listener.CallEventListener
import java.util.*


class CallActivity : AppCompatActivity(), CallEventListener {

    private var isOnHold: Boolean? = true
    private var isCallStarted: Boolean = false
    private var mp: MediaPlayer? = null
    private var inGoingCall: android.widget.FrameLayout? = null
    private var inComingCall: android.widget.FrameLayout? = null

    private var talking: ImageView? = null
    private var toggleSpeaker: ToggleButton? = null
    private var toggleCamera: ToggleButton? = null
    private var toggleMic: ToggleButton? = null
    private var toggleCameraSwitch: ToggleButton? = null

    var answer: ImageView? = null
    var hangup: ImageView? = null
    var hangup2: ImageView? = null

    private var number: String? = null
    private val TAG = CallActivity::class.java
    private var isSpeakerOn = true
    private var isMicrophoneOn = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        OngoingCallObject.registerCallEventListener(this)
        toggleSpeaker = findViewById(R.id.toggle_speaker)
        toggleCamera = findViewById(R.id.toggle_camera)
        toggleCameraSwitch = findViewById(R.id.toggle_hold_switch)
        talking = findViewById(R.id.talking)
        toggleMic = findViewById(R.id.toggle_mic)
        inComingCall = findViewById(R.id.answerlayout)
        inGoingCall = findViewById(R.id.CallerLayout)
//
        answer = findViewById<View>(R.id.answerCall) as ImageView
        hangup = findViewById<View>(R.id.Endcall) as ImageView
        hangup2 = findViewById<View>(R.id.rejectcall) as ImageView
//        callInfo = findViewById<View>(R.id.callInfo) as TextView
//        number = Objects.requireNonNull(intent.data!!.schemeSpecificPart)
        number = intent.data?.schemeSpecificPart


        SetCallerName(number.toString())

        answer!!.setOnClickListener {
            OngoingCallObject!!.answer()
//            CallService.calldata?.answer(VideoProfile.STATE_AUDIO_ONLY)
            Log.e("TAG", "onCreate:  answer")
        }
        hangup!!.setOnClickListener {
            Log.e("TAG", "onCreate:  hang up")
//            CallService.calldata?.disconnect()
//                ongoingCall!!.hangup()
            OngoingCallObject.hangup()
        }
        hangup2!!.setOnClickListener {
//            CallService.calldata?.disconnect()
//                ongoingCall!!.hangup()
            OngoingCallObject.hangup()
        }

        toggleCameraSwitch!!.setOnClickListener {
            if (isOnHold == true) {
                OngoingCallObject.call?.unhold()
                isOnHold = false
            } else {
                OngoingCallObject.call?.hold()
                isOnHold = true
            }
        }
        toggleSpeaker?.setOnClickListener {
            toggleSpeaker()
        }
        toggleMic?.setOnClickListener {
            toggleMicrophone()
        }

    }

    val audioManager: AudioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private fun toggleSpeaker() {

        if(!isSpeakerOn)//speaker off
        {
            isSpeakerOn = true;
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setBluetoothScoOn(false);
        }
        else
        {
            isSpeakerOn = false;
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(false);
            audioManager.setBluetoothScoOn(true);
        }


    }

    private fun toggleMicrophone() {
        if (!audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(true);

        } else {
            audioManager.setMicrophoneMute(false);
        }

    }

    companion object {
        fun start(context: Context , call: Call) {
            val intent = Intent(context, CallActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = call.getDetails().getHandle()
            Log.e("TAG", "start: " + intent.data)
            context.startActivity(intent)
        }

    }

    private fun SetCallerName(callerID: String) {
        val callerName = findViewById<View>(R.id.callername) as TextView
        val callertime = findViewById<View>(R.id.callTime) as TextView
        callerName.text = callerID + ""
//        if (isOutgoing!!) {
        if (true) {
            callertime.text = "Calling..."
        }
    }

    private fun CallUser() {
        toggleMic!!.visibility = View.GONE
        toggleCamera!!.visibility = View.GONE

        talking!!.visibility = View.VISIBLE


    }

    fun StartDialRinging() {
        try {
            mp = MediaPlayer.create(applicationContext, R.raw.beep)
            mp!!.isLooping = true
            mp!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun stopRinging() {
        if (mp != null && mp!!.isPlaying) {
            mp!!.stop()
        }
    }

    private fun StartTimer() {
        toggleMic!!.visibility = View.VISIBLE
        val timer = findViewById<View>(R.id.callTime) as TextView
        timer.text = "00:00"
        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            private val startTime = System.currentTimeMillis()
            override fun run() {
                while (true) {
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    handler.post {
                        val millis = (System.currentTimeMillis() - startTime).toInt() / 1000
                        val min = millis / 60
                        val sec = millis % 60
                        timer.text =
                            (if (min < 10) "0$min" else min).toString() + ":" + if (sec < 10) "0$sec" else sec
                    }
                }
            }
        }
        Thread(runnable).start()
    }


    override fun onDestroy() {
        OngoingCallObject.unRegisterCallEventListener()
        super.onDestroy()
    }


    override fun onGoingCallEvent(event: String) {
        when (event) {
            OngoingCallObject.STATE_CONNECTING -> {
//                The initial state of an outgoing Call.
                CallUser()

            }
            OngoingCallObject.STATE_RINGING -> {
//                The state of an incoming Call when ringing locally, but not yet connected.
                inGoingCall?.visibility = View.GONE
                inComingCall?.visibility = View.VISIBLE
            }
            OngoingCallObject.STATE_ACTIVE -> {
//                The state of a Call when actively supporting conversation.
                Log.e("TAG", "onGoingCallEvent:  STATE_ACTIVE ")
                inGoingCall?.visibility = View.VISIBLE
                inComingCall?.visibility = View.GONE
                isOnHold = false
                toggleCameraSwitch?.isChecked = false

                if (!isCallStarted){
                    StartTimer()
                    isCallStarted = true
                }

            }
            OngoingCallObject.STATE_DISCONNECTED -> {
                finish()
            }
            OngoingCallObject.STATE_HOLDING -> {
//                The state of a Call when in a holding state.
                Log.e("TAG", "onGoingCallEvent:  STATE_HOLDING ")
                isOnHold = true
                toggleCameraSwitch?.isChecked = true
            }
            OngoingCallObject.REJECT_REASON_DECLINED -> {
                Log.e("TAG", "onGoingCallEvent:  REJECT_REASON_DECLINED ")
            }
            OngoingCallObject.STATE_DISCONNECTING -> {
                Log.e("TAG", "onGoingCallEvent:  STATE_DISCONNECTING ")
//                The state of a Call when the user has initiated a disconnection of the call, but the call has not yet been disconnected by the underlying ConnectionService.
            }
            OngoingCallObject.STATE_DIALING -> {
                Log.e("TAG", "onGoingCallEvent:  STATE_DIALING ")
//                The state of an outgoing Call when dialing the remote number, but not yet connected.
            }
            OngoingCallObject.STATE_NEW -> {
                Log.e("TAG", "onGoingCallEvent:  STATE_NEW ")
//                The state of an outgoing Call when dialing the remote number, but not yet connected.

            }

        }
    }

}