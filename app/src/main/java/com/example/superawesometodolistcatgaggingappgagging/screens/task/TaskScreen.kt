package com.example.superawesometodolistcatgaggingappgagging.screens.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.superawesometodolistcatgaggingappgagging.Day
import com.example.superawesometodolistcatgaggingappgagging.ToDo_Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    selectedDay: Day,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onClose
    ) {

    }
}

val tempDay = Day(15, 5)
@Composable
@Preview
fun TaskScreenPreview() {
    initTempDay()
    TaskScreen(tempDay)
}
fun initTempDay() {
    val tempItem1 = ToDo_Item(
        day = 15,
        month = 5,
        "Birthday Party",
        "This is the description for a birthday"
    )

    val tempItem2 = ToDo_Item(
        day = 20,
        month = 1,
        "Start school",
        "This is the description for school starting"
    )
    tempDay.addItem(tempItem1)
    tempDay.addItem(tempItem2)
}
