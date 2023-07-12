package com.deadrudolph.feature_builder.util.regex

object CommonLanguagesRegex {
    val languagesRegexList = listOf("a-zA-Z", "а-яА-Я")
    const val emptySongLineRegex = "а-яА-Яa-zA-Z0-9"
    const val noLetterRegexStart = "(?<![а-яА-Яa-zA-Z0-9#+/])"
    const val noLetterRegexEnd = "(?![а-яА-Яa-zA-Z0-9#+/])"
}