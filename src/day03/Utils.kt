package day03

fun List<String>.parseBits() = asSequence().map { entry -> entry.toList().map { if (it == '1') 1 else -1 } }

fun List<Int>.toInt() = asReversed().foldIndexed(0) { index, accumulator, entry ->
    if (entry > 0) accumulator inject1At index else accumulator
}
