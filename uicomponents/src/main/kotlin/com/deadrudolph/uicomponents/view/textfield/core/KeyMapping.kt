package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.ui.input.key.*

internal interface KeyMapping {
    fun map(event: KeyEvent): KeyCommand?
}

internal val defaultKeyMapping: KeyMapping =
    commonKeyMapping(KeyEvent::isCtrlPressed).let { common ->
        object : KeyMapping {
            override fun map(event: KeyEvent): KeyCommand? {
                return when {
                    event.isShiftPressed && event.isCtrlPressed ->
                        when (event.key) {
                            MappedKeys.DirectionLeft -> KeyCommand.SELECT_LEFT_WORD
                            MappedKeys.DirectionRight -> KeyCommand.SELECT_RIGHT_WORD
                            MappedKeys.DirectionUp -> KeyCommand.SELECT_PREV_PARAGRAPH
                            MappedKeys.DirectionDown -> KeyCommand.SELECT_NEXT_PARAGRAPH
                            else -> null
                        }
                    event.isCtrlPressed ->
                        when (event.key) {
                            MappedKeys.DirectionLeft -> KeyCommand.LEFT_WORD
                            MappedKeys.DirectionRight -> KeyCommand.RIGHT_WORD
                            MappedKeys.DirectionUp -> KeyCommand.PREV_PARAGRAPH
                            MappedKeys.DirectionDown -> KeyCommand.NEXT_PARAGRAPH
                            MappedKeys.H -> KeyCommand.DELETE_PREV_CHAR
                            MappedKeys.Delete -> KeyCommand.DELETE_NEXT_WORD
                            MappedKeys.Backspace -> KeyCommand.DELETE_PREV_WORD
                            MappedKeys.Backslash -> KeyCommand.DESELECT
                            else -> null
                        }
                    event.isShiftPressed ->
                        when (event.key) {
                            MappedKeys.MoveHome -> KeyCommand.SELECT_HOME
                            MappedKeys.MoveEnd -> KeyCommand.SELECT_END
                            else -> null
                        }
                    event.isAltPressed ->
                        when (event.key) {
                            MappedKeys.Backspace -> KeyCommand.DELETE_FROM_LINE_START
                            MappedKeys.Delete -> KeyCommand.DELETE_TO_LINE_END
                            else -> null
                        }
                    else -> null
                } ?: common.map(event)
            }
        }
    }

internal fun commonKeyMapping(
    shortcutModifier: (KeyEvent) -> Boolean
): KeyMapping {
    return object : KeyMapping {
        override fun map(event: KeyEvent): KeyCommand? {
            return when {
                shortcutModifier(event) && event.isShiftPressed ->
                    when (event.key) {
                        MappedKeys.Z -> KeyCommand.REDO
                        else -> null
                    }
                shortcutModifier(event) ->
                    when (event.key) {
                        MappedKeys.C, MappedKeys.Insert -> KeyCommand.COPY
                        MappedKeys.V -> KeyCommand.PASTE
                        MappedKeys.X -> KeyCommand.CUT
                        MappedKeys.A -> KeyCommand.SELECT_ALL
                        MappedKeys.Y -> KeyCommand.REDO
                        MappedKeys.Z -> KeyCommand.UNDO
                        else -> null
                    }
                event.isCtrlPressed -> null
                event.isShiftPressed ->
                    when (event.key) {
                        MappedKeys.DirectionLeft -> KeyCommand.SELECT_LEFT_CHAR
                        MappedKeys.DirectionRight -> KeyCommand.SELECT_RIGHT_CHAR
                        MappedKeys.DirectionUp -> KeyCommand.SELECT_UP
                        MappedKeys.DirectionDown -> KeyCommand.SELECT_DOWN
                        MappedKeys.PageUp -> KeyCommand.SELECT_PAGE_UP
                        MappedKeys.PageDown -> KeyCommand.SELECT_PAGE_DOWN
                        MappedKeys.MoveHome -> KeyCommand.SELECT_LINE_START
                        MappedKeys.MoveEnd -> KeyCommand.SELECT_LINE_END
                        MappedKeys.Insert -> KeyCommand.PASTE
                        else -> null
                    }
                else ->
                    when (event.key) {
                        MappedKeys.DirectionLeft -> KeyCommand.LEFT_CHAR
                        MappedKeys.DirectionRight -> KeyCommand.RIGHT_CHAR
                        MappedKeys.DirectionUp -> KeyCommand.UP
                        MappedKeys.DirectionDown -> KeyCommand.DOWN
                        MappedKeys.PageUp -> KeyCommand.PAGE_UP
                        MappedKeys.PageDown -> KeyCommand.PAGE_DOWN
                        MappedKeys.MoveHome -> KeyCommand.LINE_START
                        MappedKeys.MoveEnd -> KeyCommand.LINE_END
                        MappedKeys.Enter -> KeyCommand.NEW_LINE
                        MappedKeys.Backspace -> KeyCommand.DELETE_PREV_CHAR
                        MappedKeys.Delete -> KeyCommand.DELETE_NEXT_CHAR
                        MappedKeys.Paste -> KeyCommand.PASTE
                        MappedKeys.Cut -> KeyCommand.CUT
                        MappedKeys.Copy -> KeyCommand.COPY
                        MappedKeys.Tab -> KeyCommand.TAB
                        else -> null
                    }
            }
        }
    }
}

internal  val platformDefaultKeyMapping = defaultKeyMapping

