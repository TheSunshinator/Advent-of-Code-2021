package day09

import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: List<List<Int>>): Int {
        return input.asSequence().mapIndexed { i, row ->
            row.asSequence().mapIndexed { j, height ->
                PointHeightMap(
                    height,
                    setOfNotNull(
                        input[i].getOrNull(j + 1),
                        input[i].getOrNull(j - 1),
                        input.getOrNull(i + 1)?.get(j),
                        input.getOrNull(i - 1)?.get(j),
                    )
                )
            }
        }
            .flatten()
            .filter(PointHeightMap::isLowPoint)
            .sumOf { it.height + 1 }
    }

    fun part2(input: List<List<Int>>): Int {
        return input.asSequence()
            .flatMapIndexed { index, row -> row.splitToSummits().map { index to it } }
            .fold(emptySequence<Set<Pair<Int, IntRange>>>()) { basins, basinRange: Pair<Int, IntRange> ->
                val (sameBasins, differentBasins) = basins.partition { basin ->
                    basin.any { (i, jRange) ->
                        i == basinRange.first - 1 && jRange.intersect(basinRange.second).isNotEmpty()
                    }
                }
                sequence {
                    yieldAll(differentBasins)
                    yield(sameBasins.fold(mutableSetOf(basinRange), Set<Pair<Int, IntRange>>::union))
                }
            }
            .map { basins -> basins.sumOf { it.second.count() } }
            .sorted()
            .toList()
            .takeLast(3)
            .reduce(Int::times)
    }

    val testInput = readInput("09", "test_input").let(::parse)
    val input = readInput("09", "input").let(::parse)

    part1(testInput) shouldBe 15
    println(part1(input))

    part2(testInput) shouldBe 1134
    println(part2(input))
}

fun parse(input: List<String>) = input.map { line -> line.map { it.code - '0'.code } }

data class PointHeightMap(
    val height: Int,
    val surroundingHeights: Set<Int>,
)

fun PointHeightMap.isLowPoint() = surroundingHeights.none { it <= height }

fun List<Int>.splitToSummits() = sequence {
    val summits = this@splitToSummits.mapIndexedNotNull { index, height -> index.takeIf { height == 9 } }
    if (summits.first() != 0) yield(0 until summits.first())
    summits.asSequence()
        .zipWithNext { previous, next -> if (previous + 1 == next) null else (previous + 1) until next }
        .filterNotNull()
        .let { yieldAll(it) }
    if (summits.last() != lastIndex) yield((summits.last() + 1)..lastIndex)
}
