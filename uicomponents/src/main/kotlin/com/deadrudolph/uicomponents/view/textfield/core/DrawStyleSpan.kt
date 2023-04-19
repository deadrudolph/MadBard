package com.deadrudolph.uicomponents.view.textfield.core

import android.graphics.Paint
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPathEffect
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

internal class DrawStyleSpan(
    val drawStyle: DrawStyle
) : CharacterStyle(), UpdateAppearance {
    override fun updateDrawState(textPaint: TextPaint?) {
        textPaint?.run {
            when (drawStyle) {
                Fill -> style = Paint.Style.FILL
                is Stroke -> {
                    style = Paint.Style.STROKE
                    strokeWidth = drawStyle.width
                    strokeMiter = drawStyle.miter
                    strokeJoin = drawStyle.join.toAndroidJoin()
                    strokeCap = drawStyle.cap.toAndroidCap()
                    pathEffect = drawStyle.pathEffect?.asAndroidPathEffect()
                }
            }
        }
    }

    private fun StrokeJoin.toAndroidJoin(): Paint.Join {
        return when (this) {
            StrokeJoin.Miter -> Paint.Join.MITER
            StrokeJoin.Round -> Paint.Join.ROUND
            StrokeJoin.Bevel -> Paint.Join.BEVEL
            else -> Paint.Join.MITER
        }
    }

    private fun StrokeCap.toAndroidCap(): Paint.Cap {
        return when (this) {
            StrokeCap.Butt -> Paint.Cap.BUTT
            StrokeCap.Round -> Paint.Cap.ROUND
            StrokeCap.Square -> Paint.Cap.SQUARE
            else -> Paint.Cap.BUTT
        }
    }
}