package org.example.project.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import matching.composeapp.generated.resources.Res
import matching.composeapp.generated.resources.are_you_sure
import matching.composeapp.generated.resources.ic_attachment_black_24dp
import matching.composeapp.generated.resources.ic_audiotrack_black_24dp
import matching.composeapp.generated.resources.ic_brightness_5_black_24dp
import matching.composeapp.generated.resources.ic_brush_black_24dp
import matching.composeapp.generated.resources.ic_build_black_24dp
import matching.composeapp.generated.resources.ic_flight_black_24dp
import matching.composeapp.generated.resources.ic_more_vert
import matching.composeapp.generated.resources.ic_restart
import matching.composeapp.generated.resources.ic_spa_black_24dp
import matching.composeapp.generated.resources.ic_weekend_black_24dp
import matching.composeapp.generated.resources.no
import matching.composeapp.generated.resources.restart
import matching.composeapp.generated.resources.yes
import org.example.project.MatchingGameViewModel
import org.example.project.Tile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


val drawables= arrayOf<DrawableResource>(
    Res.drawable.ic_attachment_black_24dp,
    Res.drawable.ic_audiotrack_black_24dp,
    Res.drawable.ic_brightness_5_black_24dp,
    Res.drawable.ic_brush_black_24dp,
    Res.drawable.ic_build_black_24dp,
    Res.drawable.ic_flight_black_24dp,
    Res.drawable.ic_spa_black_24dp,
    Res.drawable.ic_weekend_black_24dp,
)

@Composable
fun SureDialog(modifier: Modifier = Modifier, onConfirmation: () -> Unit, show: MutableState<Boolean>) {
    if (show.value) {
        AlertDialog(
            modifier = modifier,
            text = { Text(text = stringResource(Res.string.are_you_sure)) },
            onDismissRequest = { show.value = false },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmation()
                    show.value = false
                }) { Text(stringResource(Res.string.yes)) }
            },
            dismissButton = {
                TextButton(onClick = { show.value = false }) { Text(stringResource(Res.string.no)) }
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
        animationSpec = spring( stiffness = Spring.StiffnessVeryLow,
            dampingRatio = DampingRatioMediumBouncy),
    )
    Button(
        shape = RoundedCornerShape(4.dp),
        onClick = { tile.buttonClick(viewModel) },
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
    val scoreString=if (uiState.numMatched == ((uiState.rows * uiState.cols) / 2) ) "Complete:${uiState.score}"
    else "Score:${uiState.numMatched}/${uiState.score}"

    TopAppBar(
        modifier = modifier,
        title = { Text(text = scoreString) },
        actions = {
            IconButton(onClick = { showDropDownMenu = true }) {
                Icon(painterResource(Res.drawable.ic_more_vert), null)
            }
            DropdownMenu(
                expanded = showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.restart)) },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_restart), null) },
                    onClick = {
                        showDropDownMenu = false
                        showDialog.value = true
                    }
                )
            }
        }
    )
}

val ROWHEIGHT=128.dp
@Composable
fun MatchGame( modifier: Modifier = Modifier, viewModel: MatchingGameViewModel=MatchingGameViewModel()) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    SureDialog(modifier=modifier, {
        scope.launch {
            for (tile in uiState.tiles) {
                tile.init()
            }
            delay(1000)
            viewModel.reset()
        }
    }, showDialog)
    Scaffold(modifier = modifier,
        topBar = { AppBar(viewModel, showDialog, Modifier) },
    ) { innerPadding ->
        LazyVerticalGrid( columns = GridCells.Fixed(uiState.cols),
            contentPadding = innerPadding,
        ) {
            items(uiState.tiles.size) { index ->
                TurningButton(viewModel, uiState.tiles[index], modifier.height(ROWHEIGHT))
            }
        }
    }
}
@Preview
@Composable
fun ComposablePreview() {
    MaterialTheme {
        MatchGame()
    }
}