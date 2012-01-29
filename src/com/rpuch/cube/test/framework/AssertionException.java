package com.rpuch.cube.test.framework;

/**
 * @author rpuch
 */
public class AssertionException extends RuntimeException {
    public AssertionException(String detailMessage) {
        super(detailMessage);
    }

    public AssertionException(String detailMessage, AssertionException cause) {
        super(detailMessage, cause);
    }
}
