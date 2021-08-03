package com.idrisov.mycode

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Created by Tagir Idrisov on 03.08.2021
 */

class Extensions {

    /** для добавления красной точки (знак обязательного поля) к хинту едиттекста
    * написал экстеншен, потому что нужно отображать и скрывать его в зависимотри от оценки приложения
     */
    fun EditText.setRedPointToEndHint() {

        val point = " •"
        val start = hint.length
        val textWithPoint = "$hint$point"
        val end = textWithPoint.length

        val span = SpannableString(textWithPoint)
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        hint = span
    }


    /**
     * Возвращает нужную строку для фона
     * в зависимости от типа измерений (целое/дробь)
     */
    fun TextView.setShadowsForMeasurements(value: Double, format: ThicknessUnit? = ThicknessUnit.MKM) =

    // ThicknessUnit.MKM   - выбран формат с целыми числами
    // ThicknessUnit.MILS  - выбран формат с дробными числами

        when(format) {
            ThicknessUnit.MKM -> "8888"
            ThicknessUnit.MILS -> {
                if (value != 0.0) {
                    when (countDigits(value.toString())) {
                        1 -> "8"
                        2 -> "8.8"
                        3 -> "88.8"
                        4 -> "888.8"
                        else -> "8888"
                    }
                } else "8.8"
            }
            else -> "8888"
        }

    /**
     * Подставляет нули перед измерением, если количество символов в измерении меньше 4
     */
    fun TextView.setZerosBeforeMeasurement(measurement: Int?): String {

        val lengthMeasurement = measurement?.toString() ?: "0"
        val stringBuilder = StringBuilder()
        val measurementWithZeros = arrayListOf<Char>()

        for (i in 1..4 - lengthMeasurement.length) {
            measurementWithZeros.add(0, '0')
        }

        stringBuilder.append(
            measurementWithZeros.joinToString(
                separator = "",
                prefix = "",
                postfix = ""
            )
        )

        return stringBuilder.append(measurement).toString()
    }
}