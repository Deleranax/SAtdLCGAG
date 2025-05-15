package com.example.superawesometodolistcatgaggingappgagging.screens.calendar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superawesometodolistcatgaggingappgagging.R
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
    viewModel: CalendarViewModel = viewModel()
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

                            }
                        )
                    }
                )
                DaySelector(viewModel = viewModel)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {} // Add a task
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_a_new_task)
                )
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {}
        ) {
            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                // Add the task list here
                // Show Cat Media

                item {
                    Button(
                        onClick = { viewModel.fetchNewCat(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Show New Cat")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
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
        }
    }
}

@Composable
fun DaySelector(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val animationScope = rememberCoroutineScope()

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
    }
}

@Composable
@Preview
fun CalendarScreenPreview() {
    CalendarScreen()
}