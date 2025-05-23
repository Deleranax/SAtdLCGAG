package com.example.superawesometodolistcatgaggingappgagging.screens.calendar;

import android.content.Context
import android.util.Log
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.superawesometodolistcatgaggingappgagging.api.TodoApi
import com.example.superawesometodolistcatgaggingappgagging.api.TodoFetchWorker
import com.example.superawesometodolistcatgaggingappgagging.database.TodoTable
import com.example.superawesometodolistcatgaggingappgagging.database.TodosApplication
import com.example.superawesometodolistcatgaggingappgagging.database.TodosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import okhttp3.MultipartBody
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val TAG = "CalendarViewModel"

object CalendarViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CalendarViewModel(
                todoApplication().container.todosRepository
            )
        }
    }
}

fun CreationExtras.todoApplication(): TodosApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TodosApplication)


class CalendarViewModel(val todoRepository: TodosRepository) : ViewModel() {
    // Const
    companion object {
        const val PAGE_COUNT = Int.MAX_VALUE
        const val TODAY_PAGE = PAGE_COUNT / 2
    }

    // Attributes
    val today: LocalDate = LocalDate.now()
    val pagerState = PagerState(
        currentPage = TODAY_PAGE,
        pageCount = { PAGE_COUNT }
    )

    // Private attributes
    private val imageUrl = MutableStateFlow<String?>(null)

    // FlowState attributes
    val currentDayStateFlow = snapshotFlow { pagerState.currentPage }.map { pageToDate(it) }
    val imageUrlStateFlow: StateFlow<String?> = imageUrl
    val todos: StateFlow<List<TodoTable>> = todoRepository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun refresh(context: Context) {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<TodoFetchWorker>().build()
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
    }

    suspend fun logout() {
        TodoApi.cookieStore?.removeAll()
        todoRepository.deleteAll()
    }

    suspend fun removeTodo(context: Context, id: String): Boolean {
        try {
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .build()
            TodoApi.retrofitService?.removeTodo(body)

            refresh(context)

            return true
        } catch (e: Exception) {
            Log.e("Worker", "Network error: ${e.message}")
        }

        return false
    }

    fun pageToDate(page: Int): LocalDate {
        return today.plusDays((page - TODAY_PAGE).toLong())
    }

    fun dateToPage(date: LocalDate): Int {
        return TODAY_PAGE + today.until(date, ChronoUnit.DAYS).toInt()
    }

    suspend fun select(page: Int) {
        pagerState.animateScrollToPage(page)
    }

    suspend fun select(date: LocalDate) {
        pagerState.animateScrollToPage(dateToPage(date))
    }

    fun fetchNewCat(context: Context){
        val workRequest = OneTimeWorkRequestBuilder<CatWorker>().build()
        val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)

        workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                val url = workInfo.outputData.getString("imageUrl")
                //Log.d(TAG, url.toString())
                imageUrl.update { url }
            }
        }
    }

    fun dismissCat() {
        imageUrl.update { null }
    }
}
