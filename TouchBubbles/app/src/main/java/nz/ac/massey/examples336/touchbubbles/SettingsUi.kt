package nz.ac.massey.examples336.touchbubbles

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nz.ac.massey.examples336.touchbubbles.theme.ui.AppTypography
import kotlin.math.log
import kotlin.math.pow



@Composable
fun SettingsSwitch(modifier:Modifier=Modifier, heading:String="Example Switch", description:String="Switch me", state: State<Boolean>, onChange:(Boolean)->Unit={}) {
    Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(8.dp)){
        Column(modifier = modifier.padding(8.dp).weight(0.9f)) {
            Text(text = heading, style = AppTypography.titleMedium)
            Text(text = description, style = AppTypography.bodyMedium)
        }
        Switch(
            modifier = Modifier.fillMaxWidth().align(CenterVertically).weight(0.2f).padding(8.dp),
            checked = state.value,
            onCheckedChange = {
                onChange(it)
            })
    }
}
@Composable
fun SettingsFloatSlider(modifier:Modifier=Modifier, heading:String="Example Slider", description:String="Slide me", state: State<Float>, min:Float=0f, max:Float=1f, steps:Int=100, onChange:(Float)->Unit={}) {
    Column(modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = heading, style = AppTypography.titleMedium)
        Text(text = description, style = AppTypography.bodyMedium)
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                modifier = modifier.weight(0.9f), valueRange = min..max, steps = steps,
                value =  state.value ,
                onValueChange = {
                    onChange(it)
                })
            Text(
                modifier = modifier.width(64.dp).align(CenterVertically),
                textAlign = TextAlign.Right,
                text = "%.2f".format(state.value)
            )
        }
    }
}

const val expo=1.001
@Composable
fun SettingsSlider(modifier:Modifier=Modifier, heading:String="Example Slider", description:String="Slide me", state: State<Int>, min:Float=0f, max:Float=100f, steps:Int=(max-min).toInt(), logarithmic:Boolean=false, onChange:(Int)->Unit={}) {
    Column(modifier = modifier.padding(16.dp),verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = heading, style = AppTypography.titleMedium)
        Text(text = description, style = AppTypography.bodyMedium)
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                modifier = Modifier.weight(0.9f), valueRange = min..max, steps = steps,
                value = if (!logarithmic) state.value.toFloat() else
                    log(
                        (state.value.toDouble() * expo.pow(max.toDouble()) / max.toDouble())+1, expo).toFloat()
                ,
                onValueChange = {
                    onChange(
                        if (!logarithmic) it.toInt() else
                            (max * (expo.pow(it.toDouble())-1) / expo.pow(max.toDouble())).toInt()
                    )
                })
            Text(
                modifier = Modifier.width(64.dp).align(CenterVertically),
                textAlign = TextAlign.Right,
                text = "%d".format(state.value)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun Settings (viewmodel: SettingsViewModel, navigateBack: () -> Unit ) {
    // AppTheme() {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            SettingsSwitch(
                heading = "Native",
                description = "Use native code?",
                state = viewmodel.native.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setNative(it) })
            SettingsSwitch(
                heading = "Direct Buffers",
                description = "Use Direct Buffers?",
                state = viewmodel.usedirect.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setUseDirect(it) })

            SettingsSlider(
                heading = "Number Of Bubbles",
                description = "How many bubbles do you want?",
                state = viewmodel.nbubbles.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setNBubbles(it) },
                min = 1f, max = 10000f, steps = 100, logarithmic = true
            )
            SettingsSlider(
                heading = "Number Of Large Bubbles",
                description = "How many large bubbles do you want?",
                state = viewmodel.nlarge.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setNLarge(it); Log.d(TAG, "nlarge changed to $it") },
                min = 0f, max = 5f
            )
            SettingsSlider(
                heading = "Small Bubble Min Size",
                description = "Smallest size for a amall bubble",
                state = viewmodel.smallMin.collectAsStateWithLifecycle(),
                onChange = {
                    viewmodel.setSmallMin(it)
                    if (it > viewmodel.smallMax.value) viewmodel.setSmallMax(it)
                },
                min = 5f, max = 100f
            )
            SettingsSlider(
                heading = "Small Bubble Max Size",
                description = "Largest size for a small bubble",
                state = viewmodel.smallMax.collectAsStateWithLifecycle(),
                onChange = {
                    viewmodel.setSmallMax(it)
                    if (it < viewmodel.smallMin.value) viewmodel.setSmallMin(it)
                },
                min = 10f, max = 100f
            )
            SettingsSlider(
                heading = "Large Bubble Min Size",
                description = "Smallest size for a large bubble",
                state = viewmodel.largeMin.collectAsStateWithLifecycle(),
                onChange = {
                    viewmodel.setlargeMin(it)
                    if (it > viewmodel.largeMax.value) viewmodel.setLargeMax(it)
                },
                min = 50f, max = 300f
            )
            SettingsSlider(
                heading = "Large Bubble Max Size",
                description = "Largest size for a large bubble",
                state = viewmodel.largeMax.collectAsStateWithLifecycle(),
                onChange = {
                    viewmodel.setLargeMax(it)
                    if (it < viewmodel.largeMin.value) viewmodel.setlargeMin(it)
                },
                min = 50f, max = 300f
            )
            SettingsFloatSlider(
                heading = "Dampening",
                description = "How much dampening do you want?",
                state = viewmodel.dampening.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setDampening(it) },
            )
            SettingsFloatSlider(
                heading = "Rigidity",
                description = "How much rigidity do you want?",
                state = viewmodel.rigidity.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setRigidity(it) },
            )
            SettingsSlider(
                heading = "FPS",
                description = "Frame per second?",
                state = viewmodel.fps.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setFps(it) },
                min = 1f, max = 200f
            )
            SettingsSwitch(
                heading = "Compress Bubbles",
                description = "Compress bubbles?",
                state = viewmodel.compress.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setCompress(it) }
            )
        }
    }
    //   }
}