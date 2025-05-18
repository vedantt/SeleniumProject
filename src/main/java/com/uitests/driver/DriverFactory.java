package com.uitests.driver;

import com.uitests.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager; // Using WebDriverManager for easier driver setup
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

public class DriverFactory {

    /**
     * Creates a WebDriver instance based on the specified browser type.
     *
     * @param browser The name of the browser (e.g., "chrome", "firefox", "edge").
     * @return A WebDriver instance.
     * @throws IllegalArgumentException if the browser type is not supported.
     */
    public WebDriver createDriver(String browser) {
        WebDriver driver;
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", ConfigReader.getProperty("headless"))); // Read headless mode from system property

        switch (browser.toLowerCase()) {
            case "chrome":
                // WebDriverManager automatically downloads and sets up ChromeDriver
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                if (headless) {
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--window-size=1920,1080"); // Specify window size for headless
                }
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                }
                driver = new FirefoxDriver(firefoxOptions);
                driver.manage().window().maximize(); // Firefox needs explicit maximize after init sometimes
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                if (headless) {
                    edgeOptions.addArguments("--headless");
                    edgeOptions.addArguments("--window-size=1920,1080");
                }
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser + ". Supported browsers are: chrome, firefox, edge.");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Default implicit wait
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60)); // Default page load timeout
        return driver;
    }
}