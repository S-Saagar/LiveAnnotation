//package com.example.canvasexample.view
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.graphics.Paint.ANTI_ALIAS_FLAG
//import android.graphics.Path
//import android.util.AttributeSet
//import android.util.DisplayMetrics
//import android.view.MotionEvent
//import android.view.View
//import com.example.canvasexample.model.PathModel
//import com.example.canvasexample.R
//import kotlin.math.abs
//import kotlin.math.max
//import kotlin.math.min
//
//
//class CanvasView : View {
//    private var displayMetrics: DisplayMetrics
//    private var mTag: String = CanvasView::class.java.simpleName
//
//    private var aspectRatio: Float = 0F
//    private var maxDimen: Float = 0F
//    private var minDimen: Float = 0F
//    private lateinit var paint: Paint
//    private lateinit var mPathPaint: Paint
//
//    private var mX: Float = 0.0f
//    private var mY: Float = 0.0f
//
//    private var mPaths: ArrayList<PathModel> = ArrayList()
//    private var mPath: Path = Path()
//
//    constructor(context: Context) : super(context) {
//        initPaint()
//        initPathPaint()
//        displayMetrics = DisplayMetrics()
//    }
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        initPaint()
//        initPathPaint()
//        displayMetrics = DisplayMetrics()
//    }
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        initPaint()
//        initPathPaint()
//        displayMetrics = DisplayMetrics()
//    }
//
//    private fun initPaint() {
//        paint = Paint(ANTI_ALIAS_FLAG)
//        paint.color = resources.getColor(R.color.myPaintColor)
//        paint.strokeWidth = resources.getDimension(R.dimen.stroke_width)
//        paint.strokeJoin = Paint.Join.ROUND
//        paint.strokeCap = Paint.Cap.ROUND
//    }
//
//    private fun initPathPaint() {
//        mPathPaint = Paint(ANTI_ALIAS_FLAG)
//        mPathPaint.color = resources.getColor(R.color.myPaintColor)
//        mPathPaint.style = Paint.Style.STROKE
//        mPathPaint.strokeWidth = resources.getDimension(R.dimen.stroke_width)
//        mPathPaint.strokeJoin = Paint.Join.ROUND
//        mPathPaint.strokeCap = Paint.Cap.ROUND
//    }
//
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//
//        minDimen = min(width, height).toFloat()
//        maxDimen = max(width, height).toFloat()
//
//        aspectRatio = maxDimen / minDimen
//
//        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
//
//        canvas!!.drawPath(mPath, mPathPaint)
//
//        try {
//            for (path in mPaths) {
//                if (path.paint.alpha < 5) {
//                    mPaths.remove(path)
//                }
//            }
//        } catch (e: ConcurrentModificationException) {
//            e.printStackTrace()
//        }
//
//        try {
//            for (path in mPaths) {
//                canvas.drawPath(path.path, path.paint)
//
//                if (path.paint.alpha > 0) {
//                    path.paint.alpha = path.paint.alpha - 6
//                } else {
//                    mPaths.remove(path)
//                }
//            }
//        } catch (e: ConcurrentModificationException) {
//            e.printStackTrace()
//        }
//        if (mPaths.size > 0) {
//            invalidate()
//        }
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event!!.action) {
//            MotionEvent.ACTION_DOWN -> {
//                onTouchStart(event.x, event.y)
//            }
//
//            MotionEvent.ACTION_MOVE -> {
//                onTouchMove(event.x, event.y)
//            }
//
//            MotionEvent.ACTION_UP -> {
//                onTouchEnd(event.x, event.y)
//            }
//
//        }
//        invalidate()
//        return true
//    }
//
//    private fun onTouchStart(x: Float, y: Float) {
//        initPathPaint()
//        mX = x
//        mY = y
//        mPath.moveTo(mX, mY)
//    }
//
//    private fun onTouchMove(x: Float, y: Float) {
//        val dx = abs(x - mX)
//        val dy = abs(y - mY)
//        if (dx >= 1 || dy >= 1) {
//            mPath.quadTo(mX, mY, ((x + mX)) / 2, ((y + mY)) / 2)
//        }
//        mX = x
//        mY = y
//    }
//
//    private fun onTouchEnd(x: Float, y: Float) {
//        mX = x
//        mY = y
//        mPath.lineTo(mX, mY)
//        val mPathModel = PathModel(mPath, mPathPaint)
//        mPaths.add(mPathModel)
//        mPath = Path()
//    }
//
//    /**
//     * This method converts dp unit to equivalent pixels, depending on device density.
//     *
//     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
//     * @param context Context to get resources and device specific display metrics
//     * @return A float value to represent px equivalent to dp depending on device density
//     */
//    fun convertDpToPixel(dp: Float): Float {
//        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
//    }
//
//    /**
//     * This method converts device specific pixels to density independent pixels.
//     *
//     * @param px A value in px (pixels) unit. Which we need to convert into db
//     * @param context Context to get resources and device specific display metrics
//     * @return A float value to represent dp equivalent to px value
//     */
//    fun convertPixelsToDp(px: Float): Float {
//        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
//    }
//}
