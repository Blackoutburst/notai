package dev.blackoutburst.notai.utils

import java.text.Normalizer
import java.util.*

fun sanitizeString(input: String): String {
    val normalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
    val withoutDiacritics = normalizedString.replace("\\p{Mn}+".toRegex(), "")

    return withoutDiacritics.lowercase(Locale.getDefault())
}

fun similarity(string1: String, string2: String): Double {
    val maxLength = maxOf(string1.length, string2.length)

    return if (maxLength > 0) {
        val distance = levenshtein(string1, string2)
        (1.0 - distance.toDouble() / maxLength) * 100.0
    } else 100.0
}

fun levenshtein(string1: String, string2: String): Int {
    if (string1 == string2) return 0
    if (string1.isBlank()) return string2.length
    if (string2.isBlank()) return string1.length

    val len1 = string1.length + 1
    val len2 = string2.length + 1

    var prev = IntArray(len1)
    val curr = IntArray(len1)

    for (i in 0 until len1)
        prev[i] = i

    for (j in 1 until len2) {
        curr[0] = j

        for (i in 1 until len1) {
            val cost = if (string1[i - 1] == string2[j - 1]) 0 else 1
            curr[i] = minOf(
                minOf(curr[i - 1] + 1, prev[i] + 1),
                prev[i - 1] + cost
            )
        }

        prev = curr.copyOf()
    }

    return curr[string1.length]
}