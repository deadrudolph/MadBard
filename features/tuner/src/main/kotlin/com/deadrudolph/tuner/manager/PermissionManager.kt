package com.deadrudolph.tuner.manager

import android.Manifest
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.deadrudolph.tuner.ktx.hasPermission
import com.markodevcic.peko.Peko
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager @Inject constructor(
    private val context: Context
)  {

    private val _state by lazy { MutableStateFlow(hasRequiredPermissions) }
    val state by lazy { _state.asStateFlow() }

    val hasRequiredPermissions: Boolean
        get() = context.hasPermission(Manifest.permission.RECORD_AUDIO)

    suspend fun requestPermissions(activity: ComponentActivity) {
        runCatching {
            Peko.requestPermissionsAsync(activity, Manifest.permission.RECORD_AUDIO)
            updateState()
        }
    }

    fun updateState() {
        _state.value = hasRequiredPermissions
    }
}
