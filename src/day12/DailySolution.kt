package day12

import io.kotest.matchers.shouldBe
import readInput

fun main() {
    fun part1(input: Sequence<Path>): Long = countPaths(input) { it !in this }

    fun part2(input: Sequence<Path>): Long {
        return countPaths(input) { nodeToAdd ->
            nodeToAdd != "start"
                && (
                   nodeToAdd == "end"
                        || nodeToAdd !in this
                        || none { node -> node[0].isLowerCase() && count { node == it } > 1 }
                )
        }
    }

    val testInput = readInput("12", "test_input").mapToPath()
    val largeTestInput = readInput("12", "large_test_input").mapToPath()
    val input = readInput("12", "input").mapToPath()

    part1(testInput) shouldBe 10
    part1(largeTestInput) shouldBe 226
    println(part1(input))

    part2(testInput) shouldBe 36
    part2(largeTestInput) shouldBe 3509
    println(part2(input))
}

data class Path(private val nodes: Pair<String, String>) {
    fun nodeFrom(node: String) = when (node) {
        nodes.first -> nodes.second
        nodes.second -> nodes.first
        else -> null
    }
}

fun countPaths(input: Sequence<Path>, canAdd: List<String>.(String) -> Boolean): Long {
    return input.computeRoutes(canAdd).count().toLong()
}

fun List<String>.mapToPath() = asSequence().map { it.split('-').run { Path(this[0] to this[1]) } }

fun Sequence<Path>.computeRoutes(canAdd: List<String>.(String) -> Boolean): Set<List<String>> {
    val routes = mutableSetOf(listOf("start"))
    do {
        routes.filter { it.last() != "end" }
            .asSequence()
            .onEach(routes::remove)
            .computeNewRoutes(this, canAdd)
            .let(routes::addAll)
    } while (routes.any { it.last() != "end" })
    return routes
}

fun Sequence<List<String>>.computeNewRoutes(
    paths: Sequence<Path>,
    canAdd: List<String>.(String) -> Boolean
): Sequence<List<String>> = flatMap { route ->
    paths.mapNotNull { it.nodeFrom(route.last()) }
        .filter { it[0].isUpperCase() || route.canAdd(it) }
        .map(route::plus)
}
