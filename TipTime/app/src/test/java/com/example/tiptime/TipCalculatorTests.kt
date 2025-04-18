package com.example.tiptime

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.text.NumberFormat

class TipCalculatorTests {
    @Test
    fun calculate_20_percent_tip_no_roundup(){
        val amount = 10.00
        val tipPercent = 20.00
        val expectedTip = NumberFormat.getCurrencyInstance().format(2)
        val actualTip = calculateTip(false, amount = amount, tipPercentage = tipPercent)
        assertEquals(expectedTip, actualTip)
    }
}