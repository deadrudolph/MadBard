package com.deadrudolph.feature_builder.presentation.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.deadrudolph.feature_builder.R.drawable
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
internal fun TextEditDialog(
    modifier: Modifier,
    currentText: String,
    onTextEdited: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val textState = remember {
        mutableStateOf(currentText)
    }
    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        val brush = Brush.linearGradient(
            colors = listOf(CustomTheme.colors.dark_700_65, CustomTheme.colors.dark_800)
        )

        Column(
            modifier = modifier.then(
                Modifier
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(brush)
            )
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(CenterHorizontally)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                value = textState.value,
                textStyle = CustomTheme.typography.songsBuilder.copy(
                    color = Color.White
                ),
                cursorBrush = SolidColor(Color.White),
                onValueChange = {
                    textState.value = it
                },
                maxLines = 3
            )

            IconButton(
                modifier = Modifier
                    .size(50.dp, 50.dp)
                    .align(CenterHorizontally)
                    .padding(top = 10.dp),
                onClick = {
                    onTextEdited(textState.value)
                }
            ) {
                Image(
                    painter = painterResource(id = drawable.ic_check),
                    contentDescription = "Icon Edit"
                )
            }
        }
    }
}
