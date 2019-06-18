package org.seekerwing.aws.sqsconsumer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CalculatorTest {

    private val calculator: Calculator = Calculator()

    @Test
    fun add() {
        assertEquals(7, calculator.add(5, 2))
        assertEquals(-7, calculator.add(-5, -2))
        assertEquals(7, calculator.add(-5, 12))
        assertEquals(-7, calculator.add(5, -12))
    }

}
