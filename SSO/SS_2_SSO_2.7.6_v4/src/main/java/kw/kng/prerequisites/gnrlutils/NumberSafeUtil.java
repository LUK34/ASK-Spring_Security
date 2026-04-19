package kw.kng.prerequisites.gnrlutils;

public interface NumberSafeUtil
{
	Long defaultIfNull(Long value, Long def);
	Integer defaultIfNull(Integer value, Integer def);
	boolean isPositive(Number num);

}
