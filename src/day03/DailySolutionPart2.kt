package day03

import io.kotest.matchers.shouldBe
import readInput
import java.lang.Integer.min
import kotlin.math.max

fun main() {
    fun part2(input: List<String>): Int {
        return input.parseBits()
            .toList()
            .run { computeRating(1, ::max) * computeRating(-1, ::minNotZero) }
    }

    val testInput = readInput("03", "test_input")
    val input = readInput("03", "input")

    part2(testInput) shouldBe 230
    println(part2(input))
}

fun List<List<Int>>.computeRating(comparator: Int, selector: (Int, Int) -> Int): Int {
    return first()
        .indices
        .fold(this) { filteredBits, index ->
            filteredBits.splitByBit(index, comparator).select(selector) { it.size }
        }
        .single()
        .toInt()
}

fun List<List<Int>>.splitByBit(bitPosition: Int, value: Int) = partition { it[bitPosition] == value }

fun <T, U> Pair<T, T>.select(predicate: (U, U) -> U, selector: (T) -> U): T {
    val firstSelector = selector(first)
    return if (firstSelector == predicate(firstSelector, selector(second))) first else second
}

fun minNotZero(a: Int, b: Int) = when {
    a == 0 -> b
    b == 0 -> a
    else -> min(a, b)
}
