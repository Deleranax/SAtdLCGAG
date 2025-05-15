package com.example.superawesometodolistcatgaggingappgagging.screens.calendar

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class CatWorker(
    appContext: Context,
    workerParams: WorkerParameters
): Worker(appContext, workerParams) {
    override fun doWork(): Result {


        val imageUrl = "https://cataas.com/cat/says/${getRandomInspiration()}?timestamp=${System.currentTimeMillis()}"
        //val imageUrl = "https://cataas.com/cat/says/${getRandomInspiration()}"
        return Result.success(workDataOf("imageUrl" to imageUrl))
    }

    fun getRandomInspiration(): String{
        val inspirations = listOf<String>(
            "This was a requirement",
            "Hang in there!",
            "Almost done!",
            "Enjoy the weekend!",
            "Don't die!",
            "Cat gagging",
            "Don't talk to me till I've had my coffee",
            "Nothing says work like red wine!",
            "No, I am not a dog person",
            "Lazy Sunday coming up!",
            "So close!",
            "Today doesn't look too bad"
        )
        return ""
    }
}