package com.deadrudolph.feature_builder.presentation.tool_bar

import android.view.ActionMode
import android.view.View
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

internal class AndroidTextToolbar(
    private val view: View,
    private val onAddNewChord: () -> Unit
) : TextToolbar {

    private var actionMode: ActionMode? = null
    private val textActionModeCallback: TextActionModeCallback = TextActionModeCallback()
    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    override fun hide() {
        status = TextToolbarStatus.Hidden
        actionMode?.finish()
        actionMode = null
    }

    override fun showMenu(
        rect: androidx.compose.ui.geometry.Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        textActionModeCallback.rect = rect
        textActionModeCallback.onCopyRequested = onCopyRequested
        textActionModeCallback.onCutRequested = onCutRequested
        textActionModeCallback.onPasteRequested = onPasteRequested
        textActionModeCallback.onSelectAllRequested = onSelectAllRequested
        textActionModeCallback.onAddNewChordRequested = onAddNewChord
        if (actionMode == null) {
            status = TextToolbarStatus.Shown
            actionMode = TextToolbarHelperMethods.startActionMode(
                view,
                FloatingTextActionModeCallback(textActionModeCallback),
                ActionMode.TYPE_FLOATING
            )
        } else {
            actionMode?.invalidate()
        }
    }
}

internal object TextToolbarHelperMethods {

    fun startActionMode(
        view: View,
        actionModeCallback: ActionMode.Callback,
        type: Int
    ): ActionMode {
        return view.startActionMode(
            actionModeCallback,
            type
        )
    }
}