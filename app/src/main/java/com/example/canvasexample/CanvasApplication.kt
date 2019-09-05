package com.example.canvasexample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class CanvasApplication : Application(), Application.ActivityLifecycleCallbacks {

    lateinit var socket: Socket

    companion object {
        lateinit var instance: CanvasApplication

        var mURL = "http://192.168.1.117:3000"

        fun emitEvent(event: String, data: JSONObject) {
            instance.socket.emit(event, data)
        }

        fun onEvent(event: String, listener: Emitter.Listener?) {
            instance.socket.on(event, listener)
        }

        fun connectSocket() {
            instance.socket.connect()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(this)
        try {
            socket = IO.socket(mURL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {
        try {
            socket.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }
}