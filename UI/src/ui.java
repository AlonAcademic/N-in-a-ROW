import java.util.Scanner;

/**
 * Created by user on 30/07/2018.
 */

public class ui
{
    String xmlPath;
    Menu firstMenu;
    Menu mainMenu;
    Menu gameMenu;
    PrintMessages winningMessage;
    PrintMessages endGame;

    public static Scanner scanner = new Scanner(System.in);

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
        do {
            if (!Engine.load(this.xmlPath)) {
                System.out.println("Sorry to interrupt but the configuration file is invalid");
                checkAndPrintWhyXMLInvalid();
                System.out.println("Please provide another XML or type exit if you wish to exit");
                xmlPath = scanner.nextLine();
                if (xmlPath.toLowerCase().equals("exit")) {System.out.println("ByeBye"); System.exit(0); }
            }
        } while (!Engine.configurationLoaded());
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
                    playersType[i] = PlayersTypes.ROBOT;
                    robotsCounter++;
                } else {
                    playersType[i] = PlayersTypes.HUMAN;
                }
            }

            onceAgain = robotsCounter == playersAmount;
            if (onceAgain) { System.out.println("You chose all players to be robots and that is not legal. Please choose again"); }
        }

        for (int i = 0; i < playersAmount; i++) { Engine.setPlayerType(i, playersType[i]); }
    }
}
