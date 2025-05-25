package com.example.mjjohnso.simplematch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mjjohnso.simplematch.theme.ui.AppTheme

const val ROWS=4
const val COLS=4

class MainActivity : ComponentActivity() {

    private var score = 0
    private var numMatched = 0
    private var lastButtonIndex = -1

    private var buttonValues = MutableList(ROWS * COLS) { "" }
    private var buttonState = List(ROWS * COLS) { mutableStateOf("") }
    private var scoreState by mutableStateOf("")

    fun buttonClicked(index: Int) {
        if (buttonState[index].value == "") {
            score++
            buttonState[index].value = buttonValues[index]
            if (lastButtonIndex == -1) {
                lastButtonIndex = index
            } else {
                if (buttonState[lastButtonIndex].value == buttonValues[index]) {
                    numMatched++
                    lastButtonIndex = -1
                } else {
                    buttonState[lastButtonIndex].value = ""
                    lastButtonIndex = index
                }
            }
        }
        showscore()
    }

    @Composable
    fun MatchGame(modifier: Modifier = Modifier) {
        Column {
            Row {
                Text(
                    text = scoreState,
                    modifier = modifier.weight(3f).align(Alignment.CenterVertically)
                )
                Button(onClick = { init() }, modifier = modifier.weight(1f)) { Text("Restart") }
            }
            for (i in 0..<ROWS)
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..<COLS) {
                        val index = i * COLS + j
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            onClick = { buttonClicked(index) },
                            modifier = modifier.weight(1f).fillMaxHeight().padding(4.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = buttonState[index].value,
                                fontSize = (4 * 48 / COLS).sp,
                                modifier = Modifier.padding(all = 0.dp)
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
        MatchGame()
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
        init()
    }

    private fun showscore() {
        scoreState = if (numMatched == ((ROWS * COLS) / 2)) {
            "Complete:$score"
        } else {
            "Score:$score"
        }
    }

    private fun init() {
        numMatched = 0
        score = 0
        lastButtonIndex = -1
        for(i in buttonValues.indices)
            buttonValues[i] = ""

        for(i in 1..(ROWS * COLS) / 2) {
            var x: Int
            (0..1).forEach {
                do {
                    x = (0..(ROWS * COLS)-1).random()
                } while ("" != buttonValues[x])
                buttonValues[x] = "" + i
                buttonState[x].value = ""
            }
        }
        showscore()
    }
}
