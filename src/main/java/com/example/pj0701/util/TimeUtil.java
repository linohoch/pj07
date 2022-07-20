package com.example.pj0701.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    //dto만들 때 참고
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDatetime;

    public static String format(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        return sdf.format(date);
    }
    public static String convertToDate(String timestampStr){
        long timestamp = Long.parseLong(timestampStr);
        Date date = new Date(timestamp*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        return sdf.format(date);
    }
    public static boolean isBeforeNow(Object timestampObj){
        return new Date((Long)timestampObj *1000L).before(new Date());
    }
    //
    public static Timestamp convertToTimestamp(Date date){
        return new Timestamp(date.getTime());
    }
    public static String convertToTimestampStr(Date date){
        return new Timestamp(date.getTime()).toString();
    }
}
