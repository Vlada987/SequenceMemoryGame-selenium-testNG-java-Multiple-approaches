package services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import util.Cons;

public class BasicServiceExecutor implements ServiceExecutor {

	WebDriver driver;
	WebDriverWait wait;
	WebElement counter;

	public BasicServiceExecutor(WebDriver driver, WebElement counter) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		this.counter = counter;
	}

	@Override
	public void play_the_game_until(int until) {

		int bound = 0;
		while (!(bound == until)) {
			int round = Integer.valueOf(counter.getText());
			List<WebElement> pattern = capturePattern(round);
			filterWebElements(pattern).forEach(sq -> sq.click());
			bound++;
		}

	}

	public List<WebElement> capturePattern(int round) {
		List<WebElement> pattern = new ArrayList<>();

		long startTime = System.currentTimeMillis();
		long endTime = startTime + (round * 1000);
		while (System.currentTimeMillis() < endTime) {

			try {
				WebElement square = driver.findElement(By.xpath(Cons.active_xpath));
				if (square != null) {
					pattern.add(square);
				}
			} catch (Exception e) {
			}

		}
		return pattern;
	}

	public List<WebElement> filterWebElements(List<WebElement> elements) {
		List<WebElement> filteredList = new ArrayList<>();

		String lastXpath = null;

		for (WebElement element : elements) {
			String xpath = getAbsoluteXPath(element);

			if (lastXpath == null || !lastXpath.equals(xpath)) {
				filteredList.add(element);
				lastXpath = xpath;
			}
		}

		return filteredList;
	}

	public String getAbsoluteXPath(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		String script = "function getAbsoluteXPath(element) { " + "  var path = ''; " + "  while (element !== null) { "
				+ "    var index = 1; " + "    var sibling = element.previousSibling; "
				+ "    while (sibling != null) { " + "      if (sibling.nodeName == element.nodeName) { "
				+ "        index++; " + "      } " + "      sibling = sibling.previousSibling; " + "    } "
				+ "    path = '/' + element.nodeName.toLowerCase() + '[' + index + ']' + path; "
				+ "    element = element.parentNode; " + "  } " + "  return path; " + "} "
				+ "return getAbsoluteXPath(arguments[0]);";

		return (String) js.executeScript(script, element);
	}

}
