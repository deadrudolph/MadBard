package com.deadrudolph.uicomponents.view.textfield.core.text_field

import android.view.KeyCharacterMap
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.text.input.OffsetMapping
import com.deadrudolph.uicomponents.view.textfield.core.KeyCommand
import com.deadrudolph.uicomponents.view.textfield.core.KeyMapping
import com.deadrudolph.uicomponents.view.textfield.core.UndoManager
import com.deadrudolph.uicomponents.view.textfield.core.input.CommitTextCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.DeleteSurroundingTextCommand
import com.deadrudolph.uicomponents.view.textfield.core.input.EditCommand
import com.deadrudolph.uicomponents.view.textfield.core.platformDefaultKeyMapping
import com.deadrudolph.uicomponents.view.textfield.extension.appendCodePointX
import com.deadrudolph.uicomponents.view.textfield.extension.isTypedEvent
import com.deadrudolph.uicomponents.view.textfield.core.input.FinishComposingTextCommand
/**
 * It handles [KeyEvent]s and either process them as typed events or maps to
 * [KeyCommand] via [KeyMapping]. [KeyCommand] then is executed
 * using utility class [TextFieldPreparedSelection]
 */
internal class TextFieldKeyInput(
    val state: NewTextFieldState,
    val selectionManager: TextFieldSelectionManager,
    val value: TextFieldValue = TextFieldValue(),
    val editable: Boolean = true,
    val singleLine: Boolean = false,
    val preparedSelectionState: TextPreparedSelectionState,
    val offsetMapping: OffsetMapping = OffsetMapping.Identity,
    val undoManager: UndoManager? = null,
    private val keyCombiner: DeadKeyCombiner,
    private val keyMapping: KeyMapping = platformDefaultKeyMapping,
    private val onValueChange: (TextFieldValue) -> Unit = {}
) {

    private fun List<EditCommand>.apply() {
        val newTextFieldValue = state.processor.apply(
            this.toMutableList().apply {
                add(0, FinishComposingTextCommand())
            }
        )

        onValueChange(newTextFieldValue)
    }

    private fun EditCommand.apply() {
        listOf(this).apply()
    }

    private fun typedCommand(event: KeyEvent): CommitTextCommand? {
        if (!event.isTypedEvent) {
            return null
        }

        val codePoint = keyCombiner.consume(event) ?: return null
        val text = StringBuilder().appendCodePointX(codePoint).toString()
        return CommitTextCommand(text, 1)
    }

    fun process(event: KeyEvent): Boolean {
        typedCommand(event)?.let {
            return if (editable) {
                it.apply()
                preparedSelectionState.resetCachedX()
                true
            } else {
                false
            }
        }
        if (event.type != KeyEventType.KeyDown) {
            return false
        }
        val command = keyMapping.map(event)
        if (command == null || (command.editsText && !editable)) {
            return false
        }
        var consumed = true
        commandExecutionContext {
            when (command) {
                KeyCommand.COPY -> selectionManager.copy(false)
                // TODO(siyamed): cut & paste will cause a reset input
                KeyCommand.PASTE -> selectionManager.paste()
                KeyCommand.CUT -> selectionManager.cut()
                KeyCommand.LEFT_CHAR -> collapseLeftOr { moveCursorLeft() }
                KeyCommand.RIGHT_CHAR -> collapseRightOr { moveCursorRight() }
                KeyCommand.LEFT_WORD -> moveCursorLeftByWord()
                KeyCommand.RIGHT_WORD -> moveCursorRightByWord()
                KeyCommand.PREV_PARAGRAPH -> moveCursorPrevByParagraph()
                KeyCommand.NEXT_PARAGRAPH -> moveCursorNextByParagraph()
                KeyCommand.UP -> moveCursorUpByLine()
                KeyCommand.DOWN -> moveCursorDownByLine()
                KeyCommand.PAGE_UP -> moveCursorUpByPage()
                KeyCommand.PAGE_DOWN -> moveCursorDownByPage()
                KeyCommand.LINE_START -> moveCursorToLineStart()
                KeyCommand.LINE_END -> moveCursorToLineEnd()
                KeyCommand.LINE_LEFT -> moveCursorToLineLeftSide()
                KeyCommand.LINE_RIGHT -> moveCursorToLineRightSide()
                KeyCommand.HOME -> moveCursorToHome()
                KeyCommand.END -> moveCursorToEnd()
                KeyCommand.DELETE_PREV_CHAR ->
                    deleteIfSelectedOr {
                        DeleteSurroundingTextCommand(
                            selection.end - getPrecedingCharacterIndex(),
                            0
                        )
                    }?.apply()
                KeyCommand.DELETE_NEXT_CHAR -> {
                    // Note that some software keyboards, such as Samsungs, go through this code
                    // path instead of making calls on the InputConnection directly.
                    deleteIfSelectedOr {
                        val nextCharacterIndex = getNextCharacterIndex()
                        // If there's no next character, it means the cursor is at the end of the
                        // text, and this should be a no-op. See b/199919707.
                        if (nextCharacterIndex != BaseTextPreparedSelection.NoCharacterFound) {
                            DeleteSurroundingTextCommand(0, nextCharacterIndex - selection.end)
                        } else {
                            null
                        }
                    }?.apply()
                }
                KeyCommand.DELETE_PREV_WORD ->
                    deleteIfSelectedOr {
                        getPreviousWordOffset()?.let {
                            DeleteSurroundingTextCommand(selection.end - it, 0)
                        }
                    }?.apply()
                KeyCommand.DELETE_NEXT_WORD ->
                    deleteIfSelectedOr {
                        getNextWordOffset()?.let {
                            DeleteSurroundingTextCommand(0, it - selection.end)
                        }
                    }?.apply()
                KeyCommand.DELETE_FROM_LINE_START ->
                    deleteIfSelectedOr {
                        getLineStartByOffset()?.let {
                            DeleteSurroundingTextCommand(selection.end - it, 0)
                        }
                    }?.apply()
                KeyCommand.DELETE_TO_LINE_END ->
                    deleteIfSelectedOr {
                        getLineEndByOffset()?.let {
                            DeleteSurroundingTextCommand(0, it - selection.end)
                        }
                    }?.apply()
                KeyCommand.NEW_LINE ->
                    if (!singleLine) {
                        CommitTextCommand("\n", 1).apply()
                    } else {
                        consumed = false
                    }
                KeyCommand.TAB ->
                    if (!singleLine) {
                        CommitTextCommand("\t", 1).apply()
                    } else {
                        consumed = false
                    }
                KeyCommand.SELECT_ALL -> selectAll()
                KeyCommand.SELECT_LEFT_CHAR -> moveCursorLeft().selectMovement()
                KeyCommand.SELECT_RIGHT_CHAR -> moveCursorRight().selectMovement()
                KeyCommand.SELECT_LEFT_WORD -> moveCursorLeftByWord().selectMovement()
                KeyCommand.SELECT_RIGHT_WORD -> moveCursorRightByWord().selectMovement()
                KeyCommand.SELECT_PREV_PARAGRAPH -> moveCursorPrevByParagraph().selectMovement()
                KeyCommand.SELECT_NEXT_PARAGRAPH -> moveCursorNextByParagraph().selectMovement()
                KeyCommand.SELECT_LINE_START -> moveCursorToLineStart().selectMovement()
                KeyCommand.SELECT_LINE_END -> moveCursorToLineEnd().selectMovement()
                KeyCommand.SELECT_LINE_LEFT -> moveCursorToLineLeftSide().selectMovement()
                KeyCommand.SELECT_LINE_RIGHT -> moveCursorToLineRightSide().selectMovement()
                KeyCommand.SELECT_UP -> moveCursorUpByLine().selectMovement()
                KeyCommand.SELECT_DOWN -> moveCursorDownByLine().selectMovement()
                KeyCommand.SELECT_PAGE_UP -> moveCursorUpByPage().selectMovement()
                KeyCommand.SELECT_PAGE_DOWN -> moveCursorDownByPage().selectMovement()
                KeyCommand.SELECT_HOME -> moveCursorToHome().selectMovement()
                KeyCommand.SELECT_END -> moveCursorToEnd().selectMovement()
                KeyCommand.DESELECT -> deselect()
                KeyCommand.UNDO -> {
                    undoManager?.makeSnapshot(value)
                    undoManager?.undo()?.let { this@TextFieldKeyInput.onValueChange(it) }
                }
                KeyCommand.REDO -> {
                    undoManager?.redo()?.let { this@TextFieldKeyInput.onValueChange(it) }
                }
                KeyCommand.CHARACTER_PALETTE -> Unit
            }
        }
        undoManager?.forceNextSnapshot()
        return consumed
    }

    private fun commandExecutionContext(block: TextFieldPreparedSelection.() -> Unit) {
        val preparedSelection = TextFieldPreparedSelection(
            currentValue = value,
            offsetMapping = offsetMapping,
            layoutResultProxy = state.layoutResult,
            state = preparedSelectionState
        )
        block(preparedSelection)
        if (preparedSelection.selection != value.selection ||
            preparedSelection.annotatedString != value.annotatedString
        ) {
            onValueChange(preparedSelection.value)
        }
    }
}

@Suppress("ModifierInspectorInfo")
internal fun Modifier.textFieldKeyInput(
    state: NewTextFieldState,
    manager: TextFieldSelectionManager,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit = {},
    editable: Boolean,
    singleLine: Boolean,
    offsetMapping: OffsetMapping,
    undoManager: UndoManager
) = composed {
    val preparedSelectionState = remember { TextPreparedSelectionState() }
    val keyCombiner = remember { DeadKeyCombiner() }
    val processor = TextFieldKeyInput(
        state = state,
        selectionManager = manager,
        value = value,
        editable = editable,
        singleLine = singleLine,
        offsetMapping = offsetMapping,
        preparedSelectionState = preparedSelectionState,
        undoManager = undoManager,
        keyCombiner = keyCombiner,
        onValueChange = onValueChange
    )
    Modifier.onKeyEvent(processor::process)
}

internal class TextPreparedSelectionState {
    // it's set at the start of vertical navigation and used as the preferred value to set a new
    // cursor position.
    var cachedX: Float? = null

    fun resetCachedX() {
        cachedX = null
    }
}

internal class DeadKeyCombiner {

    private var deadKeyCode: Int? = null

    fun consume(event: KeyEvent): Int? {
        val codePoint = event.utf16CodePoint
        if (codePoint and KeyCharacterMap.COMBINING_ACCENT != 0) {
            deadKeyCode = codePoint and KeyCharacterMap.COMBINING_ACCENT_MASK
            return null
        }

        val localDeadKeyCode = deadKeyCode
        if (localDeadKeyCode != null) {
            deadKeyCode = null
            return KeyCharacterMap.getDeadChar(localDeadKeyCode, codePoint)
                // if the combo doesn't exist, fall back to the current key press
                .takeUnless { it == 0 } ?: codePoint
        }

        return codePoint
    }
}