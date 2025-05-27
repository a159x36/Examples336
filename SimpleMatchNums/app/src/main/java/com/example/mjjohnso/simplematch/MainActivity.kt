package com.example.mjjohnso.simplematch
import android.R.attr.rotation
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    private var score = 0               // how many buttons have been clicked
    private var numMatched = 0          // how many matches are there
    private var lastButtonIndex = -1    // index of the last button clicked

    private var buttonValues = mutableListOf<String>()      // values behind each button
    private var buttonState = mutableStateListOf<String>()  // displayed value of each button
    private var scoreState by mutableStateOf("")    // score displayed on the screen

    // called when a button is clicked
    fun buttonClicked(index: Int) {
        Log.i(TAG, "buttonClicked $index")
        // if the button is already showing a value, do nothing
        if (buttonState[index] == "") {
            score++ // increment score
            buttonState[index] = buttonValues[index] // show the value
            if (lastButtonIndex == -1) { // if this is the first button clicked
                lastButtonIndex = index
            } else { // this is the second button clicked
                // if the two buttons have the same value
                if (buttonState[lastButtonIndex] == buttonValues[index]) {
                    numMatched++ // increment numMatched
                    lastButtonIndex = -1 // keep buttons displayed
                } else { // buttons don't match
                    buttonState[lastButtonIndex] = "" // hide value of last button
                    lastButtonIndex = index // this becomes the last button
                }
            }
        }
        updatescore() // show score
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE, score)
        outState.putInt(LAST, lastButtonIndex)
        outState.putInt(MATCH, numMatched)
        outState.putStringArrayList(NUMS, buttonValues.toCollection(ArrayList()))
        val temp = Array(ROWS * COLS) {""}
        for (i in 0..<ROWS * COLS) {
            temp[i] = buttonState[i]
        }
        outState.putStringArray(BUTTONS, temp)
        super.onSaveInstanceState(outState)
    }

    @Composable
    fun MatchGame(modifier: Modifier = Modifier) {
        // Column is passed the modifier so it can add padding to avoid the system UI
        Column(modifier=modifier) {
            Row { // First row shows score and restart button
                Text( // score takes up 3/4 width
                    text = scoreState,
                    modifier = Modifier.weight(3f).align(Alignment.CenterVertically)
                )
                // call init() when the Restart button is pressed
                // restart button takes up 1/4 the width
                Button(onClick = { init() }, modifier = Modifier.weight(1f)) { Text("Restart") }
            }
            // draw the grid of buttons
            for (i in 0..<ROWS)
                Row(modifier = Modifier.weight(1f)) { // each row has equal weight
                    for (j in 0..<COLS) { // draw row of buttons
                        val index = i * COLS + j
                        val rotation by animateFloatAsState(targetValue=if (buttonState[index] == "") 180f else 0f,
                            animationSpec = tween(durationMillis = 1000))
                        Button( // draw a button
                            shape = RoundedCornerShape(4.dp),
                            onClick = { buttonClicked(index) }, // call buttonClicked when pressed
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)
                            .graphicsLayer { rotationY=rotation },
                            contentPadding = PaddingValues(0.dp) // remove default padding
                        ) {
                            Text( // draw the button value
                                text = if(rotation<90f) buttonValues[index] else "",
                                fontSize = (6 * 48 / COLS).sp,
                            )
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
        init()
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE, 0)
            lastButtonIndex = savedInstanceState.getInt(LAST, -1)
            numMatched = savedInstanceState.getInt(MATCH, 0)
            buttonValues = savedInstanceState.getStringArrayList(NUMS)!!
            val temp = savedInstanceState.getStringArray(BUTTONS)!!
            for (i in 0..<ROWS*COLS) {
                buttonState[i]=temp[i]
            }
            updatescore()
        }

    }

    private fun updatescore() {
        scoreState = if (numMatched == ((ROWS * COLS) / 2)) {
            "Complete:$score"
        } else {
            "Score:$score"
        }
    }

    private fun init() {
        numMatched = 0 // initialise score and numMatched
        score = 0
        lastButtonIndex = -1 // no last button
        buttonValues.clear()
        buttonState.clear()
        (1..ROWS*COLS).forEach { // add empty buttons
            buttonValues.add("")
            buttonState.add("")
        }
        for(i in 1..(ROWS * COLS) / 2) {
            var x: Int
            // put pairs of numbers behind random buttons
            (0..1).forEach {
                do {
                    x = (0..(ROWS * COLS)-1).random()
                } while ("" != buttonValues[x])
                buttonValues[x] = "$i"
            }
        }
        updatescore() // set the score
    }
}

