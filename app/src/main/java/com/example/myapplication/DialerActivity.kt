package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.dynamicanimation.animation.DynamicAnimation.ViewProperty
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.CallSliderView.SliderListener
import com.example.myapplication.customView.OnTwowaySliderListener
import com.example.myapplication.customView.TwowaySliderView


class DialerActivity : AppCompatActivity(), OnTwowaySliderListener {
    private lateinit var sliderView2: TwowaySliderView
    var phoneNumber: EditText? = null

    companion object {
        private const val REQUEST_CODE_SET_DEFAULT_DIALER = 123
        private const val REQUEST_CALL_PHONE = 10
    }

    private var dX = 0f
    private var dY = 0f

    private var xAnimation: SpringAnimation? = null
    private var yAnimation: SpringAnimation? = null
    var imageView: ImageView? = null
//    private fun imageViewDragSpringAnimation() {
//        imageView?.getViewTreeObserver()?.addOnGlobalLayoutListener(globalLayoutListener)
//        imageView?.setOnTouchListener(touchListener)
//    }

//    private val globalLayoutListener = OnGlobalLayoutListener {
//        xAnimation = createSpringAnimation(
//            imageView, SpringAnimation.X, imageView!!.x,
//            SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY
//        )
//        yAnimation = createSpringAnimation(
//            SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY
//        )
//    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = OnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
                // cancel animations
                xAnimation!!.cancel()
                yAnimation!!.cancel()
            }
            MotionEvent.ACTION_MOVE -> imageView!!.animate()
                .x(event.rawX + dX)
                .y(event.rawY + dY)
                .setDuration(0)
                .start()
            MotionEvent.ACTION_UP -> {
                xAnimation!!.start()
                yAnimation!!.start()
            }
        }
        true
    }


    fun createSpringAnimation(
        view: View?,
        property: ViewProperty?,
        finalPosition: Float,
        stiffness: Float,
        dampingRatio: Float
    ): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val springForce = SpringForce(finalPosition)
        springForce.stiffness = stiffness
        springForce.dampingRatio = dampingRatio
        animation.spring = springForce
        return animation
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkDefaultDialer()
        setContentView(R.layout.activity_dialer)

        val root: FrameLayout = findViewById(R.id.root)
        root.setOnDragListener(dragListener)
        phoneNumber = findViewById<View>(R.id.etNumber) as EditText
        val bCall: Button = findViewById<View>(R.id.btnCall) as Button

        sliderView2 = findViewById(R.id.sliderControlCostomized)
        sliderView2.listener = this


        offerReplacingDefaultDialer()
        bCall.setOnClickListener {
            makeCall()
        }
        startAnimation()


        /* val callView = findViewById<View>(R.id.slider_view) as CallSliderView
         callView.setSliderEndListener(object : SliderListener {
             override fun onSliderEnd() {
                 Toast.makeText(this@DialerActivity, "call", Toast.LENGTH_SHORT).show()
 //                finish()
             }

             override fun onSliderListen() {
                 Toast.makeText(this@DialerActivity, "cut", Toast.LENGTH_SHORT).show()
 //                finish()
             }
         })*/


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun startAnimation() {
        val animation: LottieAnimationView = findViewById(R.id.animation_view)
        animation.playAnimation()
        animation.setOnClickListener {
            val clipText = "This is out ClipData"
            val item = ClipData.Item(clipText)
            val minTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, minTypes, item)
            val drgShadowBuild = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, drgShadowBuild, it, 0)
            it.visibility = View.INVISIBLE
            true


//            startAnimationSwipe(animation, -200f)
//            startAnimationSwipe(animation, 0f)
        }

//        startAnimationSwipe(animation , -200f)
//        startAnimationSwipe(animation , 0f)


    }


    val dragListener = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()
                view.invalidate()
                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as FrameLayout
                destination.addView(v)
                v.visibility = View.VISIBLE
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }

    }

//    val dragListener = View.OnDragListener{view,event ->
//        when(event.action){
//            DragEvent.ACTION_DRAG_STARTED ->{
//                event.clipDescription.hasMimeType(ClipDescription.Mi)
//            }
//            DragEvent.ACTION_DRAG_ENTERED ->{
//
//            }
//        }
//
//    }

    private fun startAnimationSwipe(animation: View, fl: Float) {
        val anim = SpringAnimation(animation, SpringAnimation.TRANSLATION_X)
        val springForce = SpringForce()
        springForce.stiffness = SpringForce.STIFFNESS_LOW
        springForce.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
        anim.spring = springForce
        springForce.finalPosition = fl
        anim.start()
    }


    private fun makeCall() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            callOn()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PHONE
            )
        }
    }

    private fun callOn() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val phoneAccountHandle = PhoneAccountHandle(
            ComponentName(
                packageName,
                DialerActivity::class.java.name
            ), "myConnectionServiceId"
        )
        val test = Bundle()
        test.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.placeCall(Uri.parse("tel:" + phoneNumber?.text.toString()), test)


    }

    private fun offerReplacingDefaultDialer() {
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage !== packageName) {
            val ChangeDialer = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            ChangeDialer.putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                packageName
            )
            startActivity(ChangeDialer)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall()
            } else {
                Toast.makeText(this, "calling permission denied", Toast.LENGTH_LONG).show()
            }
            //return;
        }
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
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else -> "Unexpected result code $resultCode"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSliderMoveLeft() {
        Toast.makeText(this, "left", Toast.LENGTH_SHORT).show()
//        finish()
    }

    override fun onSliderMoveRight() {
        Toast.makeText(this, "right", Toast.LENGTH_SHORT).show()
//        finish()
    }

    override fun onSliderLongPress() {
    }


}