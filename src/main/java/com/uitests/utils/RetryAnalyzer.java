package com.uitests.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    // Max number of retries. Can be read from a config file.
    private static final int MAX_RETRY_COUNT = 2;
    // Example: Retry a failed test 2 times

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) { // Check if test not succeed
            if (retryCount < MAX_RETRY_COUNT) {
                retryCount++;
                logger.warn("Retrying test '{}', attempt #{}", result.getMethod().getMethodName(), retryCount);
                result.setStatus(ITestResult.FAILURE);
                // Mark test as failed before retrying
                return true; // True = Rerun the test
            } else {
                logger.error("Test '{}' failed after {} retries.", result.getMethod().getMethodName(), MAX_RETRY_COUNT);
                result.setStatus(ITestResult.FAILURE); // Ensure the final status is failure
            }
        } else {
            result.setStatus(ITestResult.SUCCESS); // If test passes on a retry, mark it as success
        }
        return false; // False = Do not rerun the test
    }

    public int getRetryCount() {
        return retryCount;
    }

    public static int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
}
