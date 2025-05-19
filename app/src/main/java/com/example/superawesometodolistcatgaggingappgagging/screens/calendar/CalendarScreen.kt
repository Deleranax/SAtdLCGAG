package com.example.superawesometodolistcatgaggingappgagging.screens.calendar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.superawesometodolistcatgaggingappgagging.R
import com.example.superawesometodolistcatgaggingappgagging.api.TodoFetchWorker
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.absoluteValue

private val TAG = "CalendarScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currentDayState = viewModel.currentDayStateFlow.collectAsState(LocalDate.now())
    val context = LocalContext.current
    val imageUrl by viewModel.imageUrl.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = currentDayState.value.month.getDisplayName(
                                TextStyle.FULL_STANDALONE, Locale.ENGLISH
                            ),
                            modifier = Modifier.graphicsLayer {
                                if (currentDayState.value.dayOfMonth == 1) {
                                    alpha = 1f + viewModel.pagerState.currentPageOffsetFraction.coerceIn(-0.5f, 0f) * 2
                                } else if (currentDayState.value.dayOfMonth == currentDayState.value.lengthOfMonth()) {
                                    alpha = 1f - viewModel.pagerState.currentPageOffsetFraction.coerceIn(0f, 0.5f) * 2
                                }
                            }
                        )
                    }
                )
                DaySelector(
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.fetchNewCat(context)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = stringResource(R.string.i_need_inspiration)
                    )
                }
                FloatingActionButton(
                    onClick = {} // Add a task
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_a_new_task)
                    )
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {}
        ) {
            if (true /* Check if there if something */) {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    // Add the task list here
                    // Show Cat Media

                    item {
                        imageUrl?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "Cat Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            } else {
                Box (
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(100.dp),
                        )
                        Text(
                            text = "All done!",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DaySelector(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val animationScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val currentPageState = snapshotFlow { viewModel.pagerState }

    LaunchedEffect(currentPageState) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
        Log.i("", "????")
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val pageSpacing = 5.dp
        val pageSize = (this.maxWidth - pageSpacing * 7) / 8
        val contentPadding = (this.maxWidth - pageSize) / 2

        HorizontalPager(
            state = viewModel.pagerState,
            pageSpacing = pageSpacing,
            pageSize = PageSize.Fill,
            contentPadding = PaddingValues(horizontal = contentPadding)
        ) { page ->
            val date = viewModel.pageToDate(page)

            Column {
                Card(
                    modifier = Modifier.graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (viewModel.pagerState.currentPage - page)
                                        + viewModel.pagerState.currentPageOffsetFraction).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                    onClick = {
                        animationScope.launch {
                            viewModel.select(page)
                        }
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${date.dayOfWeek.getDisplayName(
                                TextStyle.SHORT_STANDALONE, Locale.ENGLISH
                            )}",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${date.dayOfMonth}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        val context = LocalContext.current
        Column {
            Button(onClick = {
                viewModel.addTodo(context, "Hello", "World", "123")
            }) { Text("Add Todo") }

            val todos by viewModel.todos.collectAsState()
            LazyColumn {
                items(todos) { todo ->
                    Text(todo.todoID.toString())
                    Text(todo.name)
                    Text(todo.desc)
                    Text(todo.time)
                }
            }

        }
    }
}

@Composable
@Preview
fun CalendarScreenPreview() {
    AppTheme {
        CalendarScreen()
    }
}