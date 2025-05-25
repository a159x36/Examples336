package nz.ac.massey.examples336.beaches

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import nz.ac.massey.examples336.beaches.ui.theme.AppTheme

const val TAG="Beaches"

class MainActivity : ComponentActivity() {

    val urls = arrayOf(
        "https://www.trafficnz.info/camera/688.jpg",
        "https://www.trafficnz.info/camera/818.jpg",
        "https://www.trafficnz.info/camera/819.jpg",
        "https://www.trafficnz.info/camera/820.jpg",
        "https://www.trafficnz.info/camera/654.jpg",
        "https://www.trafficnz.info/camera/603.jpg",
        "https://www.trafficnz.info/camera/604.jpg",
        "https://www.trafficnz.info/camera/605.jpg",
        "https://www.trafficnz.info/camera/655.jpg",
        "https://www.trafficnz.info/camera/608.jpg",
        "https://www.trafficnz.info/camera/610.jpg",
        "https://www.trafficnz.info/camera/612.jpg",
        "https://www.trafficnz.info/camera/651.jpg",
        "https://www.trafficnz.info/camera/631.jpg",
        "https://www.trafficnz.info/camera/10.jpg"
    )

    @Composable
    fun BeachImage(index: Int, modifier: Modifier) {

        Log.i(TAG, "BeachImage:$index")
        Box(modifier = Modifier.background(Color.DarkGray).fillMaxSize()) {
            AsyncImage(
                model = urls[index],
                contentDescription = urls[index],
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Beaches(modifier: Modifier = Modifier) {
        Scaffold(topBar = { TopAppBar(
                title = { Text("Beaches") },)
        }) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                items(urls.size) { index ->
                    BeachImage(index, modifier.height(with(LocalDensity.current) { 720.toDp() }))
                    Text(urls[index])
                }
            }
        }
    }
    @Preview
    @Composable
    fun ComposablePreview() {
        AppTheme {
            Beaches()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Beaches(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}
