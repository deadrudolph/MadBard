package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import android.graphics.Shader
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ShaderBrush

internal class ShaderBrushSpan(
    val shaderBrush: ShaderBrush,
    val alpha: Float
) : CharacterStyle(), UpdateAppearance {
    var size: Size = Size.Unspecified
    private var cachedShader: Pair<Size, Shader>? = null

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.setAlpha(alpha)

        if (size.isUnspecified) return

        val finalCachedShader = cachedShader

        val shader = if (finalCachedShader == null || finalCachedShader.first != size) {
            // if cached shader is not initialized or the size has changed, recreate the shader
            shaderBrush.createShader(size)
        } else {
            // reuse the earlier created shader
            finalCachedShader.second
        }

        textPaint.shader = shader
        cachedShader = size to shader
    }
}