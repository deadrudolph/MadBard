package com.deadrudolph.tuner.view.tuner

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deadrudolph.tuner.manager.MessagingManager
import com.deadrudolph.tuner.manager.PermissionManager
import com.deadrudolph.tuner.manager.SettingsManager
import com.deadrudolph.tuner.manager.TunerManager
import com.deadrudolph.tuner.model.settings.Settings
import com.deadrudolph.tuner.model.tuner.Tuning
import com.deadrudolph.tuner.model.tuner.TuningDeviationPrecision
import com.deadrudolph.tuner.model.tuner.TuningDeviationResult
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TunerViewModel @Inject constructor(
    private val tunerManager: TunerManager,
    private val settingsManager: SettingsManager,
    private val permissionManager: PermissionManager,
    private val messagingManager: MessagingManager
) : ViewModel() {

    private val _state by lazy {
        MutableStateFlow(
            TunerState(
                settings = settingsManager.settings,
                hasRequiredPermissions = permissionManager.hasRequiredPermissions
            )
        )
    }
    val state by lazy { _state.asStateFlow() }

    fun startTuner(lifecycleOwner: LifecycleOwner) {
        tunerManager.bindLifecycle(lifecycleOwner)
        viewModelScope.launch {
            playStartAnimation()
            setupState()
        }
    }

    fun updatePermissionState() {
        permissionManager.updateState()
    }

    fun updateSettings(settings: Settings) {
        settingsManager.settings = settings
    }

    fun setupState() {
        tunerManager.state
            .mergeState { state, tuning ->
                state.copy(tuning = tuning)
            }
            .launchIn(viewModelScope)

        settingsManager.state
            .mergeState { state, settings ->
                tunerManager.restartListener()
                state.copy(settings = settings)
            }
            .launchIn(viewModelScope)

        permissionManager.state
            .mergeState { state, hasRequiredPermissions ->
                tunerManager.restartListener()
                state.copy(hasRequiredPermissions = hasRequiredPermissions)
            }
            .launchIn(viewModelScope)

        messagingManager.state
            .mergeState { state, message ->
                state.copy(message = message)
            }
            .launchIn(viewModelScope)
    }

    fun consumeMessage() {
        messagingManager.consume()
    }

    suspend fun requestPermissions(context: ComponentActivity) {
        if (permissionManager.hasRequiredPermissions.not()) {
            permissionManager.requestPermissions(context)
            _state.value = _state.value.copy(hasRequiredPermissions = permissionManager.hasRequiredPermissions)
        }
    }

    suspend fun playStartAnimation() {
        delay(500)

        (50 downTo 0 step 10).forEach { deviation ->
            updateAnimation(deviation)
            delay(150)
        }

        (0..50 step 10).forEach { deviation ->
            updateAnimation(deviation)
            delay(150)
        }

        _state.value = _state.value.copy(
            tuning = Tuning(
                deviation = TuningDeviationResult.NotDetected
            )
        )

        delay(500)
    }

    private fun updateAnimation(deviation: Int) {
        _state.value = _state.value.copy(
            tuning = Tuning(
                deviation = TuningDeviationResult.Animation(
                    negativeValue = -deviation,
                    negativePrecision = TuningDeviationPrecision.fromDeviation(
                        deviation = -deviation,
                        offset = settingsManager.tunerDeviationPrecision.offset
                    ),
                    positiveValue = deviation,
                    positivePrecision = TuningDeviationPrecision.fromDeviation(
                        deviation = deviation,
                        offset = settingsManager.tunerDeviationPrecision.offset
                    )
                )
            )
        )
    }

    private fun <T> Flow<T>.mergeState(action: (TunerState, T) -> TunerState): Flow<T> =
        onEach { value ->
            _state.value = action(_state.value, value)
        }
}
