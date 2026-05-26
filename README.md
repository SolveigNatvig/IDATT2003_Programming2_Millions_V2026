# Mappevurdering project IDATT2003

FIRST STUDENT NAME = "Solveig Natvig" 
FIRST STUDENT ID = "10125"
SECOND STUDENT NAME = "Elisabeth Berg" 
SECOND STUDENT ID = "10089"

## Project description
This project is a GUI-based game program written in Java, "Stock Trading Game". As the name implies, the game is about stock trading, and the goal is to gain the highest net worth through stocks. The player writes their name, starting money and chooses a CSV-file with different stocks to start the game. Throughout the game the player can see their own status, the best and worst stocks, and their portfolio. The player can then buy and sell shares that change value every week randomly. 

## Project structure
This project uses standard Maven-structure. 
The main project is located in src\main\java.
Tests are located in src\test\java.

## Link to repository
https://git.ntnu.no/solvena/Programmering2_mappe_v26

## How to run the project
Open the project in VSCode or other IDE.
You run the project via Maven by typing "mvn javafx:run" in the terminal. 
A starting game scene will pop up, there you will type chosen name, starting amount and choose a CSV-file for stocks with the format "ticker,company,price".

You can exit the program by clicking "exit" then "exit game".

## How to run the tests
To run the tests, you can either:
- Use Maven: run mvn test in the project directory.
- Run tests directly from your IDE (e.g., right-click the test class and select "Run").

The test results will be displayed in the console, showing whether tests passed or failed.

## References
Wireframes: 
https://www.figma.com/proto/hij5HpFYSpCNvGAZTeBcZo/Stock-Trading-Game?node-id=0-1&t=FXjhcz0tGCcKHnMm-1