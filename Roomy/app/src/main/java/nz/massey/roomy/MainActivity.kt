package nz.massey.roomy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import nz.massey.roomy.CourseViewModel
import nz.massey.roomy.theme.ui.AppTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = CourseViewModel(this)
        setContent {
            AppTheme {
                Nav(viewModel)
            }
        }
    }
}