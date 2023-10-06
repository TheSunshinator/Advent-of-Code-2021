package day22

import Point3D
import cartesianProduct
import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(triggerSequence: Sequence<Pair<Cube, Boolean>>): Long {
        val limit = Cube(-50..50, -50..50, -50..50)
        val limitedSequence = triggerSequence.filter { (cube, _) -> cube in limit }
        return pointsSequence(
            minX = limitedSequence.minOf { (cube, _) -> cube.xRange.first },
            maxX = limitedSequence.maxOf { (cube, _) -> cube.xRange.last },
            minY = limitedSequence.minOf { (cube, _) -> cube.yRange.first },
            maxY = limitedSequence.maxOf { (cube, _) -> cube.yRange.last },
            minZ = limitedSequence.minOf { (cube, _) -> cube.zRange.first },
            maxZ = limitedSequence.maxOf { (cube, _) -> cube.zRange.last },
        )
            .map { point -> limitedSequence.firstOrNull { (cube, _) -> point in cube }?.second ?: false }
            .fold(0L) { total, isLit -> if (isLit) total + 1 else total}
    }

    fun part2(triggerSequence: Sequence<Pair<Cube, Boolean>>): Long {
        return pointsSequence(
            minX = triggerSequence.minOf { (cube, _) -> cube.xRange.first },
            maxX = triggerSequence.maxOf { (cube, _) -> cube.xRange.last },
            minY = triggerSequence.minOf { (cube, _) -> cube.yRange.first },
            maxY = triggerSequence.maxOf { (cube, _) -> cube.yRange.last },
            minZ = triggerSequence.minOf { (cube, _) -> cube.zRange.first },
            maxZ = triggerSequence.maxOf { (cube, _) -> cube.zRange.last },
        )
            .map { point -> triggerSequence.firstOrNull { (cube, _) -> point in cube }?.second ?: false }
            .fold(0L) { total, isLit -> if (isLit) total + 1 else total}
    }

    val smallTestInput = readInput("22", "small_test_input").parse()
    val testInput = readInput("22", "test_input").parse()
    val input = readInput("22", "input").parse()

    println("Starting")
    part1(smallTestInput) shouldBe 39
    println("Asserted small test")
    part1(testInput) shouldBe 590784
    println("Asserted test")
    println(part1(input))

    part2(testInput) shouldBe 2758514936282235L
    println("Asserted part 2")
    println(part2(input))
}

private val parsingRegex =
    "(off|on) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)".toRegex()

private fun List<String>.parse(): Sequence<Pair<Cube, Boolean>> {
    return reversed()
        .asSequence()
        .mapNotNull(parsingRegex::matchEntire)
        .map(::toCubes)
}

private fun toCubes(matchResult: MatchResult): Pair<Cube, Boolean> {
    val shouldLightUp = matchResult.groupValues[1] == "on"
    return Cube(
        xRange = matchResult.groupValues[2].toInt()..matchResult.groupValues[3].toInt(),
        yRange = matchResult.groupValues[4].toInt()..matchResult.groupValues[5].toInt(),
        zRange = matchResult.groupValues[6].toInt()..matchResult.groupValues[7].toInt(),
    ) to shouldLightUp
}

data class Cube(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange,
) {
    operator fun contains(p: Point3D): Boolean = p.x in xRange && p.y in yRange && p.z in zRange
    operator fun contains(c: Cube): Boolean {
        return c.xRange.minus(xRange).isEmpty()
            && c.yRange.minus(yRange).isEmpty()
            && c.zRange.minus(zRange).isEmpty()
    }
}

fun pointsSequence(minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int): Sequence<Point3D> {
    return (minX..maxX)
        .cartesianProduct(minY..maxY)
        .let { points2d ->
            (minZ..maxZ).asSequence()
                .flatMap { z -> points2d.map { Point3D(it.x, it.y, z) } }
        }
}