package oracle.assignment;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

public class DockerTests {

	WebDriver driver;

	@BeforeClass
	public void setup() throws InterruptedException {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("https://hub.docker.com/search?type=image");
		driver.manage().window().maximize();
		Thread.sleep(2000);
	}

	@Test(priority = 1)
	@Description("Verify user is navigated to Containers tab by default and expected checkboxes are displayed")
	@Severity(SeverityLevel.CRITICAL)
	public void verifyDefaultTab() {

		// a. Verify that the user lands in the “Containers” tab by default
		// Checking that image label is displayed by default which is displayed only on
		// Containers tab
		boolean isImageLabelDisplayed = driver
				.findElement(By.xpath("//span[text()='Official Images published by Docker']")).isDisplayed();
		Assert.assertEquals(isImageLabelDisplayed, true);

		// b. Verify there are 2 check boxes under Images with labels “Verified
		// Publisher” and “Official Images”
		// Fetch all input tags for check boxes under Images and validate that size in 2
		List<WebElement> imagesCheckboxes = driver
				.findElements(By.xpath("//div[text()='Images']/following-sibling::div//input"));
		Assert.assertEquals(imagesCheckboxes.size(), 2);

		// Fetch labels of check boxes under Images and validate their names are
		// “Verified Publisher” and “Official Images”
		List<WebElement> imageCheckboxesLabels = driver
				.findElements(By.xpath("//div[text()='Images']/following-sibling::div/div/div/label/span[1]"));
		if (imageCheckboxesLabels.size() == 2) {
			Assert.assertEquals(imageCheckboxesLabels.get(0).getText(), "Verified Publisher");
			Assert.assertEquals(imageCheckboxesLabels.get(1).getText(), "Official Images");
		} else
			Assert.fail("Number of checkboxes is not 2");

		/*
		 * c. Under Categories, verify the following check boxes are present 
		 * i. Analytics 
		 * ii. Base Images
		 * iii. Databases
		 * iv. Storage
		 */

		// Fetch the categories section using XPath which contains the check box and
		// label displayed on screen
		WebElement categoriesSection = driver.findElement(By.xpath("//div[@id='categoriesFilterList']"));

		// Fetching the check box from categories section webelement which ensures it
		// falls under Categories section
		List<WebElement> analyticsCheckbox = categoriesSection.findElements(By.xpath("//input[@value='analytics']"));

		// Fetching the label of check box as well using text() to ensure that text
		// displayed on screen is also as expected
		WebElement analyticsCheckboxLabel = categoriesSection.findElement(By.xpath("//label[text()='Analytics']"));

		// Fetching rest of the check boxes and labels
		List<WebElement> baseImagesCheckbox = categoriesSection.findElements(By.xpath("//input[@value='base']"));
		WebElement baseImagesCheckboxLabel = categoriesSection.findElement(By.xpath("//label[text()='Base Images']"));

		List<WebElement> databasesCheckbox = categoriesSection.findElements(By.xpath("//input[@value='database']"));
		WebElement databasesCheckboxLabel = categoriesSection.findElement(By.xpath("//label[text()='Databases']"));

		List<WebElement> storageCheckbox = categoriesSection.findElements(By.xpath("//input[@value='storage']"));
		WebElement storageCheckboxLabel = categoriesSection.findElement(By.xpath("//label[text()='Storage']"));
		
		//Checking if the list is not empty to validate if check boxes are displayed
		//For some reason isDisplayed() is returning false for check boxes
		boolean hasCategoriesCheckboxes = (!analyticsCheckbox.isEmpty() && !baseImagesCheckbox.isEmpty()
				&& !databasesCheckbox.isEmpty() && !storageCheckbox.isEmpty());
		
		boolean hasCheckboxLabels = (analyticsCheckboxLabel.isDisplayed() && baseImagesCheckboxLabel.isDisplayed()
				&& databasesCheckboxLabel.isDisplayed() && storageCheckboxLabel.isDisplayed());

		Assert.assertEquals((hasCategoriesCheckboxes && hasCheckboxLabels), true);
	}

	@Test(priority = 2)
	@Description("Apply a filter and verify if it is displayed on top")
	@Severity(SeverityLevel.CRITICAL)
	public void applyFilter() throws InterruptedException {

		// 2. Click the “Verified Publisher” check box
		driver.findElement(By.xpath("//input[@value='store']")).click();
		Thread.sleep(2000);

		// a. Verify the filter “Publisher Content” is shown at the top of the content
		// Fetched the filter element by XPath and validating that it is displayed
		boolean isAppliedFilterDisplayed = driver
				.findElement(By.xpath("//div[@data-testid='currentFilters']/div[text()='Publisher Content']"))
				.isDisplayed();
		Assert.assertEquals(isAppliedFilterDisplayed, true);
	}

	@Test(priority = 3)
	@Description("Apply additional filters and verify if they are displayed on top")
	@Severity(SeverityLevel.CRITICAL)
	public void additionalFilter() throws InterruptedException {

		// 3. Click Base Images and Database Check boxes
		driver.findElement(By.xpath("//input[@value='base']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@value='database']")).click();
		Thread.sleep(2000);

		// a. Verify the additional filters are shown at the top of the content
		// Fetched the filter elements by XPath and validating that they are displayed
		WebElement baseImageFilter = driver
				.findElement(By.xpath("//div[@data-testid='currentFilters']/div[text()='Base Images']"));
		WebElement databasesFilter = driver
				.findElement(By.xpath("//div[@data-testid='currentFilters']/div[text()='Databases']"));
		boolean isAppliedFilterDisplayed = (baseImageFilter.isDisplayed() && databasesFilter.isDisplayed());
		Assert.assertEquals(isAppliedFilterDisplayed, true);
	}

	@Test(priority = 4)
	@Description("Remove a filter and verify if the checkbox is unchecked")
	@Severity(SeverityLevel.CRITICAL)
	public void removeFilter() throws InterruptedException {

		// 4. Click the close icon (X) on the database filter
		driver.findElement(By.xpath("//div[@data-testid='currentFilters']/div[text()='Databases']")).click();
		Thread.sleep(2000);
		// a. Verify the corresponding check box in the left filter pane is also
		// unchecked.
		// Fetched the corresponding input tag and validating that it is not selected
		boolean isDatabaseCheckboxSelected = driver.findElement(By.xpath("//label[text()='Databases']/../../input"))
				.isSelected();
		Assert.assertEquals(isDatabaseCheckboxSelected, false);
	}

	@Test(priority = 5)
	@Description("Dummy test case to see failure in report")
	@Severity(SeverityLevel.TRIVIAL)
	public void failingTest() {
		Assert.fail();
	}

	@Test(priority = 6)
	@Description("Dummy test case to see skipped test in report")
	@Severity(SeverityLevel.TRIVIAL)
	public void skippedTest() {
		throw new SkipException("Skipping test");
	}

	@AfterClass
	public void endExecution() {
		driver.quit();
	}
}
