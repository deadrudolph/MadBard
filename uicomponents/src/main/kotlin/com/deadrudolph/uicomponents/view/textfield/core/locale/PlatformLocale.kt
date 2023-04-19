package com.deadrudolph.uicomponents.view.textfield.core.locale

internal interface PlatformLocale {

    /**
     * Implementation must give ISO 639 compliant language code.
     */
    val language: String

    /**
     * Implementation must give ISO 15924 compliant 4-letter script code.
     */
    val script: String

    /**
     * Implementation must give ISO 3166 compliant region code.
     */
    val region: String

    /**
     * Implementation must return IETF BCP47 compliant language tag representation of this Locale.
     */
    fun toLanguageTag(): String
}

/**
 * Interface for providing platform dependent locale non-instance helper functions.
 *
 */
internal interface PlatformLocaleDelegate {
    /**
     * Returns the list of current locales.
     *
     * The implementation must return at least one locale.
     */
    val current: LocaleList

    /**
     * Parse the IETF BCP47 compliant language tag.
     *
     * @return The locale
     */
    fun parseLanguageTag(languageTag: String): PlatformLocale
}

internal fun createPlatformLocaleDelegate(): PlatformLocaleDelegate = AndroidLocaleDelegate()

internal val platformLocaleDelegate = createPlatformLocaleDelegate()

internal class AndroidLocale(val javaLocale: java.util.Locale) : PlatformLocale {
    override val language: String
        get() = javaLocale.language

    override val script: String
        get() = javaLocale.script

    override val region: String
        get() = javaLocale.country

    override fun toLanguageTag(): String = javaLocale.toLanguageTag()
}
