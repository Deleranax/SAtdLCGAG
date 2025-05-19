package com.example.superawesometodolistcatgaggingappgagging.screens.task

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.superawesometodolistcatgaggingappgagging.api.TodoApi
import com.example.superawesometodolistcatgaggingappgagging.api.TodoFetchWorker
import com.example.superawesometodolistcatgaggingappgagging.database.TodosApplication
import com.example.superawesometodolistcatgaggingappgagging.database.TodosRepository
import com.example.superawesometodolistcatgaggingappgagging.screens.calendar.todoApplication
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

object TaskViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskViewModel(
                todoApplication().container.todosRepository
            )
        }
    }
}

fun CreationExtras.todoApplication(): TodosApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TodosApplication)

class TaskViewModel(val todoRepository: TodosRepository) : ViewModel() {



    fun addTodo(context: Context, name: String, desc: String, time: Long) {
        viewModelScope.launch {
            try {
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", name)
                    .addFormDataPart("desc", desc)
                    .addFormDataPart("time", time.toString())
                    .build()
                TodoApi.retrofitService.addTodo(body)
                val oneTimeWorkRequest = OneTimeWorkRequestBuilder<TodoFetchWorker>().build()
                WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
            } catch (e: Exception) {
                Log.e("Worker", "Network error: ${e.message}")
            }
        }
    }
}