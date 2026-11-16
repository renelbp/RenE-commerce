package com.reneprojects.common.constants

/**
 * Canonical category slugs shared by the remote endpoint, the database and the UI.
 *
 * The API sends these values as the category identity; use them instead of free strings.
 */
internal object CategorySlug {
    const val FURNITURE = "furniture"
    const val BEAUTY = "beauty"
    const val FRAGRANCES = "fragrances"
    const val GROCERIES = "groceries"

    /** Synthetic filter that will group every other category in a future iteration. */
    const val OTHERS = "others"
}
