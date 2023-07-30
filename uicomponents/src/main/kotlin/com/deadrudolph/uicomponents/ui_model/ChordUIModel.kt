package com.deadrudolph.uicomponents.ui_model

import com.deadrudolph.common_domain.model.ChordType

data class ChordUIModel(
    val chordType: ChordType,
    val horizontalOffset: Int,
    val position: Int,
    val positionOverlapCharCount: Int = 0
) {

    /**Returns UI position depending on line position overlap*/
    fun getUIPosition(): Int {
        return position - positionOverlapCharCount
    }
}
