package tests;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.SequenceMemoryPage;
import pages.SequenceMemoryPage.ExecutionType;

public class SequenceMemoryGameTest extends BaseTest {

	SequenceMemoryPage game;

	@BeforeTest()
	public void setSequenceMemoryPageRunner() {
		if (game == null) {
			game = new SequenceMemoryPage(driver);
		}
	}

	@Test()
	@Parameters({ "executionType", "numberOfRounds" })
	public void test_Run_the_game_until(String executionType, int numberOfRounds, ITestResult testResult)
			throws InterruptedException {

		String[] results = game.play_sequence_memoryGame(numberOfRounds, ExecutionType.valueOf(executionType));

		Assert.assertEquals(results[0], String.valueOf(numberOfRounds + 1));
		Assert.assertEquals(results[1], "Level " + String.valueOf(numberOfRounds + 1));

		System.out.println(results[0]);
		System.out.println(results[1]);
		System.out.println("Test Name: " + testResult.getName() + "for " + executionType);
		System.out.println("Test Status: " + getTestStatus(testResult.getStatus()));

	}

}
