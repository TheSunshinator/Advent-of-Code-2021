package day04

import arrow.core.flatten
import arrow.core.identity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import parseIntSequence
import readInput

private typealias Grid2 = List<List<Int?>>

fun main() {
    fun part1(input: List<String>) = selectWinner(input, List<GameResult>::minByOrNull)
    fun part2(input: List<String>) = selectWinner(input, List<GameResult>::maxByOrNull)

    val testInput = readInput("04", "test_input")
    val input = readInput("04", "input")

    part1(testInput) shouldBe 4512
    part1(input) shouldBe 39902

    part2(testInput) shouldBe 1924
    part2(input) shouldBe 26936
}

private fun selectWinner(
    input: List<String>,
    selector: List<GameResult>.(valueSelector: (GameResult) -> Int) -> GameResult?
): Int {
    val (grids, numberSequence) = parse(input)
    return runBlocking {
        grids.map { grid -> async { computeWinning(grid, numberSequence) } }.awaitAll()
    }
        .selector { it.winningNumberIndex }!!
        .let { it.grid.flatten() to it.winningNumber }
        .let(encryptSolution)
}

private fun parse(input: List<String>) = parseGrids(input) to parseIntSequence(input)

private fun parseGrids(input: List<String>): List<Grid2> {
    return input.asSequence()
        .drop(2)
        .filter { it.isNotBlank() }
        .map { line ->
            numberRegex.findAll(line)
                .map { it.value.toInt() }
        }
        .chunked(5) { gridEntry ->
            gridEntry.map { it.mapTo(mutableListOf(), ::identity) }
        }
        .toList()
}

fun computeWinning(grid: Grid2, numberSequence: Sequence<Int>): GameResult {
    return numberSequence.foldIndexed(GameResult(grid, 0, 0)) { index, results, number ->
        GameResult(
            grid = results.grid.map { row -> row.map { cardNumber -> cardNumber.takeUnless { it == number } } },
            winningNumber = number,
            winningNumberIndex = index,
        ).also { if (it.hasWon) return it }
    }
}

data class GameResult(
    val grid: Grid2,
    val winningNumber: Int,
    val winningNumberIndex: Int
) {
    val hasWon: Boolean
        get() = grid.hasWon()
}

private fun Grid2.hasWon(): Boolean {
    return any { row -> row.all { it == null } }
        || (0 until 5).any { column -> all { it[column] == null } }
}
