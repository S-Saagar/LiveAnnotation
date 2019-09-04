package com.example.canvasexample.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.R
import com.example.canvasexample.model.PathModel
import com.example.canvasexample.model.PointDataModel
import com.google.gson.Gson
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.sqrt


class AnnotationCanvas : View {

    private var mPaths: ArrayList<PathModel> = ArrayList()
    private var mOppPaths: ArrayList<PathModel> = ArrayList()
    private lateinit var mPath: Path
    private lateinit var mOppPath: Path
    var shapeType = Shapes.LINE

    private var mX: Float = 0.0f
    private var _mX: Float = 0.0f
    private var mY: Float = 0.0f
    private var _mY: Float = 0.0f

    private var cX: Float = 0.0f
    private var _cX: Float = 0.0f
    private var cY: Float = 0.0f
    private var _cY: Float = 0.0f

    private var startX: Float = 0.0f
    private var _startX: Float = 0.0f
    private var startY: Float = 0.0f
    private var _startY: Float = 0.0f

    private var isGridInit = false

    constructor(context: Context?) : super(context) {
        //initPaint(R.color.myPaintColor)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        //initPaint(R.color.myPaintColor)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //initPaint(R.color.myPaintColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (path in mPaths) {
            canvas.drawPath(path.path, path.paint)
        }

        for (path in mOppPaths) {
            canvas.drawPath(path.path, path.paint)
        }

        val paint = initPaint(R.color.colorPrimaryDark)
        paint.strokeWidth = 2f
        if (!isGridInit) {
            paint.pathEffect = DashPathEffect(floatArrayOf(15f, 25f), 0f)
            isGridInit = true
        }
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchStart(event.x, event.y)
                sendData(event)
            }

            MotionEvent.ACTION_MOVE -> {
                onTouchMove(event.x, event.y)
                sendData(event)
            }

            MotionEvent.ACTION_UP -> {
                onTouchEnd()
                sendData(event)
            }

        }
        invalidate()
        return true
    }

    private fun sendData(event: MotionEvent) {
        val pointModel: PointDataModel
        var shapeTypeValue = Shapes.LINE.value
        when (shapeType.value) {
            Shapes.LINE.value -> {
                shapeTypeValue = Shapes.LINE.value
            }
            Shapes.POINTER.value -> {
                shapeTypeValue = Shapes.POINTER.value
            }
            Shapes.CIRCLE.value -> {
                shapeTypeValue = Shapes.CIRCLE.value
            }
            Shapes.OVAL.value -> {
                shapeTypeValue = Shapes.OVAL.value
            }
            Shapes.SQUARE.value -> {
                shapeTypeValue = Shapes.SQUARE.value
            }
            Shapes.TEXT.value -> {
                shapeTypeValue = Shapes.TEXT.value
            }
        }
        pointModel = PointDataModel(shapeTypeValue, event.action, height, width, startX, startY, cX, cY, event.x, event.y)
        val points = Gson().toJson(pointModel)
        CanvasApplication.emitEvent("message", JSONObject(points))
    }

    fun receiveData(pointDataModel: PointDataModel) {
        drawPoints(pointDataModel)
    }

    private fun drawPoints(pointDataModel: PointDataModel) {
        when (pointDataModel.motionAction) {
            MotionEvent.ACTION_DOWN -> {
                oppTouchStart(pointDataModel)
            }

            MotionEvent.ACTION_MOVE -> {
                oppTouchMove(pointDataModel)
            }

            MotionEvent.ACTION_UP -> {
                oppTouchEnd(pointDataModel)
            }

        }
        invalidate()
    }

    private fun oppTouchStart(pointDataModel: PointDataModel) {
        _mX = (pointDataModel.mX * width) / pointDataModel.screenWidth
        _mY = (pointDataModel.mY * height) / pointDataModel.screenHeight
        when (pointDataModel.shapeType) {
            Shapes.LINE.value -> {
                mOppPath = Path()
                mOppPath.moveTo(_mX, _mY)
                val mPathModel = PathModel(mOppPath, initPaint(R.color.oppPaintColor))
                mOppPaths.add(mPathModel)
            }
            Shapes.POINTER.value -> {
                mOppPath = Path()
                mOppPath.addCircle(_mX, _mY, resources.getDimension(R.dimen.pointer_circle_rad), Path.Direction.CCW)
                val mPathModel = PathModel(mOppPath, initPaint(R.color.oppPaintColor))
                mOppPaths.add(mPathModel)
            }
            Shapes.CIRCLE.value -> {
                _cX = (pointDataModel.cX * width) / pointDataModel.screenWidth
                _cY = (pointDataModel.cY * height) / pointDataModel.screenHeight
                mOppPath = Path()
                mOppPath.addCircle(_cX, _cY, 0f, Path.Direction.CCW)
                val mPathModel = PathModel(mOppPath, initPaint(R.color.oppPaintColor))
                mOppPaths.add(mPathModel)
            }
            Shapes.OVAL.value -> {
                _startX = (pointDataModel.startX * width) / pointDataModel.screenWidth
                _startY = (pointDataModel.startY * height) / pointDataModel.screenHeight
                mOppPath = Path()
                mOppPath.addOval(RectF(_startX, _startY, _startX, _startY), Path.Direction.CW)
                val mPathModel = PathModel(mOppPath, initPaint(R.color.oppPaintColor))
                mOppPaths.add(mPathModel)
            }
            Shapes.SQUARE.value -> {
                _startX = (pointDataModel.startX * width) / pointDataModel.screenWidth
                _startY = (pointDataModel.startY * height) / pointDataModel.screenHeight
                mOppPath = Path()
                mOppPath.addRect(RectF(_startX, _startY, _startX, _startY), Path.Direction.CW)
                val mPathModel = PathModel(mOppPath, initPaint(R.color.oppPaintColor))
                mOppPaths.add(mPathModel)
            }
            Shapes.TEXT.value -> {

            }
        }
    }

    private fun oppTouchMove(pointDataModel: PointDataModel) {
        when (pointDataModel.shapeType) {
            Shapes.LINE.value -> {
                val dx = abs(((pointDataModel.mX * width) / pointDataModel.screenWidth) - _mX)
                val dy = abs(((pointDataModel.mY * height) / pointDataModel.screenHeight) - _mY)
                if (dx >= 1 || dy >= 1) {
                    mOppPath.quadTo(_mX, _mY, ((((pointDataModel.mX * width) / pointDataModel.screenWidth) + _mX)) / 2, (((pointDataModel.mY * height) / pointDataModel.screenHeight) + _mY) / 2)
                }
            }
            Shapes.POINTER.value -> {
                mOppPath.reset()
                mOppPath.addCircle(_mX, _mY, resources.getDimension(R.dimen.pointer_circle_rad), Path.Direction.CCW)
            }
            Shapes.CIRCLE.value -> {
                val rad = sqrt(((_mX - _cX).times(_mX - _cX)) + ((_mY - _cY).times(_mY - _cY)))
                mOppPath.reset()
                mOppPath.addCircle(_cX, _cY, rad, Path.Direction.CCW)
            }
            Shapes.OVAL.value -> {
                mOppPath.reset()
                mOppPath.addOval(RectF(_startX, _startY, _mX, _mY), Path.Direction.CW)
            }
            Shapes.SQUARE.value -> {
                mOppPath.reset()
                mOppPath.addRect(RectF(_startX, _startY, _mX, _mY), Path.Direction.CW)
            }
            Shapes.TEXT.value -> {

            }
        }
        _mX = (pointDataModel.mX * width) / pointDataModel.screenWidth
        _mY = (pointDataModel.mY * height) / pointDataModel.screenHeight
    }

    private fun oppTouchEnd(pointDataModel: PointDataModel) {
        when (pointDataModel.shapeType) {
            Shapes.LINE.value -> {
                mOppPath.lineTo(_mX, _mY)
            }
            Shapes.POINTER.value -> {
                mOppPath.reset()
                mOppPath.addCircle(_mX, _mY, resources.getDimension(R.dimen.pointer_circle_rad), Path.Direction.CCW)
                mOppPath.reset()
            }
            Shapes.CIRCLE.value -> {
                val rad = sqrt((_mX - _cX).times(_mX - _cX) + (_mY - _cY).times(_mY - _cY))
                mOppPath.reset()
                mOppPath.addCircle(_cX, _cY, rad, Path.Direction.CCW)
            }
            Shapes.OVAL.value -> {
                mOppPath.reset()
                mOppPath.addOval(RectF(_startX, _startY, _mX, _mY), Path.Direction.CW)
            }
            Shapes.SQUARE.value -> {
                mOppPath.reset()
                mOppPath.addRect(RectF(_startX, _startY, _mX, _mY), Path.Direction.CW)
            }
            Shapes.TEXT.value -> {

            }
        }
        _mX = 0f
        _mY = 0f
        _cX = 0f
        _cY = 0f
        _startX = 0f
        _startY = 0f
    }

    private fun onTouchStart(x: Float, y: Float) {
        mX = x
        mY = y
        when (shapeType.value) {
            Shapes.LINE.value -> {
                mPath = Path()
                mPath.moveTo(mX, mY)
                val mPathModel = PathModel(mPath, initPaint(R.color.myPaintColor))
                mPaths.add(mPathModel)
            }
            Shapes.POINTER.value -> {
                mPath = Path()
                mPath.addCircle(mX, mY, resources.getDimension(R.dimen.pointer_circle_rad), Path.Direction.CCW)
                val mPathModel = PathModel(mPath, initPaint(R.color.myPaintColor))
                mPaths.add(mPathModel)
            }
            Shapes.CIRCLE.value -> {
                cX = x
                cY = y
                mPath = Path()
                mPath.addCircle(cX, cY, 0f, Path.Direction.CCW)
                val mPathModel = PathModel(mPath, initPaint(R.color.myPaintColor))
                mPaths.add(mPathModel)
            }
            Shapes.OVAL.value -> {
                startX = x
                startY = y
                mPath = Path()
                mPath.addOval(RectF(startX, startY, startX, startY), Path.Direction.CW)
                val mPathModel = PathModel(mPath, initPaint(R.color.myPaintColor))
                mPaths.add(mPathModel)
            }
            Shapes.SQUARE.value -> {
                startX = x
                startY = y
                mPath = Path()
                mPath.addRect(RectF(startX, startY, startX, startY), Path.Direction.CW)
                val mPathModel = PathModel(mPath, initPaint(R.color.myPaintColor))
                mPaths.add(mPathModel)
            }
            Shapes.TEXT.value -> {

            }
        }
    }

    private fun onTouchMove(x: Float, y: Float) {
        when (shapeType.value) {
            Shapes.LINE.value -> {
                val dx = abs(x - mX)
                val dy = abs(y - mY)
                if (dx >= 1 || dy >= 1) {
                    mPath.quadTo(mX, mY, ((x + mX)) / 2, ((y + mY)) / 2)
                }
            }
            Shapes.POINTER.value -> {
                mPath.reset()
                mPath.addCircle(mX, mY, resources.getDimension(R.dimen.pointer_circle_rad), Path.Direction.CCW)
            }
            Shapes.CIRCLE.value -> {
                val rad = sqrt(((mX - cX).times(mX - cX)) + ((mY - cY).times(mY - cY)))
                mPath.reset()
                mPath.addCircle(cX, cY, rad, Path.Direction.CCW)
            }
            Shapes.OVAL.value -> {
                mPath.reset()
                mPath.addOval(RectF(startX, startY, mX, mY), Path.Direction.CW)
            }
            Shapes.SQUARE.value -> {
                mPath.reset()
                mPath.addRect(RectF(startX, startY, mX, mY), Path.Direction.CW)
            }
            Shapes.TEXT.value -> {

            }
        }
        mX = x
        mY = y
    }

    private fun onTouchEnd() {
        when (shapeType.value) {
            Shapes.LINE.value -> {
                mPath.lineTo(mX, mY)
            }
            Shapes.POINTER.value -> {
                mPath.reset()
                mPath.addCircle(mX, mY, resources.getDimension(R.dimen.pointer_circle_rad), Path.Direction.CCW)
                mPath.reset()
            }
            Shapes.CIRCLE.value -> {
                val rad = sqrt((mX - cX).times(mX - cX) + (mY - cY).times(mY - cY))
                mPath.reset()
                mPath.addCircle(cX, cY, rad, Path.Direction.CCW)
            }
            Shapes.OVAL.value -> {
                mPath.reset()
                mPath.addOval(RectF(startX, startY, mX, mY), Path.Direction.CW)
            }
            Shapes.SQUARE.value -> {
                mPath.reset()
                mPath.addRect(RectF(startX, startY, mX, mY), Path.Direction.CW)
            }
            Shapes.TEXT.value -> {

            }
        }
        mX = 0f
        mY = 0f
        cX = 0f
        cY = 0f
        startX = 0f
        startY = 0f
    }

    private fun initPaint(color: Int): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = ContextCompat.getColor(context, color)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.getDimension(R.dimen.stroke_width)
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        return paint
    }

    /*fun resetCanvas() {
        mPaths.clear()
        mOppPaths.clear()
        mX = 0f
        mY = 0f
        cX = 0f
        cY = 0f
        startX = 0f
        startY = 0f
        mPath = Path()
        mOppPath = Path()
    }*/

    enum class Shapes(var value: Int) {
        LINE(0), POINTER(1), CIRCLE(2), OVAL(3), SQUARE(4), TEXT(5)
    }
}