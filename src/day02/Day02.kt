package day02

import readInput

typealias Movement = State.() -> State

fun main() {
    fun part1(input: List<String>) = computeEndState(input, ::toMovement).answer
    fun part2(input: List<String>) = computeEndState(input, ::toAimedMovement).answer

    val testInput = readInput("02", "test_input")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("02", "input")
    println(part1(input))
    println(part2(input))
}

data class State(
    val position: Int = 0,
    val depth: Int = 0,
    val aim: Int = 0,
) {
    val answer: Int by lazy { position * depth }
}

fun computeEndState(input: List<String>, computeMovement: (direction: String, quantity: Int) -> Movement): State {
    return input.asSequence()
        .map { it.split(" ") }
        .map { (instruction, quantity) -> computeMovement(instruction, quantity.toInt()) }
        .fold(State()) { state, executeMovement -> state.executeMovement() }
}

fun toMovement(direction: String, quantity: Int): Movement = {
    when (direction) {
        "up" -> copy(depth = depth - quantity)
        "down" -> copy(depth = depth + quantity)
        else -> copy(position = position + quantity)
    }
}

fun toAimedMovement(direction: String, quantity: Int): Movement = {
    when (direction) {
        "up" -> copy(aim = aim - quantity)
        "down" -> copy(aim = aim + quantity)
        else -> copy(
            position = position + quantity,
            depth = depth + aim * quantity,
        )
    }
}
