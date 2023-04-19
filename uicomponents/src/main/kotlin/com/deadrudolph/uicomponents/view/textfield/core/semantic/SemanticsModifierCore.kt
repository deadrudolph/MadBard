package com.deadrudolph.uicomponents.view.textfield.core.semantic

import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.NoInspectorInfo
import java.util.concurrent.atomic.AtomicInteger

internal class SemanticsModifierCore(
    mergeDescendants: Boolean,
    clearAndSetSemantics: Boolean,
    properties: (SemanticsPropertyReceiver.() -> Unit),
    inspectorInfo: InspectorInfo.() -> Unit = NoInspectorInfo
) : SemanticsModifier, InspectorValueInfo(inspectorInfo) {
    override val semanticsConfiguration: SemanticsConfiguration =
        SemanticsConfiguration().also {
            it.isMergingSemanticsOfDescendants = mergeDescendants
            it.isClearingSemantics = clearAndSetSemantics
            it.properties()
        }

    companion object {
        private var lastIdentifier = AtomicInteger(0)
        fun generateSemanticsId() = lastIdentifier.addAndGet(1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SemanticsModifierCore) return false
        if (semanticsConfiguration != other.semanticsConfiguration) return false
        return true
    }

    override fun hashCode(): Int {
        return semanticsConfiguration.hashCode()
    }
}