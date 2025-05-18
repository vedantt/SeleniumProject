package com.uitests.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotationTransformer implements IAnnotationTransformer {

    /**
     * This method will be invoked by TestNG to transform the TestNG annotations
     * It sets the RetryAnalyzer for all test methods.
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Set the RetryAnalyzer for all @Test methods.
        // This ensures that if a test method does not have a retryAnalyzer specified,
        // it will use the RetryAnalyzer.class.
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}