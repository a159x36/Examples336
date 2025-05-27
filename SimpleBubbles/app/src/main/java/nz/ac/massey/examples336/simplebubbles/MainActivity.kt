package nz.ac.massey.examples336.simplebubbles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import nz.ac.massey.examples336.simplebubbles.theme.ui.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel=BubbleViewModel(this)
        setContent {
            AppTheme {
                BubbleApp(viewModel)
            }
        }
    }
}


