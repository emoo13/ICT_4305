package com.month;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthApp {
    public static void main(String[] args) {
        Map<Integer, String> intToMonth = new HashMap<>();
        Map<Integer, Month<Integer>> intToMonthSet = new HashMap<>();

        List<String> monthName = new ArrayList<>(List.of("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"));

        int monthIndex  = 1;
        for (String month : monthName) {
            intToMonth.put(monthIndex , month);
            Month<Integer> monthNumConst  = new Month<>(monthIndex );
            intToMonthSet.put(monthIndex , monthNumConst);

            monthIndex ++;
        }

        // Print out the values
        intToMonthSet.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    int n = e.getKey();
                    Month<Integer> numObj = e.getValue();
                    String name = intToMonth.get(n);
                    System.out.println(numObj.getMonth() + " = " + name);
                });

    }
}
