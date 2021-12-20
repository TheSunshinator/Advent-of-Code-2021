package day20

import Point
import getOrElse
import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: List<List<Long>>, enhancement: List<Long>) = countWhitePixels(input, enhancement, 2)
    fun part2(input: List<List<Long>>, enhancement: List<Long>) = countWhitePixels(input, enhancement, 50)

    val (testInput, testEnhancementPixels) = readInput("20", "test_input").let(::parse)
    val (input, enhancementPixels) = readInput("20", "input").let(::parse)

    part1(testInput, testEnhancementPixels) shouldBe 35
    println(part1(input, enhancementPixels))

    part2(testInput, testEnhancementPixels) shouldBe 3351
    println(part2(input, enhancementPixels))
}

private fun parse(input: List<String>): Pair<List<List<Long>>, List<Long>> {
    return input.asSequence()
        .drop(2)
        .mapTo(mutableListOf()) { it.map(::toBit) } to input.first()
        .map(::toBit)
}

private fun toBit(char: Char) = if (char == '.') 0L else 1L

private fun countWhitePixels(input: List<List<Long>>, enhancement: List<Long>, times: Int): Long {
    return (0 until times)
        .fold(input to 0L) { (image, defaultColor), _ ->
            Pair(
                image.expand(enhancement, defaultColor),
                if (defaultColor == 0L) enhancement.first() else enhancement.last(),
            )
        }
        .first
        .asSequence()
        .flatten()
        .fold(0L, Long::plus)
}

private fun List<List<Long>>.expand(enhancement: List<Long>, defaultValue: Long) = List(size + 2) { row ->
    List(first().size + 2) { column ->
        Point(row, column)
            .neighbors(includeThis = true, includeDiagonal = true)
            .sortedWith(compareBy<Point> { it.x }.thenBy { it.y })
            .map { Point(it.x - 1, it.y - 1) }
            .map { getOrElse(it) { defaultValue } }
            .reduce { accumulator, nextNumber -> (accumulator shl 1) + nextNumber }
            .toInt()
            .let(enhancement::get)
    }
}
