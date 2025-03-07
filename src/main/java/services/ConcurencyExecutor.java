package services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ConcurencyExecutor implements ServiceExecutor {

	public WebDriver driver;
	public List<WebElement> squares;
	public WebElement counter;
	public ExecutorService executor;
	public final AtomicBoolean stopThreads = new AtomicBoolean(false);
	public WebDriverWait mwait;
	public final Map<WebElement, ReentrantLock> elementLocks = new HashMap<>();

	public ConcurencyExecutor(WebDriver driver, List<WebElement> squares, WebElement counter) {
		this.driver = driver;
		this.squares = squares;
		this.counter = counter;
		this.executor = Executors.newFixedThreadPool(squares.size());
		this.mwait = new WebDriverWait(driver, Duration.ofSeconds(7));
		elementLocks.putAll(squares.stream().collect(Collectors.toMap(sq -> sq, sq -> new ReentrantLock())));
	}

	@Override
	public void play_the_game_until(int until) throws InterruptedException {

		int bound = 0;
		while (!(bound == until)) {
			int round = Integer.valueOf(counter.getText());
			ConcurrentHashMap<Long, WebElement> pattern = captureActivations();
			Thread.sleep(2000);
			clickElementsInOrder(pattern);
			bound++;
		}
		shutdownExecutor();

	}

	public ConcurrentHashMap<Long, WebElement> captureActivations() {

		ConcurrentHashMap<Long, WebElement> activationMap = new ConcurrentHashMap<>();

		while (true) {

			int expectedActivations = Integer.parseInt(counter.getText());
			stopThreads.set(false);

			if (activationMap.size() == expectedActivations) {
				stopThreads.set(true);
				break;
			}
			List<Callable<Void>> tasks = new ArrayList<>();

			for (int i = 0; i < squares.size(); i++) {
				final WebElement square = squares.get(i);
				tasks.add(() -> {

					while (!stopThreads.get()) {

						if (square.getAttribute("class").contains("active")) {

							long activationTime = System.currentTimeMillis();
							ReentrantLock lock = elementLocks.get(square);
							lock.lock();

							try {
								Long lastKey = null;
								for (Long key : activationMap.keySet()) {
									if (lastKey == null || key > lastKey) {
										lastKey = key;
									}
								}

								WebElement lastValue = null;
								if (lastKey != null) {
									lastValue = activationMap.get(lastKey);
								}
								if (lastValue == null || !lastValue.equals(square)) {
									activationMap.put(activationTime, square);
								}

							} finally {
								lock.unlock();
							}
							stopThreads.set(true);
						}
					}

					return null;
				});
			}
			try {
				executor.invokeAll(tasks);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		return activationMap;
	}

	public void clickElementsInOrder(Map<Long, WebElement> activationMap) {

		activationMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			WebElement square = entry.getValue();
			String xpath = getXPath(square);
			mwait.until(ExpectedConditions.attributeToBe(square, "class", "square"));
			square.click();

			try {

				Thread.sleep(65);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	public void shutdownExecutor() {

		executor.shutdown();
		try {

			if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
				executor.shutdownNow();
				if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
					System.err.println("Executor did not terminate in time.");
				}
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public WebElement getLastAddedEntry(ConcurrentHashMap<Long, WebElement> activationMap) {

		Optional<Map.Entry<Long, WebElement>> lastEntry = activationMap.entrySet().stream()
				.max(Map.Entry.comparingByKey());
		return lastEntry.map(Map.Entry::getValue).orElse(null);
	}

	public String getXPath(WebElement element) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

		String script = "function getElementXPath(element) {" + "    var path = '';"
				+ "    while (element !== document.body) {" + "        var index = 1;"
				+ "        var sibling = element.previousElementSibling;" + "        while (sibling) {"
				+ "            if (sibling.tagName === element.tagName) {" + "                index++;"
				+ "            }" + "            sibling = sibling.previousElementSibling;" + "        }"
				+ "        path = '/' + element.tagName.toLowerCase() + '[' + index + ']' + path;"
				+ "        element = element.parentElement;" + "    }" + "    return '/html' + path;" + "}"
				+ "return getElementXPath(arguments[0]);";

		return (String) jsExecutor.executeScript(script, element);
	}

}
