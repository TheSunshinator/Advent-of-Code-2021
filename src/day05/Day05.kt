package day05

import Point
import arrow.core.identity
import io.kotest.matchers.shouldBe
import iterateTo
import readInput

fun main() {
    fun part1(input: List<String>) = computePointCount(input, Sequence<Line>::filterOutDiagonals)
    fun part2(input: List<String>) = computePointCount(input)

    val testInput = readInput("05", "test_input")
    val input = readInput("05", "input")

    part1(testInput) shouldBe 5
    println(part1(input))

    part2(testInput) shouldBe 12
    println(part2(input))
}

fun computePointCount(input: List<String>, lineFilter: Sequence<Line>.() -> Sequence<Line> = ::identity): Int {
    return parse(input)
        .lineFilter()
        .flatMap { it.points }
        .groupingBy(::identity)
        .eachCount()
        .count { it.value > 1 }
}

fun Sequence<Line>.filterOutDiagonals(): Sequence<Line> = filterNot { it.isDiagonal }

fun parse(input: List<String>): Sequence<Line> {
    return input.asSequence()
        .mapNotNull(lineRegex::matchEntire)
        .map { match ->
            Line(
                start = Point(x = match.groupValues[1].toInt(), y = match.groupValues[2].toInt()),
                end = Point(x = match.groupValues[3].toInt(), y = match.groupValues[4].toInt()),
            )
        }
}

val lineRegex = "\\A(\\d+),(\\d+) -> (\\d+),(\\d+)\\z".toRegex()

data class Line(
    val start: Point,
    val end: Point,
) {
    val isDiagonal = start.x != end.x && start.y != end.y

    val points: Iterable<Point> by lazy {
        when {
            start.x == end.x -> (start.y iterateTo end.y).map { Point(start.x, it) }
            start.y == end.y -> (start.x iterateTo end.x).map { Point(it, start.y) }
            else -> (start.x iterateTo end.x)
                .zip(start.y iterateTo end.y)
                .map { (x, y) -> Point(x, y) }
        }
    }
}
