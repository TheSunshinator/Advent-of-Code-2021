package day13

import Point
import cartesianProduct
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import readInput

typealias Fold = Pair<Boolean, Int>

fun main() {
    fun part1(points: Set<Point>, fold: Fold) = points.foldSheet(fold.first, fold.second).count()

    fun part2(points: Set<Point>, folds: Sequence<Fold>): Set<Point> {
        return folds.fold(points) { state, fold -> state.foldSheet(fold.first, fold.second) }
    }

    val (testFolds, testGrid) = readInput("13", "test_input").let(::parse)
    val (folds, grid) = readInput("13", "input").let(::parse)

    part1(testGrid, testFolds.first()) shouldBe 17
    println(part1(grid, folds.first()))

    part2(testGrid, testFolds).printCode()
    part2(grid, folds).printCode()
}

fun parse(input: List<String>) = parseFold(input) to parseGrid(input)

fun parseGrid(input: List<String>): Set<Point> {
    return input.asSequence()
        .takeWhile { it.isNotEmpty() }
        .map { it.split(',') }
        .mapTo(mutableSetOf()) { (y, x) -> Point(x.toInt(), y.toInt()) }
}

fun parseFold(input: List<String>): Sequence<Fold> {
    return input.takeLastWhile { it.isNotEmpty() }
        .asSequence()
        .mapNotNull(foldRegex::matchEntire)
        .map { (it.groupValues[1] == "x") to it.groupValues[2].toInt() }
}

val foldRegex = "fold along ([xy])=(\\d+)".toRegex()

fun Set<Point>.foldSheet(isYAxis: Boolean, position: Int) = mapTo(mutableSetOf()) {
    when {
        !isYAxis && it.x > position -> it.copy(x = 2 * position - it.x)
        isYAxis && it.y > position -> it.copy(y = 2 * position - it.y)
        else -> it
    }
}

fun Set<Point>.printCode() {
    (0..maxOf { it.x }).cartesianProduct(0..maxOf { it.y })
        .forEach { p ->
            if (p.y == 0) println()
            print(if (p in this) "#" else " ")
        }
    println()
}
