package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.*
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.deadrudolph.uicomponents.view.textfield.core.*
import com.deadrudolph.uicomponents.view.textfield.core.UnspecifiedSafeOffsetVectorConverter
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt

fun KeyboardOptions.toImeOptions(singleLine: Boolean = ImeOptions.Default.singleLine) = ImeOptions(
    singleLine = singleLine,
    capitalization = capitalization,
    autoCorrect = autoCorrect,
    keyboardType = keyboardType,
    imeAction = imeAction
)

fun VisualTransformation.filterWithValidation(text: AnnotatedString): TransformedText {
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
internal fun TextLayoutResult.canReuse(
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
    textLayoutResult: TextLayoutResult?,
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
    textLayoutResult: TextLayoutResult,
    offset: Int,
    isStart: Boolean,
    areHandlesCrossed: Boolean
): Offset {
    val line = textLayoutResult.getLineForOffset(offset)
    val x = textLayoutResult.getHorizontalPosition(offset, isStart, areHandlesCrossed)
    val y = textLayoutResult.getLineBottom(line)

    return Offset(x, y)
}

internal fun TextLayoutResult.getHorizontalPosition(
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
