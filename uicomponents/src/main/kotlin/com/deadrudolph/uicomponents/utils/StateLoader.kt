package com.deadrudolph.uicomponents.utils

import androidx.compose.runtime.Composable
import com.puls.stateutil.Result

@Composable
fun <T> Result<T>.LoadState(
    onRestartState: () -> Unit,
    enableLoading: Boolean = true,
    loadingView: (@Composable (Boolean) -> Unit)? = null,
    errorView: (@Composable (String) -> Unit)? = null,
    successContent: @Composable (T) -> Unit
) {
    when (this) {
        is Result.Success -> {
            successContent(this.data)
        }
        is Result.Failure -> {
            ErrorAction(
                errorView = errorView,
                onRestartState = onRestartState
            )
        }
        is Result.Error -> {
            ErrorAction(
                errorView = errorView,
                onRestartState = onRestartState
            )
        }
        is Result.Loading -> {
            if (!enableLoading) return

            if (loadingView != null) {
                loadingView(this.state)
            } else {
                if (this.state) LoadingDialog()
            }
        }
        is Result.IDLE -> {
            //Default action state
        }
    }
}

@Composable
private fun LoadingDialog() {
    //TODO need loading dialog
}

@Composable
private fun ErrorAction(
    message: String = "",
    errorView: @Composable ((String) -> Unit)?,
    onRestartState: () -> Unit
) {
    if (errorView != null) {
        errorView(message)
    } else {
        //TODO need error dialog
        /* ErrorDialog(
             message = message,
             onConfirmClick = onRestartState
         )*/
    }
}
