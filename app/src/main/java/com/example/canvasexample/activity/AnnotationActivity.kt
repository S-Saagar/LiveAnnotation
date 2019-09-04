package com.example.canvasexample.activity

import android.os.Bundle
import android.util.Log
import com.example.canvasexample.view.AnnotationCanvas
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.model.PointDataModel
import com.example.canvasexample.R
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_annotation.*

class AnnotationActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annotation)

        canvas_view.shapeType = AnnotationCanvas.Shapes.LINE

        CanvasApplication.instance.socket.on("message", onMessage())
    }

    private fun onMessage(): Emitter.Listener? {
        return Emitter.Listener {
            runOnUiThread {
                Log.e("Message", it[0].toString())
                val points: PointDataModel = Gson().fromJson(it[0].toString(), PointDataModel::class.java)
                canvas_view.receiveData(points)
            }
        }
    }
}
