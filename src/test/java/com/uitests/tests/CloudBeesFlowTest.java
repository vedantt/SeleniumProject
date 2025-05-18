package com.uitests.tests;

import com.uitests.pages.CloudBeesCDROPage;
import com.uitests.pages.DocumentationPage;
import com.uitests.pages.HomePage;
import com.uitests.utils.ConfigReader;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("CloudBees Website Automation")
@Feature("Main Site Flow and Documentation")
public class CloudBeesFlowTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(CloudBeesFlowTest.class);
    private final String CLOUDBEES_URL =  ConfigReader.getProperty("app.url");

    @Test(description = "Verify CloudBees CD/RO information and Documentation Search")
    @Story("E2E Test for Product Info and Documentation Search")
    @Description("This test navigates through the CloudBees website, verifies product information, " +
            "navigates to documentation, performs a search, and verifies pagination.")
    public void testCloudBeesFullFlow() {
        HomePage homePage = new HomePage();
        CloudBeesCDROPage cdroPage;
        DocumentationPage docPage;

        logger.info("Step 1: Open the application (CloudBees website)");
        homePage.navigateToHomePage(CLOUDBEES_URL);
        Assert.assertTrue(homePage.getCurrentUrl().contains("cloudbees.com"), "Homepage URL is incorrect.");

        logger.info("Step 2: Navigate to CloudBees CD/RO page");
        cdroPage = homePage.navigateToCloudBeesCDRO();
        Assert.assertTrue(cdroPage.getCurrentUrl().contains("/products/cloudbees-cdro"), "Not on CD/RO page.");

        logger.info("Step 3: Verify Cost Savings has a value of $2m");
        String costSavings = cdroPage.getCostSavingsValue();
        Assert.assertTrue(costSavings.contains("$2m") || costSavings.contains("$2M"),
                "Cost Savings value is not '$2m' or '$2M'. Found: " + costSavings);

        logger.info("Step 4: Scroll down, click Auditors / Security");
        cdroPage.clickAuditorsSecurityTab();
        // No direct assert here, next step verifies content based on this action

        logger.info("Step 5: Verify the text under Release Governance");
        String expectedGovernanceText = "Generate single-click audit reports";
        String actualGovernanceText = cdroPage.getReleaseGovernanceText();
        Assert.assertTrue(actualGovernanceText.contains(expectedGovernanceText),
                "Release Governance text mismatch. Expected to contain: '" + expectedGovernanceText + "', Found: '" + actualGovernanceText + "'");

        logger.info("Step 6: Navigate to Documentation page (from Home, as context might be lost)");
        // Re-navigate to home to ensure clean state for next major navigation
        homePage.navigateToHomePage(CLOUDBEES_URL);
        docPage = homePage.navigateToDocumentationPage();

        logger.info("Step 7: Verify that Documentation opens a new tab and switch to it");
        // The switchToNewestTab is called implicitly if needed by BasePage or explicitly in test
        homePage.switchToNewestTab(); // Ensure we are on the new tab
        docPage = new DocumentationPage(); // Re-initialize Page Object for the new tab's context
        docPage.acceptCookiesIfPresent(); // Handle cookies on the new docs tab
        Assert.assertTrue(docPage.isDocumentationPageLoaded(), "Documentation page did not load correctly in the new tab.");


        logger.info("Step 8: Click in the text field Search all CloudBees Resources");
        docPage.clickSearchField();

        logger.info("Step 9: Verify that a new page/search interface is opened in this tab");
        Assert.assertTrue(docPage.isSearchInterfaceActive(), "Search interface did not become active after clicking search field.");

        logger.info("Step 10: Search for the word 'Installation'");
        docPage.searchFor("Installation");

        logger.info("Step 11: Verify that we have pagination options at bottom");
        Assert.assertTrue(docPage.isPaginationPresent(), "Pagination was not found on search results page.");

        logger.info("CloudBees full flow test completed successfully.");
    }
}