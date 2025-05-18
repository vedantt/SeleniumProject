package com.uitests.pages;
import com.uitests.driver.DriverManager;
import com.uitests.utils.ConfigReader;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.Set; 
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);


    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Long.parseLong((Objects.requireNonNull(ConfigReader.getProperty("wait")))))); // Increased default wait
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    @Step("Accepting cookie consent banner if present")
    public void acceptCookiesIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement acceptButton = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(translate(normalize-space(), 'ACCEPTCOOKIES', 'acceptcookies'), 'accept cookies') or contains(translate(normalize-space(), 'ACCEPT', 'accept'), 'accept') or contains(translate(normalize-space(), 'AGREE', 'agree'), 'agree') or @id='onetrust-accept-btn-handler']")
            ));
            if (acceptButton.isDisplayed()) {
                logger.info("Cookie consent banner found. Clicking accept...");
                jsClick(acceptButton);
                wait.until(ExpectedConditions.invisibilityOf(acceptButton));
                logger.info("Cookie consent accepted.");
            }
        } catch (TimeoutException e) {
            logger.info("Cookie consent banner not found or already handled within 5 seconds.");
        } catch (Exception e) {
            logger.warn("An issue occurred while trying to accept cookies: " + e.getMessage());
        }
    }

    @Step("Scrolling element into view: {elementDescription}")
    protected void scrollIntoView(WebElement element, String elementDescription) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        waitFor(1);
    }

    @Step("Scrolling element into view and clicking: {elementDescription}")
    protected void scrollAndClick(WebElement element, String elementDescription) {
       
        int attempts = 0;
        int maxAttempts = 3; // Max attempts for this specific step
        while (attempts < maxAttempts) {
            try {
                scrollIntoView(element, elementDescription + " (Attempt " + (attempts + 1) + ")");
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                logger.info("Successfully clicked '{}' on attempt {}", elementDescription, attempts + 1);
                return; // Success, exit retry loop
            } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                attempts++;
                logger.warn("Attempt {} to click '{}' failed due to {}. Retrying...", attempts, elementDescription, e.getClass().getSimpleName());
                if (attempts >= maxAttempts) {
                    logger.error("Failed to click '{}' after {} attempts.", elementDescription, maxAttempts);
                    throw e; // Re-throw the exception if all retries fail
                }
                waitFor(1); // Wait a bit before retrying
                           }
        }
    }

    @Step("Clicking element using JavaScript: {elementDescription}")
    protected void jsClick(WebElement element) {
     
        int attempts = 0;
        int maxAttempts = 2;
        while(attempts < maxAttempts) {
            try {
                wait.until(ExpectedConditions.visibilityOf(element)); // Ensure element is visible
                js.executeScript("arguments[0].click();", element);
                logger.info("Successfully JS clicked element on attempt {}", attempts + 1);
                return;
            } catch (Exception e) {
                attempts++;
                logger.warn("Attempt {} to JS click failed due to {}. Retrying...", attempts, e.getClass().getSimpleName());
                if (attempts >= maxAttempts) {
                    logger.error("Failed to JS click after {} attempts.", maxAttempts);
                    throw e;
                }
                waitFor(1);
            }
        }
    }


    @Step("Waiting for {seconds} second(s)")
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted during wait", e);
        }
    }

    @Step("Getting current page URL")
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Step("Getting current page title")
    public String getPageTitle() {
        return driver.getTitle();
    }

    @Step("Switching to the newest tab")
    public void switchToNewestTab() {
        String originalWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles(); // Ensure Set is imported
        if (allWindows.size() > 1) {
            for (String windowHandle : allWindows) {
                if (!originalWindow.contentEquals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    logger.info("Switched to new tab: " + driver.getTitle());
                    break;
                }
            }
        } else {
            logger.warn("No new tab found to switch to.");
        }
    }
}
