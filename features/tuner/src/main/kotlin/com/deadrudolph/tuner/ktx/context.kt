package com.deadrudolph.tuner.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean =
    runCatching {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

fun Context.openExternalAppSettings() {
    runCatching {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", packageName, null))
            .apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            }
            .let(::startActivity)
    }.onFailure(::logError)
}

fun Activity.keepScreenOn() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
