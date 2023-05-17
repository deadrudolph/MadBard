package com.deadrudolph.common_utils.extension

import android.content.res.Resources

val Float.pxToDp
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

val Int.pxToDp
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

val Float.dpToPx
    get() = this * Resources.getSystem().displayMetrics.scaledDensity
