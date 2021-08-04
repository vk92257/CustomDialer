package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class CallSliderView : View {
    private var sliderListener: SliderListener? = null

    private var mHeight = 0

    private var mWidth = 0


    private var initialCircleOffset = 0


    private var circleOffset = 0


    private var prevX = 0


    private var maxOffset = 0


    private var sliderText: String? = "滑动"
    private var shutDownText: String? = "挂断"
    private var listenText: String? = "接听"


    private var textSize = 0f


    private var progressBackgroundColor = 0


    private var mBackgroundColor = 0
    private var leftColor = 0
    private var rightColor = 0


    private var redRegionWidth = 0

    private var isDownRight = false

    private var paint: Paint? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    interface SliderListener {

        fun onSliderEnd()


        fun onSliderListen()
    }


    fun setSliderEndListener(sliderListener: SliderListener?) {
        this.sliderListener = sliderListener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> actionDown(event)
            MotionEvent.ACTION_MOVE -> actionMove(event)
            MotionEvent.ACTION_UP -> actionUp(event)
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        redRegionWidth = mWidth / 4
        circleOffset = mWidth / 2 - redRegionWidth / 2
        initialCircleOffset = circleOffset
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas, paint)
        drawLeftCircleButton(canvas, paint)
        drawRightCircleButton(canvas, paint)
        drawRoundButton(canvas, paint)
    }

    // 绘制背景
    private fun drawBackground(canvas: Canvas, paint: Paint?) {
        paint!!.color = mBackgroundColor
        canvas.drawRoundRect(
            RectF(0F, 0f, mWidth.toFloat(), mHeight.toFloat()),
            (mHeight / 2).toFloat(),
            (mHeight / 2).toFloat(),
            paint
        )


    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    private fun drawRoundButton(canvas: Canvas, paint: Paint?) {
        val circleMargin = mHeight / 12
        paint!!.color = progressBackgroundColor
        /* canvas.drawRoundRect(
             RectF(
                 circleOffset.toFloat(), 0f, (circleOffset + redRegionWidth).toFloat(),
                 mHeight.toFloat()
             ), (mHeight / 2).toFloat(), (mHeight / 2).toFloat(), paint
         )
 */



       /* canvas.drawCircle(
            (mHeight / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mHeight / 2 - circleMargin).toFloat(),
            paint
        )*/


//        paint.textSize = textSize
//        paint.color = Color.GREEN
        val yCenterPos = (mHeight / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        val startX = (circleOffset
                + (redRegionWidth - paint.measureText(
            sliderText, 0,
            sliderText!!.length
        ).toInt()) / 2)

        val drawable = resources.getDrawable(R.drawable.shape_oval_green, null)
        val bitmapx = Bitmap.createBitmap(drawable.toBitmap())

        canvas.drawBitmap(
            bitmapx,
            circleOffset.toFloat() ,
            yCenterPos.toFloat(),
            paint
        )

//        var inputStream: InputStream
//        inputStream = resources.openRawResource(R.drawable.call_gif)
//        val opts: BitmapFactory.Options = BitmapFactory.Options()
//        var bm: Bitmap? = null
//
////        opts.inJustDecodeBounds = true
////        bm = BitmapFactory.decodeStream(inputStream, null, opts)!!
////        mBitmap = bm
//
//        inputStream = resources.openRawResource(R.drawable.call_gif)
//        mBitmap2 = BitmapFactory.decodeStream(inputStream)
//
//        val w = mBitmap2?.width
//        val h = mBitmap2?.height
//        val pixels = IntArray(w!! * h!!)
//        mBitmap2?.getPixels(pixels, 0, w, 0, 0, w, h);
//        mBitmap3 = Bitmap.createBitmap(
//            pixels, 0, w, w, h,
//            Bitmap.Config.ARGB_8888
//        );
//        mBitmap4 = Bitmap.createBitmap(
//            pixels, 0, w, w, h,
//            Bitmap.Config.ARGB_4444
//        );
//
//        mDrawable = resources.getDrawable(R.drawable.call_gif);
//        mDrawable?.setBounds(150, 20, 300, 100)
//
//        inputStream = resources.openRawResource(R.drawable.call_gif)
//        if (DECODE_STREAM) {
//            mMovie = Movie.decodeStream(inputStream)
//
//        } else {
//            val array: ByteArray = streamToBytes(inputStream)
//            mMovie = Movie.decodeByteArray(array, 0, array.size)
//        }
//
////        canvas.drawBitmap(mBitmap!!, x.toFloat() - 30, y.toFloat() - 50, paint)
//        canvas.drawBitmap(mBitmap2!!, x.toFloat() - 30, y.toFloat() - 50, paint)
//        canvas.drawBitmap(mBitmap3!!, x.toFloat() - 30, y.toFloat() - 50, paint)
//        canvas.drawBitmap(mBitmap4!!, x.toFloat() - 30, y.toFloat() - 50, paint)
//
//        val now :Long = android.os.SystemClock.uptimeMillis();
//        if (mMovieStart == 0L) { // first time
//            mMovieStart = now;
//        }
//        if (mMovie != null) {
//            var dur = mMovie?.duration();
//            if (dur == 0) {
//                dur = 1000;
//            }
//            var relTime = ((now - mMovieStart) % dur!!).toInt()
//            mMovie!!.setTime(relTime);
//            mMovie!!.draw(
//                canvas, (getWidth() - mMovie!!.width()).toFloat(), (getHeight()
//                        - mMovie!!.height()).toFloat()
//            );
//            invalidate();
//
//        }

    }

    private var mBitmap: Bitmap? = null
    private var mBitmap2: Bitmap? = null
    private var mBitmap3: Bitmap? = null
    private var mBitmap4: Bitmap? = null
    private var mDrawable: Drawable? = null
    private val DECODE_STREAM = true
    private var mMovie: Movie? = null
    private var mMovieStart: Long = 0

    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    private fun drawLeftCircleButton(canvas: Canvas, paint: Paint?) {


        val circleMargin = mHeight / 12
        paint!!.color = leftColor
        paint.textSize = textSize
        var alpha = 255

        if (circleOffset < initialCircleOffset) {
            alpha = 255 - (255 - 255.toFloat() / initialCircleOffset.toFloat()
                    * circleOffset).toInt()
            if (alpha <= 0) {
                alpha = 0
            }
        }
        paint.alpha = alpha
        canvas.drawCircle(
            (mHeight / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mHeight / 2 - circleMargin).toFloat(),
            paint
        )

        val x = (mHeight - paint.measureText(listenText, 0, listenText!!.length).toInt()) / 2
        val y = (mHeight / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        paint.color = Color.BLACK
        paint.alpha = alpha

        val drawable = resources.getDrawable(R.drawable.ic_dialpad, null)
        val bitmapx = Bitmap.createBitmap(drawable.toBitmap())
        canvas.drawBitmap(bitmapx, x.toFloat() - 30, y.toFloat() - 50, paint)


//        canvas.drawText(listenText!!, x.toFloat(), y.toFloat(), paint)
    }


    private fun streamToBytes(`is`: InputStream): ByteArray {
        val os = ByteArrayOutputStream(1024)
        val buffer = ByteArray(1024)
        var len: Int
        try {
            while (`is`.read(buffer).also { len = it } >= 0) {
                os.write(buffer, 0, len)
            }
        } catch (e: IOException) {
        }
        return os.toByteArray()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun drawRightCircleButton(canvas: Canvas, paint: Paint?) {
        //画最右边的圆形
        val circleMargin = mHeight / 12
        paint!!.color = rightColor
        paint.textSize = textSize
        var alpha = 255
        if (circleOffset > initialCircleOffset) {
            alpha =
                if (circleOffset - initialCircleOffset >= 255) 0 else 255 - (circleOffset - initialCircleOffset) //实现向右滑动，浅出
        }
        paint.alpha = alpha
        canvas.drawCircle(
            (mWidth - mHeight / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mHeight / 2 - circleMargin).toFloat(),
            paint
        )

        val x = (mWidth - mHeight
                + (mHeight - paint.measureText(shutDownText, 0, shutDownText!!.length).toInt())
                / 2)
        val y = (mHeight / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
        paint.color = Color.BLACK
        paint.alpha = alpha

        val drawable = resources.getDrawable(R.drawable.ic_hold, null)
        val bitmapx = Bitmap.createBitmap(drawable.toBitmap())
        canvas.drawBitmap(bitmapx, x.toFloat() - 30, y.toFloat() - 50, paint)

//        canvas.drawText(shutDownText!!, x.toFloat(), y.toFloat(), paint)
    }

    private fun actionDown(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (x >= circleOffset && x <= circleOffset + redRegionWidth && y >= 0 && y <= mHeight) {
            isDownRight = true
            prevX = event.x.toInt()
            maxOffset = mWidth / 2 - redRegionWidth / 2
        } else {
            isDownRight = false
            if (x > 0 && x < mHeight && y >= 0 && y < mHeight) {
                if (sliderListener != null) {
                    sliderListener!!.onSliderListen()
                }
            }
            if (x > mWidth - mHeight && x < mWidth && y >= 0 && y < mHeight) {
                if (sliderListener != null) {
                    sliderListener!!.onSliderEnd()
                }
            }
        }
    }

    private fun actionMove(event: MotionEvent) {
        if (!isDownRight) {
            return
        }
        val tempOffset = (event.x - prevX).toInt()
        circleOffset = initialCircleOffset + tempOffset
        if (tempOffset > maxOffset) {
            circleOffset = initialCircleOffset + maxOffset
            if (sliderListener != null) {
                sliderListener!!.onSliderEnd()
            }
        } else if (tempOffset < -maxOffset) {
            circleOffset = initialCircleOffset - maxOffset
            if (sliderListener != null) {
                sliderListener!!.onSliderListen()
            }
        }
        invalidate()
    }

    private fun actionUp(event: MotionEvent) {
        if (!isDownRight) {
            return
        }
        if (circleOffset > initialCircleOffset
            && circleOffset != initialCircleOffset + maxOffset
            || circleOffset < initialCircleOffset
            && circleOffset != initialCircleOffset - maxOffset
        ) {
            circleOffset = initialCircleOffset
        }
        invalidate()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CallSliderView
            )
            textSize = typedArray.getDimensionPixelSize(
                R.styleable.CallSliderView_CallSliderView_textSize, 40
            ).toFloat()
            progressBackgroundColor = typedArray.getColor(
                R.styleable.CallSliderView_CallSliderView_progressBackgroundColor,
                Color.RED
            )
            mBackgroundColor = typedArray.getColor(
                R.styleable.CallSliderView_CallSliderView_backgroundColor, 0x0fffffff
            )
            sliderText = typedArray
                .getString(R.styleable.CallSliderView_CallSliderView_text)
            shutDownText = typedArray
                .getString(R.styleable.CallSliderView_CallSliderView_Right_Text)
            listenText = typedArray
                .getString(R.styleable.CallSliderView_CallSliderView_Left_Text)
            leftColor = typedArray.getColor(
                R.styleable.CallSliderView_CallSliderView_Left_BackgroundColor,
                Color.GREEN
            )
            rightColor = typedArray.getColor(
                R.styleable.CallSliderView_CallSliderView_Right_BackgroundColor,
                Color.RED
            )
            typedArray.recycle()
        }
        paint = Paint()
        paint!!.style = Paint.Style.FILL
        paint!!.isAntiAlias = true
        paint!!.strokeWidth = 2f
        paint!!.color = mBackgroundColor
    }

    companion object {
        private val tag = CallSliderView::class.java.simpleName
    }
}