package day10

import arrow.core.*
import io.kotest.matchers.shouldBe
import readInput
import java.util.*
import kotlin.math.ceil

fun main() {
    fun part1(input: Sequence<Either<InvalidLine, IncompleteLine>>): Long {
        return input
            .mapNotNull { result -> result.fold(ifRight = { null }, ifLeft = { it.illegalCharacter }) }
            .sumOf { illegalCharacter ->
                when (illegalCharacter) {
                    ')' -> 3L
                    ']' -> 57L
                    '}' -> 1197L
                    else -> 25137L
                }
            }
    }

    fun part2(input: Sequence<Either<InvalidLine, IncompleteLine>>): Long {
        return input
            .mapNotNull { it.getOrElse { null }?.stack }
            .map { stack ->
                stack.fold(0L) { total, char ->
                    5 * total + when (char) {
                        '(' -> 1
                        '[' -> 2
                        '{' -> 3
                        else -> 4
                    }
                }
            }
            .sorted()
            .toList()
            .let { it[it.size / 2] }
    }

    val testInput = readInput("10", "test_input").asSequence().map(::toError)
    val input = readInput("10", "input").asSequence().map(::toError)

    part1(testInput) shouldBe 26397
    println(part1(input))

    part2(testInput) shouldBe 288957
    println(part2(input))
}

fun toError(line: String): Either<InvalidLine, IncompleteLine> {
    val result: Either<InvalidLine, ArrayDeque<Char>> = ArrayDeque<Char>().right()
    return line.fold(result) { state, char ->
        state.flatMap { stack ->
            when (char) {
                in openingChars -> stack.apply { push(char) }.right()
                ')' -> stack.rightIfLastCharIs('(', char)
                ']' -> stack.rightIfLastCharIs('[', char)
                '}' -> stack.rightIfLastCharIs('{', char)
                '>' -> stack.rightIfLastCharIs('<', char)
                else -> InvalidLine(char).left()
            }
        }
    }.map(::IncompleteLine)
}

data class IncompleteLine(val stack: ArrayDeque<Char>)
data class InvalidLine(val illegalCharacter: Char)

val openingChars = setOf('(', '[', '{', '<')

fun ArrayDeque<Char>.rightIfLastCharIs(expectedChar: Char, char: Char): Either<InvalidLine, ArrayDeque<Char>> {
    return if (pop() == expectedChar) right() else InvalidLine(char).left()
}
