package nz.ac.massey.examples336.matchinggrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nz.ac.massey.examples336.matchinggrid.theme.ui.AppTheme

const val ROWS=12
const val COLS=4
val ROWHEIGHT=128.dp
const val SCORE = "SCORE"
const val LAST = "LAST"
const val MATCH = "MATCH"
const val TILEVALS = "TILEVALS"
const val TURNED = "TURNED"
//const val TAG = "MatchingGame"


class MainActivity : ComponentActivity() {

    class Tile {
        var value = 0
        var showing by mutableStateOf(false)
        var turned by mutableStateOf(false)
    }

    private var tiles= Array(ROWS * COLS) { Tile() }
    private var score = 0
    private var numMatched = 0
    private var lastTile: Tile? = null
    private var scoreState by mutableStateOf("")

    private val drawables= intArrayOf(
      R.drawable.ic_attachment_black_24dp,
      R.drawable.ic_audiotrack_black_24dp,
      R.drawable.ic_brightness_5_black_24dp,
      R.drawable.ic_brush_black_24dp,
      R.drawable.ic_build_black_24dp,
      R.drawable.ic_flight_black_24dp,
      R.drawable.ic_spa_black_24dp,
      R.drawable.ic_weekend_black_24dp,
    )


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


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE, score)
        outState.putInt(LAST, if(lastTile==null)-1 else tiles.indexOf(lastTile))
        outState.putInt(MATCH, numMatched)
        outState.putIntArray(TILEVALS, tiles.map{it.value}.toIntArray())
        outState.putBooleanArray(TURNED,tiles.map{it.turned}.toBooleanArray())
        super.onSaveInstanceState(outState)
    }
    suspend fun turnback(tile:Tile) {
        delay(800)
        tile.showing=false
    }

    private fun buttonClick(tile:Tile) {
        if (!tile.turned && !tile.showing) {
            score++
            if (lastTile == null) {
                lastTile = tile
                tile.showing = true
            } else {
                lastTile?.let {
                    if (it.value == tile.value) {
                        tile.turned = true
                        it.turned = true
                        numMatched++
                        lastTile = null
                    } else {
                        tile.showing = true
                        CoroutineScope(Dispatchers.IO).launch {
                            turnback(it)
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            turnback(tile)
                        }
                        lastTile = null
                    }
                }
            }
        }
        showscore()
    }

    @Composable
    fun TurningButton( tile:Tile, modifier: Modifier = Modifier) {

        val angle by animateFloatAsState(
            targetValue = if (tile.showing || tile.turned) 0f else 180f,
            animationSpec =  tween(durationMillis = 1000),
            //spring( stiffness = StiffnessMediumLow,
            //    dampingRatio = DampingRatioMediumBouncy),
        )

        Button(
            shape = RoundedCornerShape(4.dp),
            onClick = { buttonClick(tile) },
            modifier = modifier.fillMaxSize()
                .padding(4.dp)
                .graphicsLayer { rotationY = angle },
            contentPadding = PaddingValues(0.dp)
        ) {
            if ( (angle >= -90f && angle < 90f)) {
                Image(
                    painter = painterResource(id = drawables[tile.value]),
                    contentDescription = "",
                    Modifier.fillMaxWidth().fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }
        }

    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar(showDialog: MutableState<Boolean>) {
        var showDropDownMenu by remember { mutableStateOf(false) }
        TopAppBar(
            title = { Text(text = scoreState) },
            actions = {
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
                            showDialog.value = true
                        }
                    )
                }
            }
        )
    }
    @Composable
    fun MatchGame(modifier: Modifier = Modifier) {
        val showDialog = rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        SureDialog({
            scope.launch {
                for (tile in tiles) {
                    tile.turned=false
                    tile.showing=false
                }
                delay(1000)
                init()
            }
        }, showDialog)
        Scaffold(
            topBar = { AppBar(showDialog) },
        ) { innerPadding ->
            LazyVerticalGrid( columns = GridCells.Fixed(COLS),
                modifier = Modifier.padding(innerPadding)
            ) {
                items(tiles.size) { index ->
                    TurningButton(tiles[index], modifier.height(ROWHEIGHT))
                }
            }
        }
    }
    @Preview (showSystemUi = true)
    @Composable
    fun ComposablePreview() {
        init()
        AppTheme {
            MatchGame()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MatchGame(modifier = Modifier.padding(innerPadding))
                }
            }
        }
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE, 0)
            val lb = savedInstanceState.getInt(LAST, -1)
            numMatched = savedInstanceState.getInt(MATCH, 0)
            val tilevalues = savedInstanceState.getIntArray(TILEVALS)?.map{ it }?.toTypedArray()!!
            val turned = savedInstanceState.getBooleanArray(TURNED)?.map{ mutableStateOf(it) }?.toTypedArray()!!
            for (i in 0..< ROWS * COLS) {
                tiles[i].value = tilevalues[i]
                tiles[i].turned = turned[i].value
            }
            lastTile = if (lb == -1) null else tiles[lb]
        }
        if (score == 0) init()
        showscore()
    }

    private fun showscore() {
        scoreState = if (numMatched == ((ROWS * COLS) / 2) ) {
            "Complete:$score"
        } else {
            "Score:$numMatched/$score"
        }
    }

    private fun init() {
        numMatched = 0
        score = 0
        lastTile = null
        for (tile in tiles) {
            tile.value = -1
            tile.turned = false
            tile.showing = false
        }
        for (i in drawables.indices) {
            var x: Int
            (0..<(ROWS* COLS)/drawables.size).forEach { _ ->
                do {
                    x = (0..ROWS*COLS-1).random()
                } while ( tiles[x].value!=-1)
                tiles[x].value =  i
            }
        }
        showscore()
    }
}
