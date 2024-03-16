package dev.blackoutburst.notai.dataclass

data class Action(
    val intent: String,
    val utterances: MutableList<String>,
)