package nz.ac.massey.examples336.matchinggrid

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class Tile {
    var value = 0
    private val _shown = MutableStateFlow<Boolean>(false)
    val shown = _shown.asStateFlow()

    private var matched=false

    suspend fun turnback() {
        delay(800)
        _shown.value=false
    }


    fun init() {
        _shown.value = false
        value=0
        matched=false
    }

    fun buttonClick(viewmodel: MatchingGameViewModel) {
        val uiState = viewmodel.uiState.value
        var newlt = uiState.lastTile
        var newscore = uiState.score
        var newnummatched = uiState.numMatched
        if (!matched && !shown.value) {
            newscore++
            if (uiState.lastTile == null) {
                newlt = this
                _shown.value = true
            } else {
                uiState.lastTile.let {
                    if (it.value == value) {
                        matched = true
                        _shown.value = true
                        it.matched = true
                        newnummatched++
                        newlt = null
                    } else {
                        _shown.value = true
                        CoroutineScope(Dispatchers.Default).launch {
                            turnback()
                        }
                        CoroutineScope(Dispatchers.Default).launch {
                            it.turnback()
                        }
                        newlt = null
                    }
                }
            }
            viewmodel.updateGameState(newscore, newnummatched, newlt)
        }
    }
}
