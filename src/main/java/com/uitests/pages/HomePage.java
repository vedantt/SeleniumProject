package com.uitests.pages;


import io.qameta.allure.Step;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(HomePage.class);

    @FindBy(xpath = "//button[contains(text(),'Products')]")
    private WebElement productsMenuButton;

    // This XPath is more specific to the CD/RO link within the products dropdown
    @FindBy(xpath = "//a[contains(@href, '/products/cloudbees-cdro') and contains(text(), 'CloudBees CD/RO')]")
    private WebElement cloudbeesCDROLink;

    @FindBy(xpath = "//button[contains(text(),'Resources')]")
    private WebElement resourcesMenuButton;

    @FindBy(xpath = "//a[@id='subcategory-item-0__Documentation']")
    private WebElement documentationLink;


    public HomePage() {
        super(); // Calls BasePage constructor
    }

    @Step("Navigate to CloudBees Homepage")
    public HomePage navigateToHomePage(String url) {

        int attempts = 0;
        logger.info("Navigating to URL: {}", url);
        int maxAttempts = 3;
        while (attempts < maxAttempts) {
            try {
                driver.get(url);
                return this;
            }

            catch (TimeoutException e) {
                attempts++;
        }
        }

        acceptCookiesIfPresent(); // Handle cookies on page load
        return this;
    }

    @Step("Navigate to CloudBees CD/RO page via Products menu")
    public CloudBeesCDROPage navigateToCloudBeesCDRO() {
        logger.info("Navigating to CloudBees CD/RO page");
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(productsMenuButton));
        actions.moveToElement(productsMenuButton).click().perform();
        logger.info("clicked over 'Products' menu.");

        wait.until(ExpectedConditions.visibilityOf(cloudbeesCDROLink));
        // jsClick(cloudbeesCDROLink); // Using JS click for potential overlay issues
        scrollAndClick(cloudbeesCDROLink, "CloudBees CD/RO Link");
        logger.info("Clicked 'CloudBees CD/RO' link.");
        acceptCookiesIfPresent(); // Handle cookies on new page
        return new CloudBeesCDROPage();
    }

    @Step("Navigate to Documentation page via Resources menu")
    public DocumentationPage navigateToDocumentationPage() {
        logger.info("Navigating to Documentation page");
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.elementToBeClickable(resourcesMenuButton));
        //actions.moveToElement(resourcesMenuButton).click().perform();
        jsClick(resourcesMenuButton);
        logger.info("Clicked over 'Resources' menu.");
        waitFor(1);
        try {
            wait.until(ExpectedConditions.visibilityOf(documentationLink));
        }
        catch (Exception E)
        {
         logger.warn("visibility for doc failed still trying to click");
        }
        jsClick(documentationLink);
        //scrollAndClick(documentationLink, "Documentation Link");
        logger.info("Clicked 'Documentation' link.");
        // New tab handling will be in the test or a utility, page should focus on its elements
        return new DocumentationPage();
    }
}
