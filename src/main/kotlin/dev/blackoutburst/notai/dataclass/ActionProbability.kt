package dev.blackoutburst.notai.dataclass

data class ActionProbability(
    val action: Action,
    val probability: Double,
    val utterancesProbability: Map<String, Double>,
)
