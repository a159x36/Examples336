package nz.ac.massey.examples336.touchbubbles

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.datastore.dataStore
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nz.ac.massey.examples336.touchbubbles.MainActivity

var framenumber = mutableIntStateOf(1)

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
                Icon(Icons.Filled.Settings, null)
            }
            IconButton(onClick = { showDropDownMenu = true }) {
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
                        showDialog?.value = true
                    }
                )
            }
        }
    )
}

@Composable
fun Navigation(viewmodel: SettingsViewModel, bubbles:Bubbles) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") { BubbleApp(navController, viewmodel,  bubbles) }
        composable("settings") {
            Settings(viewmodel=viewmodel, navigateBack = { navController.popBackStack() })
        }
    }
}

@Preview
@Composable
fun Preview() {
    val context = LocalContext.current
    lateinit var bubbles:Bubbles

    val viewmodel: SettingsViewModel = viewModel(factory= SettingsViewModelFactory(context.dataStore) {//SettingsViewModel(LocalContext.current) {
        CoroutineScope(Dispatchers.IO).launch {
            bubbles.init(context)
        }
    })
    bubbles=Bubbles(viewmodel)
    Navigation(viewmodel, bubbles)
}

@Composable
fun BubbleApp(navController: NavHostController,viewmodel: SettingsViewModel,  bubbles:Bubbles, init: () -> Unit = {}) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    SureDialog({
        init()
    }, showDialog)
    Scaffold(
        topBar = { AppBar(showDialog, Modifier, navController) },
    ) { innerPadding ->
        Bubbles(modifier = Modifier.padding(innerPadding), viewmodel , bubbles)
    }
}

var doUpdates=false
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
val redcf = ColorFilter.colorMatrix(ColorMatrix(redtint))
val greencf = ColorFilter.colorMatrix(ColorMatrix(greentint))

@Composable
fun Bubbles(modifier: Modifier = Modifier, viewmodel: SettingsViewModel, bubbles:Bubbles) {
    LifecycleResumeEffect(Unit)  {
        doUpdates=true
        CoroutineScope(Dispatchers.Default).launch {
            var currentTime = System.currentTimeMillis() - 10
            var secs = System.currentTimeMillis() / 1000
            var rendertime = 0L

            while (doUpdates) {
                var newtime = System.currentTimeMillis()
                val dt = newtime - currentTime
                currentTime = newtime
                bubbles.update(dt / 1000f)
                newtime = System.currentTimeMillis()
                if (newtime / 1000 > secs) {  // show stats every second for last update
                    Log.d(TAG, "Update took:" + (newtime - currentTime) + " dt=" + dt)
                    secs = newtime / 1000
                }
                if (newtime - rendertime > 10) {
                    framenumber.intValue++
                    rendertime = newtime
                }
                val waittime = (1000 / viewmodel.fps.value) - (newtime - currentTime)
                delay(if (waittime > 0) waittime else 0)
            }
        }
        onPauseOrDispose {
            doUpdates=false
        }
    }
    Canvas(modifier = modifier.fillMaxSize().background(Color.Gray).pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                bubbles.grabBubble(offset.x, offset.y)
            },
            onDrag = { change, offset ->
                bubbles.moveBubble(change.position.x, change.position.y)
            },
            onDragEnd = {
                bubbles.ungrabBubble()

            })
    }) {
        // this means that the bubbles are drawn whenever the frame number changes
        framenumber.intValue.let { inv ->
            bubbles.csize = this.size
            for (i in 0..(viewmodel.nbubbles.value - 1) * 6 step 6) {
                val r = bubbles.getbubbleradius(i)
                val offset =bubbles.getbubblexy(i)- IntOffset(r,r)
                val size = IntSize(r * 2, r * 2)
                if(bubbles.bubble!=null) drawImage(image = bubbles.bubble!!, dstSize = size,
                    dstOffset = offset,
                    colorFilter = if (i==bubbles.grabbedBubbleIndex) greencf else redcf)
            }
        }
    }
}
