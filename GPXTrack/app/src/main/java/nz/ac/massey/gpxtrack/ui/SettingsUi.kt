package nz.ac.massey.gpxtrack.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nz.ac.massey.gpxtrack.R
import nz.ac.massey.gpxtrack.Settings
import nz.ac.massey.gpxtrack.theme.ui.AppTheme
import nz.ac.massey.gpxtrack.theme.ui.AppTypography

@Composable
fun SettingsSwitch(modifier:Modifier=Modifier, heading:String="Switch", description:String="Switch me", state: Boolean=false, onChange:(Boolean)->Unit={}) {
    Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(8.dp).fillMaxWidth()){
        Column(modifier = modifier.padding(8.dp).weight(0.9f)) {
            Text(text = heading, style = AppTypography.titleMedium)
            Text(text = description, style = AppTypography.bodyMedium)
        }
        Row(Modifier.fillMaxWidth().align(CenterVertically).weight(0.2f)) {
            VerticalDivider(
                Modifier.width(2.dp).height(32.dp).align(CenterVertically).padding(end = 8.dp),
                thickness = 2.dp
            )
            Switch(
                modifier = modifier.padding(start = 16.dp),
                checked = state,
                onCheckedChange = {
                    onChange(it)
                })
        }
    }
}
@Composable
fun SettingsSlider(modifier:Modifier=Modifier, title:String="Slider",
                   description:String="Slide me", state: Float=0f, min:Float=0f, max:Float=100f, steps:Int=(max-min-1).toInt(), onChange:(Float)->Unit={}) {
    Column(modifier = modifier.padding(start=16.dp, end=8.dp),verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = title, style = AppTypography.titleMedium)
        Text(text = description, style = AppTypography.bodyMedium)
        Row(
            modifier = modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                modifier = Modifier.weight(0.9f), valueRange = min..max, steps = steps,
                value =  state,
                onValueChange = onChange)
            Text(
                modifier = Modifier.width(64.dp).align(CenterVertically),
                textAlign = TextAlign.Right,
                text = "%.2f".format(state)
            )
        }
    }
}

@Composable
fun SettingsDropDown(modifier:Modifier=Modifier, title:String="Menu", state: String="Item1", options:List<String> = listOf("Item1","Item2"), onChange:(String)->Unit={}) {
    var showItems by remember { mutableStateOf(false) }
    Column(modifier = modifier.padding(16.dp).clickable(onClick = { showItems=!showItems }),verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = title, style = AppTypography.titleMedium)
        Text(text = state, style = AppTypography.bodyMedium)
        DropdownMenu(expanded = showItems,onDismissRequest = { showItems=false }) {
            for(option in options) {
                DropdownMenuItem(text={Text(option)}, onClick = {onChange(option); showItems=false})
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SettingsScreen() {
    val activity= LocalActivity.current
    val state=Settings.settingsState.collectAsStateWithLifecycle()
    AppTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { activity?.finish() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(top=innerPadding.calculateTopPadding(),start=32.dp, bottom = innerPadding.calculateBottomPadding()).verticalScroll(rememberScrollState())
            ) {
                Text(
                    stringResource(R.string.track_colouring),
                    style = AppTypography.titleSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                SettingsSlider(title = stringResource(R.string.max_acc_title) ,
                    max = 30f, steps=59,
                    description = stringResource(R.string.acceleration_threshold_before_a_point_is_discarded),
                    state = state.value.maxAcc.toFloat(),
                    onChange = {Settings.maxAcc=it.toDouble()})
                SettingsSlider(title = stringResource(R.string.min_speed_title) ,
                    max=30f,
                    description = stringResource(R.string.minimum_speed),
                    state = state.value.minSpeed.toFloat(),
                    onChange = { Settings.minSpeed=it.toDouble()})
                SettingsSlider(title = stringResource(R.string.window_title) ,
                    max=30f,
                    description = stringResource(R.string.number_of_samples_used_to_find_speed_for_colouring_the_track),
                    state = state.value.window.toFloat(),
                    onChange = {Settings.window=it.toInt()})
                HorizontalDivider()
                Text(
                    stringResource(R.string.map_appearance),
                    style = AppTypography.titleSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                SettingsDropDown(
                    title = stringResource(R.string.map_type_title),
                    state = state.value.mapType,
                    options = stringArrayResource( R.array.map_type_entries).asList(),
                    onChange = {Settings.mapType = it})
                SettingsSwitch(
                    heading = stringResource(R.string.show_colours_title),
                    description = stringResource(R.string.show_colours_summary),
                    state = state.value.showColours,
                    onChange = {Settings.showColours = it})
                SettingsSwitch(
                    heading = stringResource(R.string.show_arrows_title),
                    description = stringResource(R.string.show_direction_arrows_on_track),
                    state = state.value.showArrows,
                    onChange = {Settings.showArrows = it})
                SettingsSwitch(
                    heading = stringResource(R.string.show_legend_title),
                    description = stringResource(R.string.show_legend_summary),
                    state = state.value.showLegend,
                    onChange = {Settings.showLegend = it})
                SettingsSwitch(
                    heading = stringResource(R.string.show_details_title),
                    description = stringResource(R.string.show_details_summary),
                    state = state.value.showDetails,
                    onChange = {Settings.showDetails = it})
                SettingsSwitch(
                    heading = stringResource(R.string.show_maxspeed_title),
                    description = stringResource(R.string.show_maxspeed_summary),
                    state = state.value.showMaxSpeed,
                    onChange = {Settings.showMaxSpeed = it})
                SettingsSlider(title = stringResource(R.string.line_width_title) ,
                    max=32f,
                    description = stringResource(R.string.width_of_track_lines_on_map),
                    state = state.value.lineWidth.toFloat(),
                    onChange = {Settings.lineWidth = it.toInt()})
                HorizontalDivider()
                Text(
                    stringResource(R.string.cat_units_title),
                    style = AppTypography.titleSmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                SettingsDropDown(title = stringResource(R.string.units_title),
                    state = state.value.units,
                    options = stringArrayResource( R.array.unit_entries).asList(),
                    onChange = {Settings.units = it})
                SettingsDropDown(title = stringResource(R.string.distance_units_title),
                    state = state.value.distanceUnits,
                    options = stringArrayResource( R.array.distance_unit_entries).asList(),
                    onChange = {Settings.distanceUnits = it})
                SettingsSwitch(
                    heading = stringResource(R.string.convert_time_title),
                    description = stringResource(R.string.convert_time_summary),
                    state = state.value.wrongTz,
                    onChange = {Settings.wrongTz = it})
            }
        }
    }
}

