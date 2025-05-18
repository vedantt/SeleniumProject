package com.uitests.tests;


import com.uitests.driver.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;


public abstract class BaseTest {
    protected WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);


    @BeforeMethod
    @Parameters("browser") 
    public void setUp(@Optional("chrome") String browser, Method method) { // Default to chrome if not specified
        System.setProperty("browser", browser); // Set system property for DriverManager
        logger.info("--------------------------------------------------------------------------------");
        logger.info("Starting test: {} with browser: {}", method.getName(), browser);
        logger.info("--------------------------------------------------------------------------------");
        driver = DriverManager.getDriver(); // Initializes driver via ThreadLocal
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        logger.info("--------------------------------------------------------------------------------");
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test FAILED: {}", result.getName());
            if (driver != null) {
                captureScreenshot(result.getMethod().getMethodName() + "_failure");
            }
            // Log throwable
            if (result.getThrowable() != null) {
                logger.error("Failure Reason: ", result.getThrowable());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test PASSED: {}", result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("Test SKIPPED: {}", result.getName());
        }
        logger.info("Finished test: {}", result.getName());
        logger.info("--------------------------------------------------------------------------------\n");
        DriverManager.quitDriver(); // Quits driver and removes from ThreadLocal
    }

    @Attachment(value = "{screenshotName}", type = "image/png")
    public byte[] captureScreenshot(String screenshotName) {
        if (driver == null) {
            logger.warn("Driver is null, cannot capture screenshot: {}", screenshotName);
            return new byte[0];
        }
        logger.info("Capturing screenshot: {}", screenshotName);
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
