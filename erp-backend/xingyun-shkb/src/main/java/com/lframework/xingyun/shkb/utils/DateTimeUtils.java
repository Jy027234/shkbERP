package com.lframework.xingyun.shkb.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 日期时间工具类
 *
 * @author kison
 */
public class DateTimeUtils {

    /**
     * 将 LocalDate 转换为 LocalDateTime，使用当天的开始时间（00:00:00）
     *
     * @param date LocalDate 对象
     * @return LocalDateTime 对象，如果输入为 null，则返回 null
     */
    public static LocalDateTime toLocalDateTime(LocalDate date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.of(date, LocalTime.MIN);
    }
}
