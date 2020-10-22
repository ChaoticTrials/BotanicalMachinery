package de.melanx.botanicalmachinery.helper;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecipeHelper2 {

    /**
     * @param list   {@link List} to remove from
     * @param arrays indexes to remove
     */
    public static void removeFromList(List<?> list, int[]... arrays) {
        List<Integer> toRemove = new ArrayList<>();
        for (int[] array : arrays) {
            for (int i : array) {
                toRemove.add(i);
            }
        }
        toRemove.sort(Comparator.naturalOrder());
        for (int i : Lists.reverse(toRemove)) {
            list.remove(i);
        }
    }
}
