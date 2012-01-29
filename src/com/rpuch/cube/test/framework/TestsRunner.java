package com.rpuch.cube.test.framework;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rpuch
 */
public class TestsRunner {
    private final List<Class<?>> classes;
    private final PrintStream out = System.out;

    public TestsRunner(List<Class<?>> classes) {
        this.classes = classes;
    }

    public void run() {
        for (Class<?> clazz : classes) {
            runSuite(clazz);
        }
    }

    private void runSuite(final Class<?> clazz) {
        final SuiteResults results = new SuiteResults(clazz.getSimpleName());
        final List<Method> testMethods = getTestMethods(clazz);
        execute(results, false, new ThrowingRunnable() {
            public void run() throws Exception {
                if (testMethods.isEmpty()) {
                    Assert.fail("Did not find any test in " + clazz.getName());
                }
                Object test = instantiate(clazz);
                for (Method method : testMethods) {
                    runMethod(test, method, results);
                }
            }
        });
        report(results);
    }

    private void report(SuiteResults results) {
        out.println(String.format("%s: %d success, %d failed, %d errors", results.getName(),
                results.getSuccessCount(), results.getFailedCount(), results.getErrorCount()));
        for (Exception e : results.getFailed()) {
            printException(e);
        }
        for (Throwable e : results.getErrors()) {
            printException(e);
        }
    }

    private void printException(Throwable e) {
        e.printStackTrace(out);
    }

    private Object instantiate(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    private List<Method> getTestMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<Method>();
        for (Method method :  clazz.getMethods()) {
            if ((method.getModifiers() & Modifier.PUBLIC) != 0
                    && method.getName().startsWith("test")
                    && method.getParameterTypes().length == 0) {
                methods.add(method);
            }
        }
        return methods;
    }

    private void runMethod(final Object test, final Method method, SuiteResults results) {
        execute(results, true, new ThrowingRunnable() {
            public void run() throws Exception {
                method.invoke(test);
            }
        });
    }

    private void execute(SuiteResults results, boolean logSuccess, ThrowingRunnable what) {
        try {
            what.run();
            if (logSuccess) {
                results.success();
            }
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException) e;
                results.throwable(ite.getTargetException());
            } else {
                results.throwable(e);
            }
        }
    }

    private static class SuiteResults {
        private final String name;
        private int successCount;

        private List<AssertionException> failed = new ArrayList<AssertionException>();
        private List<Throwable> errors = new ArrayList<Throwable>();

        public SuiteResults(String name) {
            this.name = name;
        }

        public void success() {
            successCount++;
        }

        public void throwable(Throwable e) {
            if (e instanceof AssertionException) {
                failed.add((AssertionException) e);
            } else {
                errors.add(e);
            }
        }

        public String getName() {
            return name;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailedCount() {
            return failed.size();
        }

        public int getErrorCount() {
            return errors.size();
        }

        public List<AssertionException> getFailed() {
            return failed;
        }

        public List<Throwable> getErrors() {
            return errors;
        }
    }

    private static interface ThrowingRunnable {
        void run() throws Exception;
    }
}
