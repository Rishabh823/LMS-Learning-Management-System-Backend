package com.cipherinfratech.lms.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    /**
     *
     * @param date to compare by today.
     * @return
     */
    public static Boolean CompareDateWithToday(Date date){

        Date today;
        LocalDate now = LocalDate.now();
        Instant instant = Instant.from(now.atStartOfDay(ZoneId.of("GMT")));
        today = Date.from(instant);

        System.out.println("today "+today);
        System.out.println("date "+date);

        if(today.after(date)){
            return true;
        }else return false;
    }

    /**
     *
     * @param date - date to compare with today
     * @param ch - compare parameter A=After, B=Before, E=Equal
     * @return boolean - true/false
     */
    public static Boolean CompareDateWithToday(Date date, char ch) {
//        Date today;
//        LocalDate now = LocalDate.now();
//        Instant instant = Instant.from(now.atStartOfDay(ZoneId.of("GMT")));
//        today = Date.from(instant);

        final Date currentTime = new Date();
        final SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy  hh:mm:ss a");
        // Give it to me in GMT time.
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println("GMT currentTime: " + sdf.format(currentTime));


        System.out.println("today " + currentTime);
        System.out.println("date " + date);

        if (ch == 'A' && currentTime.after(date)) {
            return true;
        } else if (ch == 'B' && currentTime.before(date)) {
            return true;
        } else return ch == 'E' && currentTime.equals(date);
    }
    /**
     *
     * @param date1 - Date to compare
     * @param date2 - Date compare with
     * @param ch - compare parameter A=After, B=Before, E=Equal, M= Middle/Between
     * @return boolean - true/false
     */
    public static Boolean CompareTwoDate(Date date1,Date date2, char ch) {
        if (ch == 'A' && date1.after(date2)) {
            return true;
        } else if (ch == 'B' && date1.before(date2)) {
            return true;
        }else if (ch == 'M' && date1.before(date2)) {
            return true;
        } else return ch == 'E' && date1.equals(date2);
    }

    public static Date getToday(){
        final Date currentTime = new Date();
        final SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy  hh:mm:ss a");
        // Give it to me in GMT time.
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        //System.out.println("GMT currentTime: " + sdf.format(currentTime));


        System.out.println("today " + currentTime);
        return currentTime;
    }
    public static String dateToken() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
         System.out.println(dtf.format(now));
        return dtf.format(now).replaceAll("-","").replaceAll(":","");
    }
}


