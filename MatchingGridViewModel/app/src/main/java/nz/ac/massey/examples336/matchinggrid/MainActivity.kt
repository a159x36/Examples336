package nz.ac.massey.examples336.matchinggrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.unit.dp
import nz.ac.massey.examples336.matchinggrid.theme.ui.AppTheme
import nz.ac.massey.examples336.matchinggrid.ui.MatchGame

val ROWHEIGHT=128.dp

const val TAG = "MatchingGame"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MatchGame()
            }
        }
    }
}
