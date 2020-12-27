package ru.bio4j.spring.commons.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Lists {
    public static <T> List<T> select(List<T> list, Predicate<T> check) {
        List<T> result = new ArrayList<>();
        if (list != null && check != null) {
            for (T item : list)
                try {
                    if (check.test(item))
                        result.add(item);
                } catch (Exception e) {
                    throw Utl.wrapErrorAsRuntimeException(e);
                }
        }
        return result;
    }
    public static <T> T first(List<T> list, Predicate<T> check) {
        if(check != null)
            list = select(list, check);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    public static <T> T first(List<T> list) throws Exception {
        return first(list, null);
    }

    public static <T> T last(List<T> list, Predicate<T> check) throws Exception {
        if(check != null)
            list = select(list, check);
        if (list != null && list.size() > 0) {
            return list.get(list.size()-1);
        }
        return null;
    }
    public static <T> T last(List<T> list) throws Exception {
        return last(list, null);
    }

    public static <T> boolean arrayContains(T[] array, T item) {
        for (T itm : array)
            if (itm == item)
                return true;
        return false;
    }

    public static Object arrayCopyOf(Object original) {
        if (original != null && original.getClass().isArray()) {
            int l = ((Object[]) original).length;
            Class<?> originalType = original.getClass();
            Object rslt = Array.newInstance(originalType.getComponentType(), l);
            System.arraycopy(original, 0, rslt, 0, l);
            return rslt;
        }
        return null;
    }

    public static boolean anyInList(final String check, final String list) {
        String[] checkArray = Strings.split(check, ',');
        String[] rolesArray = Strings.split(list, ',');
        return Arrays.stream(rolesArray)
                .distinct()
                .filter(a -> Arrays.stream(checkArray).anyMatch(c -> c.equalsIgnoreCase(a)))
                .toArray().length > 0;
    }
    public static boolean anyInList(final int[] check, final String list) {
        String[] rolesArray = Strings.split(list, ',');
        return Arrays.stream(rolesArray)
                .distinct()
                .filter(a -> Arrays.stream(check).anyMatch(c -> c == Integer.parseInt(a)))
                .toArray().length > 0;
    }
    public static boolean anyInList(final long[] check, final String list) {
        String[] rolesArray = Strings.split(list, ',');
        return Arrays.stream(rolesArray)
                .distinct()
                .filter(a -> Arrays.stream(check).anyMatch(c -> c == Long.parseLong(a)))
                .toArray().length > 0;
    }
    public static boolean anyInList(final int[] check, final int[] roles) {
        return Arrays.stream(roles)
                .distinct()
                .filter(a -> Arrays.stream(check).anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean anyInList(final long[] check, final long[] roles) {
        return Arrays.stream(roles)
                .distinct()
                .filter(a -> Arrays.stream(check).anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean anyInList(final int[] check, final List<Integer> roles) {
        return roles.stream()
                .distinct()
                .filter(a -> Arrays.stream(check).anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean anyInList(final long[] check, final List<Long> roles) {
        return roles.stream()
                .distinct()
                .filter(a -> Arrays.stream(check).anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean anyInList(final List<Integer> check, final int[] roles) {
        return Arrays.stream(roles)
                .distinct()
                .filter(a -> check.stream().anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean anyInList(final List<Long> check, final long[] roles) {
        return Arrays.stream(roles)
                .distinct()
                .filter(a -> check.stream().anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean anyInList(final List<Long> check, final List<Long> roles) {
        return roles.stream()
                .distinct()
                .filter(a -> check.stream().anyMatch(c -> c == a))
                .toArray().length > 0;
    }
    public static boolean itemInList(final int check, final String list) {
        String[] rolesArray = Strings.split(list, ',');
        return Arrays.stream(rolesArray).anyMatch(r -> Integer.parseInt(r) == check);
    }
    public static boolean itemInList(final long check, final String list) {
        String[] rolesArray = Strings.split(list, ',');
        return Arrays.stream(rolesArray).anyMatch(r -> Long.parseLong(r) == check);
    }
    public static boolean itemInList(final int check, final int[] list) {
        return Arrays.stream(list).anyMatch(r -> r == check);
    }
    public static boolean itemInList(final long check, final long[] list) {
        return Arrays.stream(list).anyMatch(r -> r == check);
    }
    public static boolean itemInList(final int check, final List<Integer> list) {
        return list.stream().anyMatch(r -> r == check);
    }
    public static boolean itemInList(final long check, final List<Long> list) {
        return list.stream().anyMatch(r -> r == check);
    }
    public static boolean itemInList(final String check, final String list) {
        String[] rolesArray = Strings.split(list, ',');
        return Arrays.stream(rolesArray).anyMatch(r -> r.equalsIgnoreCase(check));
    }
    public static boolean itemInList(final String check, final int[] list) {
        return Arrays.stream(list).anyMatch(r -> r == Integer.parseInt(check));
    }
    public static boolean itemInList(final String check, final long[] list) {
        return Arrays.stream(list).anyMatch(r -> r == Long.parseLong(check));
    }
    public static boolean itemInList(final String check, final List<Long> list) {
        return list.stream().anyMatch(r -> r == Long.parseLong(check));
    }

}
