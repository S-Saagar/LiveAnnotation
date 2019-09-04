package com.example.canvasexample.model

import com.google.gson.annotations.SerializedName

data class UserListModel(@SerializedName("userList") val userList: ArrayList<UserModel>)