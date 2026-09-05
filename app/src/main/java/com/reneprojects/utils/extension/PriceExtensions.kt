package com.reneprojects.utils.extension

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats a [Double] as a price string using the device's default locale
 * and its default currency symbol.
 */
fun Number.toPriceString(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return formatter.format(this.toDouble())
}
