package org.example.project

data class GameUiState (
    val rows: Int = 12,
    val cols: Int = 4,
    val tiles: List<Tile> = List(rows * cols) { Tile() },
    val score: Int = 0,
    val numMatched: Int = 0,
    val lastTile: Tile? = null
)
