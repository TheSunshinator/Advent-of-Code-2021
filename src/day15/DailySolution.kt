package day15

import Point
import coordinates
import digitToInt
import io.kotest.matchers.shouldBe
import readInput
import get
import java.util.PriorityQueue
import kotlin.math.min

fun main() {
    fun part1(input: List<List<Int>>) = findSafestRoute(input)
    fun part2(input: List<List<Int>>) = findSafestRoute(input.computeBiggerMap())

    val testInput = readInput("15", "test_input").let(::parse)
    val input = readInput("15", "input").let(::parse)

    part1(testInput) shouldBe 40
    println(part1(input))

    part2(testInput) shouldBe 315
    println(part2(input))
}

private fun parse(input: List<String>): List<List<Int>> = input.map { it.map(Char::digitToInt) }

private fun findSafestRoute(map: List<List<Int>>): Int {
    val unvisitedPoints = map.coordinates().toMutableSet()
    val pathCosts = mutableMapOf(Point(0, 0) to 0)
    val priorityQueue = PriorityQueue<Point> { a, b -> pathCosts.getValue(a) - pathCosts.getValue(b) }
        .apply { add(Point(0, 0)) }
    do {
        val currentPoint = priorityQueue.poll()
        unvisitedPoints.remove(currentPoint)
        currentPoint.neighbors()
            .filter { it in unvisitedPoints }
            .onEach {
                pathCosts.compute(it) { _, cost ->
                    if (cost == null) pathCosts.getValue(currentPoint) + map[it]
                    else min(cost, pathCosts.getValue(currentPoint) + map[it])
                }
            }
            .filter { it !in priorityQueue }
            .let(priorityQueue::addAll)
    } while (!pathCosts.containsKey(Point(map.lastIndex, map.last().lastIndex)))
    return pathCosts.getValue(Point(map.lastIndex, map.last().lastIndex))
}

private fun List<List<Int>>.computeBiggerMap() = List(size * 5) { x ->
    val row = this[x % size]
    List(row.size * 5) { y ->
        val risk = row[y % row.size] + x / size + y / row.size
        if (risk <= 9) risk else risk - 9
    }
}
