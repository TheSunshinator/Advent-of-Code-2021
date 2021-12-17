package day17

import Point
import cartesianProduct
import io.kotest.matchers.shouldBe

fun main() {
    fun part1(rangeY: IntRange): Int {
        return generateSequence(-rangeY.first - 1) { it - 1 }
            .takeWhile { it >= rangeY.first }
            .runningReduce(Int::plus)
            .maxOrNull()!!
    }

    fun part2(rangeX: IntRange, rangeY: IntRange) : Int {
        return (minXVelocity(rangeX.first)..rangeX.last).cartesianProduct(rangeY.first until -rangeY.first)
            .map(::trajectory)
            .map { it.lastBeforeRange(rangeX, rangeY) }
            .count { it.x in rangeX && it.y in rangeY }
    }

    val testInput = 20..30 to -10..-5
    val input = 88..125 to -157..-103

    part1(testInput.second) shouldBe 45
    println(part1(input.second))

    part2(testInput.first, testInput.second) shouldBe 112
    println(part2(input.first, input.second))
}

fun minXVelocity(minX: Int): Int {
    return generateSequence(0) { it + 1 }
        .map { initialVelocity -> (0..initialVelocity).sum() }
        .indexOfFirst { it >= minX }
}

fun trajectory(initialVelocity: Point): Sequence<Point> = sequence {
    var velocity = initialVelocity
    var position = initialVelocity
    do {
        yield(position)
        velocity = Point((velocity.x - 1).coerceAtLeast(0), velocity.y - 1)
        position = Point(position.x + velocity.x, position.y + velocity.y)
    } while (true)
}

fun Sequence<Point>.lastBeforeRange(rangeX: IntRange, rangeY: IntRange): Point {
    return takeWhile { it.x <= rangeX.last && it.y >= rangeY.first }.last()
}
