package com.practicum.playlistmaker.medialibrary.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.practicum.playlistmaker.R

@Composable
fun ConfirmDialog(
    visible: Boolean,
    title: String,
    text: String,
    dismissButtonText: String,
    confirmButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (!visible) return

    AlertDialog(
        containerColor = colorResource(R.color.white),
        onDismissRequest = {
            onDismiss()
        },
        title = { Text(text = title) },
        text = { Text(text = text) },
        dismissButton = {
            DialogButton(text = dismissButtonText, action = onDismiss)
        },
        confirmButton = {
            DialogButton(text = confirmButtonText, action = onConfirm)
        }
    )
}

@Composable
fun DialogButton(
    text: String,
    action: () -> Unit,
) {
    TextButton(
        onClick = {
            action()
        },
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = colorResource(R.color.blue)
        )
    ) {
        Text(text = text)
    }
}