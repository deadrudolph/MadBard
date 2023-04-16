package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.ui.text.input.OffsetMapping

internal class ValidatingOffsetMapping(
    private val delegate: OffsetMapping,
    private val originalLength: Int,
    private val transformedLength: Int
) : OffsetMapping {

    /**
     * Calls [originalToTransformed][OffsetMapping.originalToTransformed] and throws a detailed
     * exception if the returned value is outside the range of indices [0, [transformedLength]].
     */
    override fun originalToTransformed(offset: Int): Int {
        return delegate.originalToTransformed(offset).also { transformedOffset ->
            check(transformedOffset in 0..transformedLength) {
                "OffsetMapping.originalToTransformed returned invalid mapping: " +
                        "$offset -> $transformedOffset is not in range of transformed text " +
                        "[0, $transformedLength]"
            }
        }
    }

    /**
     * Calls [transformedToOriginal][OffsetMapping.transformedToOriginal] and throws a detailed
     * exception if the returned value is outside the range of indices [0, [originalLength]].
     */
    override fun transformedToOriginal(offset: Int): Int {
        return delegate.transformedToOriginal(offset).also { originalOffset ->
            check(originalOffset in 0..originalLength) {
                "OffsetMapping.transformedToOriginal returned invalid mapping: " +
                        "$offset -> $originalOffset is not in range of original text " +
                        "[0, $originalLength]"
            }
        }
    }
}