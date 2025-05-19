package com.example.superawesometodolistcatgaggingappgagging.api

import com.google.gson.annotations.SerializedName

data class GenericResponse(
    val success: Int,
    @SerializedName("exec_time")
    val execTime: Float
)
