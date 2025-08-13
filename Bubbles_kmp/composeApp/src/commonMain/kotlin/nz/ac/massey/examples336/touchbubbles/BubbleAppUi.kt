package nz.ac.massey.examples336.touchbubbles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bubbles.composeapp.generated.resources.Res
import bubbles.composeapp.generated.resources.baseline_settings_24
import bubbles.composeapp.generated.resources.bubble
import bubbles.composeapp.generated.resources.ic_more_vert
import bubbles.composeapp.generated.resources.ic_restart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
fun AppBar(showDialog: MutableState<Boolean>?=null, modifier:Modifier, navController: NavHostController) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = modifier,
        title = { Text(text = "Bubbles") },
        actions = {
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(painterResource(Res.drawable.baseline_settings_24), null)
            }
            IconButton(onClick = { showDropDownMenu = true }) {
                Icon(painterResource(Res.drawable.ic_more_vert), null)
            }
            DropdownMenu(
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Restart") },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_restart), null) },
                    onClick = {
                        showDropDownMenu = false
                        showDialog?.value = true
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun Navigation(viewmodel: SettingsViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            BubbleApp(navController, viewmodel)
        }
        composable("settings") {
            Settings(viewmodel=viewmodel, navigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun BubbleApp(navController: NavHostController,viewmodel: SettingsViewModel) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    SureDialog({
        viewmodel.bubbles.init()
    }, showDialog)
    Scaffold(
        topBar = { AppBar(showDialog, Modifier, navController) },
    ) { innerPadding ->
        Bubbles(modifier = Modifier.padding(innerPadding), viewmodel )
    }
}

//expect fun currentTimeMillis() : Long

@OptIn(ExperimentalTime::class)
fun currentTimeMillis():Long = Clock.System.now().toEpochMilliseconds()
val redtint:FloatArray=floatArrayOf(
    1.3f, 0f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f, 0f,
    0f, 0f, 1f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f)
val greentint:FloatArray=floatArrayOf(
    1f, 0f, 0f, 0f, 0f,
    0f, 1.3f, 0f, 0f, 0f,
    0f, 0f, 1f, 0f, 0f,
    0f, 0f, 0f, 1f, 0f)

val redcf:ColorFilter= ColorFilter.colorMatrix(ColorMatrix(redtint))
val greencf:ColorFilter= ColorFilter.colorMatrix(ColorMatrix(greentint))
var doUpdates=false
@Composable
fun Bubbles(modifier: Modifier = Modifier, viewmodel: SettingsViewModel) {
    val bubbleImage= imageResource(resource = Res.drawable.bubble)
    var framenumber by remember{ mutableIntStateOf(1) }
    LifecycleResumeEffect(Unit)  {
        doUpdates=true
        CoroutineScope(Dispatchers.Default).launch {
            var currentTime = currentTimeMillis() - 10
            var secs = currentTimeMillis() / 1000
            var rendertime = 0L
            while (doUpdates) {
                var newtime = currentTimeMillis()
                val dt = newtime - currentTime
                currentTime = newtime
                viewmodel.bubbles.update(dt / 1000f)
                newtime =currentTimeMillis()
                if (newtime / 1000 > secs) {  // show stats every second for last update
                    print("Update took:" + (newtime - currentTime) + " dt=" + dt)
                    secs = newtime / 1000
                }
                if (newtime - rendertime > 10) {
                    framenumber++
                    rendertime = newtime
                }
                val waittime = (1000 / viewmodel.fps.value) - (newtime - currentTime)
                delay(if (waittime > 0) waittime else 1)

            }
        }
        onPauseOrDispose {
            doUpdates=false
        }
    }
    Canvas(modifier = modifier.fillMaxSize().background(Color.White).pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                viewmodel.bubbles.grabBubble(offset.x, offset.y)
            },
            onDrag = { change, offset ->
                viewmodel.bubbles.moveBubble(change.position.x, change.position.y)
            },
            onDragEnd = {
                viewmodel.bubbles.ungrabBubble()

            })
    }) {
        // this means that the bubbles are drawn whenever the frame number changes
        framenumber.let { inv ->
            viewmodel.bubbles.setCsize(this.size)
            for (b: Bubble in viewmodel.bubbles.bubbleData) {
                val r = b.cr.toInt()
                drawImage(
                    image = bubbleImage, dstSize = IntSize(r * 2, r * 2),
                    dstOffset = IntOffset(b.x.toInt()-r, b.y.toInt()-r),
                    colorFilter = if (b.grabbed) greencf else redcf
                )
            }
        }
    }
}