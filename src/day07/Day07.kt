package day07

import arrow.core.foldLeft
import arrow.core.identity
import io.kotest.matchers.shouldBe
import parseIntSequence
import readInput

fun main() {
    fun part1(input: List<String>) = computeMinimumFuel(input) { movingPosition, destinationPosition ->
        PositionState(
            crabCount = movingPosition.crabCount + destinationPosition.crabCount,
            fuelCost = movingPosition.fuelCost + destinationPosition.fuelCost
        )
    }

    fun part2(input: List<String>) = computeMinimumFuel(input) { movingPosition, destinationPosition ->
        PositionState(
            crabCount = movingPosition.crabCount + destinationPosition.crabCount,
            fuelCost = movingPosition.crabCount + movingPosition.fuelCost + destinationPosition.fuelCost
        )
    }

    val testInput = readInput("07", "test_input")
    val input = readInput("07", "input")

    part1(testInput) shouldBe 37
    println(part1(input))

    part2(testInput) shouldBe 168
    println(part2(input))
}

fun computeMinimumFuel(input: List<String>, movementStrategy: (PositionState, PositionState) -> PositionState): Int {
    return parseIntSequence(input)
        .groupingBy(::identity)
        .eachCount()
        .createPositionCountList()
        .convergeCrabs(0, movementStrategy)
}

fun Map<Int, Int>.createPositionCountList() = this.foldLeft(mutableListOf<PositionState>()) { list, (position, count) ->
    list.apply {
        repeat(position - size + 1) { add(PositionState()) }
        set(position, PositionState(count, fuelCost = count))
    }
}

tailrec fun List<PositionState>.convergeCrabs(
    totalFuelCost: Int,
    movementStrategy: (PositionState, PositionState) -> PositionState,
): Int = when {
    size == 1 -> totalFuelCost
    first().fuelCost <= last().fuelCost -> {
        mutableListOf(movementStrategy(this[0], this[1]))
            .apply { addAll(this@convergeCrabs.asSequence().drop(2)) }
            .convergeCrabs(totalFuelCost + first().fuelCost, movementStrategy)
    }
    else -> {
        mutableListOf(movementStrategy(last(), this[lastIndex - 1]))
            .apply { addAll(0, this@convergeCrabs.dropLast(2)) }
            .convergeCrabs(totalFuelCost + last().fuelCost, movementStrategy)
    }
}

data class PositionState(val crabCount: Int = 0, val fuelCost: Int = 0)
