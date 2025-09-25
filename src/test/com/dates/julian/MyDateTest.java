package com.dates.julian;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test class for testing MyDate.java class.
 * @author Group B
 *
 */
class MyDateTest {

    @Test
    void testMyDateGettersDefault() {
        MyDate myDate = new MyDate();
        int year = myDate.getYear();
        int month = myDate.getMonth();
        int day = myDate.getDay();
        assertEquals(1970, year);
        assertEquals(1, month);
        assertEquals(1, day);
    }

    @Test
    void testMyDateGettersSet() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        MyDate myDate = new MyDate(day, month, year);
        assertEquals(day, myDate.getDay());
        assertEquals(month, myDate.getMonth());
        assertEquals(year, myDate.getYear());
    }

    @Test
    void testMyDateLastDay() {
        assertEquals(31, MyDate.getLastDayOfMonth(10,2025));
    }

    @Test
    void testMyDateLeapYear() {
        assertFalse(MyDate.isLeapYear(2025));
    }

    @Test void testInvalidDayMonthRel() {
        assertThrows(IllegalArgumentException.class, () -> new MyDate(31, 9, 2025));
    }

    @Test void testInvalidMonth() {
        assertThrows(IllegalArgumentException.class, () -> new MyDate(31, 13, 2025));
    }

    @Test void testInvalidNullDate() {
        assertThrows(NullPointerException.class, () -> new MyDate(null));
    }
}
