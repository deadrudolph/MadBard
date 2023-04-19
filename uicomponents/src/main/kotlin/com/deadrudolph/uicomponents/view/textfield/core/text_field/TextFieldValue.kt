package com.deadrudolph.uicomponents.view.textfield.core.text_field

import androidx.compose.runtime.saveable.Saver
import com.deadrudolph.uicomponents.view.textfield.core.range.TextRange
import com.deadrudolph.uicomponents.view.textfield.core.range.constrain
import com.deadrudolph.uicomponents.view.textfield.core.saver.AnnotatedStringSaver
import com.deadrudolph.uicomponents.view.textfield.core.saver.Saver
import com.deadrudolph.uicomponents.view.textfield.core.saver.restore
import com.deadrudolph.uicomponents.view.textfield.core.saver.save
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import kotlin.math.max
import kotlin.math.min

class TextFieldValue constructor(
    val annotatedString: AnnotatedString,
    selection: TextRange = TextRange.Zero,
    composition: TextRange? = null
) {
    /**
     * @param text the text to be rendered.
     * @param selection the selection range. If the selection is collapsed, it represents cursor
     * location. When selection range is out of bounds, it is constrained with the text length.
     * @param composition the composition range, null means empty composition or apply if a
     * composition exists on the text. Owned by IME, and if you have an instance of [TextFieldValue]
     * please use [copy] functions if you do not want to intentionally change the value of this
     * field.
     */
    constructor(
        text: String = "",
        selection: TextRange = TextRange.Zero,
        composition: TextRange? = null
    ) : this(AnnotatedString(text), selection, composition)

    val text: String get() = annotatedString.text

    /**
     * The selection range. If the selection is collapsed, it represents cursor
     * location. When selection range is out of bounds, it is constrained with the text length.
     */
    val selection: TextRange = selection.constrain(0, text.length)

    /**
     * Composition range created by  IME. If null, there is no composition range.
     *
     * Input service composition is an instance of text produced by IME. An example visual for the
     * composition is that the currently composed word is visually separated from others with
     * underline, or text background. For description of composition please check
     * [W3C IME Composition](https://www.w3.org/TR/ime-api/#ime-composition)
     *
     * Composition can be set on the by the system, however it is possible to apply an existing
     * composition by setting the value to null. Applying a composition will accept the changes
     * that were still being composed by IME.
     */
    val composition: TextRange? = composition?.constrain(0, text.length)

    /**
     * Returns a copy of the TextFieldValue.
     */
    fun copy(
        annotatedString: AnnotatedString = this.annotatedString,
        selection: TextRange = this.selection,
        composition: TextRange? = this.composition
    ): TextFieldValue {
        return TextFieldValue(annotatedString, selection, composition)
    }

    /**
     * Returns a copy of the TextFieldValue.
     */
    fun copy(
        text: String,
        selection: TextRange = this.selection,
        composition: TextRange? = this.composition
    ): TextFieldValue {
        return TextFieldValue(AnnotatedString(text), selection, composition)
    }

    // auto generated equals method
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextFieldValue) return false

        // compare selection and composition first for early return
        //  before comparing string.
        return selection == other.selection &&
                composition == other.composition &&
                annotatedString == other.annotatedString
    }

    // auto generated hashCode method
    override fun hashCode(): Int {
        var result = annotatedString.hashCode()
        result = 31 * result + selection.hashCode()
        result = 31 * result + (composition?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TextFieldValue(" +
                "text='$annotatedString', " +
                "selection=$selection, " +
                "composition=$composition)"
    }

    companion object {
        /**
         * The default [Saver] implementation for [TextFieldValue].
         */
        val Saver = Saver<TextFieldValue, Any>(
            save = {
                arrayListOf(
                    save(it.annotatedString, AnnotatedStringSaver, this),
                    save(it.selection, TextRange.Saver, this),
                )
            },
            restore = {
                @Suppress("UNCHECKED_CAST")
                val list = it as List<Any>
                TextFieldValue(
                    annotatedString = restore(list[0], AnnotatedStringSaver)!!,
                    selection = restore(list[1], TextRange.Saver)!!,
                )
            }
        )
    }
}

/**
 * Returns the text before the selection.
 *
 * @param maxChars maximum number of characters (inclusive) before the minimum value in
 * [TextFieldValue.selection].
 *
 * @see TextRange.min
 */
fun TextFieldValue.getTextBeforeSelection(maxChars: Int): AnnotatedString =
    annotatedString.subSequence(max(0, selection.min - maxChars), selection.min)

/**
 * Returns the text after the selection.
 *
 * @param maxChars maximum number of characters (exclusive) after the maximum value in
 * [TextFieldValue.selection].
 *
 * @see TextRange.max
 */
fun TextFieldValue.getTextAfterSelection(maxChars: Int): AnnotatedString =
    annotatedString.subSequence(selection.max, min(selection.max + maxChars, text.length))

/**
 * Returns the currently selected text.
 */
fun TextFieldValue.getSelectedText(): AnnotatedString = annotatedString.subSequence(selection)