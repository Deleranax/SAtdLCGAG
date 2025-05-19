package com.example.superawesometodolistcatgaggingappgagging.screens.calendar;

import android.content.Context
import android.util.Log
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.coroutines.coroutineContext

private val TAG = "CalendarViewModel"

class CalendarViewModel : ViewModel() {
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
    // Media Attributes
    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl

    // FlowState attributes
    val currentDayStateFlow = snapshotFlow { pagerState.currentPage }.map { pageToDate(it) }

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
                _imageUrl.update {
                    url
                }
            }
        }
    }

    fun dismissCat() {
        _imageUrl.update {
            null
        }
    }
}
