package com.example.superawesometodolistcatgaggingappgagging

class ToDo_Item(day: Int, month: Int, title: String, description: String? = null) {
    var date_day: Int
    var date_month: Int

    var title: String
    var description: String?

    init{
        date_day = day
        date_month = month
        this.title = title
        this.description = description
    }

}