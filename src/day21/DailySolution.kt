package day21

import arrow.core.identity
import io.kotest.matchers.shouldBe
import java.lang.Integer.min

fun main() {
    fun part1(player1Start: Int, player2Start: Int): Long {
        val initialBoard = GameState(
            GameState.PlayerState(player1Start - 1),
            GameState.PlayerState(player2Start - 1)
        )
        val (finalBoard, rolls) = diceRollSequence().chunked(3) { it.sum() }
            .runningFold(initialBoard) { state, roll -> state.advanceNextPlayerBy(roll) }
            .mapIndexedNotNull { index, gameState ->
                gameState.takeIf { it.winner(1000) != null }?.to(index * 3L)
            }
            .first()

        return rolls * min(finalBoard.player1.score, finalBoard.player2.score)
    }

    fun part2(player1Start: Int, player2Start: Int): Long {
        val initialBoard = GameState(
            GameState.PlayerState(player1Start - 1),
            GameState.PlayerState(player2Start - 1)
        )
        return initialBoard.countWinsForEachUniverses()
            .let { if (it.first > it.second) it.first else it.second }
    }

    part1(4, 8) shouldBe 739785
    println(part1(10, 3))

    part2(4, 8) shouldBe 444356092776315L
    println(part2(10, 3))
}

data class GameState(
    val player1: PlayerState,
    val player2: PlayerState,
    val isLastPlayer1: Boolean = false,
) {
    fun advanceNextPlayerBy(movement: Int) = copy(
        player1 = if (isLastPlayer1) player1 else player1.advancedBy(movement),
        player2 = if (isLastPlayer1) player2.advancedBy(movement) else player2,
        isLastPlayer1 = !isLastPlayer1,
    )

    fun winner(winningScore: Int): PlayerState? {
        return player1.takeIf { it.score >= winningScore }
            ?: player2.takeIf { it.score >= winningScore }
    }

    data class PlayerState(
        val position: Int,
        val score: Int = 0,
    ) {
        fun advancedBy(movement: Int): PlayerState {
            val newPosition = (position + movement) % 10
            return PlayerState(
                position = newPosition,
                score = score + newPosition + 1,
            )
        }
    }
}

private fun diceRollSequence() = generateSequence(1) {
    val nextNumber = (it + 1) % 100
    if (nextNumber == 0) 100 else nextNumber
}

private fun GameState.countWinsForEachUniverses(): Pair<Long, Long> {
    return movementToScoreMultiplier.entries
        .asSequence()
        .map { (roll, universeCount) ->
            val newBoard = advanceNextPlayerBy(roll)
            when (newBoard.winner(21)) {
                null -> {
                    val score = newBoard.countWinsForEachUniverses()
                    Pair(
                        score.first * universeCount,
                        score.second * universeCount,
                    )
                }
                newBoard.player1 -> universeCount to 0L
                else -> 0L to universeCount
            }
        }
        .reduce { totalScore, universeScore ->
            Pair(
                totalScore.first + universeScore.first,
                totalScore.second + universeScore.second,
            )
        }
}

val movementToScoreMultiplier: Map<Int, Long> = sequenceOf(1, 2, 3)
    .flatMap { sequenceOf(it + 1, it + 2, it + 3) }
    .flatMap { sequenceOf(it + 1, it + 2, it + 3) }
    .groupingBy(::identity)
    .fold(0L) { a, _ -> a + 1 }
