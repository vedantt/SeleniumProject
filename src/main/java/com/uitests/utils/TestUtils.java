package com.uitests.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestUtils {

    /**
     * Converts a Throwable's stack trace to a String.
     * @param throwable The throwable.
     * @return String representation of the stack trace.
     */
    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "No throwable provided.";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    // Add other utility methods here, e.g., random data generators, file readers etc.
}
