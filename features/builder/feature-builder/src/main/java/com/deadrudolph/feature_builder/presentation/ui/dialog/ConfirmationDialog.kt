package com.deadrudolph.feature_builder.presentation.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.deadrudolph.feature_builder.R.drawable.ic_check
import com.deadrudolph.uicomponents.R.drawable.ic_cross
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
internal fun ConfirmationDialog(
    modifier: Modifier,
    onConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {

        val brush = Brush.linearGradient(
            colors = listOf(CustomTheme.colors.dark_700_65, CustomTheme.colors.dark_800)
        )

        Row(
            modifier = modifier.then(
                Modifier
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(brush)
            )
        ) {
            IconButton(
                modifier = Modifier
                    .size(50.dp, 50.dp)
                    .align(Alignment.CenterVertically)
                    .padding(all = 10.dp),
                onClick = {
                    onDismiss()
                }
            ) {
                Image(
                    painter = painterResource(id = ic_cross),
                    contentDescription = "Icon Edit"
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(start = 40.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                    .size(50.dp, 50.dp)
                    .align(Alignment.CenterVertically),
                onClick = {
                    onConfirmed()
                }
            ) {
                Image(
                    painter = painterResource(id = ic_check),
                    contentDescription = "Icon Edit"
                )
            }
        }
    }
}
