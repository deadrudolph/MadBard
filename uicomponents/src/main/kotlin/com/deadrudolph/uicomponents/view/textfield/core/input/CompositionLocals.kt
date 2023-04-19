package com.deadrudolph.uicomponents.view.textfield.core.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import com.deadrudolph.uicomponents.view.textfield.core.string.ClipboardManager

/*val LocalTextInputService = staticCompositionLocalOf<TextInputService?> { null  }

val LocalClipboardManager = staticCompositionLocalOf<ClipboardManager> {
    noLocalProvidedFor("LocalClipboardManager")
}*/

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

@Composable
fun provideCompositionLocals() {
    LocalView.current.rootView
}