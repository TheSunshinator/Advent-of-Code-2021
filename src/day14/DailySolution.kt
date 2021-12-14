package day14

import arrow.core.identity
import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: Input) = computeMaxMinDifference(input, 10)
    fun part2(input: Input) = computeMaxMinDifference(input, 40)

    val testInput = readInput("14", "test_input").let(::parse)
    val input = readInput("14", "input").let(::parse)

    part1(testInput) shouldBe 1588
    println(part1(input))

    part2(testInput) shouldBe 2188189693529
    println(part2(input))
}

internal fun computeMaxMinDifference(input: Input, repetitions: Int): Long {
    return (0 until repetitions).fold(input.startCounts) { counts, _ ->
        counts.asSequence()
            .flatMap { (node, count) -> input.insertions.getValue(node).map { it to count } }
            .groupingBy { it.first }
            .eachLongCount { it.second }
            .toMap()
    }
        .asSequence()
        .groupingBy { it.key.first() }
        .eachLongCount { it.value }
        .apply { compute(input.extraElement) { _, count -> count?.plus(1) } }
        .let { elementCount -> elementCount.maxOf { it.value } - elementCount.minOf { it.value } }
}

internal data class Input(
    val startCounts: Map<String, Long>,
    val insertions: Map<String, Sequence<String>>,
    val extraElement: Char
)

internal fun parse(input: List<String>) = Input(
    startCounts = input.first().asSequence()
        .zipWithNext { previous, next -> "$previous$next" }
        .groupingBy(::identity)
        .eachLongCount { 1L },
    insertions = input.asSequence()
        .drop(2)
        .associate {
            it.take(2) to sequenceOf("${it.first()}${it.last()}", "${it.last()}${it[1]}")
        },
    extraElement = input.first().last(),
)

internal fun <T, K> Grouping<T, K>.eachLongCount(selector: (T) -> Long): MutableMap<K, Long> {
    return foldTo(mutableMapOf(), 0L) { total, element -> total + selector(element) }
}
