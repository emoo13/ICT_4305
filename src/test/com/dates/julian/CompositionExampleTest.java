package com.dates.julian;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompositionExampleTest {

    @Test
    public void TestCompositionCall() {
        Employee employeeClass = new Employee("FakeName", 100000.0);
        employeeClass.raiseSalary(50.0);
        assertEquals(150000.0, employeeClass.getSalary());
    }

    @Test
    public void TestCompositionCallConcatenation() {
        Employee employeeClass = new Employee("FakeName", 85000.0);
        String employeeCat = employeeClass.getName() + " FakeLast";
        assertEquals("FakeName FakeLast", employeeCat);
    }

}
