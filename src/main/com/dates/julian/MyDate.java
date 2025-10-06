package com.dates.julian;

import java.time.*;

public class MyDate {

    /* This private data member holds the calculated Julian number for this MyDate */
    private final int julianNumber;

    /* If no arguments were provided then default the date January 1st, 1970 (epoch time). */
    public MyDate() {
        this.julianNumber = toJulianNumber(1, 1, 1970);
    }

    /* Creates a new MyDate from an existing MyDate */
    public MyDate(MyDate date){
        if (date == null) {
            throw new NullPointerException("MyDate is null");
        }
        this.julianNumber = date.julianNumber;
    }

    /* Creates a new MyDate from a day, month, and year */
    public MyDate( int day, int month, int year){
        // Validate they are appropriate day/month values
        if (month < 1 || month > 12){
            throw new IllegalArgumentException("Invalid month, please enter a number between 1 and 12.");
        }

        int last = getLastDayOfMonth(month, year);
        if (day < 1 || day > last) {
            throw new IllegalArgumentException(
                    "Invalid day passed for the date: " + month + "/" + day + "/" + year + ". Please input a valid day!");
        }

        this.julianNumber = toJulianNumber(day, month, year);
    }

    /* Returns the day of the month for this MyDate */
    public int getDay(){
        return fromJulianNumber()[0];
    }

    /* Returns the month of the year for this MyDate */
    public int getMonth(){
        return fromJulianNumber()[1];
    }

    /* Returns the year for this MyDate */
    public int getYear(){
        return fromJulianNumber()[2];
    }

    /* Returns true if this MyDate represents a date in a leap year */
    public static boolean isLeapYear( int year ){
        return Year.isLeap(year);
    }

    public static int getLastDayOfMonth( int month, int year ){
        YearMonth monthDays = YearMonth.of(year, month);
        return monthDays.lengthOfMonth();
    }

    /* This internal method returns the calculated Julian number for the provided day, month, year
     * This method is static, as it does not require a MyDate object to perform its computation
     */
    private static int toJulianNumber(int day, int month, int year){
        JulianDateConvertor julian = new JulianDateConvertor(day, month, year);
        return julian.convertDateToJulian();
    }

    /* This internal method returns a 3-integer array
     * containing the day, month, and year of this MyDate
     */
    private int[] fromJulianNumber(){
        JulianDateConvertor julian = new JulianDateConvertor(julianNumber);
        String dateString = julian.convertJulianToDate();
        String[] splitDateString = dateString.split("-");

        // Expected to contain date, month and year, length of 3
        if (splitDateString.length != 3) {
            throw new IllegalStateException("Failed to gather month, day, and year from: " + dateString);
        }

        try {
            return new int[]{
                    Integer.parseInt(splitDateString[0]),
                    Integer.parseInt(splitDateString[1]),
                    Integer.parseInt(splitDateString[2])
            };
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Failed to parse the split array for dates: " + dateString, e);
        }

    }
}