package kw.kng.gnrlutils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CollectionSafeUtilImpl implements CollectionSafeUtil 
{
	public boolean isEmpty(Collection<?> col)
	{
		System.out.println("General Utils -> boolean isEmpty -> START");
		System.out.println("General Utils -> boolean isEmpty -> END");
        return col == null || col.isEmpty();
    }

    public <T> List<T> safeList(List<T> list) 
    {
    	System.out.println("General Utils -> <T> List<T> safeList -> START");
    	System.out.println("General Utils -> <T> List<T> safeList -> END");
        return list != null ? list : Collections.emptyList();
    }

    public <K, V> Map<K, V> safeMap(Map<K, V> map) 
    {
    	System.out.println("General Utils -> <K, V> Map<K, V> safeMap -> START");
    	System.out.println("General Utils -> <K, V> Map<K, V> safeMap -> END");
        return map != null ? map : Collections.emptyMap();
    }

}
