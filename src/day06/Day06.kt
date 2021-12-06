package day06

import arrow.core.identity
import io.kotest.matchers.shouldBe
import readInput
import java.util.LinkedList

fun main() {
    fun part1(input: List<String>) = computeSchool(input, 80)
    fun part2(input: List<String>) = computeSchool(input, 256)

    val testInput = readInput("06", "test_input")
    val input = readInput("06", "input")

    part1(testInput) shouldBe 5934
    println(part1(input))

    part2(testInput) shouldBe 26984457539
    println(part2(input))
}

fun computeSchool(input: List<String>, days: Int): Long {
    return parse(input)
        .groupingBy(::identity)
        .eachCount()
        .createFishCountList()
        .foldedBy(0 until days) { school, _ -> school.handleBabies() }
        .sum()
}

fun parse(input: List<String>) = input.first().splitToSequence(",").map(String::toInt)
inline fun <T, U> T.foldedBy(iterable: Iterable<U>, operation: (T, U) -> T): T = iterable.fold(this, operation)

fun Map<Int, Int>.createFishCountList() = (0 until 9).fold(LinkedList<Long>()) { list, index ->
    list.apply { add(this@createFishCountList[index]?.toLong() ?: 0L) }
}

fun LinkedList<Long>.handleBabies(): LinkedList<Long> = apply {
    val newParent = removeFirst()
    add(newParent)
    this[6] += newParent
}
