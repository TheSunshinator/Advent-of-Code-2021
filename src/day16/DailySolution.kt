package day16

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.merge
import arrow.core.right
import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: Operation) = sumVersions(input)
    fun part2(input: Operation) = input.value

    val input = readInput("16", "input").first().let(::parse)

    part1(parse("A0016C880162017C3686B18A3D4780")) shouldBe 31
    println(part1(input))

    part2(parse("9C0141080250320F1802104A08")) shouldBe 1
    println(part2(input))
}

private fun parse(input: String): Operation {
    return input.asSequence()
        .flatMap { bit ->
            bit.toString()
                .toInt(16)
                .toString(radix = 2)
                .padStart(4, '0')
                .asSequence()
        }
        .iterator()
        .parseOperations()
}

private fun Iterator<Char>.parseOperations(): Operation {
    val version = take(3).joinToLong()
    val typeId = take(3).joinToLong()
    return Operation(
        version,
        when {
            typeId == 4L -> parseNumber().left()
            next() == '0' -> parseOperator0().right()
            else -> parseOperator1().right()
        },
        operations[typeId.toInt()],
    )
}

private fun Iterator<Char>.parseNumber(): Long {
    val numberBuilder = StringBuilder()
    do {
        val shouldContinue = next() == '1'
        numberBuilder.append(take(4).joinToString(""))
    } while (shouldContinue)
    return numberBuilder.toString().toLong(radix = 2)
}

private fun Iterator<Char>.parseOperator0(): List<Operation> {
    val length = take(15).joinToLong()
    return buildList {
        val entry = this@parseOperator0.take(length).iterator()
        do { add(entry.parseOperations()) } while (entry.hasNext())
    }
}

private fun Iterator<Char>.parseOperator1() = List(take(11).joinToLong().toInt()) { parseOperations() }

data class Operation(
    val version: Long,
    val operands: Either<Long, List<Operation>>,
    val operator: Sequence<Long>.() -> Long,
) {
    val value: Long by lazy {
        operands.map { it.asSequence() }
            .map { operands -> operands.map { it.value } }
            .map(operator)
            .merge()
    }
}

private fun sumVersions(operation: Operation): Long = operation.version + operation.operands.sumVersions()
private fun Either<Long, List<Operation>>.sumVersions() = map(::sumByVersion).getOrElse { 0 }
private fun sumByVersion(operations: List<Operation>): Long = operations.sumOf(::sumVersions)

private fun <T> Iterator<T>.take(n: Long): Sequence<T> = sequence {
    repeat(n.toInt()) { if (hasNext()) yield(next()) }
}

private fun Sequence<Char>.joinToLong() = joinToString("").toLong(2)

val operations: List<Sequence<Long>.() -> Long> = listOf(
    Sequence<Long>::sum,
    { fold(1, Long::times) },
    { minOrNull()!! },
    { maxOrNull()!! },
    Sequence<Long>::first,
    { if (first() > last()) 1L else 0L },
    { if (first() < last()) 1L else 0L },
    { if (first() == last()) 1L else 0L },
)
