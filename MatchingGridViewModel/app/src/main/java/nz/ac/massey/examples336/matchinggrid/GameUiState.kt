package nz.ac.massey.examples336.matchinggrid

data class GameUiState (
    val ntiles: Int = 48,
    val cols: Int = 4,
    val tiles: List<Tile> = List(ntiles) { Tile() },
    val score: Int = 0,
    val numMatched: Int = 0,
    val lastTile: Tile? = null
)
