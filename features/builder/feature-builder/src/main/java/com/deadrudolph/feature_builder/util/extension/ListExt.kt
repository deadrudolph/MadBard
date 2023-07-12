package com.deadrudolph.feature_builder.util.extension

import androidx.compose.ui.text.TextRange
import com.deadrudolph.feature_builder.util.regex.CommonLanguagesRegex.noLetterRegexEnd
import com.deadrudolph.feature_builder.util.regex.CommonLanguagesRegex.noLetterRegexStart
import com.deadrudolph.uicomponents.ui_model.TextFieldState

internal fun MutableList<TextFieldState>.setFocusTo(index: Int): List<TextFieldState> {
    return mapIndexed { i, textFieldState ->
        if (index == i) {
            val newSelection = TextRange(textFieldState.value.text.length)
            textFieldState.copy(
                isFocused = true,
                value = textFieldState.value.copy(
                    selection = newSelection
                )
            )
        } else textFieldState.copy(isFocused = false)
    }
}

internal fun List<String>.toRegexConditionsString(): String {
    return if (isEmpty()) ""
    else """$noLetterRegexStart(${joinToString(separator = "|")})$noLetterRegexEnd"""
}