package com.example.mjjohnso.modifiers
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mjjohnso.modifiers.theme.ui.AppTheme

class MainActivity : ComponentActivity() {

    @Composable
    fun RowScope.TextItem(text: String, modifier: Modifier = Modifier) {
       Card(modifier.padding(2.dp), shape = RoundedCornerShape(8.dp))
       { Text(text, Modifier.padding(8.dp)) }
    }

    @Composable
    fun Label(t: String) {
        Text(t, fontSize = 15.sp, modifier = Modifier.padding(2.dp))
    }

    @Composable
    fun Modifiers(modifier: Modifier = Modifier) {
        Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            val modifier=Modifier.fillMaxWidth().background(Color.Yellow)
            Label("modifier.fillMaxWidth(),\nhorizontalArrangement = (Arrangement.SpaceEvenly)")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = (Arrangement.SpaceEvenly)
            ) {
                TextItem("Text1")
                TextItem("Text2")
                TextItem("Text3")
                TextItem("Text4")
            }
            Label("modifier.fillMaxWidth(),\nhorizontalArrangement = (Arrangement.SpaceAround)")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = (Arrangement.SpaceAround)
            ) {
                TextItem("Text1")
                TextItem("Text2")
                TextItem("Text3")
                TextItem("Text4")
            }
            Label("modifier.fillMaxWidth(),\nhorizontalArrangement = (Arrangement.SpaceBetween)")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = (Arrangement.SpaceBetween)
            ) {
                TextItem("Text1")
                TextItem("Text2")
                TextItem("Text3")
                TextItem("Text4")
            }
            Label("horizontalArrangement = (Arrangement.Start)")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = (Arrangement.Start)
            ) {
                TextItem("Text1")
                TextItem("Text2")
                TextItem("Text3")
                TextItem("Text4")
            }
            Label("horizontalArrangement = (Arrangement.End)")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = (Arrangement.End),
            ) {
                TextItem("Text1")
                TextItem("Text2")
                TextItem("Text3")
                TextItem("Text4")
            }
            Label("horizontalArrangement = (Arrangement.Center)")
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = (Arrangement.Center),
            ) {
                TextItem("Text1")
                TextItem("Text2")
                TextItem("Text3")
                TextItem("Text4")
            }
            Label("Modifier.height(IntrinsicSize.Min)\nModifier.weight(1f)")
            Row(
                modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min)
            ) {
                TextItem("Text1", Modifier.weight(1f))
                TextItem("Text2", Modifier.weight(1f))
                TextItem("Text3", Modifier.weight(1f))
                TextItem("Text4", Modifier.weight(1f))
            }
            Label("Modifier.height(IntrinsicSize.Min)\nModifier.weight(1f)\nModifier.weight(1f)\nModifier.weight(1.5f)\nModifier.weight(2f)")
            Row(
                modifier = modifier.height(IntrinsicSize.Min)
            ) {
                TextItem("Text1", Modifier.weight(1f))
                TextItem("Text2", Modifier.weight(1f))
                TextItem("Text3", Modifier.weight(1.5f))
                TextItem("Text4", Modifier.weight(2f))
            }
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun DemoScreen() {
        AppTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Modifiers(modifier = Modifier.padding(innerPadding).background(Color.White))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoScreen()
        }
    }
}

