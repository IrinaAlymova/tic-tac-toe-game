import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class TicTacToeGame {
    private static TicTacToeGame game;
    private char[][] gameField;
    private int fieldSize = 3;
    private boolean playingWithRobot;
    private String xPlayerName;
    private String oPlayerName;
    private String currentPlayerName;
    private char currentPlayerSign;
    private boolean gameOver;
    private boolean gameHasWinner;

    private TicTacToeGame() {
        gameField = new char[fieldSize][fieldSize];
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                gameField[i][j] = ' ';
            }
        }
        game = this;
    }

    public static void main(String[] args) {
        TicTacToeGame game = new TicTacToeGame();
        game.run();
    }

    private void run () {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Hey there! Let's play some TicTacToe!");
            System.out.println("Please say \"yes\" if you would like to play with robot or \"no\" if you'll play with a friend!");
            String answer = reader.readLine();
            if (answer.equalsIgnoreCase("yes")) {
                playingWithRobot = true;
                oPlayerName = "Robot";
            } else if (answer.equalsIgnoreCase("no")) {
                System.out.println("Good choice! He's not that smart anyway ッ");
            } else {
                System.out.println("I did not quite get you, but will assume you have someone to play with! ッ");
            }

            System.out.println("What is your name?");
            xPlayerName = reader.readLine();
            System.out.printf("Thank you, %s, your sign is \"X\".\n", xPlayerName);
            if (!playingWithRobot) {
                System.out.println("Who are you playing with?");
                oPlayerName = reader.readLine();
                System.out.printf("Perfect, %s has \"O\" sign.\n", oPlayerName);
            }

            System.out.println("All set, let's begin!");
            int randomInt = new Random().nextInt(2);
            switch (randomInt) {
                case 0: {
                    currentPlayerSign = 'X';
                    currentPlayerName = xPlayerName;
                    break;
                }
                case 1: {
                    currentPlayerSign = 'O';
                    currentPlayerName = oPlayerName;
                    if (playingWithRobot) {
                        robotMakesMove();
                    }
                    switchPlayer();
                }
            }
            printField();

            while (!gameOver) {
                if (currentPlayerSign == 'O' && playingWithRobot) {
                    robotMakesMove();
                } else {
                    System.out.printf("It's %s's turn! Please enter the cell number for your move.\n", currentPlayerName);
                    String line = reader.readLine();
                    int cellAddress = getValidatedValue(line);
                    if (cellAddress == -1) {
                        continue;
                    }
                    if (!putValueIntoField(cellAddress)) {
                        continue;
                    }
                    System.out.printf("Nice move, %s!\n", currentPlayerName);
                }

                printField();

                if (isGameOver()) {
                    gameOver = true;
                    if (gameHasWinner) {
                        System.out.printf("Wow, %s won! Thank you for the game!\n", currentPlayerName);
                    } else {
                        System.out.println("Oops! It's a draw! Good luck next time!");
                    }
                }

                switchPlayer();
            }
        } catch (IOException e) {
            System.out.println("Sorry, IOException occurred...");
        }
    }

    private void switchPlayer() {
        currentPlayerSign = (currentPlayerSign == 'O') ? 'X' : 'O';
        currentPlayerName = (currentPlayerSign == 'O') ? oPlayerName : xPlayerName;
    }

    private int getValidatedValue(String line) {
        int cellAddress;

        try {
            cellAddress = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.printf("Very funny, %s! That's not right!\n", currentPlayerName);
            return -1;
        }
        if (cellAddress > 9 || cellAddress < 1) {
            System.out.printf("It's not a valid cell address, %s!\n", currentPlayerName);
            return -1;
        }
        return cellAddress;
    }

    private boolean putValueIntoField(int value) {
        int cellsCount = 0;

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                cellsCount++;

                if (cellsCount == value) {
                    if (gameField[i][j] != ' ') {
                        System.out.printf("%s, this cell is already taken!\n", currentPlayerName);
                    } else {
                        gameField[i][j] = currentPlayerSign;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isGameOver() {
        char[] checkedLine = new char[fieldSize];

        //check horizontal lines
        for (int i = 0; i < fieldSize; i++) {
            checkedLine = Arrays.copyOf(gameField[i], fieldSize);
            if (isLineWinning(checkedLine)) {
                return true;
            }
        }

        //check vertical lines
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                checkedLine[j] = gameField[j][i];
            }
            if (isLineWinning(checkedLine)) {
                return true;
            }
        }

        //check first diagonal line
        for (int i = 0; i < fieldSize; i++) {
            checkedLine[i] = gameField[i][i];
        }
        if (isLineWinning(checkedLine)) {
            return true;
        }

        //check second diagonal line
        for (int i = 0; i < fieldSize; i++) {
            checkedLine[i] = gameField[fieldSize - i - 1][i];
        }
        if (isLineWinning(checkedLine)) {
            return true;
        }

        //check if no empty cells left
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (gameField[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isLineWinning(char[] line) {
        for (char value : line) {
            if (value != currentPlayerSign) {
                return false;
            }
        }
        gameHasWinner = true;
        return true;
    }

    private void printField() {
        String baldRed = "\033[1;34m";
        String baldBlue = "\033[1;31m";
        String darkGrey = "\033[90m";
        String reset = "\033[0m";
        int cellsCount = 0;

        for (int i = 0; i < fieldSize; i++) {
            System.out.printf("%s— — — — — — + — — — — — + — — — — — —\n%s", darkGrey, reset);
            System.out.printf("%s|           |           |           |\n%s", darkGrey, reset);
            for (int j = 0; j < fieldSize; j++) {
                cellsCount++;
                switch (gameField[i][j]) {
                    case ' ': {
                        System.out.printf("%s|     %d     %s", darkGrey, cellsCount, reset);
                        break;
                    }
                    case 'X': {
                        System.out.printf("%s|%s     %sX%s     ", darkGrey, reset, baldRed, reset);
                        break;
                    }
                    case 'O': {
                        System.out.printf("%s|%s     %sO%s     ", darkGrey, reset, baldBlue, reset);
                    }
                }
            }
            System.out.printf("%s|%s\n", darkGrey, reset);
            System.out.printf("%s|           |           |           |\n%s", darkGrey, reset);
        }
        System.out.printf("%s— — — — — — + — — — — — + — — — — — —\n%s", darkGrey, reset);
    }

    private void robotMakesMove() {
        System.out.println("It's robot's turn!");
        char[] checkedLine = new char[fieldSize];

        //check horizontal lines to save
        for (int i = 0; i < fieldSize; i++) {
            checkedLine = Arrays.copyOf(gameField[i], fieldSize);
            if (lineNeedsSave(checkedLine)) {
                for (int j = 0; j < fieldSize; j++) {
                    if (gameField[i][j] == ' ') {
                        gameField[i][j] = 'O';
                        return;
                    }
                }
            }
        }

        //check vertical lines to save
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                checkedLine[j] = gameField[j][i];
            }
            if (lineNeedsSave(checkedLine)) {
                for (int j = 0; j < fieldSize; j++) {
                    if (gameField[j][i] == ' ') {
                        gameField[j][i] = 'O';
                        return;
                    }
                }
            }
        }

        //check first diagonal line to save
        for (int i = 0; i < fieldSize; i++) {
            checkedLine[i] = gameField[i][i];
        }
        if (lineNeedsSave(checkedLine)) {
            for (int i = 0; i < fieldSize; i++) {
                if (gameField[i][i] == ' ') {
                    gameField[i][i] = 'O';
                    return;
                }
            }
        }

        //check second diagonal line to save
        for (int i = 0; i < fieldSize; i++) {
            checkedLine[i] = gameField[fieldSize - i - 1][i];
        }
        if (lineNeedsSave(checkedLine)) {
            for (int i = 0; i < fieldSize; i++) {
                if (gameField[fieldSize - i - 1][i] == ' ') {
                    gameField[fieldSize - i - 1][i] = 'O';
                    return;
                }
            }
        }

        //check if valuable cells are empty to get advantage
        if (gameField[1][1] == ' ') {
            putValueIntoField(5);
            return;
        }

        //randomly assign value
        int emptyCellsCount = 0;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (gameField[i][j] == ' ') {
                    emptyCellsCount++;
                }
            }
        }
        int randomNumber = new Random().nextInt(emptyCellsCount);
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                if (gameField[i][j] == ' ') {
                    if (randomNumber == 0) {
                        gameField[i][j] = 'O';
                        return;
                    }
                    randomNumber--;
                }
            }
        }
    }

    private boolean lineNeedsSave(char[] line) {
        int emptyCellsCount = 0;
        int enemyCellsCount = 0;
        for (char value : line) {
            if (value == ' ') {
                emptyCellsCount++;
            }
            if (value == 'X') {
                enemyCellsCount++;
            }
        }
        if (emptyCellsCount == 1 && enemyCellsCount == 2) {
            return true;
        }
        return false;
    }

}