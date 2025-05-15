package com.example.superawesometodolistcatgaggingappgagging.screens.calendar

import com.example.superawesometodolistcatgaggingappgagging.screens.task.ToDo_Item

class Day(day: Int, month: Int) {
    var day: Int
    var month: Int

    val todoList: MutableList<ToDo_Item> = mutableListOf()

    init{
        this.day = day
        this.month = month
    }

    fun addItem(item: ToDo_Item){
        todoList.add(item)
    }

    fun removeItem(item: ToDo_Item){
        todoList.remove(item)
    }
}