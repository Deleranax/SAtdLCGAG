package com.example.superawesometodolistcatgaggingappgagging.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.superawesometodolistcatgaggingappgagging.R
import com.example.superawesometodolistcatgaggingappgagging.api.TodoApi
import com.example.superawesometodolistcatgaggingappgagging.database.TodoTable
import com.example.superawesometodolistcatgaggingappgagging.ui.theme.AppTheme
import kotlinx.coroutines.delay
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
    onNewTask: (date: LocalDate) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val snackbarHostState = remember { SnackbarHostState() }

    val currentDayState = viewModel.currentDayStateFlow.collectAsState(LocalDate.now())
    val imageUrl by viewModel.imageUrlStateFlow.collectAsState()
    val todos by viewModel.todos.map {
        it.filter { it.time == currentDayState.value.toEpochDay() }
    }.collectAsState(listOf())
    var loadingState by remember { mutableStateOf(false) }
    var logout by remember { mutableStateOf(false) }

    LaunchedEffect(currentDayState.value) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DaySelector(
                viewModel = viewModel
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                FloatingActionButton(
                    onClick = { logout = true },
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.logout)
                    )
                }
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = loadingState,
            onRefresh = {
                scope.launch {
                    // Lie
                    loadingState = true
                    viewModel.refresh(context)
                    delay(1000)
                    loadingState = false
                }
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            if (todos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp)
                ) {
                    items(
                        items = todos,
                        key = { it.todoID }
                    ) { todo ->
                        TodoItem(
                            modifier = Modifier.animateItem(),
                            todo = todo,
                            viewModel = viewModel,
                            snackbarHostState = snackbarHostState
                        )
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
            CatDialog(
                viewModel = viewModel,
                imageUrl = it
            )
        }
        if (logout) {
            LogoutDialog(
                onClose = {
                    logout = false
                    if (it) {
                        scope.launch {
                            viewModel.logout()
                            onLogout()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    todo: TodoTable,
    viewModel: CalendarViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var done by remember { mutableStateOf(false) }
    var removed by remember { mutableStateOf(true) }
    var expended by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        removed = false
    }

    AnimatedVisibility(
        visible = !removed,
        enter = fadeIn() + slideInHorizontally(),
        exit = fadeOut() + slideOutHorizontally(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.padding(5.dp).wrapContentSize().animateContentSize(),
            onClick = {
                expended = !expended
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = done,
                        onCheckedChange = {
                            done = true

                            scope.launch{
                                delay(500)
                                removed = true
                                delay(500)

                                if (!viewModel.removeTodo(context, todo.todoID)) {
                                    done = false
                                    removed = false
                                    snackbarHostState.showSnackbar("An error has occurred.")
                                }
                            }
                        }
                    )
                    Text(
                        text = todo.name,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        textDecoration = if (done) TextDecoration.LineThrough else null,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
                AnimatedVisibility(
                    visible = todo.desc.isNotEmpty() && expended,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Text(
                        text = todo.desc,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 100,
                        textDecoration = if (done) TextDecoration.LineThrough else null,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutDialog(
    onClose: (Boolean) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = {
            onClose(false)
        },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = stringResource(R.string.logout)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.sign_out_of, stringResource(R.string.app_name_short)),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Text(
                text = stringResource(R.string.are_you_sure_you_want_to_sign_out),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClose(true)
                },
            ) {
                Text(
                    text = stringResource(R.string.confirm)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onClose(false)
                }
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDialog(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel,
    imageUrl: String
) {
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )

    BasicAlertDialog(
        onDismissRequest = { viewModel.dismissCat() },
        modifier = modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelector(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel,
) {
    val scope = rememberCoroutineScope()
    val animationScope = rememberCoroutineScope()

    val currentDayState = viewModel.currentDayStateFlow.collectAsState(LocalDate.now())

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
                val localTodos = viewModel.todos.collectAsState().value.filter { it.time == date.toEpochDay() }

                BadgedBox(
                    badge = {
                        if (localTodos.isNotEmpty())
                        Badge {
                            Text(
                                text = localTodos.size.toString()
                            )
                        }
                    }
                ) {
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
                        },
                        colors = if (date == viewModel.today) {
                            CardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            CardDefaults.cardColors()
                        },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "${
                                    date.dayOfWeek.getDisplayName(
                                        TextStyle.SHORT_STANDALONE, Locale.ENGLISH
                                    )
                                }",
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
}

@Composable
@Preview
fun CalendarScreenPreview() {
    AppTheme {
        CalendarScreen()
    }
}