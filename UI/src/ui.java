import java.util.Scanner;

import common.PlayersTypes;
import engine.GameLogic;
import engine.Game;

/**
 * Created by user on 30/07/2018.
 */

public class ui
{
    private String xmlPath;
    private Menu firstMenu;
    private Menu mainMenu;
    private Menu gameMenu;
    private PrintMessages winningMessage;
    private PrintMessages endGame;
    private int playerAmmount = 2; //TODO: must be changed???
    private GameLogic gameLogic = new Game();

    public static Scanner scanner = new Scanner(System.in);
    public static final char[] playerDiscs = {'@', '#', '$', '%', '&', '+', '~'};

    public ui(String xmlPath)
    {
        this.xmlPath = xmlPath;
        this.firstMenu = new FirstMenu();
        this.mainMenu = new PrimaryMenu();
        this.gameMenu = new GameMenu();
        this.winningMessage = new WinnerMessage();
        this.endGame = new EndGameMessage();

        prepareGame();
    }

    private void prepareGame()
    {
        MenuChoice choice = MenuChoice.INVALIDCHOICE;
        firstMenu.showMenu();
        loadFirstXML();
        handleUserChoicePrimaryMenu(mainMenu.showMenu());
    }

    private void loadFirstXML()
    {
        boolean configurationLoaded = false;

        while (!configurationLoaded) {
            try {
                gameLogic.load(this.xmlPath);
                configurationLoaded = true;
            } catch (Exception e) {
                System.out.println("Sorry to interrupt but the configuration file is invalid:\n");
                System.out.println(e);
                System.out.println("Please provide another XML or type exit if you wish to exit");
                xmlPath = scanner.nextLine();
                if (xmlPath.toLowerCase().equals("exit")) {System.out.println("ByeBye"); System.exit(0); }
            }
        }
        /*
        do {
            if (!gameLogic.load(this.xmlPath)) {
                System.out.println("Sorry to interrupt but the configuration file is invalid");
                checkAndPrintWhyXMLInvalid();
                System.out.println("Please provide another XML or type exit if you wish to exit");
                xmlPath = scanner.nextLine();
                if (xmlPath.toLowerCase().equals("exit")) {System.out.println("ByeBye"); System.exit(0); }
            }
        } while (!gameLogic.configurationLoaded());
        */
    }

    private void handleUserChoicePrimaryMenu(MenuChoice userChoice)
    {
        switch (userChoice){
            case LOADXML:
                String path = scanner.nextLine();
                Engine.load(path) ? System.out.println("New configuration loaded successfully") :
                                    System.out.println("Bad XML was loaded.\nLoaded last legal configuration");
                break;

            case STARTGAME:
                startGame();
                break;

            case EXIT:
                System.out.println("Bye Bye :(");
                System.exit(0);
                break;
        }
    }

    private void startGame()
    {
        choosePlayersType();
        playGame();
    }

    private void playGame()
    {
        while ((false == Engine.isHasWinner()) && (false == Engine.ifFull())) {
            //TODO: printBoard
            handleUserChoiceGameMenu(gameMenu.showMenu());
        }

        Engine.isHasWinner ? winningMessage.printMessage(Engine.getWinner()) : endGame.printMessage(0);

        //TODO: if Engine.isHasWinner we need to start a new game? show main menu? reset everything?
    }

    private void handleUserChoiceGameMenu(MenuChoice userChoice)
    {
        switch (userChoice){
            case GAMESTATS: showGameStats(); break;
            case MAKETURN: playTurn(); break;
            case HISTORY: showTurnsHistory(); break;
            case EXIT: System.out.println("Bye Bye"); System.exit(0);
        }
    }

    private void showGameStats()
    {
        int playerNumber = 0;

        playerNumber = Engine.getPlayerNumber();

        System.out.println("========== Game statistics ==========");
        System.out.println("=== Current Player ==");
        System.out.println("Current player: " + Engine.getPlayerNumber());

        System.out.println("=== Players discs ===");
        for (int i = 0; i < this.playerAmmount; i++) {
            System.out.println(String.format("Player: %d Disc: %c", i+1, playerDiscs[i]));
        }

        System.out.println("=== Player turns ===");
        for (int i = 0; i < this.playerAmmount; i++) {
            System.out.println(String.format("Player: %d Turns: %d", i+1, Engine.playerTurns(i)));
        }

        System.out.println("=== Elapsed time ===");
        System.out.println(String.format("Elapsed time: " + Engine.timeFromBegining));
    }

    private void playTurn()
    {
        int col = 0;

        while(true) {
            //if (Engine.playerType == PlayerTypes.HUMAN) { col = getUserInputCol(); }
            if (gameLogic.getTypeOfCurrentPlayer() == PlayersTypes.HUMAN) { col = getUserInputCol(); }

            if (!Engine.play(col)) {
                System.out.println("Illigal move. please try again.");
                col = getUserInputCol();
            }
            else { break; }
        }
    }

    private int getUserInputCol()
    {
        int input = 0;

        while (true) {
            try {
                System.out.println("Please enter the col you wish to drop a disc: ");
                input = Integer.parseInt(ui.scanner.nextLine());

                if (input > Engine.getCols() + 1 || input < 1) {
                    System.out.println("Out of bound col. Please try again. ");
                    continue;
                }
                else { break; }

            } catch (NumberFormatException nfe) {
                System.out.println("Not a number. Try again: ");
            }
        }

        return input;
    }

    private void choosePlayersType()
    {
        int playersAmount = Engine.getNumberOfPlayers();
        int robotsCounter = 0;
        PlayersTypes[] playersType = new PlayersTypes[playersAmount];
        String userChoice;
        boolean onceAgain = true;

        System.out.println("Before the game begins, please enter the types of the players! :)");
        System.out.println("Please type h for human player and r for robotic player ");
        while (onceAgain) {
            for (int i = 0; i < playersAmount; i++) {
                System.out.print("Please type your choice for player " + i + ":  ");
                userChoice = scanner.nextLine();
                System.out.println();

                while (!(userChoice.toLowerCase().equals("r") || userChoice.toLowerCase().equals("h"))) {
                    System.out.print("Oops, bad choice. Please try again:  ");
                    userChoice = scanner.nextLine();
                    System.out.println();
                }

                if (userChoice.toLowerCase().equals("r")) {
                    gameLogic.initPlayer(PlayersTypes.ROBOT, i, "Computer");
                    //playersType[i] = PlayersTypes.ROBOT;
                    robotsCounter++;
                } else {
                    System.out.print("Please type player's name: ");
                    String name = scanner.nextLine();
                    gameLogic.initPlayer(PlayersTypes.HUMAN, i, name);
                    //playersType[i] = PlayersTypes.HUMAN;
                }
            }

            onceAgain = robotsCounter == playersAmount;
            if (onceAgain) { System.out.println("You chose all players to be robots and that is not legal. Please choose again"); }
        }

        for (int i = 0; i < playersAmount; i++) { Engine.setPlayerType(i, playersType[i]); }
    }
}
