import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AutoScrollText(
    text: String,
    style: TextStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
    scrollDelay: Long = 2000L,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    var textWidth by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -textWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ((textWidth + 100) / 100 * 1000).toInt(), easing = LinearEasing)
        )
    )

    LaunchedEffect(text) {
        delay(scrollDelay)
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.width(textWidth.dp).height(24.dp)) {
            val paint = Paint().apply {
                this.color = style.color ?: Color.Black
                this.asFrameworkPaint().textSize = style.fontSize?.value ?: 16f
                this.asFrameworkPaint().isAntiAlias = true
                this.asFrameworkPaint().setShadowLayer(4f, 2f, 2f, android.graphics.Color.GRAY)
            }
            drawIntoCanvas {
                it.nativeCanvas.drawText(text, animatedOffset, 20f, paint.asFrameworkPaint())
            }
        }
    }
}