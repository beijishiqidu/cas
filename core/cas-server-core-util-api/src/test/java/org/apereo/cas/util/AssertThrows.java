package org.apereo.cas.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.util.function.Supplier;

/**
 * This is {@link AssertThrows}. Allows expected type to be null.
 *
 * @author Timur Duehr
 * @since 6.0.1
 */
public class AssertThrows {
    /**
     * Null safe assertThrows and assertDoesNotThrow..
     * @param expected Expected exception type. +null+ if no exception should be thrown.
     * @param executable Procedure to assert thrown exception.
     * @param <T> Type of exception to be expected.
     * @return Exception thrown in assertion process.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrowsOrNot(final T expected, final Executable executable) {
        if (expected == null) {
            Assertions.assertDoesNotThrow(executable);
            return null;
        }
        return (T) Assertions.assertThrows(expected.getClass(), executable);
    }

    /**
     * Null safe assertThrows and assertDoesNotThrow..
     * @param expected Expected exception type. +null+ if no exception should be thrown.
     * @param executable Procedure to assert thrown exception.
     * @param message Message for test case.
     * @param <T> Type of exception to be expected.
     * @return Exception thrown in assertion process.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrowsOrNot(final T expected, final Executable executable, final String message) {
        if (expected == null) {
            Assertions.assertDoesNotThrow(executable, message);
            return null;
        }
        return (T) Assertions.assertThrows(expected.getClass(), executable, message);
    }

    /**
     * Null safe assertThrows and assertDoesNotThrow..
     * @param expected Expected exception type. +null+ if no exception should be thrown.
     * @param executable Procedure to assert thrown exception.
     * @param messageSupplier Message supplier for test case.
     * @param <T> Type of exception to be expected.
     * @return Exception thrown in assertion process.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T assertThrowsOrNot(final T expected, final Executable executable,
                                                       final Supplier<String> messageSupplier) {
        if (expected == null) {
            Assertions.assertDoesNotThrow(executable, messageSupplier);
            return null;
        }
        return (T) Assertions.assertThrows(expected.getClass(), executable, messageSupplier);
    }


}
