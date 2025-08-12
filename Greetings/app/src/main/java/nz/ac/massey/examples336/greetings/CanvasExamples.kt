package nz.ac.massey.examples336.greetings

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Preview
@Composable
fun BasicCanvasUsage() {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(Color.Red,size=size/2f)
            }
    )
}

@Preview
@Composable
fun CanvasCircleExample() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.Red,
            radius = size.minDimension / 5F,

        )
    }
}

@Preview
@Composable
fun CanvasDrawDiagonalLineExample() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawLine(
            start = Offset(x = canvasWidth, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = Color.White,
            strokeWidth = 10f
        )
    }
}

@Preview
@Composable
fun CanvasTransformationScale() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        scale(scaleX = 10f, scaleY = 10f) {
            drawCircle(Color.Blue, radius = 20.dp.toPx())
        }
    }
}

@Preview
@Composable
fun CanvasTransformationTranslate() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(left = 100f, top = -300f) {
            drawCircle(Color.Blue, radius = 200.dp.toPx())
        }
    }
}

@Preview
@Composable
fun CanvasTransformationRotate() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        rotate(degrees = 23F) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(x = size.width / 3F, y = size.height / 3F),
                size = size / 3F
            )
        }
    }
}

@Preview
@Composable
fun CanvasTransformationInset() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasQuadrantSize = size / 2F
        inset(horizontal = 50f, vertical = 30f) {
            drawRect(color = Color.Green, size = canvasQuadrantSize)
        }
    }
}

@Preview
@Composable
fun CanvasMultipleTransformations() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        withTransform({
            translate(left = size.width / 5F)
            rotate(degrees = 45F)
        }) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(x = size.width / 3F, y = size.height / 3F),
                size = size / 3F
            )
        }
    }
}

@Preview
@Composable
fun CanvasDrawText() {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawText(textMeasurer, "Hello")
    }
}

@Preview
@Composable
fun CanvasDrawImage() {
    val dogImage = ImageBitmap.imageResource(id = R.drawable.ic_launcher_foreground)

    Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
        drawImage(dogImage)
    })
}

@Preview
@Composable
fun CanvasDrawPath() {
    Spacer(
        modifier = Modifier
            .drawWithCache {
                val path = Path()
                path.moveTo(0f, 0f)
                path.lineTo(size.width / 2f, size.height / 2f)
                path.lineTo(size.width, 0f)
                path.close()
                path.addOval(Rect(0f,0f,size.width,size.height/2f))
                onDrawBehind {
                    drawPath(path, Color.Red, style = Stroke(width = 8f))
                }
            }
            .fillMaxSize()
    )
}

@Preview
@Composable
fun CanvasMeasureText() {
    val pinkColor = Color(0xFFF48FB1)
    val longTextSample =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = Modifier
            .drawWithCache {
                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(longTextSample),
                        constraints = Constraints.fixedWidth((size.width * 2f / 3f).toInt()),
                        style = TextStyle(fontSize = 18.sp)
                    )

                onDrawBehind {
                    drawRect(pinkColor, size = measuredText.size.toSize())
                    drawText(measuredText)
                }
            }
            .fillMaxSize()
    )
}

@Preview
@Composable
fun CanvasMeasureTextOverflow() {
    val pinkColor = Color(0xFFF48FB1)
    val longTextSample =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = Modifier
            .drawWithCache {
                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(longTextSample),
                        constraints = Constraints.fixed(
                            width = (size.width / 3f).toInt(),
                            height = (size.height / 3f).toInt()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(fontSize = 18.sp)
                    )

                onDrawBehind {
                    drawRect(pinkColor, size = measuredText.size.toSize())
                    drawText(measuredText)
                }
            }
            .fillMaxSize()
    )
}

@Preview
@Composable
fun CanvasDrawIntoCanvas() {
    val drawable = ShapeDrawable(OvalShape())
    Spacer(
        modifier = Modifier
            .drawWithContent {
                drawIntoCanvas { canvas ->
                    drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                    drawable.draw(canvas.nativeCanvas)
                }
            }
            .fillMaxSize()
    )
}

@Preview
@Composable
fun CanvasDrawShape() {
    val purpleColor = Color(0xFFBA68C8)
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        onDraw = {
            drawCircle(purpleColor)
        }
    )
}

@Preview
@Composable
fun CanvasDrawOtherShapes() {
    val purpleColor = Color(0xFFBA68C8)
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        onDraw = {
            drawPoints(
                listOf(
                    Offset(0f, 0f),
                    Offset(size.width / 3f, size.height / 2f),
                    Offset(size.width / 2f, size.height / 5f),
                    Offset(size.width, size.height)
                ),
                color = purpleColor,
                pointMode = PointMode.Points, strokeWidth = 10.dp.toPx()
            )
        }
    )
}

@Preview
@Composable
fun CanvasTransformationScaleAnim() {
    val animatable = remember {
        Animatable(1f)
    }
    LaunchedEffect(Unit) {
        animatable.animateTo(10f, animationSpec = tween(3000, 3000, easing = LinearEasing))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        scale(scaleX = animatable.value, scaleY = animatable.value * 1.5f) {
            drawCircle(Color.Blue, radius = 20.dp.toPx())
        }
    }
}