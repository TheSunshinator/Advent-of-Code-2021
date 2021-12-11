package day11

import Point
import coordinates
import digitToInt
import get
import io.kotest.matchers.shouldBe
import readInput
import set

fun main() {
    fun part1(input: List<List<Int>>): Long {
        val state = input.map { it.toMutableList() }
        return (0 until 100).fold(0L) { total, _ ->
            total + state.coordinates()
                .sumOf(state::flashAt)
                .also { state.resetHighEnergy() }
        }
    }

    fun part2(input: List<List<Int>>): Long {
        val state = input.mapTo(mutableListOf()) { it.toMutableList() }
        return generateSequence(0L) { it + 1 }
            .onEach { state.coordinates().forEach(state::flashAt) }
            .first { state.haveAllFlashed().also { state.resetHighEnergy() } } + 1
    }

    val testInput = readInput("11", "test_input").mapToListOfInt()
    val input = readInput("11", "input").mapToListOfInt()

    part1(testInput) shouldBe 1656
    println(part1(input))

    part2(testInput) shouldBe 195
    println(part2(input))
}

fun List<String>.mapToListOfInt() = map { it.map(Char::digitToInt) }

fun List<MutableList<Int>>.flashAt(p: Point): Long {
    this[p]++
    return if (this[p] == 10) {
        (p.x `±` 1).cartesianProduct(p.y `±` 1)
            .filter { it isInGrid this }
            .sumOf(::flashAt) + 1
    } else 0L
}

infix fun Int.`±`(margin: Int) = (this - margin)..(this + margin)
infix fun IntRange.cartesianProduct(other: IntRange) = asSequence().flatMap { i -> other.map { j -> Point(i, j) } }
infix fun Point.isInGrid(grid: List<List<*>>) = x in grid.indices && y in grid[x].indices

fun List<MutableList<Int>>.resetHighEnergy() {
    coordinates().filter { this[it] >= 10 }.forEach { this[it] = 0 }
}

fun List<List<Int>>.haveAllFlashed(): Boolean = all { row -> row.all { it >= 10 } }
