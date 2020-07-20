package com.hyunje.fds;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTest {
    public static void main(String[] args) {
        String bDateStr = "19890626";
        String createDateStr = "20200719120000";

        DateTimeFormatter bDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        LocalDate now = LocalDate.now();

        LocalDate birthDate = LocalDate.parse(bDateStr, bDateFormatter);
        System.out.println(now);
        System.out.println(birthDate);
        long age = ChronoUnit.YEARS.between(birthDate, now);
        System.out.println("age: " + age);

        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime createDate = LocalDateTime.parse(createDateStr, fDateFormatter);
        System.out.println(nowTime);
        System.out.println(createDate);
        System.out.printf("created before %dh \n", ChronoUnit.HOURS.between(createDate, nowTime));

    }
}
