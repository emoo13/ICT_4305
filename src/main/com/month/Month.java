package com.month;

public class Month<T> {
    private T month;

    public Month(T month) {
        setMonth(month);
    }

    // getter and setter
    public void setMonth(T month) {
        this.month = month;
    }
    public T getMonth() {
        return month;
    }
};
