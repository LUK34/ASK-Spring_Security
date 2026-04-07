package kw.kng.gnrlutils;

import java.sql.ResultSet;

public interface JdbcUtilService 
{
	String getString(ResultSet rs, String column);
	Long getLong(ResultSet rs, String column);
	Integer getInt(ResultSet rs, String column);

}
