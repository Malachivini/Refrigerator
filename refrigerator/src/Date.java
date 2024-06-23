// Class representing a Date with day, month, and year fields
public class Date {
    private Integer day;
    private Integer month;
    private Integer year;

    // Constructor to initialize a Date object with day, month, and year
    public Date(Integer day, Integer month, Integer year) {
        if (isValidDate(day, month, year)) { // Validate the date before setting fields
            this.day = day;
            this.month = month;
            this.year = year;
        } else {
            throw new IllegalArgumentException("Invalid date"); // Throw exception for invalid date
        }
    }

    // Getters and setters for day, month, and year
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        if (isValidDate(day, this.month, this.year)) { // Validate the new day before setting
            this.day = day;
        } else {
            throw new IllegalArgumentException("Invalid day"); /// Throw exception for invalid day
        }
    }

    public Integer getMonth() { 
        return month;
    }

    public void setMonth(Integer month) {
        if (isValidDate(this.day, month, this.year)) { // Validate the new month before setting
            this.month = month;
        } else {
            throw new IllegalArgumentException("Invalid month"); // Throw exception for invalid month
        }
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        if (isValidDate(this.day, this.month, year)) { // Validate the new year before setting
            this.year = year;
        } else {
            throw new IllegalArgumentException("Invalid year"); // Throw exception for invalid year
        }
    }

    // Utility method to validate a date
    public static boolean isValidDate(Integer day, Integer month, Integer year) {
        if (year < 0 || month < 1 || month > 12) { // Check for valid month and year
            return false;
        }

        int[] daysInMonth = {31, (isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; // Days in each month
        return day > 0 && day <= daysInMonth[month - 1]; // Check for valid day
    }

    // Utility method to check if a year is a leap year
    public static boolean isLeapYear(Integer year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0); // Leap year logic
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year); // Return date as a formatted string
    }

    // Method to compare if two Date objects are equal
    public boolean equals(Date other) {
        return this.day.equals(other.day) && this.month.equals(other.month) && this.year.equals(other.year);
    }

    // Method to check if this date is before another date by a specified number of days
    public boolean isBefore(Date other, Integer daysBefore) {
        // Subtract daysBefore from this date
        Integer adjustedDay = this.day - daysBefore;
        Integer adjustedMonth = this.month;
        Integer adjustedYear = this.year;

        while (adjustedDay <= 0) { // Adjust day, month, and year if necessary
            adjustedMonth--;
            if (adjustedMonth <= 0) {
                adjustedMonth = 12;
                adjustedYear--;
            }
            Integer[] daysInMonth = {31, (isLeapYear(adjustedYear) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            adjustedDay += daysInMonth[adjustedMonth - 1];
        }

        // Compare adjusted date with the other date
        if (!adjustedYear.equals(other.year)) {
            return adjustedYear < other.year;
        } else if (!adjustedMonth.equals(other.month)) {
            return adjustedMonth < other.month;
        } else {
            return adjustedDay < other.day;
        }
    }

    // Method to get the next day
    public Date getNextDay() {
        int[] daysInMonth = {31, (isLeapYear(this.year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int nextDay = this.day + 1;
        int nextMonth = this.month;
        int nextYear = this.year;

        if (nextDay > daysInMonth[this.month - 1]) {
            nextDay = 1;
            nextMonth++;
            if (nextMonth > 12) {
                nextMonth = 1;
                nextYear++;
            }
        }

        return new Date(nextDay, nextMonth, nextYear);
    }
}
