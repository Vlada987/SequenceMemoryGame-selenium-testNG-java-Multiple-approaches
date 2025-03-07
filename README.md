# Sequence Memory Game Automation Framework

This framework automates the gameplay of the Sequence Memory Game on a web page with dynamic, timed elements using three different execution approaches. It is designed to handle complex dynamic scenarios, ensuring that the game is played until the specified amount of points (rounds) is reached.
The goal of this framework is to automate the game-play in an efficient and reliable manner, leveraging multiple approaches for handling dynamic elements in the game.

## Key Features

- **Multiple Execution Approaches**: The framework provides three distinct approaches for automating the Sequence Memory Game:
  1. **Basic Selenium Approach**: Utilizes waits and loops to interact with the page elements.
  2. **Complex Concurrent Logic**: Listens to multiple elements (9 in total) simultaneously using concurrent logic.
  3. **JavaScript Injection**: Injects JavaScript code to listen for changes in elements on the page.

- **Parallel Execution**: All three approaches can be executed in parallel or one-by-one, depending on the use case.

- **Customizable & Extendable**: The framework is built around the `SequenceMemoryExecutor` interface, with each execution approach having its own service class implementing this interface. This structure allows for easy extension and customization.

## Architecture

The main components of the framework are:

1. **`SequenceMemoryExecutor` Interface**: This is the core of the framework, defining the methods for executing the game-play. Each approach implements this interface.
   
2. **Approach Service Classes**: Each approach (Basic Selenium, Concurrent Logic, JavaScript Injection) has its own service class that implements the `SequenceMemoryExecutor` interface. These service classes handle different aspects of the game, such as capturing patterns, memorizing occurrences, and passing the data to the execution flow.

3. **Parallel Execution**: The framework supports running all approaches either sequentially or in parallel.

## Tools and Technologies

This framework utilizes the following tools and technologies:

- **Selenium WebDriver**: For automating browser interactions and capturing page elements.
- **TestNG**: For running and managing test cases.
- **Java Concurrency**: For managing concurrent execution of tasks and optimizing performance.
- **Java Streams**: For efficient data processing and functional-style programming.
- **JavaScriptExecutor**: For executing JavaScript code directly in the browser.
- **Other Java Utilities**: Various utility features from Java for handling common tasks.

## Installation

1. Clone this repository to your local machine:

   ```bash
   git clone https://github.com/yourusername/sequence-memory-game-automation.git
