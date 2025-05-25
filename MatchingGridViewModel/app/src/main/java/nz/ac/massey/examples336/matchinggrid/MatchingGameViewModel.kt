package nz.ac.massey.examples336.matchinggrid

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MatchingGameViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        reset()
    }
    fun updateGameState(newscore: Int, newnummatched: Int, newlasttile: Tile?) {
        _uiState.update { currentState ->
            currentState.copy(
                score = newscore,
                numMatched = newnummatched,
                lastTile = newlasttile
            )
        }
    }
    fun reset() {
        val newgame=GameUiState(numMatched = 0, score = 0, lastTile = null)

        for (tile in newgame.tiles) {
            tile.value = -1
        }
        with(newgame) {
            for (i in 0..<8) {
                var x: Int
                (0..<(rows * cols) / 8).forEach {
                    do {
                        x = (0..(rows*cols-1)).random()
                    } while (tiles[x].value != -1)
                    tiles[x].value = i
                }
            }
        }
        _uiState.update { currentState -> newgame }
    }
}