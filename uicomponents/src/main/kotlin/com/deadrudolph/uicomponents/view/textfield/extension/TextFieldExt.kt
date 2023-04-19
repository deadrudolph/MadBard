package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.deadrudolph.uicomponents.view.textfield.core.*
import com.deadrudolph.uicomponents.view.textfield.core.constants.MagnifierSpringSpec
import com.deadrudolph.uicomponents.view.textfield.core.constants.OffsetDisplacementThreshold
import com.deadrudolph.uicomponents.view.textfield.core.constants.UnspecifiedSafeOffsetVectorConverter
import com.deadrudolph.uicomponents.view.textfield.core.constants.emptyTextTransform
import com.deadrudolph.uicomponents.view.textfield.core.paragraph.ParagraphStyle
import com.deadrudolph.uicomponents.view.textfield.core.range.TextRange
import com.deadrudolph.uicomponents.view.textfield.core.semantic.AccessibilityAction
import com.deadrudolph.uicomponents.view.textfield.core.semantic.SemanticsActions
import com.deadrudolph.uicomponents.view.textfield.core.semantic.SemanticsPropertyReceiver
import com.deadrudolph.uicomponents.view.textfield.core.span.BrushStyle
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.span.TextForegroundStyle
import com.deadrudolph.uicomponents.view.textfield.core.span.lerpDiscrete
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import com.deadrudolph.uicomponents.view.textfield.core.text_field.TransformedText
import com.deadrudolph.uicomponents.view.textfield.core.text_field.VisualTransformation
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

fun KeyboardOptions.toImeOptions(singleLine: Boolean = ImeOptions.Default.singleLine) = ImeOptions(
    singleLine = singleLine,
    capitalization = capitalization,
    autoCorrect = autoCorrect,
    keyboardType = keyboardType,
    imeAction = imeAction
)

internal fun VisualTransformation.filterWithValidation(text: AnnotatedString): TransformedText {
    return filter(text).let { transformed ->
        TransformedText(
            transformed.text,
            ValidatingOffsetMapping(
                delegate = transformed.offsetMapping,
                originalLength = text.length,
                transformedLength = transformed.text.length
            )
        )
    }
}

internal fun Float.ceilToIntPx(): Int = ceil(this).roundToInt()

@OptIn(InternalFoundationTextApi::class)
internal fun NewTextLayoutResult.canReuse(
    text: AnnotatedString,
    style: TextStyle,
    placeholders: List<AnnotatedString.Range<Placeholder>>,
    maxLines: Int,
    softWrap: Boolean,
    overflow: TextOverflow,
    density: Density,
    layoutDirection: LayoutDirection,
    fontFamilyResolver: FontFamily.Resolver,
    constraints: Constraints
): Boolean {

    // NOTE(text-perf-review): might make sense to short-circuit instance equality here

    // Check if this is created from the same parameter.
    val layoutInput = this.layoutInput
    if (multiParagraph.intrinsics.hasStaleResolvedFonts) {
        // one of the resolved fonts has updated, and this MultiParagraph is no longer valid for
        // measure or display
        return false
    }
    if (!(
                layoutInput.text == text &&
                        layoutInput.style.hasSameLayoutAffectingAttributes(style) &&
                        layoutInput.placeholders == placeholders &&
                        layoutInput.maxLines == maxLines &&
                        layoutInput.softWrap == softWrap &&
                        layoutInput.overflow == overflow &&
                        layoutInput.density == density &&
                        layoutInput.layoutDirection == layoutDirection &&
                        layoutInput.fontFamilyResolver == fontFamilyResolver
                )
    ) {
        return false
    }

    // Check the given constraints can produces the same result.
    if (constraints.minWidth != layoutInput.constraints.minWidth) return false

    if (!(softWrap || overflow == TextOverflow.Ellipsis)) {
        // If width does not matter, we can result the same layout.
        return true
    }
    return constraints.maxWidth == layoutInput.constraints.maxWidth &&
            constraints.maxHeight == layoutInput.constraints.maxHeight
}

internal fun timeNowMillis(): Long = System.currentTimeMillis()

internal val ValidatingEmptyOffsetMappingIdentity: OffsetMapping =
    ValidatingOffsetMapping(
        delegate = OffsetMapping.Identity,
        originalLength = 0,
        transformedLength = 0
    )

internal fun CacheDrawScope.createHandleImage(radius: Float): ImageBitmap {
    // The edge length of the square bounding box of the selection/cursor handle. This is also
    // the size of the bitmap needed for the bitmap mask.
    val edge = ceil(radius).toInt() * 2

    var imageBitmap = HandleImageCache.imageBitmap
    var canvas = HandleImageCache.canvas
    var drawScope = HandleImageCache.canvasDrawScope

    // If the cached bitmap is null or too small, we need to create new bitmap.
    if (
        imageBitmap == null ||
        canvas == null ||
        edge > imageBitmap.width ||
        edge > imageBitmap.height
    ) {
        imageBitmap = ImageBitmap(
            width = edge,
            height = edge,
            config = ImageBitmapConfig.Alpha8
        )
        HandleImageCache.imageBitmap = imageBitmap
        canvas = Canvas(imageBitmap)
        HandleImageCache.canvas = canvas
    }
    if (drawScope == null) {
        drawScope = CanvasDrawScope()
        HandleImageCache.canvasDrawScope = drawScope
    }

    drawScope.draw(
        this,
        layoutDirection,
        canvas,
        Size(imageBitmap.width.toFloat(), imageBitmap.height.toFloat())
    ) {
        // Clear the previously rendered portion within this ImageBitmap as we could
        // be re-using it
        drawRect(
            color = Color.Black,
            size = size,
            blendMode = BlendMode.Clear
        )

        // Draw the rectangle at top left.
        drawRect(
            color = Color(0xFF000000),
            topLeft = Offset.Zero,
            size = Size(radius, radius)
        )
        // Draw the circle
        drawCircle(
            color = Color(0xFF000000),
            radius = radius,
            center = Offset(radius, radius)
        )
    }
    return imageBitmap
}

private object HandleImageCache {
    var imageBitmap: ImageBitmap? = null
    var canvas: Canvas? = null
    var canvasDrawScope: CanvasDrawScope? = null
}

internal fun LayoutCoordinates.visibleBounds(): Rect {
    // globalBounds is the global boundaries of this LayoutCoordinates after it's clipped by
    // parents. We can think it as the global visible bounds of this Layout. Here globalBounds
    // is convert to local, which is the boundary of the visible area within the LayoutCoordinates.
    val boundsInWindow = boundsInWindow()
    return Rect(
        windowToLocal(boundsInWindow.topLeft),
        windowToLocal(boundsInWindow.bottomRight)
    )
}

internal fun Rect.containsInclusive(offset: Offset): Boolean =
    offset.x in left..right && offset.y in top..bottom

internal fun getTextFieldSelection(
    textLayoutResult: NewTextLayoutResult?,
    rawStartOffset: Int,
    rawEndOffset: Int,
    previousSelection: TextRange?,
    isStartHandle: Boolean,
    adjustment: NewSelectionAdjustment
): TextRange {
    textLayoutResult?.let {
        val textRange = TextRange(rawStartOffset, rawEndOffset)

        // When the previous selection is null, it's allowed to have collapsed selection.
        // So we can ignore the SelectionAdjustment.Character.
        if (previousSelection == null && adjustment == NewSelectionAdjustment.Character) {
            return textRange
        }

        return adjustment.adjust(
            textLayoutResult = textLayoutResult,
            newRawSelectionRange = textRange,
            previousHandleOffset = -1,
            isStartHandle = isStartHandle,
            previousSelectionRange = previousSelection
        )
    }
    return TextRange(0, 0)
}

internal fun getSelectionHandleCoordinates(
    textLayoutResult: NewTextLayoutResult,
    offset: Int,
    isStart: Boolean,
    areHandlesCrossed: Boolean
): Offset {
    val line = textLayoutResult.getLineForOffset(offset)
    val x = textLayoutResult.getHorizontalPosition(offset, isStart, areHandlesCrossed)
    val y = textLayoutResult.getLineBottom(line)

    return Offset(x, y)
}

internal fun NewTextLayoutResult.getHorizontalPosition(
    offset: Int,
    isStart: Boolean,
    areHandlesCrossed: Boolean
): Float {
    val offsetToCheck =
        if (isStart && !areHandlesCrossed || !isStart && areHandlesCrossed) offset
        else kotlin.math.max(offset - 1, 0)
    val bidiRunDirection = getBidiRunDirection(offsetToCheck)
    val paragraphDirection = getParagraphDirection(offset)

    return getHorizontalPosition(
        offset = offset,
        usePrimaryDirection = bidiRunDirection == paragraphDirection
    )
}

internal fun getAdjustedCoordinates(position: Offset): Offset {
    return Offset(position.x, position.y - 1f)
}

@Composable
fun rememberAnimatedMagnifierPosition(
    targetCalculation: () -> Offset,
): State<Offset> {
    val targetValue by remember { derivedStateOf(targetCalculation) }
    val animatable = remember {
        // Can't use Offset.VectorConverter because we need to handle Unspecified specially.
        Animatable(targetValue, UnspecifiedSafeOffsetVectorConverter, OffsetDisplacementThreshold)
    }
    LaunchedEffect(Unit) {
        val animationScope = this
        snapshotFlow { targetValue }
            .collect { targetValue ->
                // Only animate the position when moving vertically (i.e. jumping between lines),
                // since horizontal movement in a single line should stay as close to the gesture as
                // possible and animation would only add unnecessary lag.
                if (
                    animatable.value.isSpecified &&
                    targetValue.isSpecified &&
                    animatable.value.y != targetValue.y
                ) {
                    // Launch the animation, instead of cancelling and re-starting manually via
                    // collectLatest, so if another animation is started before this one finishes,
                    // the new one will use the correct velocity, e.g. in order to propagate spring
                    // inertia.
                    animationScope.launch {
                        animatable.animateTo(targetValue, MagnifierSpringSpec)
                    }
                } else {
                    animatable.snapTo(targetValue)
                }
            }
    }
    return animatable.asState()
}

internal fun isLeft(
    isStartHandle: Boolean,
    direction: ResolvedTextDirection,
    handlesCrossed: Boolean
): Boolean {
    return if (isStartHandle) {
        isHandleLtrDirection(direction, handlesCrossed)
    } else {
        !isHandleLtrDirection(direction, handlesCrossed)
    }
}

internal fun isHandleLtrDirection(
    direction: ResolvedTextDirection,
    areHandlesCrossed: Boolean
): Boolean {
    return direction == ResolvedTextDirection.Ltr && !areHandlesCrossed ||
            direction == ResolvedTextDirection.Rtl && areHandlesCrossed
}

internal fun KeyEvent.cancelsTextSelection(): Boolean {
    return nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_BACK && type == KeyEventType.KeyUp
}

internal val KeyEvent.isTypedEvent: Boolean
    get() = nativeKeyEvent.action == android.view.KeyEvent.ACTION_DOWN &&
            nativeKeyEvent.unicodeChar != 0

@Suppress("DEPRECATION")
fun PointerInputChange.consume() {
    consumed.downChange = true
    consumed.positionChange = true
}

internal fun Float.ceilToInt(): Int = ceil(this).toInt()

fun SemanticsPropertyReceiver.getTextLayoutResult(
    label: String? = null,
    action: ((MutableList<NewTextLayoutResult>) -> Boolean)?
) {
    this[SemanticsActions.GetTextLayoutResult] = AccessibilityAction(label, action)
}

internal fun simpleIdentityToString(obj: Any, name: String?): String {
    val className = name ?: if (obj::class.java.isAnonymousClass) {
        obj::class.java.name
    } else {
        obj::class.java.simpleName
    }

    return className + "@" + String.format("%07x", System.identityHashCode(obj))
}

fun lerp(start: TextIndent, stop: TextIndent, fraction: Float): TextIndent {
    return TextIndent(
        lerpTextUnitInheritable(start.firstLine, stop.firstLine, fraction),
        lerpTextUnitInheritable(start.restLine, stop.restLine, fraction)
    )
}

internal fun lerpTextUnitInheritable(a: TextUnit, b: TextUnit, t: Float): TextUnit {
    if (a.isUnspecified || b.isUnspecified) return lerpDiscrete(a, b, t)
    return lerp(a, b, t)
}

fun lerp(start: Offset, stop: Offset, fraction: Float): Offset {
    return Offset(
        lerp(start.x, stop.x, fraction),
        lerp(start.y, stop.y, fraction)
    )
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

internal fun lerp(
    start: TextForegroundStyle,
    stop: TextForegroundStyle,
    fraction: Float
): TextForegroundStyle {
    return if ((start !is BrushStyle && stop !is BrushStyle)) {
        TextForegroundStyle.from(
            lerp(
                start.color,
                stop.color,
                fraction
            )
        )
    } else if (start is BrushStyle && stop is BrushStyle) {
        TextForegroundStyle.from(
            lerpDiscrete(
                start.brush,
                stop.brush,
                fraction
            ),
            lerp(start.alpha, stop.alpha, fraction)
        )
    } else {
        lerpDiscrete(start, stop, fraction)
    }
}

/**
 * Interpolate between two span styles.
 *
 * This will not work well if the styles don't set the same fields.
 *
 * The [fraction] argument represents position on the timeline, with 0.0 meaning
 * that the interpolation has not started, returning [start] (or something
 * equivalent to [start]), 1.0 meaning that the interpolation has finished,
 * returning [stop] (or something equivalent to [stop]), and values in between
 * meaning that the interpolation is at the relevant point on the timeline
 * between [start] and [stop]. The interpolation can be extrapolated beyond 0.0 and
 * 1.0, so negative values and values greater than 1.0 are valid.
 */
internal fun lerp(start: SpanStyle, stop: SpanStyle, fraction: Float): SpanStyle {
    return SpanStyle(
        color = lerp(start.color, stop.color, fraction),
        fontFamily = lerpDiscrete(
            start.fontFamily,
            stop.fontFamily,
            fraction
        ),
        fontSize = lerpTextUnitInheritable(
            start.fontSize,
            stop.fontSize,
            fraction
        ),
        fontWeight = lerp(
            start.fontWeight ?: FontWeight.Normal,
            stop.fontWeight ?: FontWeight.Normal,
            fraction
        ),
        fontStyle = lerpDiscrete(
            start.fontStyle,
            stop.fontStyle,
            fraction
        ),
        fontSynthesis = lerpDiscrete(
            start.fontSynthesis,
            stop.fontSynthesis,
            fraction
        ),
        fontFeatureSettings = lerpDiscrete(
            start.fontFeatureSettings,
            stop.fontFeatureSettings,
            fraction
        ),
        letterSpacing = lerpTextUnitInheritable(
            start.letterSpacing,
            stop.letterSpacing,
            fraction
        ),
        baselineShift = androidx.compose.ui.text.style.lerp(
            start.baselineShift ?: BaselineShift(0f),
            stop.baselineShift ?: BaselineShift(0f),
            fraction
        ),
        textGeometricTransform = androidx.compose.ui.text.style.lerp(
            start.textGeometricTransform ?: emptyTextTransform,
            stop.textGeometricTransform ?: emptyTextTransform,
            fraction
        ),
        localeList = lerpDiscrete(start.localeList, stop.localeList, fraction),
        background = lerp(
            start.background,
            stop.background,
            fraction
        ),
        textDecoration = lerpDiscrete(
            start.textDecoration,
            stop.textDecoration,
            fraction
        ),
        shadow = lerp(
            start.shadow ?: Shadow(),
            stop.shadow ?: Shadow(),
            fraction
        )
    )
}

fun lerp(start: FontWeight, stop: FontWeight, fraction: Float): FontWeight {
    val weight = lerp(start.weight, stop.weight, fraction)
        .coerceIn(1, 1000)
    return FontWeight(weight)
}

@Stable
fun lerp(start: IntOffset, stop: IntOffset, fraction: Float): IntOffset =
    IntOffset(lerp(start.x, stop.x, fraction), lerp(start.y, stop.y, fraction))

internal fun lerp(start: Int, end: Int, t: Float) = (start * (1f - t) + end * t).toInt()

@Stable
fun lerp(start: ParagraphStyle, stop: ParagraphStyle, fraction: Float): ParagraphStyle {
    return ParagraphStyle(
        textAlign = lerpDiscrete(
            start.textAlign,
            stop.textAlign,
            fraction
        ),
        textDirection = lerpDiscrete(
            start.textDirection,
            stop.textDirection,
            fraction
        ),
        lineHeight = lerpTextUnitInheritable(
            start.lineHeight,
            stop.lineHeight,
            fraction
        ),
        textIndent = lerp(
            start.textIndent ?: TextIndent(),
            stop.textIndent ?: TextIndent(),
            fraction
        )
    )
}

