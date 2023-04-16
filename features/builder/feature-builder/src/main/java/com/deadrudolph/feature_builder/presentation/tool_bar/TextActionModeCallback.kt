package com.deadrudolph.feature_builder.presentation.tool_bar

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.compose.ui.geometry.Rect
import com.deadrudolph.feature_builder.R

internal const val MENU_ITEM_COPY = 0
internal const val MENU_ITEM_PASTE = 1
internal const val MENU_ITEM_CUT = 2
internal const val MENU_ITEM_SELECT_ALL = 3
internal const val MENU_ITEM_ADD_CHORD = 4

internal class TextActionModeCallback(
    var rect: Rect = Rect.Zero,
    var onCopyRequested: (() -> Unit)? = null,
    var onPasteRequested: (() -> Unit)? = null,
    var onCutRequested: (() -> Unit)? = null,
    var onSelectAllRequested: (() -> Unit)? = null,
    var onAddNewChordRequested: (() -> Unit)? = null
) {
    fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        requireNotNull(menu)
        requireNotNull(mode)

        onAddNewChordRequested?.let {
            menu.add(0, MENU_ITEM_ADD_CHORD, 4, R.string.add_chord)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onCopyRequested?.let {
            menu.add(0, MENU_ITEM_COPY, 0, android.R.string.copy)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onPasteRequested?.let {
            menu.add(0, MENU_ITEM_PASTE, 1, android.R.string.paste)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onCutRequested?.let {
            menu.add(0, MENU_ITEM_CUT, 2, android.R.string.cut)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }

        onSelectAllRequested?.let {
            menu.add(0, MENU_ITEM_SELECT_ALL, 3, android.R.string.selectAll)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        return true
    }

    fun onPrepareActionMode(): Boolean {
        return false
    }

    fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            MENU_ITEM_COPY -> onCopyRequested?.invoke()
            MENU_ITEM_PASTE -> onPasteRequested?.invoke()
            MENU_ITEM_CUT -> onCutRequested?.invoke()
            MENU_ITEM_SELECT_ALL -> onSelectAllRequested?.invoke()
            MENU_ITEM_ADD_CHORD -> onAddNewChordRequested?.invoke()
            else -> return false
        }
        mode?.finish()
        return true
    }

    fun onDestroyActionMode() {}
}