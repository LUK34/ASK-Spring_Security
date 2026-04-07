package kw.kng.gnrlutils;

public interface NumberSafeUtil
{
	Long defaultIfNull(Long value, Long def);
	Integer defaultIfNull(Integer value, Integer def);
	boolean isPositive(Number num);

}
