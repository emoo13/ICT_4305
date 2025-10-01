package com.dates.julian;

public class Manager extends Employee {
    public Manager(String name, double salary)
    {
        super(name, salary);
    }
    @Override
    public void raiseSalary(double byPercent)
    {
        super.raiseSalary(byPercent + 1);
    }
}
