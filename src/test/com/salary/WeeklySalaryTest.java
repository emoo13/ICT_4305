package java.com.salary;

import org.junit.jupiter.api.Test;

import java.com.salary.WeeklySalary;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeeklySalaryTest {
    @Test
    public void testWeeklySalary()
    {
        WeeklySalary weeklySalary = new WeeklySalary();
        assertEquals(1538.4615384615386, weeklySalary.WeeklySalaryCalculation());
    }
}
