package com.example.canvasexample.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.R
import com.example.canvasexample.model.UserModel
import com.example.canvasexample.util.EVENT_ONLINE
import com.example.canvasexample.util.EVENT_USERS
import com.example.canvasexample.util.getUserId
import com.example.canvasexample.util.getUserName
import com.google.gson.Gson
import org.json.JSONObject

class UsersActivity : AbstractActivity() {

    private var userData: String? = null

    companion object {
        fun getUserActivity(activity: Activity): Intent {
            return Intent(activity, this::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        val userModel = UserModel(getUserName(), getUserId(), socketId)
        userData = Gson().toJson(userModel)
        CanvasApplication.emitEvent(EVENT_USERS, JSONObject(userData.toString()))
    }

    override fun onResume() {
        super.onResume()
        CanvasApplication.emitEvent(EVENT_ONLINE, JSONObject(userData.toString()))
    }

    fun updateUserList() {
        Log.d("", "Total users= ${usersModel.size}")
    }

    fun userOnline(userModel: UserModel) {
        usersModel.single { it.userId == userModel.userId }.online = true
        usersModel.single { it.userId == userModel.userId }.userSocketId = userModel.userSocketId
    }

    fun userOffline(userModel: UserModel) {
        usersModel.single { it.userId == userModel.userId }.online = false
    }
}