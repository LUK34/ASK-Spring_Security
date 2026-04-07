package kw.kng.gnrlutils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class DateUtilServiceImpl implements DateUtilService 
{

	@Override
	public LocalDate toLocalDate(ResultSet rs, String column)
	{
		System.out.println("General Utils -> toLocalDate -> START");
		try 
		{
	        Date date = rs.getDate(column);
	        System.out.println("General Utils -> toLocalDate -> END");
	        return date != null ? date.toLocalDate() : null;
	        
	    } catch (SQLException e) 
		{
	    	System.out.println("General Utils -> toLocalDate -> END");
	    	throw new RuntimeException("Error reading DATE column: " + column, e);
	    }
		
	}

	@Override
	public LocalDateTime toLocalDateTime(ResultSet rs, String column) 
	{
		System.out.println("General Utils -> toLocalDateTime -> START");
		   try 
		   {
	            Timestamp ts = rs.getTimestamp(column);
	            System.out.println("General Utils -> toLocalDateTime -> END");
	            return ts != null ? ts.toLocalDateTime() : null;
	        } 
		   catch (SQLException e) 
		   {
			   	System.out.println("General Utils -> toLocalDateTime -> END");
	            throw new RuntimeException("Error reading TIMESTAMP column: " + column, e);
	        }
	}

	@Override
	public String format(LocalDate date, String pattern) 
	{
		System.out.println("General Utils -> format -> START");
		  return date != null ? date.format(DateTimeFormatter.ofPattern(pattern)): null;
		 
	}

	@Override
	public boolean isBeforeToday(LocalDate date) 
	{
		return date != null && date.isBefore(LocalDate.now());
	}

	@Override
	public boolean isAfterToday(LocalDate date) 
	{
		 return date != null && date.isAfter(LocalDate.now());
	}
	

}
