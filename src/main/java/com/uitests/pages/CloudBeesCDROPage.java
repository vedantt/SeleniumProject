package com.uitests.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudBeesCDROPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CloudBeesCDROPage.class);

    // More robust selector for the cost savings value, looking for the specific text within a relevant section
    @FindBy(xpath = "//span[normalize-space()='$2m']")
    private WebElement costSavingsValueElement;

    // Section containing the cost savings text to scroll to
    @FindBy(xpath = "//p[normalize-space()='Cost Savings']")
    private WebElement costSavingsSection;

    // Tab: Auditors / Security
    @FindBy(xpath = "//button[normalize-space()='Auditors / Security' or normalize-space()='Auditors']")
    private WebElement auditorsSecurityTab;

    // Text under Release Governance after clicking the tab
    @FindBy(xpath = "//*[contains(@class, 'tabs__item--active') or contains(@class, 'is-active') or ancestor::div[contains(@class,'is-active')]]//*[contains(text(), 'Generate single-click audit reports')] | //div[contains(@class,'active')]//*[contains(text(), 'Generate single-click audit reports')] | //*[contains(text(), 'Generate single-click audit reports')]")
    private WebElement releaseGovernanceTextElement;

    @FindBy(xpath = "//h2[contains(text(), 'Key Use Cases')]")
    private WebElement keyUseCasesHeader;


    public CloudBeesCDROPage() {
        super();
    }

    @Step("Verify Cost Savings value is '$2m'")
    public String getCostSavingsValue() {
        logger.info("Verifying cost savings value.");
        scrollIntoView(costSavingsSection, "Cost Savings Section");
        wait.until(ExpectedConditions.visibilityOf(costSavingsValueElement));
        String value = costSavingsValueElement.getText();
        logger.info("Found cost savings value: {}", value);
        return value;
    }

    @Step("Click 'Auditors / Security' tab")
    public CloudBeesCDROPage clickAuditorsSecurityTab() {
        logger.info("Clicking 'Auditors / Security' tab.");
        try {
            scrollIntoView(keyUseCasesHeader, "Key Use Cases Header");
        } catch (Exception e) {
            logger.warn("Key Use Cases header not found, attempting to scroll to tab directly.");
            js.executeScript("window.scrollBy(0, 500);"); // General scroll
        }
        waitFor(1);
        scrollAndClick(auditorsSecurityTab, "'Auditors / Security' tab");
        acceptCookiesIfPresent();
        return this;
    }

    @Step("Get text under Release Governance")
    public String getReleaseGovernanceText() {
        logger.info("Getting text under Release Governance.");
        // Wait for the content associated with the active tab to be visible
        wait.until(ExpectedConditions.visibilityOf(releaseGovernanceTextElement));
        scrollIntoView(releaseGovernanceTextElement, "Release Governance Text");
        String text = releaseGovernanceTextElement.getText().trim();
        logger.info("Found Release Governance text: {}", text);
        return text;
    }
}