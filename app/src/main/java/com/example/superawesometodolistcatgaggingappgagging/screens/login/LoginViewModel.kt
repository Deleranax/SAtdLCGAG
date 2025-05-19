package com.example.superawesometodolistcatgaggingappgagging.screens.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.superawesometodolistcatgaggingappgagging.api.TodoApi
import com.example.superawesometodolistcatgaggingappgagging.database.PreferenceTable
import com.example.superawesometodolistcatgaggingappgagging.database.TodosApplication
import com.example.superawesometodolistcatgaggingappgagging.database.TodosDatabase
import com.example.superawesometodolistcatgaggingappgagging.database.TodosRepository
import com.example.superawesometodolistcatgaggingappgagging.screens.calendar.todoApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

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

    private var loading = MutableStateFlow(true)
    val loadingStateFlow: StateFlow<Boolean> = loading

    fun login(username: String, password: String, onLogin: (Boolean) -> Unit) {
        loading.update { true }

        viewModelScope.launch {
            try {
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()

                val result = TodoApi.retrofitService?.login(body)

                loading.update { false }

                onLogin(result != null && result.success == 1)
            } catch (e: Exception) {
                Log.e("Worker", "Network error: ${e.message}")

                loading.update { false }

                onLogin(false)
            }
        }
    }

    fun register(username: String, password: String, onRegister: (Boolean) -> Unit) {
        loading.update { true }

        viewModelScope.launch {
            try {
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("password", password)
                    .build()

                val result = TodoApi.retrofitService?.register(body)

                loading.update { false }

                onRegister(result != null && result.success == 1)
            } catch (e: Exception) {
                Log.e("Worker", "Network error: ${e.message}")

                loading.update { false }

                onRegister(false)
            }
        }
    }

    suspend fun checkSession(onSignIn: () -> Unit) {
        try {
            val result = TodoApi.retrofitService?.info()

            if (result != null && result.success == 1 && result.data.loggedIn) {
                onSignIn()
                todoRepository.setPreference(PreferenceTable("login", "OK"))
            } else {
                loading.update { false }
            }
        } catch (e: Exception) {
            val result = todoRepository.getPreference("login")

            Log.i("TAG", result.toString())

            if (result == "OK") {
                onSignIn()
            } else {
                loading.update { false }
            }
        }
    }
}