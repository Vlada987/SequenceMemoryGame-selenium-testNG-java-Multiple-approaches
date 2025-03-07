package tests;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import webdriver.DriverFactory;
import webdriver.DriverFactory.BrowserType;

public class BaseTest {
	
	WebDriver driver;
	
	@BeforeTest
	public void load() {
		if(driver==null) {
		driver=DriverFactory.createDriver(BrowserType.FIREFOX);
		}
	}
	
	@AfterTest
	public void tearDown() {
		if(driver!=null) {
			driver.quit();
		}
	}
	
	
	
	
	public String getTestStatus(int status) {
	    switch (status) {
	        case ITestResult.SUCCESS:
	            return "SUCCESS";
	        case ITestResult.FAILURE:
	            return "FAILURE";
	        case ITestResult.SKIP:
	            return "SKIPPED";
	        default:
	            return "UNKNOWN";
	    }
	
	}
}
