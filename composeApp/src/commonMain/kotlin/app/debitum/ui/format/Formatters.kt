package app.debitum.ui.format

import kotlin.math.absoluteValue

/** Formats cents as BRL, omitting the decimals when they are zero: "R$ 1.200", "R$ 1.450,50". */
fun formatBrl(cents: Long): String {
    val sign = if (cents < 0) "-" else ""
    val abs = cents.absoluteValue
    val centavos = abs % 100
    val decimals = if (centavos == 0L) "" else "," + centavos.toString().padStart(2, '0')
    return "${sign}R$ ${groupThousands(abs / 100)}$decimals"
}

/** Formats cents for an amount input, always with decimals and without the currency: "1.200,00". */
fun formatAmountInput(cents: Long): String =
    groupThousands(cents / 100) + "," + (cents % 100).toString().padStart(2, '0')

private fun groupThousands(value: Long): String =
    value.toString().reversed().chunked(3).joinToString(".").reversed()
