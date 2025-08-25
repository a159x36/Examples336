package nz.ac.massey.examples336.simplebubbles

import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "SimpleBubbles"

@Composable
fun SureDialog(onConfirmation: () -> Unit, show: MutableState<Boolean>) {
    if (show.value) {
        AlertDialog(
            text = { Text(text = "Are you sure?") },
            onDismissRequest = { show.value = false },
            confirmButton = { TextButton(onClick = { onConfirmation()
                show.value = false}) { Text("Yes") } },
            dismissButton = {
                TextButton(onClick = { show.value = false }) { Text("No") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(showDialog: MutableState<Boolean>, modifier:Modifier) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "Bubbles") },
        actions = {
            IconButton(onClick = { showDialog.value=true}) {Icon(Icons.Filled.Refresh, null)}
            IconButton(onClick = { showDropDownMenu = true}) {
                Icon(Icons.Filled.MoreVert, null)
            }
            DropdownMenu(
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Restart") },
                    leadingIcon = { Icon(Icons.Filled.Refresh, null) },
                    onClick = {
                        showDropDownMenu = false
                        showDialog.value = true
                    }
                )
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun BubbleApp(  viewModel: BubbleViewModel = viewModel()) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    SureDialog({
        viewModel.init(viewModel.canvasSize)
    }, showDialog)
    Scaffold(
        topBar = { AppBar(showDialog, Modifier) },
    ) { innerPadding ->
        Bubbles(modifier = Modifier.padding(innerPadding) , viewModel)
    }
}


@Composable
fun Bubbles(modifier: Modifier = Modifier, viewModel: BubbleViewModel) {
//    val context=LocalContext.current
//    val bubbleImage = remember { BitmapFactory.decodeResource(context.resources,R.drawable.bubble).asImageBitmap() }
    var doUpdates = remember { false }
    LifecycleResumeEffect(Unit)  {
        doUpdates=true
        CoroutineScope(Dispatchers.Default).launch {
            var currentTime = System.nanoTime() - 10
            var secs = System.nanoTime() / 1000000000
            var renderTime = 0L

            while (doUpdates) {
                var newtime = System.nanoTime()
                val dt = newtime - currentTime
                currentTime = newtime
                viewModel.update(dt / 1000000000f, viewModel.canvasSize)
                newtime = System.nanoTime()
                if (newtime / 1000000000 > secs) {  // show stats every second for last update
                    Log.d(TAG, "Update took:" + (newtime - currentTime)/1000000 + " dt=" + dt/1000000f)
                    secs = newtime / 1000000000
                }
                if (newtime - renderTime > 10000000) {
                    viewModel.frameNumber.intValue++
                    renderTime = newtime
                }
                val waittime = (1000000000 / viewModel.FPS) - (newtime - currentTime)
                delay(if (waittime > 0) waittime/1000000 else 0)
            }
        }
        onPauseOrDispose {
            doUpdates=false
        }
    }

    Canvas(modifier = modifier.fillMaxSize().background(Color.Gray)) {
        if(size!=viewModel.canvasSize) {
            viewModel.canvasSize=size
            viewModel.init(size)
        }
        // this means that the bubbles are drawn whenever the frame number changes
        viewModel.frameNumber.intValue.let { inv ->
            for(bubble in viewModel.bubbles) {
                val r = bubble.r

                /* Uncomment for Bubble Images
                val cf= ColorFilter.lighting(add=Color.DarkGray,multiply=bubble.color)
                val offsetTL = IntOffset((bubble.x-r).toInt(),(bubble.y-r).toInt())
                val size = IntSize(r.toInt() * 2, r.toInt() * 2)
                drawImage(image = bubbleImage, dstSize = size, dstOffset = offsetTL, colorFilter = cf)
                */

                val offset = Offset(bubble.x,bubble.y)
                val inner = r*0.75f
                val size1=  Size(inner * 2, inner * 2)
                val inneroffset=offset-Offset(inner,inner)
                val col=bubble.color
                drawCircle(col, radius = r.toFloat(), center = offset , style = Stroke(8f))
                drawArc(col, startAngle = 300f, sweepAngle = 45f, useCenter = false, topLeft = inneroffset, size = size1, style = Stroke(8f))


            }
        }
    }
}