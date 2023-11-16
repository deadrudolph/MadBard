package com.deadrudolph.tuner.ktx

import android.net.Uri
import androidx.core.net.MailTo

fun Uri.isEmail(): Boolean =
    scheme == MailTo.MAILTO_SCHEME
