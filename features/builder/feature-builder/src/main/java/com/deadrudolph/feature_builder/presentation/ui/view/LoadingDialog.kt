package com.deadrudolph.feature_builder.presentation.ui.view

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = { }
    ) {
        CircularProgressIndicator(
            color = CustomTheme.colors.dark_600
        )
    }
}