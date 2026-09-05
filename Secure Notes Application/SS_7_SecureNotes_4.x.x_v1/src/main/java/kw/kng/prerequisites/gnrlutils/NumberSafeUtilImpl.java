package kw.kng.prerequisites.gnrlutils;

import org.springframework.stereotype.Service;

@Service
public class NumberSafeUtilImpl implements NumberSafeUtil
{

	@Override
	public Long defaultIfNull(Long value, Long def) 
	{
		System.out.println("General Utils -> Long defaultIfNull -> START");
		System.out.println("General Utils -> Long defaultIfNull -> END");
		return value != null ? value : def;
	}

	@Override
	public Integer defaultIfNull(Integer value, Integer def) {
		
		System.out.println("General Utils -> Integer defaultIfNull -> START");
		System.out.println("General Utils -> Integer defaultIfNull -> END");
		return value != null ? value : def;
	}

	@Override
	public boolean isPositive(Number num) 
	{
		System.out.println("General Utils -> boolean isPositive -> START");
		System.out.println("General Utils -> boolean isPositive -> END");
		return num != null && num.longValue() > 0;
	}

}
