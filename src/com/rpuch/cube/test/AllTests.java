package com.rpuch.cube.test;

import com.rpuch.cube.test.framework.TestsRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rpuch
 */
public class AllTests {
    private final List<Class<?>> suites = new ArrayList<Class<?>>();

    public static void main(String[] args) {
        new AllTests().run();
    }

    private void run() {
        new TestsRunner(suites).run();
    }

    private AllTests() {
        addSuites();
    }

    private void addSuites() {
        addSuite(CubeTest.class);
    }

    private void addSuite(Class<CubeTest> clazz) {
        suites.add(clazz);
    }
}
