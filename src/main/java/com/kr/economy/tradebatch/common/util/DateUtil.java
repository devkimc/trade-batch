package com.kr.economy.tradebatch.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.kr.economy.tradebatch.common.constants.StaticValues.NON_HYPHEN_DAY_PATTERN;

public class DateUtil {

    public static String toNonHyphenDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new RuntimeException("dateTime 이 null 입니다.");
        }
        return dateTime.format(DateTimeFormatter.ofPattern(NON_HYPHEN_DAY_PATTERN));
    }
}
