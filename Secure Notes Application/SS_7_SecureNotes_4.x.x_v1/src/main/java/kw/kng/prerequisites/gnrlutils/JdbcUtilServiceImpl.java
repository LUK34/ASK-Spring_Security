package kw.kng.prerequisites.gnrlutils;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Service;

@Service
public class JdbcUtilServiceImpl implements JdbcUtilService 
{

	@Override
	public String getString(ResultSet rs, String column) 
	{	
		System.out.println("General Utils -> getString -> START");
	    try 
	    {
            String val = rs.getString(column);
            System.out.println("General Utils -> getString -> END");
            return val != null ? val.trim() : null;
        } 
	    catch (SQLException e) 
	    {
	    	System.out.println("General Utils -> getString -> END");
            throw new RuntimeException("Error reading String column: " + column, e);
        }
	}

	@Override
	public Long getLong(ResultSet rs, String column) 
	{
		System.out.println("General Utils -> getLong -> START");
		   try 
		   {
	            long val = rs.getLong(column);
	            System.out.println("General Utils -> getLong -> END");
	            return rs.wasNull() ? null : val;
	        } 
		   catch (SQLException e) 
		   {
	        	 System.out.println("General Utils -> getLong -> END");
	            throw new RuntimeException("Error reading Long column: " + column, e);
	        }
	}

	@Override
	public Integer getInt(ResultSet rs, String column) 
	{
		System.out.println("General Utils -> getInt -> START");
		 try 
		 {
	            int val = rs.getInt(column);
	            System.out.println("General Utils -> getInt -> END");
	            return rs.wasNull() ? null : val;
	     } catch (SQLException e) 
		 {
	    	 	System.out.println("General Utils -> getInt -> END");
	            throw new RuntimeException("Error reading Integer column: " + column, e);
	     }
	}
	

}
