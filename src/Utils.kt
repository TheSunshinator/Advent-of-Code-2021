import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: String, name: String) = File("src/day$day", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

inline fun <T, U, V> Pair<T, U>.mapFirst(transform: (T) -> V) = transform(first) to second
inline fun <T, U, V> Pair<T, U>.mapSecond(transform: (U) -> V) = first to transform(second)

data class Point(val x: Int, val y: Int) {
    fun neighbors(
        includeThis: Boolean = false,
        includeDiagonal: Boolean = false,
    ) = sequence {
        if (includeDiagonal) yield(Point(x - 1, y - 1))
        yield(Point(x, y - 1))
        if (includeDiagonal) yield(Point(x + 1, y - 1))
        yield(Point(x - 1, y))
        if (includeThis) yield(this@Point)
        yield(Point(x + 1, y))
        if (includeDiagonal) yield(Point(x - 1, y + 1))
        yield(Point(x, y + 1))
        if (includeDiagonal) yield(Point(x + 1, y + 1))
    }
}

operator fun <T> List<List<T>>.get(p: Point) = this[p.x][p.y]
operator fun <T> List<MutableList<T>>.set(p: Point, value: T) {
    this[p.x][p.y] = value
}
fun <T> List<List<T>>.getOrElse(p: Point, defaultValue: (Point) -> T) : T {
    return if (p.x in indices && p.y in this[p.x].indices) this[p]
    else defaultValue(p)
}

infix fun Int.iterateTo(other: Int) = if (this <= other) rangeTo(other) else downTo(other)

fun parseIntSequence(input: List<String>) = input.first().splitToSequence(",").map(String::toInt)

fun <T> List<List<T>>.coordinates() = indices.asSequence().flatMap { i -> this[i].indices.map { j -> Point(i, j) } }
infix fun IntRange.cartesianProduct(other: IntRange) = asSequence().flatMap { i -> other.map { j -> Point(i, j) } }
