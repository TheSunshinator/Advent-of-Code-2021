package day03

import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return input.parseBits()
            .reduce { accumulator, entry -> accumulator.zip(entry, Int::plus) }
            .toInt()
            .withReverse(input.first().length)
            .let { it.first * it.second }
    }

    val testInput = readInput("03", "test_input")
    val input = readInput("03", "input")

    part1(testInput) shouldBe 198
    println(part1(input))
}

fun Int.withReverse(length: Int) = (0 until length).fold(0, Int::inject1At).let { this to xor(it) }
infix fun Int.inject1At(bitIndex: Int) = 1 shl bitIndex or this
