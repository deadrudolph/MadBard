package com.deadrudolph.feature_builder.presentation.ui.dialog

import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.deadrudolph.feature_builder.R
import com.deadrudolph.uicomponents.compose.view.ChordCustomView
import com.deadrudolph.uicomponents.ui_model.ChordUIModel

@Composable
internal fun ChordEditDialog(
    chord: ChordUIModel,
    modifier: Modifier,
    onDismiss: () -> Unit,
    onChordRemoved: () -> Unit
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
            IconButton(
                modifier = Modifier
                    .wrapContentSize()
                    .align(CenterHorizontally),
                onClick = {
                    onChordRemoved()
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = "Icon Delete"
                )
            }

            ChordCustomView(
                modifier = Modifier
                    .size(120.dp, 140.dp)
                    .padding(top = 10.dp),
                chordType = chord.chordType
            )

        }
    }
}