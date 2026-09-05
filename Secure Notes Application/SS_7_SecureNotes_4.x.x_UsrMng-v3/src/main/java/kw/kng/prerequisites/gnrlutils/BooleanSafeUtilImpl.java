package kw.kng.prerequisites.gnrlutils;

import org.springframework.stereotype.Service;

@Service
public class BooleanSafeUtilImpl implements BooleanSafeUtil 
{

	@Override
	public boolean isTrue(Boolean value)
	{
		System.out.println("General Utils -> boolean isTrue -> START");
		System.out.println("General Utils -> boolean isTrue -> END");
		return Boolean.TRUE.equals(value);
	}

	@Override
	public boolean isFalse(Boolean value) 
	{
		System.out.println("General Utils -> boolean isFalse -> START");
		System.out.println("General Utils -> boolean isFalse -> END");
		return Boolean.FALSE.equals(value);
	}

}
