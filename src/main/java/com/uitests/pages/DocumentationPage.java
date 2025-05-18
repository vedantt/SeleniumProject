package com.uitests.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentationPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(DocumentationPage.class);

    // Search input field on the documentation page (might need adjustment)
    @FindBy(xpath = "//input[@placeholder='Search all CloudBees Resources']")
    private WebElement searchInputField;

    // A more specific search input that might appear after first click or on search results page
    @FindBy(xpath = "//input[@placeholder='Search']")
    private WebElement activeSearchInput;

    // Pagination element container
    @FindBy(xpath = "//ul[@class='pagination pagination-sm justify-content-center flex-wrap']")
    private WebElement paginationElement;


    public DocumentationPage() {
        super();
    }

    @Step("Verify Documentation page is loaded by checking title or URL")
    public boolean isDocumentationPageLoaded() {
        String currentUrl = getCurrentUrl();
        String pageTitle = getPageTitle().toLowerCase();
        boolean loaded = currentUrl.contains("docs.cloudbees.com") || pageTitle.contains("documentation");
        logger.info("Documentation page loaded: {}, URL: {}, Title: {}", loaded, currentUrl, pageTitle);
        return loaded;
    }


    @Step("Click in the search field")
    public DocumentationPage clickSearchField() {
        logger.info("Clicking in the search field.");
        wait.until(ExpectedConditions.elementToBeClickable(searchInputField)).click();
        waitFor(1); // Allow time for any overlay or UI change
        return this;
    }

    @Step("Verify search interface is active or URL changed")
    public boolean isSearchInterfaceActive() {
        String currentUrl = getCurrentUrl();
        if (currentUrl.contains("/search") || currentUrl.contains("?q=") || currentUrl.contains("?query=")) {
            logger.info("Search interface active: URL indicates search page. URL: {}", currentUrl);
            return true;
        }
        try {
            // Check if the active search input is now visible and enabled
            wait.until(ExpectedConditions.visibilityOf(activeSearchInput));
            if(activeSearchInput.isDisplayed() && activeSearchInput.isEnabled()){
                logger.info("Search interface active: Active search input is visible and enabled.");
                return true;
            }
        } catch (Exception e) {
            logger.info("Could not confirm active search input, checking original search field's state.");
            try {
                // Fallback: check if the original search input is still the active element
                WebElement focusedElement = driver.switchTo().activeElement();
                if (searchInputField.equals(focusedElement) || activeSearchInput.equals(focusedElement)) {
                    logger.info("Search interface active: Search input field is focused.");
                    return true;
                }
            } catch (Exception e2) {
                logger.warn("Could not determine focused element for search interface verification.");
            }
        }
        logger.warn("Search interface does not appear to be active based on URL or element visibility/focus.");
        return false;
    }


    @Step("Search for term: {searchTerm}")
    public DocumentationPage searchFor(String searchTerm) {
        logger.info("Searching for term: {}", searchTerm);
        WebElement inputToUse;
        try {
            // Prefer the active/specific search input if available
            wait.until(ExpectedConditions.visibilityOf(activeSearchInput));
            inputToUse = activeSearchInput;
            logger.info("Using active search input field.");
        } catch (Exception e) {
            logger.info("Active search input not found, using initial search input field.");
            inputToUse = searchInputField; // Fallback to the initially clicked field
        }

        wait.until(ExpectedConditions.elementToBeClickable(inputToUse));
        inputToUse.clear();
        inputToUse.sendKeys(searchTerm);
        inputToUse.sendKeys(Keys.ENTER);
        logger.info("Submitted search for: {}", searchTerm);
        acceptCookiesIfPresent(); // Handle cookies that might pop up after search
        waitFor(3); // Wait for search results to load
        return this;
    }

    @Step("Verify pagination is present")
    public boolean isPaginationPresent() {
        logger.info("Verifying pagination presence.");
        try {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            waitFor(1); // Wait for scroll and potential lazy loading
            wait.until(ExpectedConditions.visibilityOf(paginationElement));
            boolean present = paginationElement.isDisplayed();
            logger.info("Pagination present: {}", present);
            return present;
        } catch (Exception e) {
            logger.warn("Pagination element not found or not visible: " + e.getMessage());
            // Fallback: Check for common text like "Next" or page numbers if the above generic nav fails
            boolean hasNextOrPageNumbers = !driver.findElements(By.xpath("//*[self::a or self::button][contains(translate(normalize-space(), 'NEXT', 'next'),'next') or matches(text(), '^\\d+$')]")).isEmpty();
            if(hasNextOrPageNumbers){
                logger.info("Fallback: Found 'Next' or page numbers, considering pagination present.");
                return true;
            }
            logger.warn("Pagination not found even with fallback.");
            return false;
        }
    }
}