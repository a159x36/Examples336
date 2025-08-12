package nz.ac.massey.examples336.matchinggrid.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nz.ac.massey.examples336.matchinggrid.MAXCOLS
import nz.ac.massey.examples336.matchinggrid.MINCOLS
import nz.ac.massey.examples336.matchinggrid.MatchingGameViewModel
import nz.ac.massey.examples336.matchinggrid.R
import nz.ac.massey.examples336.matchinggrid.Tile
import nz.ac.massey.examples336.matchinggrid.theme.ui.AppTheme
import java.lang.Integer.max
import kotlin.math.min
import kotlin.math.roundToInt

val drawables= intArrayOf(
    R.drawable.ic_attachment_black_24dp,
    R.drawable.ic_audiotrack_black_24dp,
    R.drawable.ic_brightness_5_black_24dp,
    R.drawable.ic_brush_black_24dp,
    R.drawable.ic_build_black_24dp,
    R.drawable.ic_flight_black_24dp,
    R.drawable.ic_spa_black_24dp,
    R.drawable.ic_weekend_black_24dp,
)

//const val TAG = "MatchingGame"

@Composable
fun SureDialog(modifier: Modifier = Modifier, onConfirmation: () -> Unit, show: MutableState<Boolean>) {
    if (show.value) {
        AlertDialog(
            modifier = modifier,
            text = { Text(text = stringResource(R.string.are_you_sure)) },
            onDismissRequest = { show.value = false },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmation()
                    show.value = false
                }) { Text(stringResource(R.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = { show.value = false }) { Text(stringResource(R.string.no)) }
            }
        )
    }
}
@Composable
fun TurningButton( viewModel: MatchingGameViewModel, tile:Tile, modifier: Modifier = Modifier) {
    val shown=tile.shown.collectAsState().value
    val turned=tile.turned.collectAsState().value
    val angle by animateFloatAsState(
        targetValue = if (shown || turned) 0f else 180f,
        animationSpec =  tween(durationMillis = 1000),
        //spring( stiffness = StiffnessMediumLow,
        //    dampingRatio = DampingRatioMediumBouncy),
    )
    Button(
        shape = RoundedCornerShape(4.dp),
        onClick = { tile.buttonClick(/*uiState,*/viewModel) },
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .graphicsLayer { rotationY = angle; clip = false },
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp),
    ) {
        if ( (angle <= 90f) ) {
            Image(
                painter = painterResource(drawables[tile.value]),
                contentDescription = "",
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentScale = ContentScale.Fit
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(viewModel:MatchingGameViewModel, showDialog: MutableState<Boolean>, modifier:Modifier) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val scorestring=if (uiState.numMatched == ((uiState.ntiles) / 2) ) "Complete:${uiState.score}"
    else "Score:${uiState.numMatched}/${uiState.score}"

    TopAppBar(
        modifier = modifier,
        title = { Text(text = scorestring) },
        actions = {
            IconButton(onClick = { showDropDownMenu = true }) {
                Icon(Icons.Filled.MoreVert, null)
            }
            DropdownMenu(
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false },

            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.restart)) },
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
@Composable
fun MatchGame( modifier: Modifier = Modifier, viewModel: MatchingGameViewModel=viewModel()) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var zoom by remember { mutableFloatStateOf(1f) }
    val animatedZoom = animateFloatAsState(zoom, SpringSpec(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy))
    val uiState by viewModel.uiState.collectAsState()

    SureDialog(modifier = modifier, {
        scope.launch {
            for (tile in uiState.tiles) {
                tile.init()
            }
            delay(1000)
            viewModel.reset()
        }
    }, showDialog)
    Scaffold(
        modifier = modifier,
        topBar = { AppBar(viewModel, showDialog, Modifier) },
    ) { innerPadding ->
        Box(
            Modifier.padding(innerPadding).fillMaxSize()
            .graphicsLayer { scaleX = animatedZoom.value; scaleY = animatedZoom.value }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val zoomChange = event.calculateZoom()
                        if (zoomChange != 1f) {
                            zoom *= zoomChange
                            if(zoom!=0f) {
                                val cols =
                                    min(max((uiState.cols / zoom).roundToInt(), MINCOLS), MAXCOLS)
                                if (cols != uiState.cols) {
                                    viewModel.updatecols(cols)
                                    zoom = 1f
                                }
                            }
                            event.changes.forEach { it.consume() }
                        }
                    } while (event.changes.any { it.pressed })
                    zoom = 1f
                }
            }) {
            LazyVerticalGrid(columns = GridCells.Fixed(uiState.cols)) {
                items(uiState.tiles.size) { index ->
                    TurningButton(
                        viewModel,
                        uiState.tiles[index],
                        modifier.aspectRatio(.75f).animateItem()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    AppTheme {
        MatchGame()
    }
}
