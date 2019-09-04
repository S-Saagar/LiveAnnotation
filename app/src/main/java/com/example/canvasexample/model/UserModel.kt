package com.example.canvasexample.model

import com.google.gson.annotations.SerializedName

data class UserModel(@SerializedName("userName") var userName: String,

                     @SerializedName("userId") var userId: String,

                     @SerializedName("userSocketId") var userSocketId: String) {
    var online: Boolean = true
}