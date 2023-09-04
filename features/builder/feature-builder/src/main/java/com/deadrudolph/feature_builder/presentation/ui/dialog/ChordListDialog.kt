package com.deadrudolph.feature_builder.presentation.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.feature_builder.presentation.ui.chord.ChordPicker

@Composable
internal fun ChordsListDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onChordSelected: (ChordType) -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            decorFitsSystemWindows = false
        )
    ) {
        ChordPicker(
            modifier = modifier,
            onChordSelected = { chord ->
                onChordSelected(chord)
            },
            onDismissDialog = onDismiss
        )
    }
}
