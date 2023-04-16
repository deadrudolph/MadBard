package com.deadrudolph.common_utils.extension

import android.content.res.Resources

val Float.pxToDp
    get() = this / Resources.getSystem().displayMetrics.density

val Int.pxToDp
    get() = this / Resources.getSystem().displayMetrics.density
