package com.parkinglot.enums;

import com.parkinglot.utils.DateRange;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

public enum PermitDurations {
    Q1(Month.AUGUST, Month.OCTOBER),
    Q2(Month.NOVEMBER, Month.DECEMBER),
    Q3(Month.JANUARY, Month.MARCH),
    Q4(Month.MARCH, Month.MAY),
    H1(Month.AUGUST, Month.DECEMBER),
    H2(Month.JANUARY, Month.MAY),
    YEARLY(Month.AUGUST, Month.MAY);

    private final Month startMonth;
    private final Month endMonth;

    PermitDurations(Month startMonth, Month endMonth) {
        this.startMonth = startMonth;
        this.endMonth   = endMonth;
    }

    public DateRange rangeForYear(int year) {
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate end = YearMonth.of(year, endMonth).atEndOfMonth();
        return new DateRange(start, end);
    }

    public DateRange nextOccurrence(LocalDate today) {
        DateRange current = rangeForYear(today.getYear());
        if (!today.isAfter(current.end()) && !today.isAfter(current.start())) {
            return current;
        }
        return rangeForYear(today.getYear() + 1);
    }

    public DateRange nextOccurrenceStartingNow(LocalDate today) {
        DateRange r = rangeForYear(today.getYear());
        if (today.isBefore(r.start())) return r;
        if (!today.isAfter(r.end())) return new DateRange(today, r.end());
        return rangeForYear(today.getYear() + 1);
    }
}
