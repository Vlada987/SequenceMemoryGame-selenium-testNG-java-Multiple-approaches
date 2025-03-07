package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import services.BasicServiceExecutor;
import services.ConcurencyExecutor;
import services.JavaScriptExecutorExecutor;
import services.ServiceExecutor;
import util.Cons;

public class SequenceMemoryPage extends BasePage {

	public WebElement counter;
	ServiceExecutor executor;
	String[] results;

	public SequenceMemoryPage(WebDriver driver) {
		super(driver);
		results = new String[2];

	}

	public String[] play_sequence_memoryGame(int until, ExecutionType executionType) throws InterruptedException {

		openUrl(Cons.url);
		zoomPage(0.7);
		closePopup();
		startTheGame();
		counter = waiting(By.xpath(Cons.counter_xpath));
		switch (executionType) {
		case BASIC:
			executor = new BasicServiceExecutor(driver, counter);
			break;

		case CONCURRENCY:
			executor = new ConcurencyExecutor(driver, driver.findElements(By.xpath(Cons.squares_xpath)), counter);
			break;

		case JAVASCRIPT_EXECUTOR:
			executor = new JavaScriptExecutorExecutor(driver, counter, jsexecutor,
					driver.findElements(By.xpath(Cons.squares_xpath)));
			break;

		default:
			System.out.println("Unknown execution type.");
			break;
		}

		executor.play_the_game_until(until);
		Thread.sleep(4000);
		String count = counter.getText();
		String result = failTheGameAndGetResults();
		results[0] = count;
		results[1] = result;
		return results;
	}

	public void openUrl(String url) {

		try {
			String currentUrl = driver.getCurrentUrl();
			if (currentUrl.contains("humanbenchmark")) {
				driver.navigate().to(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		driver.get(url);
	}

	public void closePopup() {
		try {
			clickOn(By.xpath(Cons.pop_up_xpath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startTheGame() {
		clickOn(By.xpath(Cons.start_button_xpath));
	}

	public String failTheGameAndGetResults() {

		try {
			driver.findElements(By.xpath(Cons.squares_xpath)).forEach(e -> e.click());
		} catch (Exception e) {
		}
		WebElement resultHeader = waiting(By.xpath(Cons.results_header_xpath));
		return resultHeader.getText();
	}

	public enum ExecutionType {
		BASIC, CONCURRENCY, JAVASCRIPT_EXECUTOR
	}

}
