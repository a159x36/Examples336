package nz.ac.massey.examples336.touchbubbles

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        Navigation()
    }
}