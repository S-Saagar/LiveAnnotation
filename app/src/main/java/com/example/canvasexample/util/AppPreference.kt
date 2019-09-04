package com.example.canvasexample.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.annotation.NonNull
import com.example.canvasexample.CanvasApplication
import com.example.canvasexample.R


private const val key_isLogin = "IS_LOGIN"
private const val key_userId = "USER_ID"
private const val key_userName = "USER_NAME"

fun Context.setLogin(@NonNull isLogin: Boolean) {
    getSharedPreferences(CanvasApplication.instance.resources.getString(R.string.app_name), MODE_PRIVATE).edit().putBoolean(key_isLogin, isLogin).apply()
}

fun Context.getLogin(): Boolean {
    return getSharedPreferences(CanvasApplication.instance.resources.getString(R.string.app_name), MODE_PRIVATE).getBoolean(key_isLogin, false)
}

fun Context.setUserId(@NonNull userId: String) {
    getSharedPreferences(CanvasApplication.instance.resources.getString(R.string.app_name), MODE_PRIVATE).edit().putString(key_userId, userId).apply()
}

fun Context.getUserId(): String {
    return getSharedPreferences(CanvasApplication.instance.resources.getString(R.string.app_name), MODE_PRIVATE).getString(key_userId, "")!!
}

fun Context.setUserName(@NonNull userName: String) {
    getSharedPreferences(CanvasApplication.instance.resources.getString(R.string.app_name), MODE_PRIVATE).edit().putString(key_userName, userName).apply()
}

fun Context.getUserName(): String {
    return getSharedPreferences(CanvasApplication.instance.resources.getString(R.string.app_name), MODE_PRIVATE).getString(key_userName, "")!!
}