package nz.ac.massey.examples336.touchbubbles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import bubbles.composeapp.generated.resources.Res
import bubbles.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.floor

import kotlin.math.log
import kotlin.math.pow
import kotlin.math.roundToInt


@Composable
fun SettingsSwitch(modifier:Modifier=Modifier, heading:String="Example Switch", description:String="Switch me", state: State<Boolean>, onChange:(Boolean)->Unit={}) {
    Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(8.dp)){
        Column(modifier = modifier.padding(8.dp).weight(0.9f)) {
            Text(text = heading, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(
            modifier = Modifier.fillMaxWidth().align(CenterVertically).weight(0.2f).padding(8.dp),
            checked = state.value,
            onCheckedChange = {
                onChange(it)
            })
    }
}

fun Float.toPrecision(precision: Int) =
    this.toDouble().toPrecision(precision)

fun Double.toPrecision(precision: Int) =
    if (precision < 1) {
        "${this.roundToInt()}"
    } else {
        val p = 10.0.pow(precision)
        val v = (abs(this) * p).roundToInt()
        val i = floor(v / p)
        var f = "${floor(v - (i * p)).toInt()}"
        while (f.length < precision) f = "0$f"
        val s = if (this < 0) "-" else ""
        "$s${i.toInt()}.$f"
    }

@Composable
fun SettingsFloatSlider(modifier:Modifier=Modifier, heading:String="Example Slider", description:String="Slide me", state: State<Float>, min:Float=0f, max:Float=1f, steps:Int=100, onChange:(Float)->Unit={}) {
    Column(modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = heading, style = MaterialTheme.typography.titleMedium)
        Text(text = description, style =MaterialTheme.typography.bodyMedium)
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
                text = state.value.toPrecision(2)
            )
        }
    }
}

const val BASE=1.001
@Composable
fun SettingsSlider(modifier:Modifier=Modifier, heading:String="Example Slider", description:String="Slide me", state: State<Int>, min:Float=0f, max:Float=100f, steps:Int=(max-min).toInt(), logarithmic:Boolean=false, onChange:(Int)->Unit={}) {
    Column(modifier = modifier.padding(16.dp),verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = heading, style = MaterialTheme.typography.titleMedium)
        Text(text = description, style =MaterialTheme.typography.bodyMedium)
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                modifier = Modifier.weight(0.9f), valueRange = min..max, steps = steps,
                value = if (!logarithmic) state.value.toFloat() else
                    log(
                        (state.value.toDouble() * BASE.pow(max.toDouble()) / max.toDouble())+1, BASE).toFloat()
                ,
                onValueChange = {
                    onChange(
                        if (!logarithmic) it.toInt() else
                            (max * (BASE.pow(it.toDouble())-1) / BASE.pow(max.toDouble())).toInt()
                    )
                })
            Text(
                modifier = Modifier.width(64.dp).align(CenterVertically),
                textAlign = TextAlign.Right,
                text = state.value.toInt().toString()
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
                            painterResource(Res.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            SettingsSlider(
                heading = "Number Of Bubbles",
                description = "Total number of bubbles",
                state = viewmodel.nbubbles.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setnbubbles(it) },
                min = 1f, max = 10000f, steps = 100, logarithmic = true
            )
            SettingsSlider(
                heading = "Number Of Large Bubbles",
                description = "How many of the bubblas are large",
                state = viewmodel.nlarge.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setnlarge(it) },
                min = 0f, max = 5f
            )
            SettingsSlider(
                heading = "Small Bubble Min Size",
                description = "Smallest size for a small bubble",
                state = viewmodel.smallMin.collectAsStateWithLifecycle(),
                onChange = {
                    if (it > viewmodel.smallMax.value) viewmodel.setsmall_max(it)
                    viewmodel.setsmall_min(it)
                },
                min = 5f, max = 100f
            )
            SettingsSlider(
                heading = "Small Bubble Max Size",
                description = "Largest size for a small bubble",
                state = viewmodel.smallMax.collectAsStateWithLifecycle(),
                onChange = {
                    if (it < viewmodel.smallMin.value) viewmodel.setsmall_min(it)
                    viewmodel.setsmall_max(it)
                },
                min = 10f, max = 100f
            )
            SettingsSlider(
                heading = "Large Bubble Min Size",
                description = "Smallest size for a large bubble",
                state = viewmodel.largeMin.collectAsStateWithLifecycle(),
                onChange = {
                    if (it > viewmodel.largeMax.value) viewmodel.setlarge_max(it)
                    viewmodel.setlarge_min(it)
                },
                min = 50f, max = 300f
            )
            SettingsSlider(
                heading = "Large Bubble Max Size",
                description = "Largest size for a large bubble",
                state = viewmodel.largeMax.collectAsStateWithLifecycle(),
                onChange = {
                    if (it < viewmodel.largeMin.value) viewmodel.setlarge_min(it)
                    viewmodel.setlarge_max(it)
                },
                min = 50f, max = 300f
            )
            SettingsFloatSlider(
                heading = "Dampening",
                description = "How much energy do the bubbles lose when they bounce",
                state = viewmodel.dampening.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setdampening(it) },
            )
            SettingsFloatSlider(
                heading = "Rigidity",
                description = "How rigid are the bubbles",
                state = viewmodel.rigidity.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setrigidity(it) },
            )
            SettingsSlider(
                heading = "FPS",
                description = "Frames per second?",
                state = viewmodel.fps.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setfps(it) },
                min = 1f, max = 200f
            )
            SettingsSwitch(
                heading = "Compress Bubbles",
                description = "Compress bubbles?",
                state = viewmodel.compress.collectAsStateWithLifecycle(),
                onChange = { viewmodel.setcompress(it) }
            )
        }
    }
    //   }
}
