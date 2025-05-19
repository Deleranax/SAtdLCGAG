package com.example.superawesometodolistcatgaggingappgagging.api

import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    val data: TodoListData,
    val success: Int,
    @SerializedName("exec_time")
    val execTime: Float
)

data class TodoListData(
    val todos: List<TodoListItem>
)

data class TodoListItem(
    val id: String,
    val name: String,
    val desc: String,
    val time: Long
)