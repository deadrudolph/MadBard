package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.text.*
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.EditProcessor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputSession
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@OptIn(InternalFoundationTextApi::class)
internal class TextFieldState(
    var textDelegate: TextDelegate,
    val recomposeScope: RecomposeScope
) {
    val processor = EditProcessor()
    var inputSession: TextInputSession? = null

    /**
     * This should be a state as every time we update the value we need to redraw it.
     * state observation during onDraw callback will make it work.
     */
    var hasFocus by mutableStateOf(false)

    var isInTouchMode by mutableStateOf(true)

    /**
     * Set to a non-zero value for single line TextFields in order to prevent text cuts.
     */
    var minHeightForSingleLineField by mutableStateOf(0.dp)

    /** The last layout coordinates for the Text's layout, used by selection */
    var layoutCoordinates: LayoutCoordinates? = null

    /**
     * You should be using proxy type [TextLayoutResultProxy] if you need to translate touch
     * offset into text's coordinate system. For example, if you add a gesture on top of the
     * decoration box and want to know the character in text for the given touch offset on
     * decoration box.
     * When you don't need to shift the touch offset, you should be using `layoutResult.value`
     * which omits the proxy and calls the layout result directly. This is needed when you work
     * with the text directly, and not the decoration box. For example, cursor modifier gets
     * position using the [TextFieldValue.selection] value which corresponds to the text directly,
     * and therefore does not require the translation.
     */
    private val layoutResultState: MutableState<TextLayoutResultProxy?> = mutableStateOf(null)
    var layoutResult: TextLayoutResultProxy?
        get() = layoutResultState.value
        set(value) {
            layoutResultState.value = value
            isLayoutResultStale = false
        }

    /**
     * [textDelegate] keeps a reference to the visually transformed text that is visible to the
     * user. TextFieldState needs to have access to the underlying value that is not transformed
     * while making comparisons that test whether the user input actually changed.
     *
     * This field contains the real value that is passed by the user before it was visually
     * transformed.
     */
    var untransformedText: AnnotatedString? = null

    /**
     * The gesture detector state, to indicate whether current state is selection, cursor
     * or editing.
     *
     * In the none state, no selection or cursor handle is shown, only the cursor is shown.
     * TextField is initially in this state. To enter this state, input anything from the
     * keyboard and modify the text.
     *
     * In the selection state, there is no cursor shown, only selection is shown. To enter
     * the selection mode, just long press on the screen. In this mode, finger movement on the
     * screen changes selection instead of moving the cursor.
     *
     * In the cursor state, no selection is shown, and the cursor and the cursor handle are shown.
     * To enter the cursor state, tap anywhere within the TextField.(The TextField will stay in the
     * edit state if the current text is empty.) In this mode, finger movement on the screen
     * moves the cursor.
     */
    var handleState by mutableStateOf(HandleState.None)

    /**
     * A flag to check if the floating toolbar should show.
     */
    var showFloatingToolbar = false

    /**
     * True if the position of the selection start handle is within a visible part of the window
     * (i.e. not scrolled out of view) and the handle should be drawn.
     */
    var showSelectionHandleStart by mutableStateOf(false)

    /**
     * True if the position of the selection end handle is within a visible part of the window
     * (i.e. not scrolled out of view) and the handle should be drawn.
     */
    var showSelectionHandleEnd by mutableStateOf(false)

    /**
     * True if the position of the cursor is within a visible part of the window (i.e. not scrolled
     * out of view) and the handle should be drawn.
     */
    var showCursorHandle by mutableStateOf(false)

    /**
     * TextFieldState holds both TextDelegate and layout result. However, these two values are not
     * updated at the same time. TextDelegate is updated during composition according to new
     * arguments while layoutResult is updated during layout phase. Therefore, [layoutResult] might
     * not indicate the result of [textDelegate] at a given time during composition. This variable
     * indicates whether layout result is lacking behind the latest TextDelegate.
     */
    var isLayoutResultStale: Boolean = true
        private set

    private val keyboardActionRunner: KeyboardActionRunner = KeyboardActionRunner()

    /**
     * DO NOT USE, use [onValueChange] instead. This is original callback provided to the TextField.
     * In order the CoreTextField to work, the recompose.invalidate() has to be called when we call
     * the callback and [onValueChange] is a wrapper that mainly does that.
     */
    private var onValueChangeOriginal: (TextFieldValue) -> Unit = {}

    val onValueChange: (TextFieldValue) -> Unit = {
        if (it.text != untransformedText?.text) {
            // Text has been changed, enter the HandleState.None and hide the cursor handle.
            handleState = HandleState.None
        }
        onValueChangeOriginal(it)
        recomposeScope.invalidate()
    }

    val onImeActionPerformed: (ImeAction) -> Unit = { imeAction ->
        keyboardActionRunner.runAction(imeAction)
    }

    /** The paint used to draw highlight background for selected text. */
    val selectionPaint: Paint = Paint()

    fun update(
        untransformedText: AnnotatedString,
        visualText: AnnotatedString,
        textStyle: TextStyle,
        softWrap: Boolean,
        density: Density,
        fontFamilyResolver: FontFamily.Resolver,
        onValueChange: (TextFieldValue) -> Unit,
        keyboardActions: KeyboardActions,
        focusManager: FocusManager,
        selectionBackgroundColor: Color
    ) {
        this.onValueChangeOriginal = onValueChange
        this.selectionPaint.color = selectionBackgroundColor
        this.keyboardActionRunner.apply {
            this.keyboardActions = keyboardActions
            this.focusManager = focusManager
            this.inputSession = this@TextFieldState.inputSession
        }
        this.untransformedText = untransformedText

        val newTextDelegate = updateTextDelegate(
            current = textDelegate,
            text = visualText,
            style = textStyle,
            softWrap = softWrap,
            density = density,
            fontFamilyResolver = fontFamilyResolver,
            placeholders = emptyList(),
        )

        if (textDelegate !== newTextDelegate) isLayoutResultStale = true
        textDelegate = newTextDelegate
    }

    @OptIn(InternalFoundationTextApi::class)
    internal fun updateTextDelegate(
        current: TextDelegate,
        text: AnnotatedString,
        style: TextStyle,
        density: Density,
        fontFamilyResolver: FontFamily.Resolver,
        softWrap: Boolean = true,
        overflow: TextOverflow = TextOverflow.Clip,
        maxLines: Int = Int.MAX_VALUE,
        minLines: Int = DefaultMinLines,
        placeholders: List<AnnotatedString.Range<Placeholder>>
    ): TextDelegate {
        // NOTE(text-perf-review): whenever we have remember intrinsic implemented, this might be a
        // lot slower than the equivalent `remember(a, b, c, ...) { ... }` call.
        return if (current.text != text ||
            current.style != style ||
            current.softWrap != softWrap ||
            current.overflow != overflow ||
            current.maxLines != maxLines ||
            current.minLines != minLines ||
            current.density != density ||
            current.placeholders != placeholders ||
            current.fontFamilyResolver !== fontFamilyResolver
        ) {
            TextDelegate(
                text = text,
                style = style,
                softWrap = softWrap,
                overflow = overflow,
                maxLines = maxLines,
                minLines = minLines,
                density = density,
                fontFamilyResolver = fontFamilyResolver,
                placeholders = placeholders,
            )
        } else {
            current
        }
    }
}