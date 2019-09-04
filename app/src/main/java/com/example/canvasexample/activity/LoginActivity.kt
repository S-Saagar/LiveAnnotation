package com.example.canvasexample.activity

import android.os.Bundle
import android.widget.Toast
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.R
import com.example.canvasexample.model.UserModel
import com.example.canvasexample.util.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.util.*

class LoginActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLogin()) {
            startActivity(UsersActivity.getUserActivity(this))
            finish()
        }
        setContentView(R.layout.activity_login)

        setUserId(UUID.randomUUID().toString())

        btnLogin.setOnClickListener {
            if (edtUserName.text.toString().isEmpty()) {
                Toast.makeText(this, "User name require", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (edtUserName.text.toString().contains(" ")) {
                Toast.makeText(this, "Space not allowed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (edtUserName.text.toString().length < 4) {
                Toast.makeText(this, "User name length must be greater then 3", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userModel = UserModel(edtUserName.text.toString(), getUserId(), socketId)

            val userData = Gson().toJson(userModel)

            CanvasApplication.emitEvent(EVENT_LOGIN, JSONObject(userData))
        }
    }

    fun onLoginSuccess() {
        setUserName(edtUserName.text.toString())
        setLogin(true)
        startActivity(UsersActivity.getUserActivity(this))
        finish()
    }
}
