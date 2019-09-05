package com.example.canvasexample.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.model.UserListModel
import com.example.canvasexample.model.UserModel
import com.example.canvasexample.util.*
import com.google.gson.Gson
import io.socket.client.Socket.*
import io.socket.emitter.Emitter

abstract class AbstractActivity : AppCompatActivity() {

    private var initEvents = false
    private var isSocketInit = false

    var socketId = ""
    var usersModel: ArrayList<UserModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSocket()
        initEvent()
    }

    private fun initSocket() {
        if (!isSocketInit) {
            CanvasApplication.connectSocket()
            CanvasApplication.onEvent(EVENT_CONNECT, onConnection(EVENT_CONNECT))
            CanvasApplication.onEvent(EVENT_DISCONNECT, onConnection(EVENT_DISCONNECT))
            CanvasApplication.onEvent(EVENT_RECONNECTING, onConnection(EVENT_RECONNECTING))
            CanvasApplication.onEvent(EVENT_CONNECT_ERROR, onConnection(EVENT_CONNECT_ERROR))
            isSocketInit = true
        }
    }

    private fun initEvent() {
        when (!getLogin()) {
            true -> {
                CanvasApplication.onEvent(EVENT_LOGIN, onUserEvent(EVENT_LOGIN))
            }
        }

        when (getLogin() && !initEvents) {
            true -> {
                CanvasApplication.onEvent(EVENT_JOIN, onUserEvent(EVENT_JOIN))
                CanvasApplication.onEvent(EVENT_LEFT, onUserEvent(EVENT_LEFT))
                CanvasApplication.onEvent(EVENT_USERS, onUserEvent(EVENT_USERS))
                CanvasApplication.onEvent(EVENT_ONLINE, onUserEvent(EVENT_ONLINE))
                CanvasApplication.onEvent(EVENT_OFFLINE, onUserEvent(EVENT_OFFLINE))
                CanvasApplication.onEvent(EVENT_SOCKET_MESSAGE, onUserEvent(EVENT_SOCKET_MESSAGE))
                initEvents = true
            }
        }
    }

    private fun onUserEvent(action: String): Emitter.Listener? {
        return Emitter.Listener {
            runOnUiThread {
                when (action) {

                    EVENT_LOGIN -> {
                        Log.e("AbstractActivity", "mEventLogin " + it[0].toString())
                        if (this is LoginActivity && it[0].toString().contains(getUserId())) {
                            this.onLoginSuccess()
                        }
                    }

                    EVENT_JOIN -> {
                        Log.e("AbstractActivity", "mEventJoin " + it[0].toString())
                        val userModel: UserModel = Gson().fromJson(it[0].toString(), UserModel::class.java)
                        usersModel.add(userModel)
                        if (this is UsersActivity) {
                            this.updateUserList()
                        }
                    }

                    EVENT_LEFT -> {
                        Log.e("AbstractActivity", "mEventLeft " + it[0].toString())
                        val userModel: UserModel = Gson().fromJson(it[0].toString(), UserModel::class.java)
                        usersModel.remove(usersModel.single { it.userId == userModel.userId })
                        if (this is UsersActivity) {
                            this.updateUserList()
                        }
                    }

                    EVENT_ONLINE -> {
                        Log.e("AbstractActivity", "mEventOnline " + it[0].toString())
                        val userModel: UserModel = Gson().fromJson(it[0].toString(), UserModel::class.java)
                        if (this is UsersActivity) {
                            this.userOnline(userModel)
                        } else {
                            usersModel.single { it.userId == userModel.userId }.online = true
                            usersModel.single { it.userId == userModel.userId }.userSocketId = userModel.userSocketId
                        }
                    }

                    EVENT_SOCKET_MESSAGE -> {
                        Log.e("AbstractActivity", "mEventMessage " + it[0].toString())
                    }

                    EVENT_OFFLINE -> {
                        Log.e("AbstractActivity", "mEventOffline " + it[0].toString())
                        val userModel: UserModel = Gson().fromJson(it[0].toString(), UserModel::class.java)
                        if (this is UsersActivity) {
                            this.userOffline(userModel)
                        } else {
                            usersModel.single { it.userId == userModel.userId }.online = false
                        }
                    }

                    EVENT_USERS -> {
                        Log.e("AbstractActivity", "mEventUsers " + it[0].toString())
                        val usrs = Gson().fromJson(it[0].toString(), UserListModel::class.java)
                        usersModel.addAll(usrs.userList)
                        if (this is UsersActivity) {
                            this.updateUserList()
                        }
                    }
                }
            }
        }
    }

    private fun onConnection(event: String): Emitter.Listener? {
        return Emitter.Listener {
            runOnUiThread {
                when (event) {
                    EVENT_CONNECT -> {
                        socketId = CanvasApplication.instance.socket.id()
                    }

                    EVENT_CONNECT_ERROR -> {
                        ToastUtils.showShortToast(this, "Connection error")
                    }

                    EVENT_DISCONNECT -> {
                        ToastUtils.showShortToast(this, "Disconnected")
                    }

                    EVENT_RECONNECTING -> {
                        ToastUtils.showShortToast(this, "Reconnecting")
                    }
                }
            }
        }
    }
}