package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.selection.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Constraints
import com.deadrudolph.uicomponents.view.textfield.core.HandleReferencePoint.*
import com.deadrudolph.uicomponents.view.textfield.extension.*
import com.deadrudolph.uicomponents.view.textfield.extension.cancelsTextSelection
import com.deadrudolph.uicomponents.view.textfield.extension.ceilToIntPx
import com.deadrudolph.uicomponents.view.textfield.extension.textFieldFocusModifier
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
@OptIn(InternalFoundationTextApi::class, ExperimentalFoundationApi::class)
internal fun CoreTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Color.Unspecified),
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = DefaultMinLines,
    imeOptions: ImeOptions = ImeOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() }
) {
    val focusRequester = remember { FocusRequester() }

    // CompositionLocals
    // If the text field is disabled or read-only, we should not deal with the input service
    val textInputService = if (!enabled || readOnly) null else LocalTextInputService.current
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val selectionBackgroundColor = LocalTextSelectionColors.current.backgroundColor
    val focusManager = LocalFocusManager.current

    // Scroll state
    val singleLine = maxLines == 1 && !softWrap && imeOptions.singleLine
    val orientation = if (singleLine) Orientation.Horizontal else Orientation.Vertical
    val scrollerPosition = rememberSaveable(
        orientation,
        saver = TextFieldScrollerPosition.Saver
    ) { TextFieldScrollerPosition(orientation) }

    // State
    val transformedText = remember(value, visualTransformation) {
        val transformed = visualTransformation.filterWithValidation(value.annotatedString)
        value.composition?.let {
            TextFieldDelegate.applyCompositionDecoration(it, transformed)
        } ?: transformed
    }

    val visualText = transformedText.text
    val offsetMapping = transformedText.offsetMapping

    // If developer doesn't pass new value to TextField, recompose won't happen but internal state
    // and IME may think it is updated. To fix this inconsistent state, enforce recompose.
    val scope = currentRecomposeScope
    val state = remember {
        TextFieldState(
            TextDelegate(
                text = visualText,
                style = textStyle,
                softWrap = softWrap,
                density = density,
                fontFamilyResolver = fontFamilyResolver
            ),
            recomposeScope = scope
        )
    }
    state.update(
        value.annotatedString,
        visualText,
        textStyle,
        softWrap,
        density,
        fontFamilyResolver,
        onValueChange,
        keyboardActions,
        focusManager,
        selectionBackgroundColor
    )

    // notify the EditProcessor of value every recomposition
    state.processor.reset(value, state.inputSession)

    val undoManager = remember { UndoManager() }
    undoManager.snapshotIfNeeded(value)

    val manager = remember { TextFieldSelectionManager(undoManager) }
    manager.offsetMapping = offsetMapping
    manager.visualTransformation = visualTransformation
    manager.onValueChange = state.onValueChange
    manager.state = state
    manager.value = value
    manager.clipboardManager = LocalClipboardManager.current
    manager.textToolbar = LocalTextToolbar.current
    manager.hapticFeedBack = LocalHapticFeedback.current
    manager.focusRequester = focusRequester
    manager.editable = !readOnly

    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    // Focus
    val focusModifier = Modifier.textFieldFocusModifier(
        enabled = enabled,
        focusRequester = focusRequester,
        interactionSource = interactionSource
    ) {
        if (state.hasFocus == it.isFocused) {
            return@textFieldFocusModifier
        }
        state.hasFocus = it.isFocused

        if (textInputService != null) {
            notifyTextInputServiceOnFocusChange(
                textInputService,
                state,
                value,
                imeOptions,
                offsetMapping
            )

            // The focusable modifier itself will request the entire focusable be brought into view
            // when it gains focus – in this case, that's the decoration box. However, since text
            // fields may have their own internal scrolling, and the decoration box can do anything,
            // we also need to specifically request that the cursor itself be brought into view.
            // TODO(b/216790855) If this request happens after the focusable's request, the field
            //  will only be scrolled far enough to show the cursor, _not_ the entire decoration
            //  box.
            if (it.isFocused) {
                state.layoutResult?.let { layoutResult ->
                    coroutineScope.launch {
                        bringIntoViewRequester.bringSelectionEndIntoView(
                            value,
                            state.textDelegate,
                            layoutResult.value,
                            offsetMapping
                        )
                    }
                }
            }
        }
        if (!it.isFocused) manager.deselect()
    }

    // Hide the keyboard if made disabled or read-only while focused (b/237308379).
    if (enabled && !readOnly) {
        // TODO(b/230536793) This is a workaround since we don't get an explicit focus blur event
        //  when the text field is removed from the composition entirely.
        DisposableEffect(state) {
            onDispose {
                if (state.hasFocus) {
                    onBlur(state)
                }
            }
        }
    }

    val pointerModifier = if (isInTouchMode) {
        val selectionModifier =
            Modifier.longPressDragGestureFilter(manager.touchSelectionObserver, enabled)
        Modifier
            .tapPressTextFieldModifier(interactionSource, enabled) { offset ->
                tapToFocus(state, focusRequester, !readOnly)
                if (state.hasFocus) {
                    if (state.handleState != HandleState.Selection) {
                        state.layoutResult?.let { layoutResult ->
                            TextFieldDelegate.setCursorOffset(
                                offset,
                                layoutResult,
                                state.processor,
                                offsetMapping,
                                state.onValueChange
                            )
                            // Won't enter cursor state when text is empty.
                            if (state.textDelegate.text.isNotEmpty()) {
                                state.handleState = HandleState.Cursor
                            }
                        }
                    } else {
                        manager.deselect(offset)
                    }
                }
            }
            .then(selectionModifier)
    } else {
        Modifier
            .mouseDragGestureDetector(
                observer = manager.mouseSelectionObserver,
                enabled = enabled
            )
            .pointerHoverIcon(PointerIcon.Text)
    }

    val drawModifier = Modifier.drawBehind {
        state.layoutResult?.let { layoutResult ->
            drawIntoCanvas { canvas ->
                TextFieldDelegate.draw(
                    canvas,
                    value,
                    offsetMapping,
                    layoutResult.value,
                    state.selectionPaint
                )
            }
        }
    }

    val onPositionedModifier = Modifier.onGloballyPositioned {
        state.layoutCoordinates = it
        if (enabled) {
            if (state.handleState == HandleState.Selection) {
                if (state.showFloatingToolbar) {
                    manager.showSelectionToolbar()
                } else {
                    manager.hideSelectionToolbar()
                }
                state.showSelectionHandleStart =
                    manager.isSelectionHandleInVisibleBound(isStartHandle = true)
                state.showSelectionHandleEnd =
                    manager.isSelectionHandleInVisibleBound(isStartHandle = false)
            } else if (state.handleState == HandleState.Cursor) {
                state.showCursorHandle =
                    manager.isSelectionHandleInVisibleBound(isStartHandle = true)
            }
            notifyFocusedRect(state, value, offsetMapping)
        }
        state.layoutResult?.innerTextFieldCoordinates = it
    }

    val isPassword = visualTransformation is PasswordVisualTransformation
    val semanticsModifier = Modifier.semantics(true) {
        // focused semantics are handled by Modifier.focusable()
        this.imeAction = imeOptions.imeAction
        this.editableText = transformedText.text
        this.textSelectionRange = value.selection
        if (!enabled) this.disabled()
        if (isPassword) this.password()
        getTextLayoutResult {
            if (state.layoutResult != null) {
                it.add(state.layoutResult!!.value)
                true
            } else {
                false
            }
        }
        setText { text ->
            // If the action is performed while in an active text editing session, treat this like
            // an IME command and update the text by going through the buffer. This keeps the buffer
            // state consistent if other IME commands are performed before the next recomposition,
            // and is used for the testing code path.
            state.inputSession?.let { session ->
                TextFieldDelegate.onEditCommand(
                    ops = listOf(DeleteAllCommand(), CommitTextCommand(text, 1)),
                    editProcessor = state.processor,
                    state.onValueChange,
                    session
                )
            } ?: run {
                state.onValueChange(TextFieldValue(text.text, TextRange(text.text.length)))
            }
            true
        }
        setSelection { selectionStart, selectionEnd, relativeToOriginalText ->
            // in traversal mode we get selection from the `textSelectionRange` semantics which is
            // selection in original text. In non-traversal mode selection comes from the Talkback
            // and indices are relative to the transformed text
            val start = if (relativeToOriginalText) {
                selectionStart
            } else {
                offsetMapping.transformedToOriginal(selectionStart)
            }
            val end = if (relativeToOriginalText) {
                selectionEnd
            } else {
                offsetMapping.transformedToOriginal(selectionEnd)
            }

            if (!enabled) {
                false
            } else if (start == value.selection.start && end == value.selection.end) {
                false
            } else if (start.coerceAtMost(end) >= 0 &&
                start.coerceAtLeast(end) <= value.annotatedString.length
            ) {
                // Do not show toolbar if it's a traversal mode (with the volume keys), or
                // if the cursor just moved to beginning or end.
                if (relativeToOriginalText || start == end) {
                    manager.exitSelectionMode()
                } else {
                    manager.enterSelectionMode()
                }
                state.onValueChange(
                    TextFieldValue(
                        value.annotatedString,
                        TextRange(start, end)
                    )
                )
                true
            } else {
                manager.exitSelectionMode()
                false
            }
        }
        onClick {
            // according to the documentation, we still need to provide proper semantics actions
            // even if the state is 'disabled'
            tapToFocus(state, focusRequester, !readOnly)
            true
        }
        onLongClick {
            manager.enterSelectionMode()
            true
        }
        if (!value.selection.collapsed && !isPassword) {
            copyText {
                manager.copy()
                true
            }
            if (enabled && !readOnly) {
                cutText {
                    manager.cut()
                    true
                }
            }
        }
        if (enabled && !readOnly) {
            pasteText {
                manager.paste()
                true
            }
        }
    }

    val cursorModifier =
        Modifier.cursor(state, value, offsetMapping, cursorBrush, enabled && !readOnly)

    DisposableEffect(manager) {
        onDispose { manager.hideSelectionToolbar() }
    }

    DisposableEffect(imeOptions) {
        if (textInputService != null && state.hasFocus) {
            state.inputSession = TextFieldDelegate.restartInput(
                textInputService = textInputService,
                value = value,
                editProcessor = state.processor,
                imeOptions = imeOptions,
                onValueChange = state.onValueChange,
                onImeActionPerformed = state.onImeActionPerformed
            )
        }
        onDispose { /* do nothing */ }
    }

    val textKeyInputModifier =
        Modifier.textFieldKeyInput(
            state = state,
            manager = manager,
            value = value,
            onValueChange = state.onValueChange,
            editable = !readOnly,
            singleLine = maxLines == 1,
            offsetMapping = offsetMapping,
            undoManager = undoManager
        )

    // Modifiers that should be applied to the outer text field container. Usually those include
    // gesture and semantics modifiers.
    val decorationBoxModifier = modifier
        .then(focusModifier)
        .interceptDPadAndMoveFocus(state, focusManager)
        .previewKeyEventToDeselectOnBack(state, manager)
        .then(textKeyInputModifier)
        .textFieldScrollable(scrollerPosition, interactionSource, enabled)
        .then(pointerModifier)
        .then(semanticsModifier)
        .onGloballyPositioned {
            state.layoutResult?.decorationBoxCoordinates = it
        }

    val showHandleAndMagnifier = enabled && state.hasFocus && state.isInTouchMode
    val magnifierModifier = if (showHandleAndMagnifier) {
        Modifier.textFieldMagnifier(manager)
    } else {
        Modifier
    }

    CoreTextFieldRootBox(decorationBoxModifier) {
        decorationBox {
            // Modifiers applied directly to the internal input field implementation. In general,
            // these will most likely include draw, layout and IME related modifiers.
            val coreTextFieldModifier = Modifier
                // min height is set for maxLines == 1 in order to prevent text cuts for single line
                // TextFields
                .heightIn(min = state.minHeightForSingleLineField)
                .heightInLines(
                    textStyle = textStyle,
                    minLines = minLines,
                    maxLines = maxLines
                )
                .textFieldScroll(
                    scrollerPosition,
                    value,
                    visualTransformation,
                    { state.layoutResult }
                )
                .then(cursorModifier)
                .then(drawModifier)
                .textFieldMinSize(textStyle)
                .then(onPositionedModifier)
                .then(magnifierModifier)
                .bringIntoViewRequester(bringIntoViewRequester)

            SimpleLayout(coreTextFieldModifier) {
                Layout(
                    content = { },
                    measurePolicy = object : MeasurePolicy {
                        override fun MeasureScope.measure(
                            measurables: List<Measurable>,
                            constraints: Constraints
                        ): MeasureResult {
                            val prevResult = Snapshot.withoutReadObservation {
                                state.layoutResult?.value
                            }
                            val (width, height, result) = TextFieldDelegate.layout(
                                state.textDelegate,
                                constraints,
                                layoutDirection,
                                prevResult
                            )
                            if (prevResult != result) {
                                state.layoutResult = TextLayoutResultProxy(result)
                                onTextLayout(result)
                                notifyFocusedRect(state, value, offsetMapping)
                            }

                            // calculate the min height for single line text to prevent text cuts.
                            // for single line text maxLines puts in max height constraint based on
                            // constant characters therefore if the user enters a character that is
                            // longer (i.e. emoji or a tall script) the text is cut
                            state.minHeightForSingleLineField = with(density) {
                                when (maxLines) {
                                    1 -> result.getLineBottom(0).ceilToIntPx()
                                    else -> 0
                                }.toDp()
                            }

                            return layout(
                                width = width,
                                height = height,
                                alignmentLines = mapOf(
                                    FirstBaseline to result.firstBaseline.roundToInt(),
                                    LastBaseline to result.lastBaseline.roundToInt()
                                )
                            ) {}
                        }

                        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
                            measurables: List<IntrinsicMeasurable>,
                            height: Int
                        ): Int {
                            state.textDelegate.layoutIntrinsics(layoutDirection)
                            return state.textDelegate.maxIntrinsicWidth
                        }
                    }
                )

                SelectionToolbarAndHandles(
                    manager = manager,
                    show = state.handleState == HandleState.Selection &&
                            state.layoutCoordinates != null &&
                            state.layoutCoordinates!!.isAttached &&
                            showHandleAndMagnifier
                )
                if (
                    state.handleState == HandleState.Cursor &&
                    !readOnly &&
                    showHandleAndMagnifier
                ) {
                    TextFieldCursorHandle(manager = manager)
                }
            }
        }
    }
}

@Composable
private fun CoreTextFieldRootBox(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier, propagateMinConstraints = true) {
        content()
    }
}

/**
 * The selection handle state of the TextField. It can be None, Selection or Cursor.
 * It determines whether the selection handle, cursor handle or only cursor is shown. And how
 * TextField handles gestures.
 */
internal enum class HandleState {
    /**
     * No selection is active in this TextField. This is the initial state of the TextField.
     * If the user long click on the text and start selection, the TextField will exit this state
     * and enters [HandleState.Selection] state. If the user tap on the text, the TextField
     * will exit this state and enters [HandleState.Cursor] state.
     */
    None,

    /**
     * Selection handle is displayed for this TextField. User can drag the selection handle to
     * change the selected text. If the user start editing the text, the TextField will exit this
     * state and enters [HandleState.None] state. If the user tap on the text, the TextField
     * will exit this state and enters [HandleState.Cursor] state.
     */
    Selection,

    /**
     * Cursor handle is displayed for this TextField. User can drag the cursor handle to change
     * the cursor position. If the user start editing the text, the TextField will exit this
     * state and enters [HandleState.None] state. If the user long click on the text and start
     * selection, the TextField will exit this state and enters [HandleState.Selection] state.
     * Also notice that TextField won't enter this state if the current input text is empty.
     */
    Cursor
}

/**
 * Indicates which handle is being dragged when the user is dragging on a text field handle.
 * @see TextFieldState.handleState
 */
internal enum class Handle {
    Cursor,
    SelectionStart,
    SelectionEnd
}

/**
 * Modifier to intercept back key presses, when supported by the platform, and deselect selected
 * text and clear selection popups.
 */
private fun Modifier.previewKeyEventToDeselectOnBack(
    state: TextFieldState,
    manager: TextFieldSelectionManager
) = onPreviewKeyEvent { keyEvent ->
    if (state.handleState == HandleState.Selection && keyEvent.cancelsTextSelection()) {
        manager.deselect()
        true
    } else {
        false
    }
}

/**
 * Request focus on tap. If already focused, makes sure the keyboard is requested.
 */
private fun tapToFocus(
    state: TextFieldState,
    focusRequester: FocusRequester,
    allowKeyboard: Boolean
) {
    if (!state.hasFocus) {
        focusRequester.requestFocus()
    } else if (allowKeyboard) {
        state.inputSession?.showSoftwareKeyboard()
    }
}

@OptIn(InternalFoundationTextApi::class)
private fun notifyTextInputServiceOnFocusChange(
    textInputService: TextInputService,
    state: TextFieldState,
    value: TextFieldValue,
    imeOptions: ImeOptions,
    offsetMapping: OffsetMapping
) {
    if (state.hasFocus) {
        state.inputSession = TextFieldDelegate.onFocus(
            textInputService,
            value,
            state.processor,
            imeOptions,
            state.onValueChange,
            state.onImeActionPerformed
        )
        notifyFocusedRect(state, value, offsetMapping)
    } else {
        onBlur(state)
    }
}

private fun onBlur(state: TextFieldState) {
    state.inputSession?.let { session ->
        TextFieldDelegate.onBlur(session, state.processor, state.onValueChange)
    }
    state.inputSession = null
}

/**
 * Calculates the location of the end of the current selection and requests that it be brought into
 * view using [bringIntoView][BringIntoViewRequester.bringIntoView].
 *
 * Text fields have a lot of different edge cases where they need to make sure they stay visible:
 *
 * 1. Focusable node newly receives focus – always bring entire node into view.
 * 2. Unfocused text field is tapped – always bring cursor area into view (conflicts with above, see
 *    b/216790855).
 * 3. Focused text field is tapped – always bring cursor area into view.
 * 4. Text input occurs – always bring cursor area into view.
 * 5. Scrollable parent resizes and the currently-focused item is now hidden – bring entire node
 *    into view if it was also in view before the resize. This handles the case of
 *    `softInputMode=ADJUST_RESIZE`. See b/216842427.
 * 6. Entire window is panned due to `softInputMode=ADJUST_PAN` – report the correct focused rect to
 *    the view system, and the view system itself will keep the focused area in view.
 *    See aosp/1964580.
 *
 * This function is used to handle 2, 3, and 4, and the others are automatically handled by the
 * focus system.
 */
@OptIn(ExperimentalFoundationApi::class, InternalFoundationTextApi::class)
internal suspend fun BringIntoViewRequester.bringSelectionEndIntoView(
    value: TextFieldValue,
    textDelegate: TextDelegate,
    textLayoutResult: TextLayoutResult,
    offsetMapping: OffsetMapping
) {
    val selectionEndInTransformed = offsetMapping.originalToTransformed(value.selection.max)
    val selectionEndBounds = when {
        selectionEndInTransformed < textLayoutResult.layoutInput.text.length -> {
            textLayoutResult.getBoundingBox(selectionEndInTransformed)
        }

        selectionEndInTransformed != 0 -> {
            textLayoutResult.getBoundingBox(selectionEndInTransformed - 1)
        }

        else -> { // empty text.
            val defaultSize = computeSizeForDefaultText(
                textDelegate.style,
                textDelegate.density,
                textDelegate.fontFamilyResolver
            )
            Rect(0f, 0f, 1.0f, defaultSize.height.toFloat())
        }
    }
    bringIntoView(selectionEndBounds)
}

@Composable
private fun SelectionToolbarAndHandles(manager: TextFieldSelectionManager, show: Boolean) {
    with(manager) {
        if (show) {
            // Check whether text layout result became stale. A stale text layout might be
            // completely unrelated to current TextFieldValue, causing offset errors.
            state?.layoutResult?.value?.takeIf { !(state?.isLayoutResultStale ?: true) }?.let {
                if (!value.selection.collapsed) {
                    val startOffset = offsetMapping.originalToTransformed(value.selection.start)
                    val endOffset = offsetMapping.originalToTransformed(value.selection.end)
                    val startDirection = it.getBidiRunDirection(startOffset)
                    val endDirection = it.getBidiRunDirection(max(endOffset - 1, 0))
                    if (manager.state?.showSelectionHandleStart == true) {
                        TextFieldSelectionHandle(
                            isStartHandle = true,
                            direction = startDirection,
                            manager = manager
                        )
                    }
                    if (manager.state?.showSelectionHandleEnd == true) {
                        TextFieldSelectionHandle(
                            isStartHandle = false,
                            direction = endDirection,
                            manager = manager
                        )
                    }
                }

                state?.let { textFieldState ->
                    // If in selection mode (when the floating toolbar is shown) a new symbol
                    // from the keyboard is entered, text field should enter the editing mode
                    // instead.
                    if (isTextChanged()) textFieldState.showFloatingToolbar = false
                    if (textFieldState.hasFocus) {
                        if (textFieldState.showFloatingToolbar) showSelectionToolbar()
                        else hideSelectionToolbar()
                    }
                }
            }
        } else hideSelectionToolbar()
    }
}

@Composable
internal fun TextFieldCursorHandle(manager: TextFieldSelectionManager) {
    if (manager.state?.showCursorHandle == true) {
        val observer = remember(manager) { manager.cursorDragObserver() }
        val position = manager.getCursorPosition(LocalDensity.current)
        CursorHandle(
            handlePosition = position,
            modifier = Modifier
                .pointerInput(observer) {
                    detectDownAndDragGesturesWithObserver(observer)
                }
                .semantics {
                    this[SelectionHandleInfoKey] = SelectionHandleInfo(
                        handle = Handle.Cursor,
                        position = position
                    )
                },
            content = null
        )
    }
}

// TODO(b/262648050) Try to find a better API.
@OptIn(InternalFoundationTextApi::class)
private fun notifyFocusedRect(
    state: TextFieldState,
    value: TextFieldValue,
    offsetMapping: OffsetMapping
) {
    // If this reports state reads it causes an invalidation cycle.
    // This function doesn't need to be invalidated anyway because it's already explicitly called
    // after updating text layout or position.
    Snapshot.withoutReadObservation {
        val layoutResult = state.layoutResult ?: return
        val inputSession = state.inputSession ?: return
        val layoutCoordinates = state.layoutCoordinates ?: return
        TextFieldDelegate.notifyFocusedRect(
            value,
            state.textDelegate,
            layoutResult.value,
            layoutCoordinates,
            inputSession,
            state.hasFocus,
            offsetMapping
        )
    }
}

