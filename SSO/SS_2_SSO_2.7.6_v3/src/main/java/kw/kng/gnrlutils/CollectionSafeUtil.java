package kw.kng.gnrlutils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CollectionSafeUtil 
{
	boolean isEmpty(Collection<?> col);
	<T> List<T> safeList(List<T> list);
	<K, V> Map<K, V> safeMap(Map<K, V> map);

}
