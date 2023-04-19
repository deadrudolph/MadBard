package com.deadrudolph.uicomponents.view.textfield.core.semantic

import androidx.compose.ui.Modifier

interface SemanticsModifier : Modifier.Element {
    @Deprecated(
        message = "SemanticsModifier.id is now unused and has been set to a fixed value. " +
                "Retrieve the id from LayoutInfo instead.",
        replaceWith = ReplaceWith("")
    )
    val id: Int get() = -1

    /**
     * The SemanticsConfiguration holds substantive data, especially a list of key/value pairs
     * such as (label -> "buttonName").
     */
    val semanticsConfiguration: SemanticsConfiguration
}