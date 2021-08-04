package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.telecom.Call
import android.telecom.CallAudioState
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.customView.OnTwowaySliderListener
import com.example.myapplication.databinding.ActivityCallBinding
import com.example.myapplication.listener.CallEventListener


class CallActivity : AppCompatActivity(), CallEventListener, OnTwowaySliderListener {
    private var isOnHold: Boolean? = true
    private var mp: MediaPlayer? = null
    private var vibration: Vibrator? = null
    private var number: String? = null
    private val TAG = CallActivity::class.java
    private var isSpeakerOn = true
    private var proximityWakeLock: PowerManager.WakeLock? = null
    private var _binding: ActivityCallBinding? = null
    private val binding get() = _binding

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (_binding == null)
            _binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)

        OngoingCallObject.registerCallEventListener(this)
        number = intent.data?.schemeSpecificPart
        vibration = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        setCallerName(number.toString())
        if (OngoingCallObject.call?.state == Call.STATE_RINGING) {
            binding!!.apply {
                inComingCallLayout.visibility = View.VISIBLE
                runningCallLayout.visibility = View.GONE
                callTime.text = "incoming.."

                val anim = AnimationUtils.loadAnimation(this@CallActivity, R.anim.zoomin_out)
                anim.duration = 1500
                zoomInRed.startAnimation(anim)
                zoomIbGreen.startAnimation(anim)


                sliderControlCostomized.listener = object : OnTwowaySliderListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onSliderMoveLeft() {
                        OngoingCallObject.hangup()
                        vibration!!.vibrate(
                            VibrationEffect.createOneShot(
                                100,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        );
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onSliderMoveRight() {
                        OngoingCallObject.answer()
                        vibration!!.vibrate(
                            VibrationEffect.createOneShot(
                                500,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        );
                    }

                    override fun onSliderLongPress() {

                    }

                }

            }


            val pattern = longArrayOf(0, 1500, 1500, 1500, 1500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibration!!.vibrate(
                    VibrationEffect.createWaveform(
                        pattern,
                        VibrationEffect.CONTENTS_FILE_DESCRIPTOR
                    )
                );
            }
            startDialRinging()
        } else if (OngoingCallObject.call?.state == Call.STATE_ACTIVE) {
            binding?.apply {
                toggleMic.isEnabled = true
                toggleHoldSwitch.isEnabled = true
            }
        }




        binding!!.apply {
            endCall.setOnClickListener {

                OngoingCallObject.hangup()
            }

            toggleHoldSwitch.setOnClickListener {
                if (isOnHold == true) {
                    OngoingCallObject.call?.unhold()
                    isOnHold = false
                } else {
                    OngoingCallObject.call?.hold()
                    isOnHold = true
                }
            }
            toggleSpeaker.setOnClickListener {
                toggleSpeaker()
            }
            toggleMic.setOnClickListener {
                toggleMicrophone()
            }
        }


    }


    override fun onResume() {
        super.onResume()
        OngoingCallObject.registerCallEventListener(this)
    }

    val audioManager: AudioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleSpeaker() {
        audioManager.mode = AudioManager.MODE_IN_CALL;
        isSpeakerOn = audioManager.isSpeakerphoneOn
        val earPiece = CallAudioState.ROUTE_WIRED_OR_EARPIECE
        val speaker = CallAudioState.ROUTE_SPEAKER
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            CallService.instance?.setAudioRoute(if (isSpeakerOn) earPiece else speaker)
        } else {
            audioManager.isSpeakerphoneOn = !isSpeakerOn
        }

    }


    private fun toggleMicrophone() {
        audioManager.isMicrophoneMute = !audioManager.isMicrophoneMute
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.M)
        fun start(context: Context, call: Call) {
            val intent = Intent(context, CallActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = call.getDetails().getHandle()
            context.startActivity(intent)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setCallerName(callerID: String) {
        binding!!.apply {
            callername.text = callerID + ""
            callTime.text = "Calling..."
        }
    }


    private fun startDialRinging() {
        try {
            mp = MediaPlayer.create(applicationContext, R.raw.beep)
            mp!!.isLooping = true
            mp!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun stopRinging() {
        if (mp != null && mp!!.isPlaying) {
            mp!!.stop()
        }
    }

    private val MINUTE_SECONDS = 60
    private fun initProximitySensor() {
        if (proximityWakeLock == null || proximityWakeLock?.isHeld == false) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            proximityWakeLock = powerManager.newWakeLock(
                PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                "com.bjs.pdanative.ui.defaultdialer.pro:wake_lock"
            )
            proximityWakeLock!!.acquire(10 * MINUTE_SECONDS * 1000L)
        }
    }

    override fun onDestroy() {
        vibration?.cancel()
        stopRinging()
        OngoingCallObject.unRegisterCallEventListener()
        super.onDestroy()
        if (_binding != null)
            _binding = null
        if (proximityWakeLock?.isHeld == true) {
            proximityWakeLock!!.release()
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        super.onBackPressed()
        if (OngoingCallObject.call?.state == Call.STATE_DIALING) {
            OngoingCallObject.hangup()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onGoingCallEvent(event: String) {
        when (event) {
            OngoingCallObject.STATE_CONNECTING -> {
//                The initial state of an outgoing Call.
                initProximitySensor()
            }
            OngoingCallObject.STATE_RINGING -> {
            }
            OngoingCallObject.STATE_ACTIVE -> {
//                The state of a Call when actively supporting conversation.
                initProximitySensor()
                binding!!.apply {
                    inComingCallLayout.visibility = View.GONE
                    runningCallLayout.visibility = View.VISIBLE
                    toggleHoldSwitch.isChecked = false
                    toggleMic.visibility = View.VISIBLE
                    toggleHoldSwitch.visibility = View.VISIBLE
                    icAddCall.visibility = View.VISIBLE
                    icDialPad.visibility = View.VISIBLE
                    toggleMic.isEnabled = true
                    toggleHoldSwitch.isEnabled = true
                }
                isOnHold = false
                vibration?.cancel()
                stopRinging()
            }
            OngoingCallObject.STATE_DISCONNECTED -> {
                finish()
            }
            OngoingCallObject.STATE_HOLDING -> {
//               The state of a Call when in a holding state.
                isOnHold = true
                binding!!.toggleHoldSwitch.isChecked = true
            }
            OngoingCallObject.REJECT_REASON_DECLINED -> {
            }
            OngoingCallObject.STATE_DISCONNECTING -> {
//                The state of a Call when the user has initiated a disconnection of the call, but the call has not yet been disconnected by the underlying ConnectionService.
            }
            OngoingCallObject.STATE_DIALING -> {
//                The state of an outgoing Call when dialing the remote number, but not yet connected.
            }
            OngoingCallObject.STATE_NEW -> {
//                The state of an outgoing Call when dialing the remote number, but not yet connected.

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onGoingCallInfo(time: String, number: String) {
        binding!!.apply {
            callTime.text = time
            callername.text = number
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSliderMoveLeft() {
        OngoingCallObject.hangup()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSliderMoveRight() {
        OngoingCallObject.answer()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onSliderLongPress() {


    }

}