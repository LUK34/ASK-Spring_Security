package kw.kng.gnrlutils;

import java.util.List;

public interface SqlSafeUtil 
{
	String like(String value);
	String inClause(List<?> list);

}
