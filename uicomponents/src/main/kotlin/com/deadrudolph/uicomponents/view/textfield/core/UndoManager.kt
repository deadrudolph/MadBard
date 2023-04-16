package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.ui.text.input.TextFieldValue
import com.deadrudolph.uicomponents.view.textfield.extension.timeNowMillis

internal class UndoManager(
    val maxStoredCharacters: Int = 100_000
) {
    private class Entry(
        var next: Entry? = null,
        var value: TextFieldValue
    )

    private var undoStack: Entry? = null
    private var redoStack: Entry? = null
    private var storedCharacters: Int = 0
    private var lastSnapshot: Long? = null
    private var forceNextSnapshot = false

    /**
     * It gives an undo manager a chance to save a snapshot if needed because either it's time
     * for periodic snapshotting or snapshot was previously forced via [forceNextSnapshot]. It
     * can be called during every TextField recomposition.
     */
    fun snapshotIfNeeded(value: TextFieldValue, now: Long = timeNowMillis()) {
        if (forceNextSnapshot || now > (lastSnapshot ?: 0) + SNAPSHOTS_INTERVAL_MILLIS) {
            lastSnapshot = now
            makeSnapshot(value)
        }
    }

    /**
     * It forces making a snapshot during the next [snapshotIfNeeded] call
     */
    fun forceNextSnapshot() {
        forceNextSnapshot = true
    }

    /**
     * Unconditionally makes a new snapshot (if a value differs from the last one)
     */
    fun makeSnapshot(value: TextFieldValue) {
        forceNextSnapshot = false
        if (value == undoStack?.value) {
            return
        } else if (value.text == undoStack?.value?.text) {
            // if text is the same, but selection / composition is different we a not making a
            // new record, but update the last one
            undoStack?.value = value
            return
        }
        undoStack = Entry(
            value = value,
            next = undoStack
        )
        redoStack = null
        storedCharacters += value.text.length

        if (storedCharacters > maxStoredCharacters) {
            removeLastUndo()
        }
    }

    private fun removeLastUndo() {
        var entry = undoStack
        if (entry?.next == null) return
        while (entry?.next?.next != null) {
            entry = entry.next
        }
        entry?.next = null
    }

    fun undo(): TextFieldValue? {
        return undoStack?.let { undoEntry ->
            undoEntry.next?.let { nextEntry ->
                undoStack = nextEntry
                storedCharacters -= undoEntry.value.text.length
                redoStack = Entry(
                    value = undoEntry.value,
                    next = redoStack
                )
                nextEntry.value
            }
        }
    }

    fun redo(): TextFieldValue? {
        return redoStack?.let { redoEntry ->
            redoStack = redoEntry.next
            undoStack = Entry(
                value = redoEntry.value,
                next = undoStack
            )
            storedCharacters += redoEntry.value.text.length
            redoEntry.value
        }
    }
}