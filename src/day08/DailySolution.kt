package day08

import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return parse(input)
            .asSequence()
            .map { it.takeLast(4) }
            .sumOf { numbers -> numbers.count { it.length in uniqueLength } }
    }

    fun part2(input: List<String>) = parse(input).sumOf(::computeNumber)

    val testInput = readInput("08", "test_input")
    val input = readInput("08", "input")

    part1(testInput) shouldBe 26
    println(part1(input))

    part2(testInput) shouldBe 61229
    println(part2(input))
}

fun parse(input: List<String>) = input.map { it.split(splitRegex) }

val splitRegex = " \\| | ".toRegex()
val uniqueLength = setOf(2, 3, 4, 7)

fun computeNumber(patterns: List<String>): Int {
    val computedNumbers = computeNumberToPatternMap(patterns)
    return patterns.takeLast(4)
        .asSequence()
        .map { numberPattern ->
            computedNumbers.firstNotNullOf { (number, pattern) ->
                number.takeIf { pattern.length == numberPattern.length && pattern.all { it in numberPattern } }
            }
        }
        .reduce { total, number -> total * 10 + number }
}

fun computeNumberToPatternMap(patterns: List<String>) = buildMap<Int, String> {
    put(1, patterns.first { it.length == 2 })
    put(4, patterns.first { it.length == 4 })
    put(7, patterns.first { it.length == 3 })
    put(8, patterns.first { it.length == 7 })
    put(9, patterns.first { pattern -> pattern.length == 6 && getValue(4).all { it in pattern } })
    put(6, patterns.first { pattern -> pattern.length == 6 && getValue(7).any { it !in pattern } })
    put(0, patterns.first { it.length == 6 && it != getValue(9) && it != getValue(6) })
    put(3, patterns.first { pattern -> pattern.length == 5 && getValue(1).all { it in pattern } })
    put(5, patterns.first { pattern -> pattern.length == 5 && pattern.all { it in getValue(6) } })
    put(2, patterns.first { it.length == 5 && it != getValue(3) && it != getValue(5) })
}
