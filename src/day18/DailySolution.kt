package day18

import arrow.core.identity
import cartesianProduct
import io.kotest.matchers.shouldBe
import kotlin.math.ceil
import readInput

fun main() {
    fun part1(input: List<String>) = input.reduce(::add).magnitude()
    fun part2(input: List<String>): Long {
        return input.indices.cartesianProduct(input.indices)
            .map { add(input[it.x], input[it.y]) }
            .maxOf { it.magnitude() }
    }

    val testInput = readInput("18", "test_input")
    val input = readInput("18", "input")

    part1(testInput) shouldBe 4140
    println(part1(input))

    part2(testInput) shouldBe 3993
    println(part2(input))
}

private fun add(a: String, b: String) = "[$a,$b]".reduceExpression()

private fun String.reduceExpression(): String {
    var reduced = this
    do {
        val wasReduced = (reduced.findDeepNode()?.let { match ->
            reduced = reduced.computeExplodedNumber(match)
        } ?: bigNumberRegex.find(reduced)?.let { match ->
            reduced = reduced.computeSplitNumber(match)
        }) != null
    } while (wasReduced)
    return reduced
}

private fun String.findDeepNode(): MatchResult? {
    val depths = depths()
    return simplePairRegex.findAll(this)
        .firstOrNull { simplePairMatch -> depths[simplePairMatch.range.first] > 4 }
}

private fun String.depths() = fold(mutableListOf<Int>()) { depths, char ->
    depths.add(when (char) {
        '[' -> depths.getOrElse(depths.lastIndex) { 0 } + 1
        ']' -> depths.last() - 1
        else -> depths.last()
    })
    depths
}

private fun String.computeExplodedNumber(pair: MatchResult): String {
    return replaceFirst(numberRegex, pair.range.last) { (it.toLong() + pair.groupValues[2].toLong()).toString() }
        .replaceRange(pair.range, "0")
        .replaceLast(numberRegex, pair.range.first) { (it.toLong() + pair.groupValues[1].toLong()).toString() }
}

private fun String.replaceFirst(regex: Regex, startIndex: Int = 0, transform: (String) -> String = ::identity): String {
    return regex.find(this, startIndex)
        ?.let { match -> replaceRange(match.range, transform(match.value)) }
        ?: this
}

private fun String.replaceLast(regex: Regex, endIndex: Int = this.length, transform: (String) -> String = ::identity): String {
    return regex.findAll(this)
        .lastOrNull { it.range.last < endIndex }
        ?.let { match -> replaceRange(match.range, transform(match.value)) }
        ?: this
}

private fun String.computeSplitNumber(subNumber: MatchResult): String {
    val numberToReplace = subNumber.value.toLong()
    return replaceRange(subNumber.range, "[${numberToReplace / 2},${ceil(numberToReplace / 2.0).toLong()}]")
}

private fun String.magnitude(): Long {
    return (0..4).fold(this) { expression, _ ->
        expression.replace(simplePairRegex) { match ->
            (3 * match.groupValues[1].toLong() + 2 * match.groupValues[2].toLong()).toString()
        }
    }.toLong()
}

private val simplePairRegex = "\\[(\\d+),(\\d+)]".toRegex()
private val numberRegex = "\\d+".toRegex()
private val bigNumberRegex = "\\d{2,}".toRegex()
