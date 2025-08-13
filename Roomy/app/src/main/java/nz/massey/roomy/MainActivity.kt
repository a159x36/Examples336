package nz.massey.roomy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import nz.massey.roomy.theme.ui.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: CourseViewModel by viewModels { CourseViewModelFactory(application) }
        setContent {
            AppTheme {
                Nav(viewModel)
            }
        }
    }
}