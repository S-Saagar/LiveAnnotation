package com.example.canvasexample.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.CanvasApplication.Companion.mURL
import com.example.canvasexample.R
import com.example.canvasexample.model.MediaModel
import com.example.canvasexample.model.PointDataModel
import com.example.canvasexample.util.PICK_FILE_REQUEST_CODE
import com.example.canvasexample.util.READ_STORAGE_PERMISSION_REQUEST_CODE
import com.example.canvasexample.util.ToastUtils
import com.example.canvasexample.view.AnnotationCanvas
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_annotation.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class AnnotationActivity : AbstractActivity() {
    var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annotation)

        canvas_view.shapeType = AnnotationCanvas.Shapes.LINE

        CanvasApplication.instance.socket.on("message", onMessage())
        CanvasApplication.instance.socket.on("image", onImage())

        fab_pick_image.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_REQUEST_CODE)
                } else {
                    pickImage()
                }
            } else {
                pickImage()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    private fun onImage(): Emitter.Listener? {
        return Emitter.Listener {
            runOnUiThread {
                Log.e("Image", it[0].toString())
                val mediaModel: MediaModel = Gson().fromJson(it[0].toString(), MediaModel::class.java)
                downloadImage(mediaModel)
            }
        }
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

    private fun downloadImage(mediaModel: MediaModel) {
        Log.d("AnnotationActivity", "URL-> ${mediaModel.imageUrl}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        when (requestCode) {
            PICK_FILE_REQUEST_CODE -> {
                try {
                    val selectedImage = data.data
                    val filePath = arrayOf(MediaStore.Images.Media.DATA)
                    val c = contentResolver.query(selectedImage!!, filePath, null, null, null)
                    c!!.moveToFirst()
                    val columnIndex = c.getColumnIndex(filePath[0])
                    path = c.getString(columnIndex)
                    c.close()
                    uploadImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun uploadImage() {
        print(path)
        val f = File(path!!)

        Ion.with(this)
                .load("$mURL + /upload")
                .uploadProgress { uploaded, total ->
                    Log.i("AnnotationActivity", "Progress-> $uploaded / $total")
                }
                .setMultipartFile("image", f)
                .asString()
                .withResponse()
                .setCallback { e, result ->
                    if (e == null) {
                        ToastUtils.showShortToast(this, "Error")
                        return@setCallback
                    }
                    try {
                        val jObj = JSONObject(result.result)
                        ToastUtils.showShortToast(this, jObj.getString("response"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_STORAGE_PERMISSION_REQUEST_CODE -> {
                when (grantResults[0]) {
                    PERMISSION_GRANTED -> {
                        pickImage()
                    }
                    else -> {
                        val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        if (showRationale) {
                            ToastUtils.showShortToast(this, "Allow permission to pick image from gallery")
                        } else {
                            ToastUtils.showShortToast(this, "Allow permission from setting")
                            openAppSettings(this)
                        }
                    }
                }
            }
        }
    }

    private fun openAppSettings(context: Activity?) {
        if (context == null) {
            return
        }
        val i = Intent()
        i.action = ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }
}
