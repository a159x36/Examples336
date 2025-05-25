package nz.ac.massey.examples336.touchbubbles

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {


    Window(
        onCloseRequest = ::exitApplication,
        title = "Matching",
    ) {
        Navigation()
    }
}