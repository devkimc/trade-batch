package com.kr.economy.tradebatch.common.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static com.kr.economy.tradebatch.common.constants.StaticValues.NON_HYPHEN_DAY_PATTERN;

public class DateUtil {

    public static String toNonHyphenDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new RuntimeException("dateTime 이 null 입니다.");
        }
        return dateTime.format(DateTimeFormatter.ofPattern(NON_HYPHEN_DAY_PATTERN));
    }

    /**
     * 오늘 기준 시, 분, 초로 LocalDateTime 자료형 반환
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static LocalDateTime getTodayLocalDateTime(int hour, int minute, int second) {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        Month month = now.getMonth();
        int dayOfMonth = now.getDayOfMonth();

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
    }
}
