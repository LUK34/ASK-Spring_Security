package kw.kng.prerequisites.gnrlutils;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SqlSafeUtilImpl implements SqlSafeUtil 
{

	@Override
    public String like(String value)
	{
		System.out.println("General Utils -> String like -> START");
		System.out.println("General Utils -> String like -> END");
        return value != null ? "%" + value.trim() + "%" : null;
    }

	@Override
	public String inClause(List<?> list) 
	{
		System.out.println("General Utils -> String inClause -> START");
		System.out.println("General Utils -> String inClause -> END");
		 return (list != null && !list.isEmpty()) ?  String.join(",", Collections.nCopies(list.size(), "?")) : "";
	}

}
