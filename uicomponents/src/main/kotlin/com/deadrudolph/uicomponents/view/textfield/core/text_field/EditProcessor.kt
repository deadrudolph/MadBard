package com.deadrudolph.uicomponents.view.textfield.core.text_field

import com.deadrudolph.uicomponents.view.textfield.core.input.BackspaceCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.CommitTextCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.DeleteAllCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.DeleteSurroundingTextCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.DeleteSurroundingTextInCodePointsCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.EditCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.FinishComposingTextCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.MoveCursorCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.SetComposingRegionCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.SetComposingTextCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.SetSelectionCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.TextInputSession
import com.deadrudolph.uicomponents.view.textfield.core.range.TextRange
import com.deadrudolph.uicomponents.view.textfield.core.string.emptyAnnotatedString
import com.deadrudolph.uicomponents.view.textfield.extension.fastForEach

class EditProcessor {

    /**
     * The current state of the internal editing buffer as a [TextFieldValue].
     */
    /*@VisibleForTesting*/
    internal var mBufferState: TextFieldValue = TextFieldValue(
        emptyAnnotatedString(),
        TextRange.Zero,
        null
    )
        private set

    // The editing buffer used for applying editor commands from IME.
    /*@VisibleForTesting*/
    internal var mBuffer: EditingBuffer = EditingBuffer(
        text = mBufferState.annotatedString,
        selection = mBufferState.selection
    )
        private set

    /**
     * Must be called whenever new editor model arrives.
     *
     * This method updates the internal editing buffer with the given editor model.
     * This method may tell the IME about the selection offset changes or extracted text changes.
     */
    fun reset(
        value: TextFieldValue,
        textInputSession: TextInputSession?,
    ) {
        var textChanged = false
        var selectionChanged = false
        val compositionChanged = value.composition != mBuffer.composition

        if (mBufferState.annotatedString != value.annotatedString) {
            mBuffer = EditingBuffer(
                text = value.annotatedString,
                selection = value.selection
            )
            textChanged = true
        } else if (mBufferState.selection != value.selection) {
            mBuffer.setSelection(value.selection.min, value.selection.max)
            selectionChanged = true
        }

        if (value.composition == null) {
            mBuffer.commitComposition()
        } else if (!value.composition.collapsed) {
            mBuffer.setComposition(value.composition.min, value.composition.max)
        }

        // this is the same code as in TextInputServiceAndroid class where restartInput is decided
        // if restartInput is going to be called the composition has to be cleared otherwise it
        // results in keyboards behaving strangely.
        val newValue = if (textChanged || (!selectionChanged && compositionChanged)) {
            mBuffer.commitComposition()
            value.copy(composition = null)
        } else {
            value
        }

        val oldValue = mBufferState
        mBufferState = newValue

        textInputSession?.updateState(oldValue, newValue)
    }

    /**
     * Applies a set of [editCommands] to the internal text editing buffer.
     *
     * After applying the changes, returns the final state of the editing buffer as a
     * [TextFieldValue]
     *
     * @param editCommands [EditCommand]s to be applied to the editing buffer.
     *
     * @return the [TextFieldValue] representation of the final buffer state.
     */
    fun apply(editCommands: List<EditCommand>): TextFieldValue {
        var lastCommand: EditCommand? = null
        try {
            editCommands.fastForEach {
                lastCommand = it
                it.applyTo(mBuffer)
            }
        } catch (e: Exception) {
            throw RuntimeException(generateBatchErrorMessage(editCommands, lastCommand), e)
        }

        val newState = TextFieldValue(
            annotatedString = mBuffer.toAnnotatedString(),
            selection = mBuffer.selection,
            composition = mBuffer.composition
        )

        mBufferState = newState
        return newState
    }

    /**
     * Returns the current state of the internal editing buffer as a [TextFieldValue].
     */
    fun toTextFieldValue(): TextFieldValue = mBufferState

    private fun generateBatchErrorMessage(
        editCommands: List<EditCommand>,
        failedCommand: EditCommand?,
    ): String = buildString {
        appendLine(
            "Error while applying EditCommand batch to buffer (" +
                    "length=${mBuffer.length}, " +
                    "composition=${mBuffer.composition}, " +
                    "selection=${mBuffer.selection}):"
        )
        @Suppress("ListIterator")
        editCommands.joinTo(this, separator = "\n") {
            val prefix = if (failedCommand === it) " > " else "   "
            prefix + it.toStringForLog()
        }
    }

    /**
     * Generate a description of the command that is suitable for logging – this should not include
     * any user-entered text, which may be sensitive.
     */
    private fun EditCommand.toStringForLog(): String = when (this) {
        is CommitTextCommand ->
            "CommitTextCommand(text.length=${text.length}, newCursorPosition=$newCursorPosition)"
        is SetComposingTextCommand ->
            "SetComposingTextCommand(text.length=${text.length}, " +
                    "newCursorPosition=$newCursorPosition)"
        is SetComposingRegionCommand -> toString()
        is DeleteSurroundingTextCommand -> toString()
        is DeleteSurroundingTextInCodePointsCommand -> toString()
        is SetSelectionCommand -> toString()
        is FinishComposingTextCommand -> toString()
        is BackspaceCommand -> toString()
        is MoveCursorCommand -> toString()
        is DeleteAllCommand -> toString()
        // Do not return toString() by default, since that might contain sensitive text.
        else -> "Unknown EditCommand: " + (this::class.simpleName ?: "{anonymous EditCommand}")
    }
}