package com.clique.retire.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static String getDataFormatada(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }

    public static String getHoraFormatada(LocalDateTime data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return data.format(formatter);
    }
    
    public static String getDataHoraFormatadaDrogatel(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
    }

    public static LocalDateTime dateParaLocalDateTimeDoSqlTimestamp(Date data) {
        return new java.sql.Timestamp(
                data.getTime()).toLocalDateTime();
    }

    public static String dateParaDataFormatada(Date data){
        return getDataFormatada(dateParaLocalDateTimeDoSqlTimestamp(data));
    }
    
    public static Date em30Minutos() {
    	Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 30);
		date = cal.getTime();
		return date;
    }

}
