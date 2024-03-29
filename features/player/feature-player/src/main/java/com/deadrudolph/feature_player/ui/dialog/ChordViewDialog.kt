package com.deadrudolph.feature_player.ui.dialog

import android.view.Gravity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.view.ChordCustomView

@Composable
internal fun ChordViewDialog(
    chordType: ChordType,
    modifier: Modifier,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {

        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.TOP)

        Column(
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = chordType.marker,
                    style = CustomTheme.typography.chord.copy(color = Color.White)
                )
            }

            ChordCustomView(
                modifier = Modifier
                    .size(120.dp, 140.dp)
                    .padding(top = 10.dp),
                chordType = chordType
            )
        }
    }
}
