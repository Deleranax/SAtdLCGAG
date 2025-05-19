package com.example.superawesometodolistcatgaggingappgagging.screens.login

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

enum class LoginResult {
    NONE, LOADING, INCORRECT, FAILED, CORRECT
}

object LoginViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                todoApplication().container.todosRepository
            )
        }
    }
}

fun CreationExtras.todoApplication(): TodosApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TodosApplication)

class LoginViewModel(val todoRepository: TodosRepository) : ViewModel() {

    var loginStateFlow = MutableStateFlow(LoginResult.NONE)

    fun login(context: Context, username: String, password: String) {
        loginStateFlow.update {
            LoginResult.LOADING
        }

        viewModelScope.launch {
            try {
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()
                val result = TodoApi.retrofitService.login(body)

                if (result.success == 1) {
                    loginStateFlow.update {
                        LoginResult.CORRECT
                    }
                } else {
                    loginStateFlow.update {
                        LoginResult.INCORRECT
                    }
                }
            } catch (e: Exception) {
                Log.e("Worker", "Network error: ${e.message}")
                loginStateFlow.update {
                    LoginResult.FAILED
                }
            }
        }
    }

    fun register(context: Context, username: String, password: String) {
        loginStateFlow.update {
            LoginResult.LOADING
        }

        viewModelScope.launch {
            try {
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()
                val result = TodoApi.retrofitService.register(body)

                if (result.success == 1) {
                    loginStateFlow.update {
                        LoginResult.CORRECT
                    }
                } else {
                    loginStateFlow.update {
                        LoginResult.INCORRECT
                    }
                }
            } catch (e: Exception) {
                Log.e("Worker", "Network error: ${e.message}")
                loginStateFlow.update {
                    LoginResult.FAILED
                }
            }
        }
    }
}