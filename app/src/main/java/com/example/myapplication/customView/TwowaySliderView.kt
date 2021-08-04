package com.example.myapplication.customView

import com.example.myapplication.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Vibrator
import android.view.MotionEvent
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.util.AttributeSet
import android.view.View

class TwowaySliderView : View {
    // View global variables
    private var ctx: Context? = null
    private var measuredWth = 0
    private var measuredHht // height and weigth of the view
            = 0
    private var density = 0f

    // Setter and Getter for TwowaySlider event listeners
    var listener: OnTwowaySliderListener? = null
    private var mBackgroundPaint: Paint? = null
    private var mSliderPaint: Paint? = null
    private val mImagePaint // paint that has to be drawn
            : Paint? = null
    private var rx = 0f
    private var ry // Corner radius
            = 0f
    private var mRoundedRectPath: Path? = null
    private var sliderImage = 0
    private var leftImage = 0
    private var rightImage = 0
    var xPos // circles x position
            = 0f
    var imageTop = 0f
    var event_x = 0f
    var event_y // run time view moved position
            = 0f
    var radius // circles radius
            = 0f
    var X_MIN = 0f
    var X_MAX // min and max boundaries of background
            = 0f
    private var ignoreTouchEvents // Should we ignore the movement event
            = false
    private var cancelOnYExit // Do we cancel when the Y coordinate leaves the view?
            = false
    private var useDefaultCornerRadiusX = false
    private var useDefaultCornerRadiusY = false
    private var noSliderImage = false
    private var noLeftImage = false
    private var noRightImage // Do we use default corner radius if not provided
            = false

    // Default values
    private var  bgdColor :Int = -0x7f8485
    var sliderColor = -0x55bfbfc0
    var fillCircle = false

    // TwowaySlider view constructors
    constructor(context: Context) : super(context, null, 0) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    // Initialization of view
    private fun init(context: Context, attrs: AttributeSet?, style: Int) {
        this.ctx = context
        // Get the attributes set by user, if not available use default
        val res = resources
        density = res.displayMetrics.density
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.TwowaySliderView, style, 0)
        rx = a.getDimension(R.styleable.TwowaySliderView_cornerRadiusX, rx)
        useDefaultCornerRadiusX = rx == 0f
        ry = a.getDimension(R.styleable.TwowaySliderView_cornerRadiusY, ry)
        useDefaultCornerRadiusY = ry == 0f
        bgdColor =
            a.getColor(R.styleable.TwowaySliderView_sliderBackgroundColor, bgdColor)
        sliderColor = a.getColor(R.styleable.TwowaySliderView_sliderColor, sliderColor)
        fillCircle = a.getBoolean(R.styleable.TwowaySliderView_fillCircle, fillCircle)
        sliderImage = a.getResourceId(R.styleable.TwowaySliderView_sliderImage, sliderImage)
        noSliderImage = sliderImage == 0
        leftImage = a.getResourceId(R.styleable.TwowaySliderView_leftImage, leftImage)
        noLeftImage = leftImage == 0
        rightImage = a.getResourceId(R.styleable.TwowaySliderView_rightImage, rightImage)
        noRightImage = rightImage == 0
        cancelOnYExit = a.getBoolean(R.styleable.TwowaySliderView_cancelOnYExit, false)
        a.recycle()
        // Initialize needed view components
        mRoundedRectPath = Path()
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint!!.style = Paint.Style.FILL
        mBackgroundPaint!!.color = bgdColor
        mSliderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (fillCircle) {
            mSliderPaint!!.style = Paint.Style.FILL_AND_STROKE
        } else {
            mSliderPaint!!.style = Paint.Style.STROKE
        }
        mSliderPaint!!.color = sliderColor
        mSliderPaint!!.strokeWidth = 2 * density
        if (!isInEditMode) {
            val direction = floatArrayOf(0.0f, -1.0f, 0.5f)
            val filter: MaskFilter = EmbossMaskFilter(direction, 0.8f, 15f, 1f)
            mSliderPaint!!.maskFilter = filter
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measuredHht = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        measuredWth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        if (useDefaultCornerRadiusX) {
            rx = measuredHht * 0.52f
        }
        if (useDefaultCornerRadiusY) {
            ry = measuredHht * 0.52f
        }
        radius = measuredHht * 0.38f
        X_MIN = 1.2f * radius
        X_MAX = measuredWth - X_MIN
        xPos = measuredWth * 0.5f
        imageTop = measuredHht * 0.25f
        setMeasuredDimension(measuredWth, measuredHht)
    }

    private fun drawRoundRect(c: Canvas) {
        mRoundedRectPath!!.reset()
        mRoundedRectPath!!.moveTo(rx, 0f)
        mRoundedRectPath!!.lineTo(measuredWth - rx, 0f)
        mRoundedRectPath!!.quadTo(measuredWth.toFloat(), 0f, measuredWth.toFloat(), ry)
        mRoundedRectPath!!.lineTo(measuredWth.toFloat(), measuredHht - ry)
        mRoundedRectPath!!.quadTo(
            measuredWth.toFloat(),
            measuredHht.toFloat(),
            measuredWth - rx,
            measuredHht.toFloat()
        )
        mRoundedRectPath!!.lineTo(rx, measuredHht.toFloat())
        mRoundedRectPath!!.quadTo(0f, measuredHht.toFloat(), 0f, measuredHht - ry)
        mRoundedRectPath!!.lineTo(0f, ry)
        mRoundedRectPath!!.quadTo(0f, 0f, rx, 0f)
        c.drawPath(mRoundedRectPath!!, mBackgroundPaint!!)
    }

    @SuppressLint("NewApi")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (measuredHht <= 0 || measuredWth <= 0) {
            return
        }
        if (Build.VERSION.SDK_INT >= 21) {
            canvas.drawRoundRect(
                0f,
                0f,
                measuredWth.toFloat(),
                measuredHht.toFloat(),
                rx,
                ry,
                mBackgroundPaint!!
            )
        } else {
            drawRoundRect(canvas)
        }
        if (!noLeftImage) {
//            Drawable drawable = getResources().getDrawable(R.drawable.ic_dialpad, null);
            val bitmap = drawableToBitmap(resources.getDrawable(R.drawable.shape_oval_red))
            val cx = 0 + bitmap.width * 0.25f
            val cy = (measuredHht - bitmap.height) * 0.5f
            canvas.drawBitmap(bitmap, cx, cy, null)
        }
        if (!noRightImage) {
            val bitmap = drawableToBitmap(resources.getDrawable(R.drawable.shape_oval_green))
            val cx = measuredWth - bitmap.width * 1.25f
            val cy = (measuredHht - bitmap.height) * 0.5f
            canvas.drawBitmap(bitmap, cx, cy, null)
        }
        canvas.drawCircle(xPos, measuredHht * 0.5f, radius, mSliderPaint!!)
        if (!noSliderImage) {
            val bitmap = drawableToBitmap(resources.getDrawable(R.drawable.calls))
            val cx = xPos - radius * 0.75f
            val cy = (measuredHht - bitmap.height) * 0.5f
            canvas.drawBitmap(bitmap, cx, cy, null)
            // canvas.drawBitmap(bitmap, x-(radius*0.75f), imageTop, null);
        }
    }

    private val mBitmap: Bitmap? = null
    private val mBitmap2: Bitmap? = null
    private val mBitmap3: Bitmap? = null
    private val mBitmap4: Bitmap? = null
    private fun onSlideRight() {
        if (listener != null) {
            listener!!.onSliderMoveRight()
        }
        //        vibrate(30);
    }

    private fun onSlideLeft() {
        if (listener != null) {
            listener!!.onSliderMoveLeft()
        }
        //        vibrate(30);
    }

    private fun onLongPress() {
        if (listener != null) {
            listener!!.onSliderLongPress()
        }
        //        vibrate(30);
        reset()
    }

    val hndlr = Handler()
    var longPress = Runnable { onLongPress() }
    private fun reset() {
        radius = measuredHht * 0.38f
        xPos = measuredWth * 0.5f
        invalidate()
    }

    private fun vibrate(value: Int) {
        val vibrator = ctx!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(value.toLong())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_UP -> {
                ignoreTouchEvents = false
                hndlr.removeCallbacks(longPress)
                reset()
                true
            }
            MotionEvent.ACTION_DOWN -> {
                radius = measuredHht * 0.45f
                event_x = event.getX(0)
                event_y = event.getY(0)
                val squareRadius = (radius * radius).toDouble()
                val squaredXDistance = ((event_x - xPos) * (event_x - xPos)).toDouble()
                val squaredYDistance =
                    ((event_y - measuredHht / 2) * (event_y - measuredHht / 2)).toDouble()
                if (squaredXDistance + squaredYDistance > squareRadius) {
                    // User touched outside the button, ignore his touch
                    ignoreTouchEvents = true
                } else {
//                    vibrate(30);
                    invalidate()
                }
                hndlr.postDelayed(longPress, 1500)
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                hndlr.removeCallbacks(longPress)
                ignoreTouchEvents = true
                reset()
                if (!ignoreTouchEvents) {
                    radius = measuredHht * 0.45f
                    event_x = event.getX(0)
                    val min_val = (measuredWth / 2 - radius / 3).toDouble()
                    val max_val = (measuredWth / 2 + radius / 3).toDouble()
                    if (!(max_val > event_x && min_val < event_x)) {
                        hndlr.removeCallbacks(longPress)
                    }
                    //                    vibrate(2);
                    if (cancelOnYExit) {
                        event_y = event.getY(0)
                        if (event_y < 0 || event_y > measuredHht) {
                            ignoreTouchEvents = true
                            reset()
                        }
                    }
                    xPos = if (event_x > X_MAX) X_MAX else if (event_x < X_MIN) X_MIN else event_x
                    if (event_x >= X_MAX) {
//                        ignoreTouchEvents = true;
                        onSlideRight()
                    } else if (event_x <= X_MIN) {
//                        ignoreTouchEvents = true;
                        onSlideLeft()
                    }
                    invalidate()
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!ignoreTouchEvents) {
                    radius = measuredHht * 0.45f
                    event_x = event.getX(0)
                    val min_val = (measuredWth / 2 - radius / 3).toDouble()
                    val max_val = (measuredWth / 2 + radius / 3).toDouble()
                    if (!(max_val > event_x && min_val < event_x)) {
                        hndlr.removeCallbacks(longPress)
                    }
                    if (cancelOnYExit) {
                        event_y = event.getY(0)
                        if (event_y < 0 || event_y > measuredHht) {
                            ignoreTouchEvents = true
                            reset()
                        }
                    }
                    xPos = if (event_x > X_MAX) X_MAX else if (event_x < X_MIN) X_MIN else event_x
                    if (event_x >= X_MAX) {
                        onSlideRight()
                    } else if (event_x <= X_MIN) {
                        onSlideLeft()
                    }
                    invalidate()
                }
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    companion object {
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            var width = drawable.intrinsicWidth
            width = if (width > 0) width else 1
            var height = drawable.intrinsicHeight
            height = if (height > 0) height else 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}