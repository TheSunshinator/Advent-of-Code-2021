package day01

import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: List<String>) = input.getIncreaseCount()
    fun part2(input: List<String>) = input.getIncreaseCount {
        windowed(3) { (current, next, afterNext) -> current + next + afterNext }
    }

    val testInput = readInput("01", "test_input")
    part1(testInput) shouldBe 7
    part2(testInput) shouldBe 5

    val input = readInput("01", "input")
    println(part1(input))
    println(part2(input))
}

inline fun List<String>.getIncreaseCount(group: Sequence<Int>.() -> Sequence<Int> = { this }): Int {
    return asSequence()
        .map { it.toInt() }
        .group()
        .zipWithNext()
        .count { (previous, next) -> previous < next }
}
