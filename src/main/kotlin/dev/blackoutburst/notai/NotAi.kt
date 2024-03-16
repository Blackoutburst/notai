package dev.blackoutburst.notai

import dev.blackoutburst.notai.dataclass.Action
import dev.blackoutburst.notai.dataclass.ActionProbability
import dev.blackoutburst.notai.utils.similarity

private const val LEARNING_UTTERANCE_ACTION_MATCH_THRESHOLD = 50.0
private const val LEARNING_UTTERANCE_MATCH_THRESHOLD = 80.0
private const val MATCHING_UTTERANCE_THRESHOLD = 70.0

private val actions = listOf(
    Action("dummy", mutableListOf("a way to reach dummy", "another way of doing so", "and so on...")),
)

object NotAi {
    fun guessIntent(request: String): List<Action> {
        val actionProbabilities = mutableListOf<ActionProbability>()

        actions.forEach { action ->
            var probability = 0.0
            val utterancesProbabilities = mutableMapOf<String, Double>()

            action.utterances.forEach { utterance ->
                val utProb = (similarity(request, utterance))
                utterancesProbabilities[utterance] = utProb
                probability += utProb
            }

            probability /= action.utterances.size
            actionProbabilities.add(ActionProbability(action, probability, utterancesProbabilities))
        }

        printDetails(actionProbabilities)
        learnNewUtterance(actionProbabilities, request)
        return giveAction(actionProbabilities)
    }

    private fun printDetails(actionProbabilities: List<ActionProbability>) {
        actionProbabilities.forEach {
            println("${it.action.intent}: ${it.probability}")
            it.utterancesProbability.forEach { ut ->
                println("- ${ut.key}: ${ut.value}")
            }
        }
    }

    private fun giveAction(actionProbabilities: List<ActionProbability>): List<Action> {
        for (aProb in actionProbabilities) {
            if (aProb.utterancesProbability.filter { it.value == 100.0 }.isNotEmpty())
                return listOf(aProb.action)
        }

        return actionProbabilities
            .filter {
                it.utterancesProbability.filter {
                        ut -> ut.value >= MATCHING_UTTERANCE_THRESHOLD
                }.isNotEmpty()
            }
            .map { it.action }
    }

    private fun learnNewUtterance(actionProbabilities: List<ActionProbability>, newUtterance: String) {
        val candidates = actionProbabilities.filter { it.probability >= LEARNING_UTTERANCE_ACTION_MATCH_THRESHOLD }
        if (candidates.isEmpty()) return

        val filteredCandidates = candidates.filter {
            it.utterancesProbability.filter {
                    ut -> ut.value >= LEARNING_UTTERANCE_MATCH_THRESHOLD && ut.value != 100.0
            }.isNotEmpty()
        }

        if (filteredCandidates.size != 1) return

        val shouldLearn = filteredCandidates[0].utterancesProbability.filter {
                ut -> ut.value >= LEARNING_UTTERANCE_MATCH_THRESHOLD && ut.value != 100.0
        }.isNotEmpty()

        if (!shouldLearn) return

        actions.find {
                originalAction -> originalAction.intent == filteredCandidates[0].action.intent
        }?.utterances?.add(newUtterance)
    }
}