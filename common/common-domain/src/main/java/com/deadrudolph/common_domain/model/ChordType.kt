package com.deadrudolph.common_domain.model

enum class ChordType(val marker: String, val scheme: List<Int>) {
    EM("Em", listOf(0, 0, 0, 2, 2, 0)),
    DM("Dm", listOf(1, 3, 2, 0, 0, 0)),
    AM("Am", listOf(0, 1, 2, 2, 0, 0)),
    E("E", listOf(0, 0, 1, 2, 2, 0)),
    A("A", listOf(0, 2, 2, 2, 0, 0)),
    A_PLUS("A+", listOf(1, 2, 2, 3, 0, 0)),
    A_MAJOR("A#", listOf(1, 3, 3, 3, 1, 1)),
    AMAJ7("Amaj7", listOf(0, 2, 1, 2, 0, 0)),
    ASUS4("Asus4", listOf(0, 3, 2, 2, 0, 0)),
    A6("A6", listOf(2, 2, 2, 2, 0, 0)),
    AM6("Am6", listOf(2, 1, 2, 2, 0, 0)),
    A7("A7", listOf(0, 2, 0, 2, 0, 0)),
    AM7("Am7", listOf(0, 1, 0, 2, 0, 0)),
    A_MAJOR_7("A#7", listOf(1, 3, 1, 3, 1, 1)),
    ADIM7("Adim7", listOf(8, 7, 8, 7, 0, 0)),
    A7SUS4("A7sus4", listOf(0, 3, 0, 2, 0, 0)),
    A76("A7/6", listOf(2, 2, 0, 2, 0, 0)),
    A9("A9", listOf(0, 0, 2, 2, 2, 0)),
    AM9("Am9", listOf(0, 1, 2, 2, 2, 0))
}
