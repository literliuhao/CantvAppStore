package cn.can.tvlib.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class CollectionUtil {
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list != null ? list : Collections.<T>emptyList();
    }

    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set != null ? set : Collections.<T>emptySet();
    }

    public static <T> Iterator<T> emptyIfNull(Iterator<T> iterator) {
        return iterator != null ? iterator : Collections.<T>emptyIterator();
    }

    public static <T> Enumeration<T> emptyIfNull(Enumeration<T> enumeration) {
        return enumeration != null ? enumeration : Collections.<T>emptyEnumeration();
    }

    public static <T> ListIterator<T> emptyIfNull(ListIterator<T> listIterator) {
        return listIterator != null ? listIterator : Collections.<T>emptyListIterator();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map != null ? map : Collections.<K, V>emptyMap();
    }
}
