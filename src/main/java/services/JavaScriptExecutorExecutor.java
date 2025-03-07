package services;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JavaScriptExecutorExecutor implements ServiceExecutor {

	WebDriver driver;
	List<WebElement> squares;
	WebElement counter;
	JavascriptExecutor jsExecutor;

	public JavaScriptExecutorExecutor(WebDriver driver, WebElement counter, JavascriptExecutor jsExecutor,
			List<WebElement> squares) {
		this.driver = driver;
		this.jsExecutor = jsExecutor;
		this.counter = counter;
		this.squares = squares;
	}

	@Override
	public void play_the_game_until(int until) throws InterruptedException {

		int bound = 0;
		while (!(bound == until)) {
			int round = Integer.valueOf(counter.getText());
			LinkedHashMap<Long, WebElement> pattern = capturePattern(until);
			Thread.sleep(1000);
			pattern.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue)
					.forEach(WebElement::click);
			bound++;
		}

	}

	public LinkedHashMap<Long, WebElement> capturePattern(int until) {
		LinkedHashMap<Long, WebElement> activationMap = new LinkedHashMap<>();

		int activationCount = Integer.valueOf(counter.getText());

		String script = "const elements = arguments[0];" + "const activationMap = {};" + "let activationCount = "
				+ activationCount + ";" + "let activationTracker = 0;" + "let observerStopped = false;"
				+ "const observer = new MutationObserver((mutationsList) => {" + "if (observerStopped) return;"
				+ "mutationsList.forEach((mutation) => {"
				+ "if (mutation.type === 'attributes' && mutation.target.className.includes('active')) {"
				+ "const currentTimeMillis = new Date().getTime();"
				+ "activationMap[currentTimeMillis] = mutation.target;" + "activationTracker++;"
				+ "if (activationTracker >= activationCount) {" + "observerStopped = true;" + "observer.disconnect();"
				+ "}" + "}" + "});" + "});" + "elements.forEach((element) => {"
				+ "observer.observe(element, { attributes: true });" + "});" + "return new Promise((resolve) => {"
				+ "const checkInterval = setInterval(() => {" + "if (activationTracker >= activationCount) {"
				+ "clearInterval(checkInterval);" + "resolve(activationMap);" + "}" + "}, 100);" + "});";

		Object result = jsExecutor.executeScript(script, squares);

		if (result instanceof Map) {
			Map<?, ?> mapResult = (Map<?, ?>) result;

			for (Object key : mapResult.keySet()) {
				Object value = mapResult.get(key);
				if (value instanceof WebElement) {

					try {
						Long keyLong = Long.parseLong(key.toString());
						activationMap.put(keyLong, (WebElement) value);
					} catch (NumberFormatException e) {
						System.out.println("Error parsing key to Long: " + key);
					}
				}
			}
		} else {
			System.out.println("Result is not of expected Map type.");
		}

		return activationMap;
	}

}
