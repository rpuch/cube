package com.rpuch.cube.test.framework;

/**
 * @author rpuch
 */
public class Assert {
    public static void fail(String message) {
        throw new AssertionException(message);
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (!safeEquals(expected, actual)) {
            fail(message);
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(String.format("Expected %s but got %s", safeToString(expected), safeToString(actual)), expected, actual);
    }

    private static boolean safeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    private static String safeToString(Object o) {
        return String.valueOf(o);
    }
}
