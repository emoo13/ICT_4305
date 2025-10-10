package com.month;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonthAppTest {

    @Test
    public void testMonthInt() {
        Month<Integer> integerTest = new Month<>(4);
        assertEquals(4, integerTest.getMonth());
    }

    @Test
    public void testMonthString() {
        Month<String> stringTest = new Month<>("April");
        assertEquals("April", stringTest.getMonth());
    }

    @Test
    void supportsNull_String() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new Month<String>(null));
        assertEquals("Month cannot be null!", ex.getMessage());
    }

}
