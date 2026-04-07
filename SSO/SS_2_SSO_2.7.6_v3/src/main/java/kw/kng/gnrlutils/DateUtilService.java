package kw.kng.gnrlutils;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DateUtilService 
{
	LocalDate toLocalDate(ResultSet rs, String column);
	LocalDateTime toLocalDateTime(ResultSet rs, String column);
	String format(LocalDate date, String pattern);
	boolean isBeforeToday(LocalDate date);
	boolean isAfterToday(LocalDate date);

}
