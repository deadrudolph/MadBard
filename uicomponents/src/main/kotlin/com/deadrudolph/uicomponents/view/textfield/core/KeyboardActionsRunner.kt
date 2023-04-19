package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import com.deadrudolph.uicomponents.view.textfield.core.input.TextInputSession

internal class KeyboardActionRunner : KeyboardActionScope {

    /**
     * The developer specified [KeyboardActions].
     */
    lateinit var keyboardActions: KeyboardActions

    /**
     * A reference to the [FocusManager] composition local.
     */
    lateinit var focusManager: FocusManager

    /**
     * A reference to the current [TextInputSession].
     */
    // TODO(b/241399013) replace with SoftwareKeyboardController when it becomes stable.
    var inputSession: TextInputSession? = null

    /**
     * Run the keyboard action corresponding to the specified imeAction. If a keyboard action is
     * not specified, use the default implementation provided by [defaultKeyboardAction].
     */
    fun runAction(imeAction: ImeAction) {
        val keyboardAction = when (imeAction) {
            ImeAction.Done -> keyboardActions.onDone
            ImeAction.Go -> keyboardActions.onGo
            ImeAction.Next -> keyboardActions.onNext
            ImeAction.Previous -> keyboardActions.onPrevious
            ImeAction.Search -> keyboardActions.onSearch
            ImeAction.Send -> keyboardActions.onSend
            ImeAction.Default, ImeAction.None -> null
            else -> error("invalid ImeAction")
        }
        keyboardAction?.invoke(this) ?: defaultKeyboardAction(imeAction)
    }

    /**
     * Default implementations for [KeyboardActions].
     */
    override fun defaultKeyboardAction(imeAction: ImeAction) {
        when (imeAction) {
            ImeAction.Next -> focusManager.moveFocus(FocusDirection.Next)
            ImeAction.Previous -> focusManager.moveFocus(FocusDirection.Previous)
            ImeAction.Done -> inputSession?.hideSoftwareKeyboard()
            // Note: Don't replace this with an else. These are specified explicitly so that we
            // don't forget to update this when statement when new imeActions are added.
            ImeAction.Go, ImeAction.Search, ImeAction.Send, ImeAction.Default, ImeAction.None -> Unit // Do Nothing.
        }
    }
}