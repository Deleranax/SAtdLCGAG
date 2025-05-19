package com.example.superawesometodolistcatgaggingappgagging.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.example.superawesometodolistcatgaggingappgagging.R
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme
import kotlinx.coroutines.flow.map
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
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModelProvider.Factory),
    onNewTask: (date: LocalDate) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val currentDayState = viewModel.currentDayStateFlow.collectAsState(LocalDate.now())
    val imageUrl by viewModel.imageUrl.collectAsState()
    val todos by viewModel.todos.map {
        it.filter {
            it.time == currentDayState.value.toEpochDay()
        }
    }.collectAsState(listOf())

    LaunchedEffect(currentDayState.value) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp)
                        ) {
                            Text(
                                text = currentDayState.value.year.toString(),
                                modifier = Modifier
                                    .width(50.dp)
                                    .graphicsLayer {
                                        if (currentDayState.value.dayOfYear == 1) {
                                            alpha =
                                                1f + viewModel.pagerState.currentPageOffsetFraction.coerceIn(
                                                    -0.5f,
                                                    0f
                                                ) * 2
                                        } else if (currentDayState.value.dayOfYear == currentDayState.value.lengthOfYear()) {
                                            alpha =
                                                1f - viewModel.pagerState.currentPageOffsetFraction.coerceIn(
                                                    0f,
                                                    0.5f
                                                ) * 2
                                        }
                                    }
                            )
                            VerticalDivider(
                                modifier.height(20.dp)
                            )
                            Text(
                                text = currentDayState.value.month.getDisplayName(
                                    TextStyle.FULL_STANDALONE, Locale.ENGLISH
                                ),
                                modifier = Modifier.graphicsLayer {
                                    if (currentDayState.value.dayOfMonth == 1) {
                                        alpha =
                                            1f + viewModel.pagerState.currentPageOffsetFraction.coerceIn(
                                                -0.5f,
                                                0f
                                            ) * 2
                                    } else if (currentDayState.value.dayOfMonth == currentDayState.value.lengthOfMonth()) {
                                        alpha =
                                            1f - viewModel.pagerState.currentPageOffsetFraction.coerceIn(
                                                0f,
                                                0.5f
                                            ) * 2
                                    }
                                }
                            )
                            Spacer(
                                Modifier.weight(1.0f)
                            )
                            AnimatedVisibility(
                                visible = currentDayState.value != viewModel.today,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            viewModel.select(viewModel.today)
                                        }
                                    },
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Today,
                                            contentDescription = stringResource(R.string.go_back_to_today),
                                        )
                                        Text(
                                            text = stringResource(R.string.today)
                                        )
                                    }
                                }
                            }
                        }
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
                    onClick = { onNewTask(currentDayState.value) }
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
            if (false /* Check if there if something */) {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    items(todos) { todo ->
                        Text(todo.todoID.toString())
                        Text(todo.name)
                        Text(todo.desc)
                        Text(todo.time.toString())
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
                            text = stringResource(R.string.all_done),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
        imageUrl?.let {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(it)
                    .size(coil.size.Size.ORIGINAL)
                    .build()
            )

            BasicAlertDialog(
                onDismissRequest = { viewModel.dismissCat() }
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(R.string.cat_image),
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                text = "Powered by CATAAS",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    else -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                        }
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
    AppTheme {
        CalendarScreen()
    }
}