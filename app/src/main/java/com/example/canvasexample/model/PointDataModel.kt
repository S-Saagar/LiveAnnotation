package com.example.canvasexample.model

import com.google.gson.annotations.SerializedName

data class PointDataModel(@SerializedName("shapeType") var shapeType: Int = 0,
                          @SerializedName("motionAction") var motionAction: Int = 0,
                          @SerializedName("screenHeight") var screenHeight: Int = 0,
                          @SerializedName("screenWidth") var screenWidth: Int = 0,
                          @SerializedName("startX") var startX: Float = 0f,
                          @SerializedName("startY") var startY: Float = 0f,
                          @SerializedName("cX") var cX: Float = 0f,
                          @SerializedName("cY") var cY: Float = 0f,
                          @SerializedName("mX") var mX: Float = 0f,
                          @SerializedName("mY") var mY: Float = 0f)