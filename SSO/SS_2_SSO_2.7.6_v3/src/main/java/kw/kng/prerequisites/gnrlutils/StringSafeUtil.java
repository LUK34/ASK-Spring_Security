package kw.kng.prerequisites.gnrlutils;

public interface StringSafeUtil 
{

	String safeTrim(String value);
	String normalizeSpaces(String value);
	String defaultIfNull(String value, String defaultVal);
	boolean hasText(String value);
}
