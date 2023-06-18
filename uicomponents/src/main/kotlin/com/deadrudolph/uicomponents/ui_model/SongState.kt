package com.deadrudolph.uicomponents.ui_model

data class SongState(
    val title: String = "",
    val textFields: List<TextFieldState> = emptyList(),
    val imagePath: String = ""
)
