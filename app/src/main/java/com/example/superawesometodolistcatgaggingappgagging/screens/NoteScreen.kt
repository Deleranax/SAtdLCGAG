package com.example.superawesometodolistcatgaggingappgagging.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onClose
    ) {

    }
}

@Composable
@Preview
fun NoteScreenPreview() {
    NoteScreen()
}