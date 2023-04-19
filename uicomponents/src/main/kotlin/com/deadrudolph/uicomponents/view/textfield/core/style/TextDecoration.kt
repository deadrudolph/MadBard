package com.deadrudolph.uicomponents.view.textfield.core.style

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.deadrudolph.uicomponents.view.textfield.extension.fastFold
import com.deadrudolph.uicomponents.view.textfield.extension.fastJoinToString


@Immutable
class TextDecoration internal constructor(val mask: Int) {

    companion object {
        @Stable
        val None: TextDecoration = TextDecoration(0x0)

        /**
         * Draws a horizontal line below the text.
         *
         * @sample androidx.compose.ui.text.samples.TextDecorationUnderlineSample
         */
        @Stable
        val Underline: TextDecoration = TextDecoration(0x1)

        /**
         * Draws a horizontal line over the text.
         *
         * @sample androidx.compose.ui.text.samples.TextDecorationLineThroughSample
         */
        @Stable
        val LineThrough: TextDecoration = TextDecoration(0x2)

        /**
         * Creates a decoration that includes all the given decorations.
         *
         * @sample androidx.compose.ui.text.samples.TextDecorationCombinedSample
         *
         * @param decorations The decorations to be added
         */
        fun combine(decorations: List<TextDecoration>): TextDecoration {
            val mask = decorations.fastFold(0) { acc, decoration ->
                acc or decoration.mask
            }
            return TextDecoration(mask)
        }
    }

    /**
     * Creates a decoration that includes both of the TextDecorations.
     *
     * @sample androidx.compose.ui.text.samples.TextDecorationCombinedSample
     */
    operator fun plus(decoration: TextDecoration): TextDecoration {
        return TextDecoration(this.mask or decoration.mask)
    }

    /**
     * Check whether this [TextDecoration] contains the given decoration.
     *
     * @param other The [TextDecoration] to be checked.
     */
    operator fun contains(other: TextDecoration): Boolean {
        return (mask or other.mask) == mask
    }

    override fun toString(): String {
        if (mask == 0) {
            return "TextDecoration.None"
        }

        val values: MutableList<String> = mutableListOf()
        if ((mask and Underline.mask) != 0) {
            values.add("Underline")
        }
        if ((mask and LineThrough.mask) != 0) {
            values.add("LineThrough")
        }
        if ((values.size == 1)) {
            return "TextDecoration.${values[0]}"
        }
        return "TextDecoration[${values.fastJoinToString(separator = ", ")}]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextDecoration) return false
        if (mask != other.mask) return false
        return true
    }

    override fun hashCode(): Int {
        return mask
    }
}