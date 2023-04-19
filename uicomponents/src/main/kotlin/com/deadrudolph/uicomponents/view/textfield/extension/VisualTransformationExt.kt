package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.deadrudolph.uicomponents.view.textfield.core.ValidatingOffsetMapping

internal fun VisualTransformation.filterWithValidation(text: AnnotatedString): TransformedText {
    return filter(text).let { transformed ->
        TransformedText(
            transformed.text,
            ValidatingOffsetMapping(
                delegate = transformed.offsetMapping,
                originalLength = text.length,
                transformedLength = transformed.text.length
            )
        )
    }
}