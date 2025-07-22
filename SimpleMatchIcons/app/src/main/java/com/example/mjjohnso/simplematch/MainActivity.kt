package com.example.mjjohnso.simplematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mjjohnso.simplematch.ui.theme.AppTheme

const val ROWS=4
const val COLS=4
const val SCORE = "SCORE"
const val LAST = "LAST"
const val MATCH = "MATCH"
const val NUMS = "NUMS"
const val TURNED = "TURNED"

class MainActivity : ComponentActivity() {

    private var score = 0
    private var numMatched = 0
    private var lastButtonIndex = -1

    private var tileValues = Array(ROWS * COLS) {0}
    private var scoreState = mutableStateOf("")
    private var turned = Array(ROWS * COLS) {mutableStateOf(false)}

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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE, score)
        outState.putInt(LAST, lastButtonIndex)
        outState.putInt(MATCH, numMatched)
        outState.putIntArray(NUMS, tileValues.toIntArray())
        outState.putBooleanArray(TURNED,turned.map{it.value}.toBooleanArray())
        super.onSaveInstanceState(outState)
    }

    private fun buttonClick(index: Int) {
        if (!turned[index].value) {
            score++
            turned[index].value = true
            if (lastButtonIndex == -1) {
                lastButtonIndex = index
            } else {
                if (tileValues[lastButtonIndex] == tileValues[index]) {
                    numMatched++
                    lastButtonIndex = -1
                } else {
                    val bi=lastButtonIndex
                    lastButtonIndex = index
                    turned[bi].value = false
                }
            }
        }
        showscore()
    }

    @Composable
    fun TurningButton( index: Int, modifier: Modifier = Modifier) {
        val angle by animateFloatAsState(
            targetValue = if (turned[index].value) 0f else 180f,
            animationSpec =  spring( stiffness = StiffnessMediumLow,
                dampingRatio = DampingRatioMediumBouncy)
        )
        Button( shape = RoundedCornerShape(4.dp),
            onClick= { buttonClick(index)},
            modifier = modifier.fillMaxSize()
                .padding(4.dp)
                .graphicsLayer { rotationY=angle },
            contentPadding=PaddingValues(0.dp)) {
            if(angle<=90f && turned[index].value) {
                Image(painter = painterResource(id =  drawables[tileValues[index]]),
                    contentDescription = "",
                    modifier.fillMaxWidth().
                    fillMaxHeight(),
                    contentScale=ContentScale.Fit)
            }
        }
    }

    @Composable
    fun MatchGame(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Row {
                Text(text = scoreState.value, modifier = Modifier.weight(2f).align(Alignment.CenterVertically))
                Button(onClick = { init() }, modifier = Modifier.weight(1f)) {Text("Restart")}
            }
            for (i in 0..<ROWS) {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..<COLS) {
                        val index = i * COLS + j
                        TurningButton(index, Modifier.weight(1f))
                    }
                }
            }
        }
    }
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun ComposablePreview() {
        init()
        AppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MatchGame(modifier = Modifier.padding(innerPadding))
            }
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
            lastButtonIndex = savedInstanceState.getInt(LAST, -1)
            numMatched = savedInstanceState.getInt(MATCH, 0)
            tileValues = savedInstanceState.getIntArray(NUMS)?.map{ it }?.toTypedArray()!!
            turned = savedInstanceState.getBooleanArray(TURNED)?.map{ mutableStateOf(it) }?.toTypedArray()!!
        }
        if (score == 0) init()
        showscore()
    }

    private fun showscore() {
        if (numMatched == ((ROWS * COLS) / 2) ) {
            scoreState.value = "Complete:$score"
        } else {
            scoreState.value = "Score:$score"
        }
    }

    private fun init() {
        numMatched = 0
        score = 0
        lastButtonIndex = -1
        for (i in 0..< ROWS * COLS) {
            tileValues[i] = -1
            turned[i].value = false
        }
        for (i in 0..<(ROWS * COLS)/2) {
            var x: Int
            (0..1).forEach { _ ->
                do {
                    x = (Math.random() * (ROWS * COLS)).toInt()
                } while ( tileValues[x]!=-1)
                tileValues[x] =  i
            }
        }
        showscore()
    }
}
