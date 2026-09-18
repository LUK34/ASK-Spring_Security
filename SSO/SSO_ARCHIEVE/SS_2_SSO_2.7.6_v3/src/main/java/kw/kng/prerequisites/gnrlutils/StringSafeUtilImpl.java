package kw.kng.prerequisites.gnrlutils;

import org.springframework.stereotype.Service;

@Service
public class StringSafeUtilImpl implements StringSafeUtil 
{

	@Override
	public String safeTrim(String value) 
	{
		System.out.println("General Utils -> String safeTrim -> START");
		System.out.println("General Utils -> String safeTrim -> END");
		return value != null ? value.trim() : null;
	}

	@Override
	public String normalizeSpaces(String value)
	{
		System.out.println("General Utils -> String normalizeSpaces -> START");
		System.out.println("General Utils -> String normalizeSpaces-> END");
		return value != null ? value.replaceAll("\\s+", " ").trim() : null;
	}

	@Override
	public String defaultIfNull(String value, String defaultVal) 
	{
		System.out.println("General Utils -> String defaultIfNull -> START");
		System.out.println("General Utils -> String defaultIfNull-> END");
		return value != null ? value : defaultVal;
	}

	@Override
	public boolean hasText(String value) 
	{
		System.out.println("General Utils -> boolean hasText -> START");
		System.out.println("General Utils -> boolean hasText -> END");
		return value != null && !value.trim().isEmpty();
	}
	

}
