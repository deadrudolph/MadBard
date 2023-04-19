package com.deadrudolph.uicomponents.view.textfield.core.locale


internal class AndroidLocaleDelegate : PlatformLocaleDelegate {

    override val current: LocaleList
        get() = LocaleList(listOf(Locale(AndroidLocale(java.util.Locale.getDefault()))))

    override fun parseLanguageTag(languageTag: String): PlatformLocale =
        AndroidLocale(java.util.Locale.forLanguageTag(languageTag))
}