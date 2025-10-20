package java.com.salary;

public class Employee {
    private final String name;
    private double salary;

    public Employee(String name, double salary)
    {
        this.name = name;
        this.salary = salary;
    }
    public String getName()
    {
        return name;
    }
    public double getSalary()
    {
        return salary;
    }
    public void raiseSalary(double byPercent)
    {
        salary += salary * (byPercent / 100);
    }
}

