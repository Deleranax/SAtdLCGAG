package com.example.superawesometodolistcatgaggingappgagging.api

import com.google.gson.annotations.SerializedName

data class AuthInfoResponse(
    val data: AuthInfoData,
    val success: Int,
    @SerializedName("exec_time")
    val execTime: Float
)

data class AuthInfoData(
    @SerializedName("logged_in")
    val loggedIn: Boolean
)