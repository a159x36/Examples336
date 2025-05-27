package nz.ac.massey.examples336.beaches

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nz.ac.massey.examples336.beaches.ui.theme.AppTheme
import java.net.URL

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

    suspend fun loadImage(url: String):Bitmap? {
        return withContext(Dispatchers.IO) {
            val u = URL(url)
            try {
                val connection = u.openConnection()
                BitmapFactory.decodeStream(connection.getInputStream())
            } catch (e: Exception) {
                Log.i(TAG, e.stackTrace.toString())
                null
            }
        }
    }

    val reload = mutableIntStateOf(0)

    @Composable
    fun BeachImage(index: Int) {
        val bitmap:MutableState<Bitmap?> = remember { mutableStateOf(null) }
//        Log.i(TAG, "BeachImage:$index ${bitmap.value} ${Thread.currentThread().id}")
        LaunchedEffect(reload.intValue) {
            bitmap.value = null
            // uncomment to see loading text
            // delay(1000)
            bitmap.value = loadImage(urls[index])
//            Log.i(TAG, "Image loaded:$index $bitmap ${Thread.currentThread().id}")
        }
        Box(modifier = Modifier.height(with(LocalDensity.current){720.toDp()}).background(Color.LightGray)) {
            if(bitmap.value!=null) {
                Log.i(TAG, "Image drawing $bitmap.value")
                Image(bitmap.value!!.asImageBitmap(), contentDescription = "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxHeight())
            }
            else {
                Text("Loading....",Modifier.fillMaxSize(), fontSize = 24.sp)
            }

        /*    AsyncImage(
                model = urls[index],
                contentDescription = urls[index],
                contentScale = ContentScale.Crop,
                modifier = modifier.fillMaxSize()
            )

         */
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Beaches(modifier: Modifier = Modifier) {
        Scaffold(modifier, topBar = { TopAppBar(
                title = { Text("Beaches") },
            actions = {IconButton(onClick = {reload.intValue++}) {Icon(Icons.Default.Refresh,"")}
            })
        }) { innerPadding ->
            Box {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(urls.size) { index ->
                        BeachImage(index)
                        Text(urls[index])
                    }
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
