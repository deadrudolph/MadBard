package com.deadrudolph.uicomponents.view.textfield.core.string

interface ClipboardManager {
    /**
     * This method put the text into the Clipboard.
     *
     * @param annotatedString The [AnnotatedString] to be put into Clipboard.
     */
    @Suppress("GetterSetterNames")
    fun setText(annotatedString: AnnotatedString)

    /**
     * This method get the text from the Clipboard.
     *
     * @return The text in the Clipboard.
     * It could be null due to 2 reasons: 1. Clipboard is empty; 2. Cannot convert the
     * [CharSequence] text in Clipboard to [AnnotatedString].
     */
    fun getText(): AnnotatedString?

    /**
     * This method returns true if there is a text in the Clipboard, false otherwise.
     */
    fun hasText(): Boolean = getText()?.isNotEmpty() == true
}