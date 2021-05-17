package assignment3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CreateContact {

	WebDriver driver;
	String randomEmail;
	String browser;
	String contactName;
	String contactPhone;
	String contactAddress;
	String contactCity;
	String contactState;
	String contactZip;

	public void randomEmail() {

		Random random = new Random();
		int randomNumber = random.nextInt(999);
		randomEmail = (randomNumber + "@gmail.com");
	}

	public void waitForElement(WebDriver driver, WebElement element, int waitTime) {

		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void selectDropdown(WebElement element, String selection) {

		Select select = new Select(element);
		select.selectByVisibleText(selection);
	}

	@BeforeTest
	public void readConfig() {

		Properties prop = new Properties();

		try {
			InputStream input = new FileInputStream("./src/main/java/config/config.properties");
			prop.load(input);
			browser = prop.getProperty("browser");
			contactName = prop.getProperty("name");
			contactPhone = prop.getProperty("phone");
			contactAddress = prop.getProperty("address");
			contactCity = prop.getProperty("city");
			contactState = prop.getProperty("state");
			contactZip = prop.getProperty("zip");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@BeforeMethod
	public void init() {

		if (browser.equalsIgnoreCase("chrome")) {

			System.setProperty("webdriver.chrome.driver", "./driver/chromedriver.exe");
			driver = new ChromeDriver();

		} else if (browser.equalsIgnoreCase("firefox")) {

			System.setProperty("webdriver.gecko.driver", "./driver/geckodriver.exe");
			driver = new FirefoxDriver();

		} else if (browser.equalsIgnoreCase("edge")) {
			
			System.setProperty("webdriver.edge.driver", "./driver/msedgedriver.exe");
			driver = new EdgeDriver();
			
		}

		driver.get("https://www.techfios.com/billing/?ng=admin");
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	@Test
	public void AddContact() {

		// LOGGING IN AND NAVIGATING TO THE ADD CUSTOMER PAGE

		By USERNAME_ELEMENT = By.xpath("//input[@id='username']");
		By PASSWORD_ELEMENT = By.xpath("//input[@id='password']");
		By LOGIN_BUTTON_ELEMENT = By.xpath("//button[contains(text(), 'Sign in')]");

		driver.findElement(USERNAME_ELEMENT).sendKeys("demo@techfios.com");
		driver.findElement(PASSWORD_ELEMENT).sendKeys("abc123");
		driver.findElement(LOGIN_BUTTON_ELEMENT).click();

		driver.findElement(By.xpath("//span[contains(text(), 'Customers')]")).click();

		driver.findElement(By.xpath("//a[contains(text(), 'Add Customer')]")).click();

		// FILLING OUT THE FORM

		randomEmail();

		WebElement ACCOUNT_NAME_ELEMENT = driver.findElement(By.xpath("//input[@id='account']"));
		
		waitForElement(driver, ACCOUNT_NAME_ELEMENT, 20);

		ACCOUNT_NAME_ELEMENT.sendKeys(contactName);

		WebElement COMPANY_DROPDOWN_ELEMENT = driver.findElement(By.xpath("//select[@id='cid']"));
		selectDropdown(COMPANY_DROPDOWN_ELEMENT, "Techfios");

		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(randomEmail);
		driver.findElement(By.xpath("//input[@id='phone']")).sendKeys(contactPhone);
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(contactAddress);
		driver.findElement(By.xpath("//input[@id='city']")).sendKeys(contactCity);
		driver.findElement(By.xpath("//input[@id='state']")).sendKeys(contactState);
		driver.findElement(By.xpath("//input[@id='zip']")).sendKeys(contactZip);

		By COUNTRY_DROPDOWN_ELEMENT = By.xpath("//select[@name='country']");
		selectDropdown(driver.findElement(COUNTRY_DROPDOWN_ELEMENT), "Turkey");

		driver.findElement(By.xpath("//button[@id='submit']")).click();

		// CHECKING IF THE CUSTOMER IS ADDED TO THE LIST 
		
		WebElement SUMMARY_ELEMENT = driver.findElement(By.xpath("//img[@class='img-thumbnail img-responsive']"));
		waitForElement(driver, SUMMARY_ELEMENT, 20);

		By LIST_CONTACTS_ELEMENT = By.xpath("//a[@href='https://techfios.com/billing/?ng=contacts/list/']");
		
		driver.findElement(LIST_CONTACTS_ELEMENT).click();
		
		// Codes below are executed in the List Customers page
		
		waitForElement(driver, driver.findElement(By.xpath("//i[@class='fa fa-download']")), 20);
		
		String nameXpath = ("//table/tbody/tr/td[3]/a[contains(text(), " + "'" + contactName + "')]");
		
		System.out.println(nameXpath);
		
		WebElement NAME_LIST_ELEMENT = driver.findElement(By.xpath(nameXpath));

		Assert.assertEquals(NAME_LIST_ELEMENT.getText(), contactName, "Account is not on the list");

	}

}