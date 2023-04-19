package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.emoji2.text.EmojiCompat

internal interface EmojiCompatStatusDelegate {
    val fontLoaded: State<Boolean>
}

private class ImmutableBool(override val value: Boolean) : State<Boolean>
private val Falsey = ImmutableBool(false)

internal object EmojiCompatStatus : EmojiCompatStatusDelegate {
    private var delegate: EmojiCompatStatusDelegate = DefaultImpl()

    /**
     * True if the emoji2 font is currently loaded and processing will be successful
     *
     * False when emoji2 may complete loading in the future.
     */
    override val fontLoaded: State<Boolean>
        get() = delegate.fontLoaded

    /**
     * Do not call.
     *
     * This is for tests that want to control EmojiCompatStatus behavior.
     */
    @VisibleForTesting
    internal fun setDelegateForTesting(newDelegate: EmojiCompatStatusDelegate?) {
        delegate = newDelegate ?: DefaultImpl()
    }
}

private class DefaultImpl : EmojiCompatStatusDelegate {

    private var loadState: State<Boolean>?

    init {
        loadState = if (EmojiCompat.isConfigured()) {
            getFontLoadState()
        } else {
            // EC isn't configured yet, will check again in getter
            null
        }
    }

    override val fontLoaded: State<Boolean>
        get() = if (loadState != null) {
            loadState!!
        } else {
            // EC wasn't configured last time, check again and update loadState if it's ready
            if (EmojiCompat.isConfigured()) {
                loadState = getFontLoadState()
                loadState!!
            } else {
                // ec disabled path
                // no observations allowed, this is pre init
                Falsey
            }
        }

    private fun getFontLoadState(): State<Boolean> {
        val ec = EmojiCompat.get()
        return if (ec.loadState == EmojiCompat.LOAD_STATE_SUCCEEDED) {
            ImmutableBool(true)
        } else {
            val mutableLoaded = mutableStateOf(false)
            val initCallback = object : EmojiCompat.InitCallback() {
                override fun onInitialized() {
                    mutableLoaded.value = true // update previous observers
                    loadState = ImmutableBool(true) // never observe again
                }

                override fun onFailed(throwable: Throwable?) {
                    loadState = Falsey // never observe again
                }
            }
            ec.registerInitCallback(initCallback)
            mutableLoaded
        }
    }
}