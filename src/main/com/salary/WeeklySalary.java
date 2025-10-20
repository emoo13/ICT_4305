package java.com.salary;

public class WeeklySalary {
    Employee employeeObject;
    public WeeklySalary(){
        this.employeeObject = new Employee("Jane", 80000.0);
    }

    public double WeeklySalaryCalculation() {
        return employeeObject.getSalary()/52.0;
    }
}
