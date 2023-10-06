package day19

import arrow.core.identity
import io.kotest.matchers.shouldBe
import readInput

typealias Point3D = Triple<Int, Int, Int>

fun main() {
    fun part1(input: List<Set<Point3D>>) = input.size
    fun part2(input: List<Set<Point3D>>) = input.size

    val testInput = readInput("19", "test_input").let(::parse)
    val input = readInput("19", "input").let(::parse)

    part1(testInput) shouldBe 79
    println(part1(input))

    part2(testInput) shouldBe 3351
    println(part2(input))
}

private fun parse(input: List<String>): List<Set<Point3D>> {
    return input.asSequence()
        .filter { it.isNotEmpty() }
        .fold(mutableListOf<MutableSet<Triple<Int, Int, Int>>>()) { scanners, line ->
            if (line.startsWith("---")) scanners.add(mutableSetOf())
            else scanners.last().add(
                line.split(',').let { Triple(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            )
            scanners
        }
}

private fun Set<Point3D>.eachOrientation(): Sequence<Set<Point3D>> {
    TODO()
}

val orientationTransformations: Sequence<(Point3D) -> Point3D> = sequenceOf(
    ::identity,
    TODO()
)
