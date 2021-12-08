package day04

import arrow.core.identity
import io.kotest.matchers.shouldBe
import parseIntSequence
import readInput

typealias Winner = Pair<List<Int?>, Int>
typealias Grid = List<Int?>

fun main() {
    fun part1(input: List<String>) = selectWinner(input, Sequence<Winner>::first)
    fun part2(input: List<String>) = selectWinner(input, Sequence<Winner>::last)

    val testInput = readInput("04", "test_input")
    val input = readInput("04", "input")

    part1(testInput) shouldBe 4512
    println(part1(input))

    part2(testInput) shouldBe 1924
    println(part2(input))
}

private fun selectWinner(input: List<String>, selector: Sequence<Winner>.() -> Winner): Int {
    return parse(input)
        .let(computeWinnerSequence)
        .selector()
        .let(encryptSolution)
}

private fun parse(input: List<String>) = parseGrids(input) to parseIntSequence(input)

private val computeWinnerSequence: (Pair<List<Grid>, Sequence<Int>>) -> Sequence<Winner> = { (grids, numberSequence) ->
    sequence {
        val gridsToMarks = grids.map {
            it to Marks(MutableList(5) { 0 }, MutableList(5) { 0 })
        }

        numberSequence.fold(gridsToMarks) { accumulator, drawnNumber ->
            val (winners, stillPlaying) = accumulator.map { entry ->
                val grid = entry.first
                grid.indexOf(drawnNumber)
                    .takeIf { it in grid.indices }
                    ?.let { index -> entry.mark(index) }
                    ?: entry
            }.partition { (_, marks) -> marks.hasWon }

            winners.asSequence()
                .map { it.first to drawnNumber }
                .let { yieldAll(it) }

            stillPlaying
        }
    }
}

val encryptSolution: (Winner) -> Int = { (winner, winningNumber) ->
    winner.asSequence()
        .filterNotNull()
        .sum()
        .times(winningNumber)
}

private fun parseGrids(input: List<String>): List<Grid> {
    return input.asSequence()
        .drop(2)
        .filter { it.isNotBlank() }
        .map { line ->
            numberRegex.findAll(line)
                .mapTo(mutableListOf()) { it.value.toInt() }
        }
        .chunked(5) {
            it.flatten()
                .mapTo(mutableListOf<Int?>(), ::identity)
        }
        .toList()
}

val numberRegex = "\\d+".toRegex()

private fun Pair<Grid, Marks>.mark(index: Int) = Pair(
    first.toMutableList().apply { this[index] = null },
    second.incrementedAt(index / 5, index % 5)
)

private data class Marks(
    val marksInRows: List<Int>,
    val marksInColumns: List<Int>,
) {
    val hasWon: Boolean by lazy {
        marksInRows.any { it >= marksInRows.size }
            || marksInColumns.any { it >= marksInColumns.size }
    }

    fun incrementedAt(i: Int, j: Int) = copy(
        marksInRows = marksInRows.mapIndexed { rowIndex, count -> if (rowIndex == i) count + 1 else count },
        marksInColumns = marksInColumns.mapIndexed { columnIndex, count -> if (columnIndex == j) count + 1 else count },
    )
}
