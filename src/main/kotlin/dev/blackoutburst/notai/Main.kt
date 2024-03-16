package dev.blackoutburst.notai

import dev.blackoutburst.notai.utils.sanitizeString
import kotlin.system.exitProcess

fun main() {
    while(true) {
        val line = sanitizeString(readln())
        if (line == "quit") exitProcess(0)

        val actions = NotAi.guessIntent(line)

        when (actions.size) {
            0 -> println("No actions found")
            1 -> println("Action found: ${actions[0].intent}")
            else -> println("Too many actions found: ${actions.joinToString(", ") { it.intent }}")
        }
    }
}