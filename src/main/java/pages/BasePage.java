package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {

	public WebDriver driver;
	public WebDriverWait wait;
	public JavascriptExecutor jsexecutor;

	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		jsexecutor = (JavascriptExecutor) driver;

	}

	public WebElement waiting(By by) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public void clickOn(By by) {
		wait.until(ExpectedConditions.elementToBeClickable(by));
		driver.findElement(by).click();
	}

	public void scrollToElement(By by) {
		WebElement element = driver.findElement(by);
		jsexecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
	}

	public void scrollAndClick(WebElement element) {
		try {
			jsexecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
			Thread.sleep(250);
			element.click();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: Unable to scroll and click.");
		}
	}

	public void zoomPage(double zoomFactor) {

		if (zoomFactor < 0.1 || zoomFactor > 3) {
			System.out.println("Zoom factor must be between 0.1 and 3.");
			return;
		}
		String zoomScript = "document.body.style.zoom = '" + zoomFactor + "';";
		jsexecutor.executeScript(zoomScript);
		System.out.println("Page zoomed to: " + zoomFactor);
	}

}
