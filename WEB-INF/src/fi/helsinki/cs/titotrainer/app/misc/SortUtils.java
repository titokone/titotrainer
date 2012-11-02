package fi.helsinki.cs.titotrainer.app.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Static utility functions for convenient sorting.
 */
public class SortUtils {
    
    @SuppressWarnings("unchecked") // damn erasure..
    public static <T> Object[] getSortedArray(Collection<? extends T> stuff, Comparator<? super T> cmp) {
        Object[] arr = stuff.toArray(new Object[stuff.size()]);
        Arrays.sort(arr, (Comparator)cmp);
        return arr;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> getSortedList(Collection<? extends T> stuff, Comparator<? super T> cmp) {
        return (List<T>)Arrays.asList(getSortedArray(stuff, cmp));
    }
    
}
