package io.selfproject.ticketservice.utils;

import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateFormatter {

    public static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    public static String shortDate(String date) {
        return date.split(" ")[0];
    }

    public static String today(String formatter) {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        var dateFormat = new SimpleDateFormat(formatter);
        return dateFormat.format(calendar.getTime());
    }

    public static String formattedDate() {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        var dayNumberSuffix = getDayNumberSuffix(calendar.get(Calendar.DAY_OF_MONTH));
        var dateFormat = new SimpleDateFormat("MMMM d'" + dayNumberSuffix + ", ' yyyy - hh:mm:ss a");
        return dateFormat.format(calendar.getTime());
    }

    private static String getDayNumberSuffix(int day) {
        if(day >= 11 && day <= 13){
            return "th";
        }
        return switch (day % 10 ){
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

}
