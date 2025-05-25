package com.example.mjjohnso.simplematch
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.launch

const val ROWS=4
const val COLS=4
const val SCORE = "SCORE"
const val LAST = "LAST"
const val MATCH = "MATCH"
const val NUMS = "NUMS"
const val BUTTONS = "BUTTONS"

const val TAG="MainActivity"

class MainActivity : ComponentActivity() {

    private var score = 0
    private var numMatched = 0
    private var lastButtonIndex = -1

    private var buttonValues: Array<String> = Array(ROWS * COLS) {""}
    private var buttonState = Array(ROWS * COLS) { mutableStateOf("") }
    private var scoreState = mutableStateOf("")
    private var animations = Array(ROWS * COLS) {Animatable(0f)}

    private suspend fun setButton(i: Int, s: String) {
        val from: Float
        val to: Float
        if (s === "") {
            from = 0f
            to = 180f
        } else {
            from = 180f
            to = 0f
        }
        animations[i].snapTo(from)
        animations[i].animateTo((from + to) / 2)
        buttonState[i].value = s
        animations[i].animateTo(to)
        animations[i].snapTo(0f)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE, score)
        outState.putInt(LAST, lastButtonIndex)
        outState.putInt(MATCH, numMatched)
        outState.putStringArray(NUMS, buttonValues)
        val temp = Array(ROWS * COLS) {""}
        for (i in 0..<ROWS * COLS) {
            temp[i] = buttonState[i].value
        }
        outState.putStringArray(BUTTONS, temp)
        super.onSaveInstanceState(outState)
    }

    @Composable
    fun MatchGame(modifier: Modifier = Modifier) {
        val scope = rememberCoroutineScope()
        Column {
            Row {
                Text(text = scoreState.value, modifier = modifier.weight(3f).align(Alignment.CenterVertically))
                Button(onClick = { init() }, modifier = modifier.weight(1f)) {Text("Restart")}
            }
            for (i in 0..<ROWS)
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..<COLS) {
                        val index = i * COLS + j
                        Button( shape = RoundedCornerShape(4.dp),
                            onClick = {
                                if (buttonState[index].value == "" && animations[index].value <= 0.1f) {
                                    score++
                                    scope.launch { setButton(index, buttonValues[index]) }
                                    if (lastButtonIndex == -1) {
                                        lastButtonIndex = index
                                    } else {
                                        if (buttonState[lastButtonIndex].value == buttonValues[index]) {
                                            numMatched++
                                            lastButtonIndex = -1
                                        } else {
                                            val bi=lastButtonIndex
                                            lastButtonIndex = index
                                            scope.launch { setButton(bi, "") }
                                        }
                                    }
                                }
                                showscore()
                            },
                            modifier = modifier.weight(1f).fillMaxHeight().padding(4.dp)
                                .graphicsLayer { rotationY=animations[index].value }, contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = buttonState[index].value, fontSize = (4*48/COLS).sp, modifier = Modifier.padding(all=0.dp) )
                        }
                    }
                }
        }
    }
    @Preview
    @Composable
    fun ComposablePreview() {
        init()
        MaterialTheme(colorScheme = MaterialTheme.colorScheme) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MatchGame(modifier = Modifier.padding(innerPadding))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = MaterialTheme.colorScheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MatchGame(modifier = Modifier.padding(innerPadding))
                }
            }
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE, 0)
            lastButtonIndex = savedInstanceState.getInt(LAST, -1)
            numMatched = savedInstanceState.getInt(MATCH, 0)
            buttonValues = savedInstanceState.getStringArray(NUMS)!!
            val temp = savedInstanceState.getStringArray(BUTTONS)!!
            for (i in 0..<ROWS*COLS) {
                buttonState[i].value=temp[i]
            }
        }
        if (score == 0) init()

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
            buttonValues[i] = ""
        }
        for (i in 1..(ROWS * COLS)/2) {
            var x: Int
            for (j in 0..1) {
                do {
                    x = (Math.random() * (ROWS * COLS)).toInt()
                } while ("" != buttonValues[x])
                buttonValues[x] = "$i"
                buttonState[x].value = ""
            }
        }
        showscore()
    }
}

