package com.uitests.driver;


import com.uitests.utils.ConfigReader;
import org.openqa.selenium.WebDriver;

public class DriverManager {

    // ThreadLocal WebDriver instance to ensure thread safety for parallel execution
    private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
    private static final DriverFactory driverFactory = new DriverFactory();

    /**
     * Gets the current WebDriver instance for this thread.
     * If an instance does not exist, it initializes one based on the "browser" system property.
     *
     * @return The WebDriver instance for the current thread.
     */
    public static WebDriver getDriver() {
        if (webDriverThreadLocal.get() == null) {
            // Default to Chrome if browser property is not set
            String browserType = System.getProperty("browser", ConfigReader.getProperty("browser"));
            WebDriver driver = driverFactory.createDriver(browserType);
            webDriverThreadLocal.set(driver);
        }
        return webDriverThreadLocal.get();
    }

    /**
     * Quits the WebDriver instance for the current thread and removes it from ThreadLocal.
     */
    public static void quitDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            webDriverThreadLocal.remove();
        }
    }
}