package com.example.superawesometodolistcatgaggingappgagging.screens.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superawesometodolistcatgaggingappgagging.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    date: LocalDate,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel(factory = TaskViewModelProvider.Factory),
    onClose: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onClose,
        scrimColor = Color.Transparent,
        dragHandle = {},
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "New Task",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(
                    R.string.add_a_new_task_for,
                    date.dayOfWeek.getDisplayName(
                        TextStyle.FULL, Locale.ENGLISH
                    ),
                    date.dayOfMonth, date.month.getDisplayName(
                        TextStyle.FULL, Locale.ENGLISH
                    ),
                    date.year
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onClose()
                        }
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.addTodo(
                                context = context,
                                name = title,
                                desc = description,
                                time = date.toEpochDay()
                            )
                            sheetState.hide()
                            onClose()
                        }
                    }
                ) {
                    Text(stringResource(R.string.create))
                }
            }
        }
    }
}

@Composable
@Preview
fun TaskScreenPreview() {
    TaskScreen(LocalDate.now())
}
