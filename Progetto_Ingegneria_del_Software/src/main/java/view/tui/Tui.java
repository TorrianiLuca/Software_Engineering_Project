package view.tui;

import controller.ClientController;
import enumerations.*;
import enumerations.Color;
import model.card.Card;
import model.card.CardPile;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.card.cardsType.MeteorSwarm;
import model.card.cardsType.Pirates;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.shipBoard.ShipBoard;
import network.messages.Message;
import observer.ViewObservable;
import support.Quadruple;
import view.View;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Text interface (TUI) for the game.
 */
public class Tui extends ViewObservable implements View {
    private static final String STR_INPUT_CANCELED = "User input canceled.";
    private boolean isSocket;
    private final PrintStream out;
    private FlightType flightType;
    private Card cardInUse;
    private int numCards;
    private int numLines = 0;
    private Runnable recoverAction = () -> {}; // used to "save" the previous state

    private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Message> serverMessageQueue = new LinkedBlockingQueue<>();

    private final Object outputLock = new Object();
    private final Object stateLock = new Object();

    private State state;
    private boolean showTiles = true; // flag used to show the tiles or not
    private boolean showTimer = true; // flag used to show the timer
    private int count = 0;
    private int count1 = 0;
    private int countCards = 0;
    private int remainingPositions = 2;
    private int time = 0;
    private int sum;
    int countOut=0;
    int countErr=0;

    private boolean cannonFireSmall = false;

    String defaultAddress = "localhost";
    String defaultSocketPort = "12345";
    String defaultRMIPort = "1099";
    String clientId;
    String nickname;
    HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games;
    ComponentTile componentTile;
    CardPile cardPile;
    int num =0;

    public enum State {
        WAIT,
        SERVER_ADDRESS,
        SERVER_PORT,
        NICKNAME,
        MAX_PLAYERS,
        FLIGHT_TYPE,
        CREATE,
        JOIN,
        PICK_UP,
        PUT_TILE,
        PUT_TILE_RESERVE,
        SHOW_SHIPS,
        FIX,
        SHOW_PILE,
        WAIT_BUILD,
        LEADER,
        NOT_LEADER,
        IN_TURN,
        NOT_IN_TURN,
        PLANETS,
        SMUGGLERS,
        SLAVERS,
        PIRATES,
        ABANDONED_STATION,
        ABANDONED_SHIP,
        OPEN_SPACE,
        METEOR_SWARM,
        METEOR_SWARM_SMALL,
        METEOR_SWARM_LARGE,
        ROLL_DICE,
        WAIT_ROLL_DICE,
        GAIN_GOOD,
        PROCEED,
        NOT_PROCEED,
        PROCEED_PHASE,
        WAIT_TURN,
        ACTIVATE_CANNON,
        ACTIVATE_ENGINE,
        ACTIVATE_SHIELD,
        REMOVE_BATTERY,
        REMOVE_GOOD,
        REMOVE_FIGURE,
        WAIT_METEOR,
        REPAIR,
        WAIT_REPAIR,
        PHASE2_LEVEL1,
        PHASE3_LEVEL1,
        PHASE1_LEVEL2,
        PHASE2_LEVEL2,
        CANNON_FIRE_SMALL,
        CANNON_FIRE_SMALL_PIRATES,
        PUT_FIGURES,
        WAIT_FIGURE,
        WAIT_POPULATE,
        WAIT_FOREVER,
        SHOW_SHIPS_FOREVER,
        DISCONNECTED,
    }

    /**
     * Constructor for Tui
     */
    public Tui(boolean isSocket) {
        this.isSocket = isSocket;
        this.out = System.out;
        synchronized (stateLock) {
            this.state = State.WAIT;
        }

        Thread inputThread = new Thread(this::handleUserInput);
        inputThread.setDaemon(true);
        inputThread.start();

        Thread inputProcessor = new Thread(this::processUserInput);
        inputProcessor.setDaemon(true);
        inputProcessor.start();

        Thread serverMessageProcessor = new Thread(this::processServerMessages);
        serverMessageProcessor.setDaemon(true);
        serverMessageProcessor.start();
    }

    /**
     * This method continuously reads user input from the terminal and adds it to the input queue.
     */
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            inputQueue.offer(input);
        }

    }

    /**
     * Method used to process the user input
     */
    private void processUserInput() {
        HashMap<String, String> serverInfo = new HashMap<>();
        String num;
        int numOfPlayers = 0;
        int numFlightType = 0;
        boolean validInput;

        while (true) {
            try {
                String choice = inputQueue.take().toLowerCase();
                synchronized (stateLock) {
                    synchronized (outputLock) {
                        if(state == State.SERVER_ADDRESS) {
                            inputQueue.clear();
                            if(choice.equals("")){
                                serverInfo.put("address", defaultAddress);
                                this.askServerPort();
                            } else if (ClientController.isAddressValid(choice)) {
                                serverInfo.put("address", choice);
                                this.askServerPort();
                            } else {
                                out.println("Invalid input!");
                                this.askServerAddress();
                            }
                        }
                        else if(state == State.SERVER_PORT) {
                            inputQueue.clear();
                            if(choice.equals("")){
                                if(isSocket) {
                                    serverInfo.put("port", defaultSocketPort);
                                } else {
                                    serverInfo.put("port", defaultRMIPort);
                                }
                                notifyObserver(viewObserver -> viewObserver.onUpdateServerInfo(serverInfo));
                            } else if (ClientController.isPortValid(choice)) {
                                serverInfo.put("port", choice);
                                notifyObserver(viewObserver -> viewObserver.onUpdateServerInfo(serverInfo));
                            } else {
                                out.println("Invalid input!");
                                this.askServerPort();
                            }
                        }
                        else if(state == State.NICKNAME) {
                            inputQueue.clear();
                            if(choice.equals("Controller") || choice.equals("Model") || choice.equals("Client") || choice.equals("Server") || choice.equals("")) {
                                out.println("The nickname \"" + choice + "\" is not permitted.");
                                this.askNickname(clientId);
                            }  else {
                                this.nickname = choice;
                                notifyObserver(viewObserver -> viewObserver.onUpdateNickname(choice));
                            }
                        }
                        else if(state == State.MAX_PLAYERS) {
                            try {
                                inputQueue.clear();
                                numOfPlayers = Integer.parseInt(choice);
                                if (numOfPlayers >= 2 && numOfPlayers <= 4) {
                                    this.askFlightType();
                                } else {
                                    out.println("The number is not within the correct bounds. It must be 2 <= players <= 4");
                                    this.askMaxPlayerAndFlightType();
                                }
                            } catch (NumberFormatException e) {
                                    out.println(e.getMessage());
                                this.askMaxPlayerAndFlightType();
                            }
                        }
                        else if(state == State.FLIGHT_TYPE) {
                            inputQueue.clear();
                            try {
                                numFlightType = Integer.parseInt(choice);
                                if (numFlightType == 1 || numFlightType == 2) {
                                    int finalNumOfPlayers = numOfPlayers;
                                    if (numFlightType == 1) {
                                        notifyObserver(obs -> obs.createGame());
                                        notifyObserver(viewObserver -> viewObserver.onUpdateMaxPlayerAndFlightType(finalNumOfPlayers, FlightType.FIRST_FLIGHT));
                                    } else {
                                        notifyObserver(obs -> obs.createGame());
                                        notifyObserver(viewObserver -> viewObserver.onUpdateMaxPlayerAndFlightType(finalNumOfPlayers, FlightType.STANDARD_FLIGHT));
                                    }
                                } else {
                                    out.println("The number is not correct. It must be 1 or 2");
                                    this.askFlightType();
                                }
                            } catch (NumberFormatException e) {
                                out.println(e.getMessage());
                                this.askFlightType();
                            }
                        }
                        else if(state == State.CREATE) {
                            inputQueue.clear();
                            if (choice.equals("1")) {
                                this.askMaxPlayerAndFlightType();
                            } else if (choice.equals("2")) {
                                this.joinGame();
                            } else if (choice.equals("3")) {
                                notifyObserver(obs -> obs.refreshGameOnServer());
                            }
                            else {
                                out.println("Invalid input!");
                                this.showAvailableGames(games);
                            }
                        }
                        else if(state==State.PUT_FIGURES)
                        {
                            if(flightType==FlightType.FIRST_FLIGHT)
                            {
                                inputQueue.clear();
                                if(choice.trim().isEmpty()) {
                                    System.out.print("\033[1A");
                                    System.out.print("\033[2K");
                                    System.out.print("\r");
                                }
                                else if(!choice.startsWith("put_astronaut_") && !choice.equals("finish")){
                                    countErr=0;
                                    if(countOut==1)
                                    {
                                        numLines++;
                                        numLines++;
                                    }
                                    numLines++;
                                    clearLines();;
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    countOut=1;
                                    this.showInstructPutFigureFirst();
                                }
                                else if(choice.toLowerCase().equals("finish"))
                                {
                                    countOut=0;
                                    countErr=0;
                                    this.state = State.WAIT_FIGURE;
                                    this.clearLines();
                                    notifyObserver(obs -> obs.onUpdateFinishedPopulate());
                                }
                                else{
                                    String withoutPrefix = choice.substring("put_astronaut_".length());

                                    String[] parts = withoutPrefix.split("_");

                                    if(parts.length == 2){
                                        int numRow;
                                        try {
                                            numRow = Integer.parseInt(parts[0]);
                                            if(numRow < 5 || numRow > 9) {
                                                if(countOut==1)
                                                {
                                                    numLines++;
                                                    numLines++;
                                                    numLines++;
                                                }
                                                countErr=0;
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                countOut=1;
                                                this.showInstructPutFigureFirst();
                                            }
                                            else {
                                                int numCol;
                                                try {
                                                    numCol = Integer.parseInt(parts[1]);
                                                    if(numCol < 4 || numCol > 10) {
                                                        if(countOut==1)
                                                        {
                                                            numLines++;
                                                            numLines++;
                                                            numLines++;
                                                        }
                                                        countErr=0;
                                                        this.clearLines();
                                                        System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                        numLines++;
                                                        numLines++;
                                                        countOut=1;
                                                        this.showInstructPutFigureFirst();
                                                    }
                                                    else{
                                                        notifyObserver(obs -> obs.onUpdatePutAstronautInShip(numRow, numCol));
                                                    }
                                                } catch (NumberFormatException e) {
                                                    if(countOut==1)
                                                    {
                                                        numLines++;
                                                        numLines++;
                                                        numLines++;
                                                    }
                                                    countErr=0;
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    countOut=1;
                                                    this.showInstructPutFigureFirst();
                                                }
                                            }
                                        } catch (NumberFormatException e) {
                                            if(countOut==1)
                                            {
                                                numLines++;
                                                numLines++;
                                                numLines++;
                                            }
                                            countErr=0;
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            countOut=1;
                                            this.showInstructPutFigureFirst();
                                        }
                                    }
                                    else {
                                        if(countOut==1)
                                        {
                                            numLines++;
                                            numLines++;
                                            numLines++;
                                        }
                                        countErr=0;
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again! "+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        countOut=1;
                                        this.showInstructPutFigureFirst();
                                    }
                                }
                            }
                            else
                            {
                                inputQueue.clear();
                                if(choice.trim().isEmpty()) {
                                    System.out.print("\033[1A");
                                    System.out.print("\033[2K");
                                    System.out.print("\r");
                                }
                                else if(!choice.startsWith("put_astronaut_") && !choice.equals("finish") && !choice.startsWith("put_purple_") && !choice.startsWith("put_brown_")){
                                    if(countOut==1)
                                    {
                                        numLines++;
                                        numLines++;
                                    }
                                    countErr=0;
                                    numLines++;
                                    clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again! "+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    countOut=1;
                                    this.showInstructPutFigureStandard();
                                }
                                else if(choice.toLowerCase().equals("finish"))
                                {
                                    countOut=0;
                                    countErr=0;
                                    this.state = State.WAIT_FIGURE;
                                    this.clearLines();
                                    notifyObserver(obs -> obs.onUpdateFinishedPopulate());
                                }
                                else if(choice.toLowerCase().startsWith("put_purple_"))
                                {
                                    String withoutPrefix = choice.substring("put_purple_".length());

                                    String[] parts = withoutPrefix.split("_");
                                    numLines++;

                                    if(parts.length == 2){
                                        int numRow;
                                        try {
                                            numRow = Integer.parseInt(parts[0]);
                                            if(numRow < 5 || numRow > 9) {
                                                if(countOut==1)
                                                {
                                                    numLines++;
                                                    numLines++;
                                                }
                                                countErr=0;
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                countOut=1;
                                                this.showInstructPutFigureStandard();
                                            }
                                            else {
                                                int numCol;
                                                try {
                                                    numCol = Integer.parseInt(parts[1]);
                                                    if(numCol < 4 || numCol > 10) {
                                                        if(countOut==1)
                                                        {
                                                            numLines++;
                                                            numLines++;
                                                        }
                                                        countErr=0;
                                                        this.clearLines();
                                                        System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                        numLines++;
                                                        numLines++;
                                                        countOut=1;
                                                        this.showInstructPutFigureStandard();
                                                    }
                                                    else{
                                                        notifyObserver(obs -> obs.onUpdatePutPurpleInShip(numRow, numCol));
                                                    }
                                                } catch (NumberFormatException e) {
                                                    if(countOut==1)
                                                    {
                                                        numLines++;
                                                        numLines++;
                                                    }
                                                    countErr=0;
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    countOut=1;
                                                    this.showInstructPutFigureStandard();
                                                }
                                            }
                                        } catch (NumberFormatException e) {
                                            if(countOut==1)
                                            {
                                                numLines++;
                                                numLines++;
                                            }
                                            this.clearLines();
                                            countErr=0;
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            countOut=1;
                                            this.showInstructPutFigureStandard();
                                        }
                                    }
                                    else {
                                        if(countOut==1)
                                        {
                                            numLines++;
                                            numLines++;
                                        }
                                        countErr=0;
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again! "+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        countOut=1;
                                        this.showInstructPutFigureStandard();
                                    }
                                }//End if purple
                                else if(choice.toLowerCase().startsWith("put_brown_"))
                                {
                                    String withoutPrefix = choice.substring("put_brown_".length());

                                    String[] parts = withoutPrefix.split("_");
                                    numLines++;

                                    if(parts.length == 2){
                                        int numRow;
                                        try {
                                            numRow = Integer.parseInt(parts[0]);
                                            if(numRow < 5 || numRow > 9) {
                                                if(countOut==1)
                                                {
                                                    numLines++;
                                                    numLines++;
                                                }
                                                countErr=0;
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                countOut=1;
                                                this.showInstructPutFigureStandard();
                                            }
                                            else {
                                                int numCol;
                                                try {
                                                    numCol = Integer.parseInt(parts[1]);
                                                    if(numCol < 4 || numCol > 10) {
                                                        if(countOut==1)
                                                        {
                                                            numLines++;
                                                            numLines++;
                                                        }
                                                        countErr=0;
                                                        this.clearLines();
                                                        System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                        numLines++;
                                                        numLines++;
                                                        countOut=1;
                                                        this.showInstructPutFigureStandard();
                                                    }
                                                    else{
                                                        notifyObserver(obs -> obs.onUpdatePutBrownInShip(numRow, numCol));
                                                    }
                                                } catch (NumberFormatException e) {
                                                    if(countOut==1)
                                                    {
                                                        numLines++;
                                                        numLines++;
                                                    }
                                                    countErr=0;
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    countOut=1;
                                                    this.showInstructPutFigureStandard();
                                                }
                                            }
                                        } catch (NumberFormatException e) {
                                            if(countOut==1)
                                            {
                                                numLines++;
                                                numLines++;
                                            }
                                            countErr=0;
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            countOut=1;
                                            this.showInstructPutFigureStandard();
                                        }
                                    }
                                    else {
                                        if(countOut==1)
                                        {
                                            numLines++;
                                            numLines++;
                                        }
                                        countErr=0;
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again! "+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        countOut=1;
                                        this.showInstructPutFigureStandard();
                                    }
                                }//End if brown
                                else{
                                    String withoutPrefix = choice.substring("put_astronaut_".length());

                                    String[] parts = withoutPrefix.split("_");
                                    numLines++;

                                    if(parts.length == 2){
                                        int numRow;
                                        try {
                                            numRow = Integer.parseInt(parts[0]);
                                            if(numRow < 5 || numRow > 9) {
                                                if(countOut==1)
                                                {
                                                    numLines++;
                                                    numLines++;
                                                }
                                                countErr=0;
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                countOut=1;
                                                this.showInstructPutFigureStandard();
                                            }
                                            else {
                                                int numCol;
                                                try {
                                                    numCol = Integer.parseInt(parts[1]);
                                                    if(numCol < 4 || numCol > 10) {
                                                        if(countOut==1)
                                                        {
                                                            numLines++;
                                                            numLines++;
                                                        }
                                                        countErr=0;
                                                        this.clearLines();
                                                        System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                        numLines++;
                                                        numLines++;
                                                        countOut=1;
                                                        this.showInstructPutFigureStandard();
                                                    }
                                                    else{
                                                        notifyObserver(obs -> obs.onUpdatePutAstronautInShip(numRow, numCol));
                                                    }

                                                } catch (NumberFormatException e) {
                                                    if(countOut==1)
                                                    {
                                                        numLines++;
                                                        numLines++;
                                                    }
                                                    countErr=0;
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    countOut=1;
                                                    this.showInstructPutFigureStandard();
                                                }
                                            }
                                        } catch (NumberFormatException e) {
                                            if(countOut==1)
                                            {
                                                numLines++;
                                                numLines++;
                                            }
                                            countErr=0;
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            countOut=1;
                                            this.showInstructPutFigureStandard();
                                        }
                                    }
                                    else {
                                        if(countOut==1)
                                        {
                                            numLines++;
                                            numLines++;
                                        }
                                        countErr=0;
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        countOut=1;
                                        this.showInstructPutFigureStandard();
                                    }
                                }
                            }

                        }
                        else if(state == State.JOIN) {
                            inputQueue.clear();
                            notifyObserver(obs -> obs.joinGame(choice));
                        }
                        else if(state == State.PICK_UP) {
                            if(flightType == FlightType.FIRST_FLIGHT) {
                                inputQueue.clear();
                                if(choice.trim().isEmpty()) {
                                    System.out.print("\033[1A");
                                    System.out.print("\033[2K");
                                    System.out.print("\r");
                                }
                                else if(!choice.startsWith("pick_tile_") && !choice.startsWith("show_ships") && !choice.equals("finish_build")){
                                    numLines++;
                                    clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPickUpFirst();
                                }
                                else if(choice.toLowerCase().equals("show_ships")) {
                                    this.state = State.SHOW_SHIPS;
                                    this.clearLines();
                                    showTiles = false;
                                    notifyObserver(obs -> obs.onUpdateShowShips());
                                }
                                else if(choice.toLowerCase().equals("finish_build")) {
                                    this.state = State.WAIT_BUILD;
                                    this.showTimer = false;
                                    this.clearLines();
                                    showTiles = false;
                                    notifyObserver(obs -> obs.onUpdateFinishedBuild());
                                }
                                else if(choice.toLowerCase().startsWith("pick_tile_")){

                                    numLines++;
                                    String withoutPrefix = choice.substring("pick_tile_".length());
                                    String[] parts = withoutPrefix.split("_");

                                    if(parts.length == 1){
                                        int number;
                                        try {
                                            number = Integer.parseInt(parts[0]);
                                            if(number < 0 || number > 139) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID number. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                showInstrucPickUpFirst();
                                            }
                                            else {
                                                showTiles = false;
                                                this.clearLines();
                                                notifyObserver(obs -> obs.onUpdatePickTile(number));
                                            }

                                        } catch (NumberFormatException e) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPickUpFirst();
                                        }
                                    }
                                    else {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPickUpFirst();
                                    }

                                }
                                else {
                                    numLines++;
                                    this.clearLines();
                                    System.out.println(Font.RED + "\nInvalid choice!" + Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPickUpFirst();
                                }
                            }
                            else if(flightType == FlightType.STANDARD_FLIGHT) {
                                inputQueue.clear();
                                if(choice.trim().isEmpty()) {
                                    System.out.print("\033[1A");
                                    System.out.print("\033[2K");
                                    System.out.print("\r");
                                }
                                else if(!choice.startsWith("pick_tile_") && !choice.startsWith("turn_timer") && !choice.equals("finish_build") && !choice.startsWith("show_ships") && !choice.startsWith("pick_pile_")){
                                    numLines++;
                                    clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPickUpStandard();
                                }
                                else if(this.remainingPositions == 2 && !choice.toLowerCase().equals("turn_timer")) {
                                    numLines++;
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nYou have to turn the timer to begin building your ship. Turn the timer!!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    showInstrucPickUpStandard();
                                }
                                else if(choice.toLowerCase().equals("show_ships")) {
                                    this.state = State.SHOW_SHIPS;
                                    this.clearLines();
                                    showTiles = false;
                                    notifyObserver(obs -> obs.onUpdateShowShips());
                                }
                                else if(choice.toLowerCase().equals("finish_build")) {
                                    this.state = State.WAIT_BUILD;
                                    this.showTimer = true;
                                    this.clearLines();
                                    showTiles = false;
                                    notifyObserver(obs -> obs.onUpdateFinishedBuild());
                                }
                                else if(choice.toLowerCase().equals("turn_timer")) {
                                    numLines++;
                                    this.clearLines();
                                    notifyObserver(obs -> obs.onUpdateTimerMessage());
                                    this.showInstrucPickUpStandard();
                                }
                                else if(choice.toLowerCase().startsWith("pick_tile_")){
                                    numLines++;
                                    String withoutPrefix = choice.substring("pick_tile_".length());
                                    String[] parts = withoutPrefix.split("_");

                                    if(parts.length == 1){
                                        int number;
                                        try {
                                            number = Integer.parseInt(parts[0]);
                                            if(number < 0 || number > 151) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID number. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                showInstrucPickUpStandard();
                                            }
                                            else {
                                                showTiles = false;
                                                this.clearLines();
                                                notifyObserver(obs -> obs.onUpdatePickTile(number));
                                            }
                                        } catch (NumberFormatException e) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPickUpStandard();
                                        }
                                    }
                                    else if(parts.length == 2){
                                        int numRow;
                                        try {
                                            numRow = Integer.parseInt(parts[0]);
                                            if(numRow != 5) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nYou can't pick up a tile from this space. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPickUpStandard();
                                            }
                                            else {
                                                int numCol;
                                                try {
                                                    numCol = Integer.parseInt(parts[1]);
                                                    if(numCol < 9 || numCol > 10) {
                                                        this.clearLines();
                                                        System.out.println(Font.RED+"\nYou can't pick up a tile from this space. Try again!"+Font.RESET);
                                                        numLines++;
                                                        numLines++;
                                                        this.showInstrucPickUpStandard();
                                                    }
                                                    else {
                                                        this.clearLines();
                                                        showTiles = false;
                                                        this.state = State.PUT_TILE_RESERVE;
                                                        notifyObserver(obs -> obs.onUpdatePickTileFromShip(numRow, numCol));
                                                    }
                                                } catch (NumberFormatException e) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucPickUpStandard();
                                                }
                                            }
                                        } catch (NumberFormatException e) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPickUpStandard();
                                        }
                                    }
                                    else {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPickUpStandard();
                                    }
                                }
                                else if(choice.toLowerCase().startsWith("pick_pile_")) {
                                    numLines++;
                                    String withoutPrefix = choice.substring("pick_pile_".length());
                                    String[] parts = withoutPrefix.split("_");

                                    if(parts.length == 1){
                                        int number;
                                        try {
                                            number = Integer.parseInt(parts[0]);
                                            if(number < 1 || number > 3) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID number. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                showInstrucPickUpStandard();
                                            }
                                            else {
                                                showTiles = false;
                                                this.clearLines();
                                                notifyObserver(obs -> obs.onUpdatePickCardPile(number));
                                            }
                                        } catch (NumberFormatException e) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPickUpStandard();
                                        }
                                    }
                                    else {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPickUpStandard();
                                    }
                                }
                                else {
                                    numLines++;
                                    this.clearLines();
                                    System.out.println(Font.RED + "\nInvalid choice!" + Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPickUpStandard();
                                }
                            }
                        }
                        else if(state == State.PUT_TILE) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.startsWith("put_ship_") && !choice.startsWith("put_back") && !choice.startsWith("set_")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                showInstrucPutTileBackFirst(componentTile);
                            }
                            else if(choice.toLowerCase().equals("put_back")){
                                numLines++;
                                count = 0;
                                showTiles = true;
                                notifyObserver(obs -> obs.onUpdatePutTileInDeck());
                            }
                            else if(choice.startsWith("set_")){
                                numLines++;
                                String withoutPrefix = choice.substring("set_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 1){
                                    String direction = parts[0];
                                    if(!(direction.toLowerCase().equals("nord")) && !(direction.toLowerCase().equals("est")) && !(direction.toLowerCase().equals("sud")) && !(direction.toLowerCase().equals("ovest"))) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid direction. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPutTileBackFirst(componentTile);
                                    }
                                    else {
                                        notifyObserver(obs -> obs.onUpdateSetDirection(direction));
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPutTileBackFirst(componentTile);
                                }
                            }
                            else {
                                String withoutPrefix = choice.substring("put_ship_".length());

                                String[] parts = withoutPrefix.split("_");
                                numLines++;

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPutTileBackFirst(componentTile);
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    showInstrucPutTileBackFirst(componentTile);
                                                }
                                                else {
                                                    count = 0;
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdatePutTileInShip(numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPutTileBackFirst(componentTile);
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPutTileBackFirst(componentTile);
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPutTileBackFirst(componentTile);
                                }
                            }
                        }
                        else if(state == State.PUT_TILE_RESERVE) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.startsWith("put_ship_") && !choice.startsWith("set_")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                showInstrucPutTileBackReserve(componentTile);
                            }
                            else if(choice.startsWith("set_")){
                                numLines++;
                                String withoutPrefix = choice.substring("set_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 1){
                                    String direction = parts[0];
                                    if(!(direction.toLowerCase().equals("nord")) && !(direction.toLowerCase().equals("est")) && !(direction.toLowerCase().equals("sud")) && !(direction.toLowerCase().equals("ovest"))) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid direction. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPutTileBackReserve(componentTile);
                                    }
                                    else {
                                        notifyObserver(obs -> obs.onUpdateSetDirection(direction));
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPutTileBackReserve(componentTile);
                                }
                            }
                            else {
                                String withoutPrefix = choice.substring("put_ship_".length());

                                String[] parts = withoutPrefix.split("_");
                                numLines++;

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPutTileBackReserve(componentTile);
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    showInstrucPutTileBackReserve(componentTile);
                                                }
                                                else {
                                                    this.state = State.PICK_UP;
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdatePutTileInShip(numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPutTileBackReserve(componentTile);
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPutTileBackReserve(componentTile);
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPutTileBackReserve(componentTile);
                                }
                            }
                        }
                        else if(state == State.SHOW_SHIPS) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.equals("stop")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                showInstrucStopWatchingShip();
                            }
                            else {
                                numLines++;
                                count = 0;
                                count1 = 0;
                                showTiles = true;
                                notifyObserver(obs -> obs.onUpdateStopWatchingShips(cardInUse));
                            }
                        }
                        else if(state == State.SHOW_SHIPS_FOREVER) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.equals("stop")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                showInstrucStopWatchingShip2();
                            }
                            else {
                                numLines++;
                                count = 0;
                                count1 = 0;
                                showTiles = true;
                                state = State.WAIT_FOREVER;
                                notifyObserver(obs -> obs.onUpdateStopWatchingShips(cardInUse));
                            }
                        }
                        else if(state == State.SHOW_PILE) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.equals("stop")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                showInstrucStopWatchingCardPile(cardPile);
                            }
                            else {
                                numLines++;
                                count = 0;
                                count1 = 0;
                                showTiles = true;
                                notifyObserver(obs -> obs.onUpdateStopWatchingCardPile());
                            }
                        }
                        else if(state==State.WAIT){
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                        }
                        else if(state==State.WAIT_POPULATE){
                            System.out.print("\033[1A");
                            System.out.print("\033[2K");
                            System.out.print("\r");
                        }
                        else if(state==State.WAIT_BUILD){
                            System.out.print("\033[1A");
                            System.out.print("\033[2K");
                            System.out.print("\r");
                        }
                        else if(state==State.WAIT_FIGURE){
                            System.out.print("\033[1A");
                            System.out.print("\033[2K");
                            System.out.print("\r");
                        }
                        else if(state == State.FIX) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("remove_tile_") && !choice.equals("finish_build")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucRemoveTiles();
                            } else if (choice.toLowerCase().equals("finish_build")) {
                                this.state = State.WAIT_BUILD;
                                clearLines();


                                out.print("\033[s");
                                if(!showTimer) {
                                    int num1 = 0;
                                    for(int i = 0; i<30; i++){
                                        out.printf("\033[%d;%dH", 54+num1, 100);
                                        out.flush();
                                        out.print("\033[K");
                                        out.flush();
                                        num1++;
                                    }
                                }
                                out.print("\033[u");



                                showTiles = false;
                                notifyObserver(obs -> obs.onUpdateFinishedBuild());
                            } else if (choice.toLowerCase().startsWith("remove_tile_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("remove_tile_".length());
                                String[] parts = withoutPrefix.split("_");

                                if (parts.length == 2) {
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if (numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED + "\nInvalid space on ship board. Try again!" + Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucRemoveTiles();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if (numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED + "\nInvalid space on ship board. Try again!" + Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucRemoveTiles();
                                                }
                                                else {
                                                    this.clearLines();
                                                    showTiles = false;
                                                    notifyObserver(obs -> obs.onUpdateRemoveTile(numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucRemoveTiles();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucRemoveTiles();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucRemoveTiles();
                                }
                            }
                        }
                        else if (state == State.LEADER) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if (!choice.toLowerCase().equals("pick_card")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPickUpCard();
                            }
                            else {
                                numLines++;
                                this.clearLines();
                                notifyObserver(obs -> obs.onUpdatePickCard());
                            }

                        }
                        else if (state == State.NOT_LEADER) {
                            System.out.print("\033[1A");
                            System.out.print("\r");
                        }
                        else if (state == State.NOT_PROCEED) {
                            System.out.print("\033[1A");
                            System.out.print("\r");
                        }
                        else if (state == State.PLANETS) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("land_") && !choice.startsWith("skip")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPlanets();
                            } else if (choice.toLowerCase().equals("skip")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "skip", -1));
                            } else if (choice.toLowerCase().startsWith("land_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("land_".length());
                                String[] parts = withoutPrefix.split("_");

                                if (parts.length == 1) {
                                    int numPlanet;
                                    try {
                                        numPlanet = Integer.parseInt(parts[0]);
                                        if (numPlanet < 1 || numPlanet > 4) {
                                            this.clearLines();
                                            System.out.println(Font.RED + "\nInvalid planet ID. Try again!" + Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPlanets();
                                        }
                                        else {
                                            this.clearLines();
                                            notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", numPlanet));
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPlanets();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPlanets();
                                }
                            }
                        }
                        else if (state == State.SMUGGLERS) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_cannon_") && !choice.equals("fight")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucSmugglers();
                            } else if (choice.toLowerCase().equals("fight")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateSmugglersChoice(cardInUse));
                            } else if (choice.toLowerCase().startsWith("activate_cannon_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_cannon_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucSmugglers();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucSmugglers();
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucSmugglers();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucSmugglers();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucSmugglers();
                                }
                            }
                        }
                        else if (state == State.SLAVERS) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_cannon_") && !choice.equals("fight")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucSlavers();
                            } else if (choice.toLowerCase().equals("fight")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateSlaversChoice(cardInUse));
                            } else if (choice.toLowerCase().startsWith("activate_cannon_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_cannon_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucSlavers();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucSlavers();
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucSlavers();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucSlavers();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucSlavers();
                                }
                            }
                        }
                        else if (state == State.PIRATES) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_cannon_") && !choice.equals("fight")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPirates();
                            } else if (choice.toLowerCase().equals("fight")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdatePiratesChoice(cardInUse));
                            } else if (choice.toLowerCase().startsWith("activate_cannon_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_cannon_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPirates();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucPirates();
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPirates();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPirates();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPirates();
                                }
                            }
                        }
                        else if (state == State.ABANDONED_STATION) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.equals("dock") && !choice.equals("skip")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucAbandonedStation();
                            } else if (choice.toLowerCase().equals("dock")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateAbandonedStationChoice(cardInUse, "dock"));
                            } else if (choice.toLowerCase().startsWith("skip")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateAbandonedStationChoice(cardInUse, "skip"));
                            }
                        }
                        else if (state == State.ABANDONED_SHIP) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.equals("dock") && !choice.equals("skip")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucAbandonedShip();
                            } else if (choice.toLowerCase().equals("dock")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateAbandonedShipChoice(cardInUse, "dock"));
                            } else if (choice.toLowerCase().startsWith("skip")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateAbandonedShipChoice(cardInUse, "skip"));
                            }
                        }
                        else if (state == State.OPEN_SPACE) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_engine_") && !choice.startsWith("move")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucOpenSpace();
                            } else if (choice.toLowerCase().equals("move")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateOpenSpaceChoice(cardInUse));
                            } else if (choice.toLowerCase().startsWith("activate_engine_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_engine_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucOpenSpace();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucOpenSpace();
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateEngine(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucOpenSpace();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucOpenSpace();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucOpenSpace();
                                }
                            }
                        }
                        else if (state == State.PHASE2_LEVEL1) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_engine_") && !choice.startsWith("show_ships") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPhase2Level1();
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdatePhase2Choice(cardInUse));

                            }
                            else if(choice.toLowerCase().equals("show_ships")) {
                                this.state = State.SHOW_SHIPS;
                                this.clearLines();
                                notifyObserver(obs -> obs.onUpdateShowShips());
                            }
                            else if (choice.toLowerCase().startsWith("activate_engine_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_engine_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPhase2Level1();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucPhase2Level1();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateActivateEngine(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPhase2Level1();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPhase2Level1();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPhase2Level1();
                                }
                            }
                        }
                        else if (state == State.PHASE3_LEVEL1) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_cannon_") && !choice.startsWith("show_ships") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPhase3Level1();
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdatePhase3Choice(cardInUse));

                            }
                            else if(choice.toLowerCase().equals("show_ships")) {
                                this.state = State.SHOW_SHIPS;
                                this.clearLines();
                                notifyObserver(obs -> obs.onUpdateShowShips());
                            }
                            else if (choice.toLowerCase().startsWith("activate_cannon_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_cannon_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPhase3Level1();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucPhase3Level1();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPhase3Level1();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPhase3Level1();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPhase3Level1();
                                }
                            }
                        }
                        else if (state == State.PHASE1_LEVEL2) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_cannon_") && !choice.startsWith("show_ships") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPhase1Level2();
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdatePhase1Choice(cardInUse));

                            }
                            else if(choice.toLowerCase().equals("show_ships")) {
                                this.state = State.SHOW_SHIPS;
                                this.clearLines();
                                notifyObserver(obs -> obs.onUpdateShowShips());
                            }
                            else if (choice.toLowerCase().startsWith("activate_cannon_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_cannon_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPhase1Level2();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucPhase1Level2();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPhase1Level2();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPhase1Level2();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPhase1Level2();
                                }
                            }
                        }
                        else if (state == State.PHASE2_LEVEL2) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_engine_") && !choice.startsWith("show_ships") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucPhase2Level2();
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdatePhase2Choice(cardInUse));

                            }
                            else if(choice.toLowerCase().equals("show_ships")) {
                                this.state = State.SHOW_SHIPS;
                                this.clearLines();
                                notifyObserver(obs -> obs.onUpdateShowShips());
                            }
                            else if (choice.toLowerCase().startsWith("activate_engine_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_engine_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucPhase2Level2();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucPhase2Level2();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateActivateEngine(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucPhase2Level2();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucPhase2Level2();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucPhase2Level2();
                                }
                            }
                        }
                        else if (state == State.GAIN_GOOD) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.startsWith("put_") && !choice.startsWith("skip")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucGainGoodBlock();
                            }
                            else if(choice.toLowerCase().equals("skip")){
                                numLines++;
                                notifyObserver(obs -> obs.onUpdateGainGood(cardInUse, "skip", -1, -1));
                            }
                            else if(choice.startsWith("put_")) {
                                String withoutPrefix = choice.substring("put_".length());

                                String[] parts = withoutPrefix.split("_");
                                numLines++;

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucGainGoodBlock();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucGainGoodBlock();
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateGainGood(cardInUse, "put", numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucGainGoodBlock();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucGainGoodBlock();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucGainGoodBlock();
                                }
                            }
                        }
                        else if (state == State.NOT_IN_TURN) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(choice.toLowerCase().equals("show_ships")) {
                                this.state = State.WAIT_TURN;
                                this.clearLines();
                                showTiles = false;
                                notifyObserver(obs -> obs.onUpdateShowShips());
                            }
                            else {
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                this.showWait(cardInUse);
                            }
                        }
                        else if (state == State.WAIT_FOREVER) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(choice.toLowerCase().equals("show_ships")) {
                                this.state = State.SHOW_SHIPS_FOREVER;
                                this.clearLines();
                                showTiles = false;
                                notifyObserver(obs -> obs.onUpdateShowShips());
                            }
                            else {
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                this.showWait2(cardInUse);
                            }
                        }
                        else if (state == State.WAIT_METEOR) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                        }
                        else if(state == State.PROCEED) {
                            if(this.flightType == FlightType.FIRST_FLIGHT) {
                                if(choice.trim().isEmpty()) {
                                    System.out.print("\033[1A");
                                    System.out.print("\033[2K");
                                    System.out.print("\r");
                                }
                                else if(!choice.equals("proceed")){
                                    numLines++;
                                    clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    showInstrucProceed();
                                }
                                else {
                                    numLines++;
                                    count = 0;
                                    count1 = 0;
                                    showTiles = true;
                                    notifyObserver(obs -> obs.onUpdateProceed());
                                }
                            }
                            else {
                                if(choice.trim().isEmpty()) {
                                    System.out.print("\033[1A");
                                    System.out.print("\033[2K");
                                    System.out.print("\r");
                                }
                                else if(!choice.equals("proceed") && !choice.equals("retire")) {
                                    numLines++;
                                    clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    showInstrucProceed();
                                }
                                else if(choice.equals("proceed")) {
                                    numLines++;
                                    count = 0;
                                    count1 = 0;
                                    showTiles = true;
                                    notifyObserver(obs -> obs.onUpdateProceed());
                                }
                                else if(choice.equals("retire")) {
                                    numLines++;
                                    count = 0;
                                    count1 = 0;
                                    showTiles = true;
                                    this.state = State.WAIT_FOREVER;
                                    notifyObserver(obs -> obs.onUpdateRetire());
                                }
                            }

                        }
                        else if(state == State.PROCEED_PHASE) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.equals("next_phase")){
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                showInstrucNextPhase();
                            }
                            else {
                                numLines++;
                                count = 0;
                                count1 = 0;
                                showTiles = true;
                                notifyObserver(obs -> obs.onUpdateNextPhase(cardInUse));
                            }
                        }
                        else if (state == State.REMOVE_BATTERY) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(choice.startsWith("remove_battery_")) {
                                String withoutPrefix = choice.substring("remove_battery_".length());

                                String[] parts = withoutPrefix.split("_");
                                numLines++;

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucRemoveBattery();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucRemoveBattery();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateRemoveBattery(cardInUse, numRow, numCol, sum));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucRemoveBattery();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucRemoveBattery();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucRemoveBattery();
                                }
                            }
                            else {
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucRemoveBattery();
                            }
                        }
                        else if (state == State.REMOVE_GOOD) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(choice.startsWith("remove_good_")) {
                                String withoutPrefix = choice.substring("remove_good_".length());

                                String[] parts = withoutPrefix.split("_");
                                numLines++;

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucRemoveGood();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucRemoveGood();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateRemoveGood(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucRemoveGood();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucRemoveGood();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucRemoveGood();
                                }
                            }
                            else {
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucRemoveGood();
                            }
                        }
                        else if (state == State.REMOVE_FIGURE) {
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(choice.startsWith("remove_figure_")) {
                                String withoutPrefix = choice.substring("remove_figure_".length());

                                String[] parts = withoutPrefix.split("_");
                                numLines++;

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucRemoveFigure();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucRemoveFigure();
                                                }
                                                else {
                                                    notifyObserver(obs -> obs.onUpdateRemoveFigure(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucRemoveFigure();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucRemoveFigure();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucRemoveFigure();
                                }
                            }
                            else {
                                numLines++;
                                clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucRemoveFigure();
                            }
                        }
                        else if(state==State.ROLL_DICE){
                            if(choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            }
                            else if(!choice.toLowerCase().equals("roll_dice")){
                                clearLines();
                                System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                numLines++;
                                numLines++;
                                numLines++;
                                this.showInstrucRollDice();
                            }
                            else {
                                count = 0;
                                count1 = 0;
                                showTiles = true;
                                notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                            }
                        }
                        else if(state==State.WAIT_ROLL_DICE){
                            System.out.print("\033[1A");
                            System.out.print("\033[2K");
                            System.out.print("\r");
                        }
                        else if (state == State.METEOR_SWARM_SMALL) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_shield_") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucMeteorSwarmSmall(sum);
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateMeteorSwarmChoice(cardInUse, sum));
                            } else if (choice.toLowerCase().startsWith("activate_shield_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_shield_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucMeteorSwarmSmall(sum);
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucMeteorSwarmSmall(sum);
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateShield(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucMeteorSwarmSmall(sum);
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucMeteorSwarmSmall(sum);
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucMeteorSwarmSmall(sum);
                                }
                            }
                        }
                        else if (state == State.METEOR_SWARM_LARGE) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_cannon_") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucMeteorSwarmLarge(sum);
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateMeteorSwarmChoice(cardInUse, sum));
                            } else if (choice.toLowerCase().startsWith("activate_cannon_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_cannon_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucMeteorSwarmLarge(sum);
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucMeteorSwarmLarge(sum);
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucMeteorSwarmLarge(sum);
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucMeteorSwarmLarge(sum);
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucMeteorSwarmLarge(sum);
                                }
                            }
                        }
                        else if (state == State.CANNON_FIRE_SMALL) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_shield_") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucCannonFireSmall(sum);
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateCombatZoneChoice(cardInUse, sum));
                            } else if (choice.toLowerCase().startsWith("activate_shield_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_shield_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucCannonFireSmall(sum);                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucCannonFireSmall(sum);
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateShield(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucCannonFireSmall(sum);                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucCannonFireSmall(sum);                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucCannonFireSmall(sum);                                }
                            }
                        }
                        else if (state == State.CANNON_FIRE_SMALL_PIRATES) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("activate_shield_") && !choice.startsWith("proceed")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucCannonFireSmallPirates(sum);
                            } else if (choice.toLowerCase().equals("proceed")) {
                                numLines++;
                                clearLines();
                                notifyObserver(obs -> obs.onUpdateDefeatedPiratesChoice(cardInUse, sum));
                            } else if (choice.toLowerCase().startsWith("activate_shield_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("activate_shield_".length());
                                String[] parts = withoutPrefix.split("_");

                                if(parts.length == 2){
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if(numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucCannonFireSmallPirates(sum);                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if(numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED+"\nInvalid space on ship board. Try again!"+Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucCannonFireSmallPirates(sum);
                                                }
                                                else {
                                                    showTiles = true;
                                                    notifyObserver(obs -> obs.onUpdateActivateShield(cardInUse, numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucCannonFireSmallPirates(sum);                                          }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED+"\nInvalid ID. Try again!"+Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucCannonFireSmallPirates(sum);                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucCannonFireSmallPirates(sum);                                }
                            }
                        }
                        else if(state == State.REPAIR) {
                            if (choice.trim().isEmpty()) {
                                System.out.print("\033[1A");
                                System.out.print("\033[2K");
                                System.out.print("\r");
                            } else if (!choice.startsWith("remove_tile_") && !choice.startsWith("repaired")) {
                                numLines++;
                                this.clearLines();
                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                numLines++;
                                numLines++;
                                this.showInstrucRepairShip();
                            } else if (choice.toLowerCase().equals("repaired")) {
                                this.state = State.WAIT;
                                clearLines();


                                out.print("\033[s");
                                if(!showTimer) {
                                    int num1 = 0;
                                    for(int i = 0; i<30; i++){
                                        out.printf("\033[%d;%dH", 54+num1, 100);
                                        out.flush();
                                        out.print("\033[K");
                                        out.flush();
                                        num1++;
                                    }
                                }
                                out.print("\033[u");



                                showTiles = false;
                                notifyObserver(obs -> obs.onUpdateRepaired(cardInUse));
                            } else if (choice.toLowerCase().startsWith("remove_tile_")) {
                                numLines++;
                                String withoutPrefix = choice.substring("remove_tile_".length());
                                String[] parts = withoutPrefix.split("_");

                                if (parts.length == 2) {
                                    int numRow;
                                    try {
                                        numRow = Integer.parseInt(parts[0]);
                                        if (numRow < 5 || numRow > 9) {
                                            this.clearLines();
                                            System.out.println(Font.RED + "\nInvalid space on ship board. Try again!" + Font.RESET);
                                            numLines++;
                                            numLines++;
                                            this.showInstrucRepairShip();
                                        }
                                        else {
                                            int numCol;
                                            try {
                                                numCol = Integer.parseInt(parts[1]);
                                                if (numCol < 4 || numCol > 10) {
                                                    this.clearLines();
                                                    System.out.println(Font.RED + "\nInvalid space on ship board. Try again!" + Font.RESET);
                                                    numLines++;
                                                    numLines++;
                                                    this.showInstrucRepairShip();
                                                }
                                                else {
                                                    this.clearLines();
                                                    showTiles = false;
                                                    notifyObserver(obs -> obs.onUpdateRemoveTile(numRow, numCol));
                                                }
                                            } catch (NumberFormatException e) {
                                                this.clearLines();
                                                System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                                numLines++;
                                                numLines++;
                                                this.showInstrucRepairShip();
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        this.clearLines();
                                        System.out.println(Font.RED + "\nInvalid choice. Try again!" + Font.RESET);
                                        numLines++;
                                        numLines++;
                                        this.showInstrucRepairShip();
                                    }
                                }
                                else {
                                    this.clearLines();
                                    System.out.println(Font.RED+"\nInvalid choice. Try again!"+Font.RESET);
                                    numLines++;
                                    numLines++;
                                    this.showInstrucRepairShip();
                                }
                            }
                        }
                        else if (state == State.WAIT_REPAIR) {
                            System.out.print("\033[1A");
                            System.out.print("\033[2K");
                            System.out.print("\r");
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void addServerMessage(Message message) {
        serverMessageQueue.offer(message);
    }

    /**
     * Thread used to process the messages that are in the queue.
     */
    private void processServerMessages() {
        while (true) {
            try {
                Message message = serverMessageQueue.take();
                synchronized (stateLock) {
                    synchronized (outputLock) {
                        message.updateClient(this);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Method that initializes the tui.
     */
    public void initTui(){
        synchronized (outputLock) {
            out.println(
                    "  ______             __                                      ________                              __                           \n" +
                            " /      \\           |  \\                                    |        \\                            |  \\                          \n" +
                            "|  $$$$$$\\  ______  | $$  ______   __    __  __    __        \\$$$$$$$$______   __    __   _______ | $$   __   ______    ______  \n" +
                            "| $$ __\\$$ |      \\ | $$ |      \\ |  \\  /  \\|  \\  |  \\         | $$  /      \\ |  \\  |  \\ /       \\| $$  /  \\ /      \\  /      \\ \n" +
                            "| $$|    \\  \\$$$$$$\\| $$  \\$$$$$$\\ \\$$\\/  $$| $$  | $$         | $$ |  $$$$$$\\| $$  | $$|  $$$$$$$| $$_/  $$|  $$$$$$\\|  $$$$$$\\\n" +
                            "| $$ \\$$$$ /      $$| $$ /      $$  >$$  $$ | $$  | $$         | $$ | $$   \\$$| $$  | $$| $$      | $$   $$ | $$    $$| $$   \\$$\n" +
                            "| $$__| $$|  $$$$$$$| $$|  $$$$$$$ /  $$$$\\ | $$__/ $$         | $$ | $$      | $$__/ $$| $$_____ | $$$$$$\\ | $$$$$$$$| $$      \n" +
                            " \\$$    $$ \\$$    $$| $$ \\$$    $$|  $$ \\$$\\ \\$$    $$         | $$ | $$       \\$$    $$ \\$$     \\| $$  \\$$\\ \\$$     \\| $$      \n" +
                            "  \\$$$$$$   \\$$$$$$$ \\$$  \\$$$$$$$ \\$$   \\$$ _\\$$$$$$$          \\$$  \\$$        \\$$$$$$   \\$$$$$$$ \\$$   \\$$  \\$$$$$$$ \\$$      \n" +
                            "                                            |  \\__| $$                                                                          \n" +
                            "                                             \\$$    $$                                                                          \n" +
                            "                                              \\$$$$$$                                                                           \n" +
                            "                                                                                                                                \n" +
                            "                                                                                                                                ");
        }
        try{
            askServerInfo();
        } catch (ExecutionException e){
            synchronized (outputLock) {
                out.println(STR_INPUT_CANCELED);
            }
        }
    }

    /**
     * This method asks the user to insert the server connection information.
     */
    private void askServerInfo() throws ExecutionException {
        synchronized (outputLock) {
            out.println("Please enter the connection settings.");
        }
        this.askServerAddress();
    }

    /**
     * This method asks the user the server IP address.
     */
    private void askServerAddress() {
        synchronized (stateLock) {
            this.state = State.SERVER_ADDRESS;
        }
        synchronized (outputLock) {
            out.print("Enter the server address (default: " + defaultAddress + "):");
        }
    }

    /**
     * This method asks the user the server port.
     */
    private void askServerPort() {
        synchronized (stateLock) {
            this.state = State.SERVER_PORT;
        }
        synchronized (outputLock) {
            if(isSocket){
                out.print("Enter the server port (default: " + defaultSocketPort + "):");
            } else {
                out.print("Enter the server port (default: " + defaultRMIPort + "):");
            }
        }
    }

    @Override
    public void askNickname(String clientId) {
        synchronized (stateLock) {
            this.state = State.NICKNAME;
        }
        recoverAction = () -> askNickname(clientId);
        this.clientId = clientId;
        synchronized (outputLock) {
            out.print("Enter your nickname: ");
        }
    }

    @Override
    public void showAvailableGames(HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games) {
        recoverAction = () -> showAvailableGames(games);
        this.games = games;
        synchronized (stateLock) {
            this.state = State.WAIT;
        }
        synchronized (outputLock) {
            if (games == null) {
                out.println("There is no active game right now.");
            } else {
                out.println(Font.BOLD + "\nActive Games\tFlight Type\t\tConneted Players\tPlayers" + Font.RESET);
                for (String game : games.keySet()) {
                    out.print(game + "\t\t" + games.get(game).getFirst() + "\t\t" + games.get(game).getSecond() + "/" + games.get(game).getThird() + "\t\t\t");
                    ArrayList<String> tempNicknames = (ArrayList<String>) games.get(game).getFourth();
                    int i = 0;
                    for(String nickname : tempNicknames){
                        if(i!=0) {
                            out.print(", " + nickname);
                        }
                        else{
                            out.print(nickname);
                        }
                        i++;
                    }
                    out.print("\n");
                }

            }
            out.print("\n");
        }
        this.createOrJoinGame();
    }

    /**
     * This method asks the player to create or join a game.
     */
    public void createOrJoinGame() {
        synchronized (stateLock) {
            this.state = State.CREATE;
        }
        synchronized (outputLock) {
            out.println("\nGame Menu:");
            out.println("1. Create a new game");
            out.println("2. Join a game");
            out.println("3. Refresh the games on server");
            out.print("Select an option: ");
        }
    }

    /**
     * This method asks the player the game id.
     */
    public void joinGame() {
        synchronized (stateLock) {
            this.state = State.JOIN;
        }
        synchronized (outputLock) {
            out.print("Inserisci l'ID della partita: ");
        }
    }

    /**
     * This method asks the player the number of players for the game.
     */
    @Override
    public void askMaxPlayerAndFlightType() {
        synchronized (stateLock) {
            this.state = State.MAX_PLAYERS;
        }
        recoverAction = this::askMaxPlayerAndFlightType;

        synchronized (outputLock) {
            out.print("How many players are going to play?\n" +
                    "Minimum: 2\n" +
                    "Maximum: 4\n");
        }
    }

    /**
     * This method asks the player the flight type for the game.
     */
    public void askFlightType() {
        synchronized (stateLock) {
            this.state = State.FLIGHT_TYPE;
        }
        synchronized (outputLock) {
            out.print("Which flight type do you want to select?\n" +
                    "1: First Flight\n" +
                    "2: Standard Flight\n");
        }
    }

    @Override
    public void showGenericError(String error) {
        synchronized (outputLock) {
            if(error.endsWith("already contains a component")) {
                showTiles = false;
            }

            if(state==State.PUT_FIGURES)
            {
                if(countOut==1 && flightType==FlightType.FIRST_FLIGHT)
                {
                    numLines++;
                    numLines++;
                    numLines++;
                    countOut=0;
                }
                else if(countOut==1 && flightType==FlightType.STANDARD_FLIGHT)
                {
                    numLines++;
                    numLines++;
                    countOut=0;
                }
                this.clearLines();
                out.print("\n");
                out.println(Font.RED+"Error: " + error+Font.RESET);
                numLines++;
                numLines++;
                countErr=1;
                recoverAction.run();

            }
            else {
                this.clearLines();
                out.print("\n");
                out.println(Font.RED+"Error: " + error+Font.RESET);
                numLines++;
                numLines++;
                recoverAction.run();
            }
        }
    }

    @Override
    public void showShipErrors(ArrayList<String> errors) {
        synchronized (outputLock) {
            if(cardInUse == null) {
                int num = 0;

                for(int i = 0; i<20; i++){
                    out.printf("\033[%d;%dH", 54+num, 100);
                    out.flush();
                    out.print("\033[K");
                    out.flush();
                    num++;
                }

                num = 0;

                for(String error : errors) {
                    System.out.printf("\033[%d;%dH", 54+num, 100);
                    System.out.flush();
                    System.out.print("\033[K");
                    System.out.flush();
                    System.out.printf("\033[%d;%dH", 54+num, 100);
                    System.out.flush();
                    out.print(Font.RED + "Error: " + error+Font.RESET);
                    num++;
                }

                for(int i=0; i < 20; i++) {
                    out.printf("\033[%d;%dH",80+i, 0);
                    out.flush();
                    System.out.print("\033[K");
                    System.out.flush();
                }
                out.print("\033[80;0H");
                out.flush();

                numLines=0;
                this.showInstrucRemoveTiles();
            }
            else {
                clearLines();
                int num = 0;

                for(int i = 0; i<50; i++){
                    out.printf("\033[%d;%dH", 45+num, 100);
                    out.flush();
                    out.print("\033[K");
                    out.flush();
                    num++;
                }

                num = 0;

                for(String error : errors) {
                    System.out.printf("\033[%d;%dH", 54+num, 100);
                    System.out.flush();
                    System.out.print("\033[K");
                    System.out.flush();
                    System.out.printf("\033[%d;%dH", 54+num, 100);
                    System.out.flush();
                    out.print(Font.RED + "Error: " + error+Font.RESET);
                    num++;
                }

                for(int i=0; i < 20; i++) {
                    out.printf("\033[%d;%dH",80+i, 0);
                    out.flush();
                    System.out.print("\033[K");
                    System.out.flush();
                }
                out.print("\033[80;0H");
                out.flush();

                numLines=0;
                this.showInstrucRepairShip();
            }
        }
    }

    @Override
    public void showGenericMessage(String message) {
        synchronized (stateLock) {
            synchronized (outputLock) {
                if(message.equals("Waiting for other players to finish building their ships.")) {
                    clearLines();
                    this.state = State.WAIT_BUILD;
                }
                if(message.equals("Waiting for other players to finish repairing their ships.")) {
                    clearLines();
                    this.state = State.WAIT_BUILD;
                }
                if(message.equals("Your ship board is correct. You have to wait the other players to finish correcting their ship.")) {
                    clearLines();
                    out.print("\033[82;0H");
                    out.flush();
                }
                if(message.startsWith("The dice rolled returned")) {
                    clearLines();
                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH", 20+i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }
                    out.print("\033[20;0H");
                    out.flush();
                }
                if(message.equals("\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players...")) {
                    numLines=0;
                    clearLines();
                    out.print("\033[80;0H");
                    out.flush();
                }
                if(message.equals("You are not in the flight anymore. Watch the other player!!")) {
                    clearLines();
                    for(int i=0; i < 10; i++) {
                        out.printf("\033[%d;%dH", 80+i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }
                    out.print("\033[82;0H");
                    out.flush();
                }
                out.println(message);
                numLines++;
            }
        }

    }

    /**
     * This method prints the board.
     * @param shipBoard is the ship board of the player passed by the controller.
     */
    @Override
    public void showShipBoard(ShipBoard shipBoard){
        synchronized (stateLock) {
            if(state == State.FIX) {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[80;0H");
                    out.flush();

                    numLines = 0;
                    this.showInstrucRemoveTiles();
                }
            }
            else if(state==State.PUT_FIGURES) {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[80;0H");
                    out.flush();

                    countOut=0;
                    countErr=0;
                    if(flightType==FlightType.FIRST_FLIGHT)
                    {
                        numLines = 0;
                        this.showInstructPutFigureFirst();
                    }
                    else
                    {
                        numLines = 0;
                        this.showInstructPutFigureStandard();
                    }
                }
            }
            else if(state == State.WAIT_POPULATE) {
                synchronized (outputLock) {
                    for(int i=0; i < 35; i++) {
                        out.printf("\033[%d;%dH",45+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }

                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[80;0H");
                    out.flush();

                    if(flightType==FlightType.FIRST_FLIGHT)
                    {
                        numLines = 0;
                        this.showInstructPutFigureFirst();
                    }
                    else {
                        numLines = 0;
                        this.showInstructPutFigureStandard();
                    }
                }
            }
            else if(state == State.WAIT) {
                synchronized (outputLock) {
                    for(int i=0; i < 35; i++) {
                        out.printf("\033[%d;%dH",45+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }

                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[80;0H");
                    out.flush();

                }
            }
            else if(state == State.WAIT_BUILD) {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[80;0H");
                    out.flush();
                }
            }
            else if(state == State.GAIN_GOOD || state == State.SMUGGLERS || state == State.SLAVERS || state == State.PIRATES || state == State.ABANDONED_SHIP || state == State.ABANDONED_STATION || state == State.REMOVE_BATTERY || state == State.REMOVE_GOOD || state == State.REMOVE_FIGURE || (state == State.WAIT_METEOR && cardInUse.getCardType() != CardName.PIRATES) || state == State.PHASE1_LEVEL2 || state == State.PHASE2_LEVEL2 || state == State.PROCEED_PHASE || state == State.NOT_PROCEED || state == State.ROLL_DICE || state == State.OPEN_SPACE) {
                synchronized (outputLock) {
                    numLines = 0;
                    out.print("\033[s");
                    out.flush();

                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }

                    out.print("\033[u");
                    out.flush();
                }
            }
            else if(state == State.METEOR_SWARM_SMALL || state == State.METEOR_SWARM_LARGE || (state == State.SHOW_SHIPS && cardInUse != null) || state == State.NOT_IN_TURN || state == State.CANNON_FIRE_SMALL || state == State.CANNON_FIRE_SMALL_PIRATES) {
                synchronized (outputLock) {

                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }

                    out.print("\033[20;0H");
                    out.flush();
                }
            }
            else if(state == State.WAIT_METEOR && cardInUse.getCardType() == CardName.PIRATES) {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[22;0H");
                    out.flush();
                }
            }
            else if(state == State.REPAIR) {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    for(int i=0; i < 20; i++) {
                        out.printf("\033[%d;%dH",80+i, 0);
                        out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                    }
                    out.print("\033[80;0H");
                    out.flush();

                    numLines = 0;
                    this.showInstrucRepairShip();
                }
            }
            else if(state == State.PUT_TILE_RESERVE) {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    System.out.print("\033[48;100H");
                    System.out.flush();
                    out.print(Font.BOLD + "TIMER:  " + time + Font.RESET + " seconds remaining");
                    System.out.print("\033[49;100H");
                    System.out.flush();
                    out.print("Remaining Positions:  " + this.remainingPositions);

                    System.out.print("\033[54;100H");
                    System.out.flush();
                    out.print(Font.BOLD + "These are the cards piles:" + Font.RESET);
                    for (int i = 0; i < 17; i++) {
                        System.out.printf("\033[%d;%dH", 55 + i, 100);
                        System.out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                        System.out.printf("\033[%d;%dH", 55 + i, 100);
                        System.out.flush();
                        if (i == 0) {
                            out.print("╭─────────────────────╮╮╮     ╭─────────────────────╮╮╮     ╭─────────────────────╮╮╮");
                        } else if (i == 15) {
                            out.print("╰─────────────────────╯╯╯     ╰─────────────────────╯╯╯     ╰─────────────────────╯╯╯");
                        } else if (i == 16) {
                            out.print("           1                             2                              3");
                        } else {
                            out.print("│                     │││     │                     │││     │                     │││");
                        }
                    }
                    System.out.print("\033[u");
                    System.out.flush();
                }
            }
            else {
                synchronized (outputLock) {
                    out.print("\033[45;0H");
                    out.flush();
                    out.println(Font.BOLD + "\n\nThis is your ship board:" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawShipBoard(shipBoard);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.componentInShipBoard(shipBoard);

                    if (flightType == FlightType.FIRST_FLIGHT) {
                        numLines = 0;
                        this.showInstrucPickUpFirst();
                    } else {
                        System.out.print("\033[48;100H");
                        System.out.flush();
                        out.print(Font.BOLD + "TIMER:  " + time + Font.RESET + " seconds remaining");
                        System.out.print("\033[49;100H");
                        System.out.flush();
                        out.print("Remaining Positions:  " + this.remainingPositions);

                        System.out.print("\033[54;100H");
                        System.out.flush();
                        out.print(Font.BOLD + "These are the cards piles:" + Font.RESET );
                        for(int i=0; i < 17; i++) {
                            System.out.printf("\033[%d;%dH",55+i,100);
                            System.out.flush();
                            System.out.print("\033[K");
                            System.out.flush();
                            System.out.printf("\033[%d;%dH",55+i,100);
                            System.out.flush();
                            if(i == 0) {
                                out.print("╭─────────────────────╮╮╮     ╭─────────────────────╮╮╮     ╭─────────────────────╮╮╮");
                            }
                            else if(i == 15) {
                                out.print("╰─────────────────────╯╯╯     ╰─────────────────────╯╯╯     ╰─────────────────────╯╯╯");
                            }
                            else if(i == 16) {
                                out.print("           1                             2                              3");
                            }
                            else {
                                out.print("│                     │││     │                     │││     │                     │││");
                            }
                        }
                        System.out.print("\033[u");
                        System.out.flush();

                        numLines = 0;
                        this.showInstrucPickUpStandard();
                    }
                }
            }
        }
    }

    @Override
    public void showTiles(GameTile gameTile) {
        synchronized (outputLock) {
            if(count == 0) {
                if(showTiles){
                    this.clearPage();
                    this.flightType = gameTile.getFlightType();
                    System.out.println(Font.BOLD + "\nThese are the tiles:" + Font.RESET);
                    num++;
                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawTilesDeck(gameTile);
                    for(String line : lines){
                        System.out.println(line);
                    }
                }
            }
            else {
                if(showTiles){
                    this.flightType = gameTile.getFlightType();
                    System.out.print("\033[s");
                    System.out.flush();
                    System.out.print("\033[H");
                    System.out.flush();
                    System.out.println(Font.BOLD + "\nThese are the tiles:" + Font.RESET);
                    num++;
                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawTilesDeck(gameTile);
                    for(String line : lines){
                        System.out.println(line);
                    }
                    System.out.print("\033[u");
                    System.out.flush();
                }
            }
        }
        count = 1;
    }

    /**
     * This method shows the player the general instructions in the first flight.
     */
    public void showInstrucPickUpFirst() {
        synchronized (stateLock) {
            this.state = State.PICK_UP;
        }
        recoverAction = this::showInstrucPickUpFirst;
        synchronized (outputLock) {
            System.out.println("\nYou have to build your own ship!!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "pick_tile_" + Font.RESET + Font.BOLD + "ID" + Font.RESET + "  if you want to pick the tile with the given ID");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "finish_build" + Font.RESET + "  if you have completed your ship");
            numLines++;
        }
    }

    /**
     * This method shows the player the general instructions in the first flight.
     */
    public void showInstrucPickUpStandard() {
        synchronized (stateLock) {
            this.state = State.PICK_UP;
        }
        recoverAction = this::showInstrucPickUpStandard;
        synchronized (outputLock) {
            System.out.println("\nYou have to build your own ship!!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "pick_tile_" + Font.RESET + Font.BOLD + "ID" + Font.RESET + "  if you want to pick the tile with the given ID");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "pick_tile_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to pick the tile from the ship (from the reserve spaces)");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "pick_pile_" + Font.RESET + Font.BOLD + "ID" + Font.RESET + "  if you want to pick the card pile with the given ID");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "turn_timer" + Font.RESET + "  if you want to turn the timer (if the time is finished)");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "finish_build" + Font.RESET + "  if you have completed your ship");
            numLines++;
        }
    }

    @Override
    public void showComponentTile(ComponentTile componentTile) {
        this.numLines = 0;
        synchronized (outputLock) {
            System.out.print("\033[H");
            System.out.flush();
            for(int i=0; i<42; i++) {
                System.out.print(Font.clearSuccessiveLine());
            }
            System.out.print("\033[H");
            System.out.flush();

            System.out.println(Font.BOLD + "\nThis is the legend of the components:" + Font.RESET);
            DrawTui drawTui = new DrawTui();
            String[] lines = drawTui.drawComponentLegend(flightType);
            for (String line : lines) {
                System.out.println(line);
            }

            System.out.println(Font.BOLD + "\nThis is the component tile you picked!" + Font.RESET);
            DrawTui drawTui2 = new DrawTui();
            String[] lines2 = drawTui2.drawComponentTile(componentTile);
            for(String line2 : lines2){
                out.println(line2);
            }
        }

        synchronized (stateLock) {
            if (state == State.PUT_TILE_RESERVE) {
                this.componentTile = componentTile;
                numLines = 0;
                this.showInstrucPutTileBackReserve(componentTile);
            }
            else {
                this.componentTile = componentTile;
                numLines = 0;
                this.showInstrucPutTileBackFirst(componentTile);
            }
        }
    }

    @Override
    public void showCardPile(CardPile cardPile) {
        this.numLines = 0;
        synchronized (outputLock) {
            System.out.print("\033[H");
            System.out.flush();
            for(int i=0; i<42; i++) {
                System.out.print(Font.clearSuccessiveLine());
            }
            System.out.print("\033[H");
            System.out.flush();

            System.out.println(Font.BOLD + "\nThis are the cards that are contained in the cards pile:" + Font.RESET);
            DrawTui drawTui = new DrawTui();
            String[] lines = drawTui.drawCardsPile(cardPile);
            for (String line : lines) {
                System.out.println(line);
            }
        }
        this.cardPile = cardPile;
        numLines = 0;
        this.showInstrucStopWatchingCardPile(cardPile);
    }

    @Override
    public void showAllShipBoards(HashMap<String, ShipBoard> shipBoards) {
        synchronized (stateLock) {
            if(state == State.SHOW_SHIPS) {
                if(count1 == 0) {
                    synchronized (outputLock) {
                        shipBoards.remove(this.nickname);
                        System.out.print("\033[H");
                        System.out.flush();

                        for(int i=0; i<42; i++) {
                            System.out.print(Font.clearSuccessiveLine());
                        }

                        System.out.print("\033[H");
                        System.out.flush();

                        System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                        num++;

                        DrawTui drawTui = new DrawTui();
                        String[] lines = drawTui.drawAllShips(shipBoards);
                        for(String line : lines){
                            System.out.println(line);
                        }

                        drawTui.componentInAllShipBoards(shipBoards, count1);
                        numLines = 0;
                        this.showInstrucStopWatchingShip();
                    }
                    count1 = 1;
                }
                else {
                    synchronized (outputLock) {
                        shipBoards.remove(this.nickname);
                        System.out.print("\033[s");
                        System.out.flush();
                        System.out.print("\033[H");
                        System.out.flush();

                        for(int i=0; i<30; i++) {
                            System.out.print(Font.clearSuccessiveLine());
                        }

                        System.out.print("\033[H");
                        System.out.flush();

                        System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                        num++;

                        DrawTui drawTui = new DrawTui();
                        String[] lines = drawTui.drawAllShips(shipBoards);
                        for(String line : lines){
                            System.out.println(line);
                        }

                        drawTui.componentInAllShipBoards(shipBoards, count1);

                        System.out.print("\033[u");
                        System.out.flush();
                    }
                }
            }
            else if(state == State.SHOW_SHIPS_FOREVER) {

                synchronized (outputLock) {
                    shipBoards.remove(this.nickname);
                    System.out.print("\033[H");
                    System.out.flush();

                    for(int i=0; i<42; i++) {
                        System.out.print(Font.clearSuccessiveLine());
                    }

                    System.out.print("\033[H");
                    System.out.flush();

                    System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                    num++;

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawAllShips(shipBoards);
                    for(String line : lines){
                        System.out.println(line);
                    }

                    drawTui.componentInAllShipBoards(shipBoards, count1);
                    numLines = 0;
                    this.showInstrucStopWatchingShip2();

                }
            }
            else if(state == State.WAIT || state == State.FIX || state == State.WAIT_BUILD || state == State.WAIT_POPULATE || state == State.PUT_FIGURES || state == State.WAIT_FIGURE) {
                if(count1 == 0) {
                    synchronized (outputLock) {
                        shipBoards.remove(this.nickname);
                        System.out.print("\033[H");
                        System.out.flush();

                        for(int i=0; i<42; i++) {
                            System.out.print(Font.clearSuccessiveLine());
                        }

                        System.out.print("\033[H");
                        System.out.flush();

                        System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                        num++;

                        DrawTui drawTui = new DrawTui();
                        String[] lines = drawTui.drawAllShips(shipBoards);
                        for(String line : lines){
                            System.out.println(line);
                        }

                        drawTui.componentInAllShipBoards(shipBoards, count1);

                        out.print("\033[80;0H");

                        System.out.println(Font.BLUE + "\nShip terminated." + Font.RESET);
                    }
                    count1 = 1;
                }
                else {
                    synchronized (outputLock) {
                        shipBoards.remove(this.nickname);
                        System.out.print("\033[s");
                        System.out.flush();
                        System.out.print("\033[H");
                        System.out.flush();

                        for(int i=0; i<40; i++) {
                            System.out.print(Font.clearSuccessiveLine());
                        }

                        System.out.print("\033[H");
                        System.out.flush();

                        System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                        num++;

                        DrawTui drawTui = new DrawTui();
                        String[] lines = drawTui.drawAllShips(shipBoards);
                        for(String line : lines){
                            System.out.println(line);
                        }

                        drawTui.componentInAllShipBoards(shipBoards, count1);

                        System.out.print("\033[u");
                        System.out.flush();
                    }
                }
            }
            else if (state == State.WAIT_TURN || state == State.WAIT_METEOR) {

                synchronized (outputLock) {
                    shipBoards.remove(this.nickname);
                    System.out.print("\033[H");
                    System.out.flush();

                    for (int i = 0; i < 42; i++) {
                        System.out.print(Font.clearSuccessiveLine());
                    }

                    System.out.print("\033[H");
                    System.out.flush();

                    System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                    num++;

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawAllShips(shipBoards);
                    for (String line : lines) {
                        System.out.println(line);
                    }

                    drawTui.componentInAllShipBoards(shipBoards, count1);

                    System.out.print("\033[37;0H");
                    System.out.flush();

                    numLines = 0;
                    this.showInstrucStopWatchingShip();
                }

            }
            else if (state == State.REPAIR) {
                synchronized (outputLock) {
                    shipBoards.remove(this.nickname);
                    System.out.print("\033[H");
                    System.out.flush();

                    for (int i = 0; i < 42; i++) {
                        System.out.print(Font.clearSuccessiveLine());
                    }

                    System.out.print("\033[H");
                    System.out.flush();

                    System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                    num++;

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawAllShips(shipBoards);
                    for (String line : lines) {
                        System.out.println(line);
                    }

                    drawTui.componentInAllShipBoards(shipBoards, count1);

                    System.out.print("\033[84;0H");
                }
            }
            else if (state == State.WAIT_REPAIR) {
                synchronized (outputLock) {
                    shipBoards.remove(this.nickname);
                    System.out.print("\033[H");
                    System.out.flush();

                    for (int i = 0; i < 42; i++) {
                        System.out.print(Font.clearSuccessiveLine());
                    }

                    System.out.print("\033[H");
                    System.out.flush();

                    System.out.println(Font.BOLD + "\nThese are the ship boards:\n" + Font.RESET);
                    num++;

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawAllShips(shipBoards);
                    for (String line : lines) {
                        System.out.println(line);
                    }

                    drawTui.componentInAllShipBoards(shipBoards, count1);

                    System.out.print("\033[80;0H");
                }

            }
        }
    }

    @Override
    public void showTimer(int time) {
        synchronized (stateLock) {
            if(showTimer) {
                if(state != State.FIX && state != State.WAIT) {
                    synchronized (outputLock) {
                        this.time = time;
                        if(time == 10) {
                            this.remainingPositions--;
                        }
                        System.out.print("\033[s");
                        System.out.flush();

                        System.out.print("\033[48;80H");
                        System.out.flush();
                        System.out.print("\033[K");
                        System.out.flush();
                        System.out.print("\033[49;80H");
                        System.out.flush();
                        System.out.print("\033[K");
                        System.out.flush();

                        System.out.print("\033[48;100H");
                        System.out.flush();
                        out.print(Font.BOLD + "TIMER:  " + time + Font.RESET + " seconds remaining");
                        System.out.print("\033[49;100H");
                        System.out.flush();
                        out.print("Remaining Positions:  " + this.remainingPositions);

                        System.out.print("\033[u");
                        System.out.flush();
                    }
                }
            }
        }
    }

    @Override
    public void finishBuilding() {
        synchronized (stateLock) {
            synchronized (outputLock) {
                if(this.remainingPositions == 0) {

                    for(int i=0; i<20; i++) {
                        out.printf("\033[%d;%dH",80+i,0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }
                    numLines = 0;
                    this.state = State.WAIT_BUILD;
                    this.showTimer = false;
                    showTiles = false;
                    notifyObserver(obs -> obs.onUpdateFinishedBuild());
                }
            }
        }
    }


    @Override
    public void proceed2() {
        synchronized (stateLock) {
            this.state = State.WAIT_POPULATE;
            this.showTimer = false;
        }
    }

    @Override
    public void showTempPositions(ArrayList<Player> tempPositions) {
        synchronized (outputLock) {
            int num = 0;

            System.out.print("\033[s");
            System.out.flush();
            System.out.print("\033[48;100H");
            System.out.flush();
            System.out.print("\033[K");
            System.out.flush();
            System.out.print("\033[49;100H");
            System.out.flush();
            System.out.print("\033[K");
            System.out.flush();

            System.out.print("\033[48;100H");
            System.out.flush();
            out.print(Font.BOLD + "These are the final starting position on the flight board:" + Font.RESET);

            for(Player player : tempPositions){
                System.out.printf("\033[%d;%dH", 49+num, 100);
                System.out.flush();
                System.out.print("\033[K");
                System.out.flush();
                System.out.printf("\033[%d;%dH", 49+num, 100);
                System.out.flush();
                out.print(Font.BOLD +"" + (num+1) + Font.RESET + ": " + player.getNickname());
                num++;
            }

            System.out.print("\033[u");
            System.out.flush();
        }
    }

    /**
     * This method shows the instructions to put the tile back in the first flight.
     * @param componentTile is the tile in hand.
     */
    public void showInstrucPutTileBackFirst(ComponentTile componentTile) {
        synchronized (stateLock) {
            this.state = State.PUT_TILE;
        }
        recoverAction = () -> showInstrucPutTileBackFirst(componentTile);
        count = 0;
        synchronized (outputLock) {
            System.out.println("\nNow you can decide if you want to put the tile in your ship, or not. If you put the tile back in the deck, it will be facing up, visible to all the players!! ");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_ship_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to put the tile in your ship board at space (numRow, numCol)");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_back" + Font.RESET + "  if you want to put the tile back in the deck");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "set_" +Font.BOLD_ITALIC+"direction  (direction = nord, sud, est, ovest)"  + Font.RESET + "  if you want to change the tile direction");
            numLines++;
        }
    }

    /**
     * This method shows the instructions to put the tile back when is picked from a reserve space.
     * @param componentTile is the tile in hand.
     */
    public void showInstrucPutTileBackReserve(ComponentTile componentTile) {
        synchronized (stateLock) {
            this.state = State.PUT_TILE_RESERVE;
        }
        recoverAction = () -> showInstrucPutTileBackFirst(componentTile);
        count = 0;
        synchronized (outputLock) {
            System.out.println("\nYou have to put the tile in the ship. You cannot put it back in the deck.");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_ship_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to put the tile in your ship board at space (numRow, numCol)");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "set_" +Font.BOLD_ITALIC+"direction  (direction = nord, sud, est, ovest)"  + Font.RESET + "  if you want to change the tile direction");
            numLines++;
        }
    }

    /**
     * This method shows the instructions to stop watching at other players ship boards.
     */
    public void showInstrucStopWatchingShip() {
        synchronized (stateLock) {
            this.state = State.SHOW_SHIPS;
        }
        recoverAction = () -> showInstrucStopWatchingShip();
        count = 0;
        synchronized (outputLock) {
            numLines++;
            System.out.println("\n- write  " + Font.BOLD + "stop" + Font.RESET + "  if you want to stop watching the ships and return building your own ship");
            numLines++;
        }
    }

    /**
     * This method shows the instructions to stop watching at other players ship boards, when the player is retired.
     */
    public void showInstrucStopWatchingShip2() {
        synchronized (stateLock) {
            this.state = State.SHOW_SHIPS_FOREVER;
        }
        recoverAction = () -> showInstrucStopWatchingShip2();
        count = 0;
        synchronized (outputLock) {
            numLines++;
            System.out.println("\n- write  " + Font.BOLD + "stop" + Font.RESET + "  if you want to stop watching the other players ship boards");
            numLines++;
        }
    }

    /**
     * This method shows the instructions to stop watching the card pile.
     * @param cardPile is the card pile the player has in hand.
     */
    public void showInstrucStopWatchingCardPile(CardPile cardPile) {
        synchronized (stateLock) {
            this.state = State.SHOW_PILE;
        }
        recoverAction = () -> showInstrucStopWatchingCardPile(cardPile);
        count = 0;
        synchronized (outputLock) {
            numLines++;
            System.out.println("\n- write  " + Font.BOLD + "stop" + Font.RESET + "  if you want to stop watching the cards pile and return building your own ship");
            numLines++;
        }
    }

    /**
     * This method shows the instructions to stop remove a tile from the ship.
     */
    public void showInstrucRemoveTiles() {
        synchronized (stateLock) {
            this.state = State.FIX;
        }
        recoverAction = () -> showInstrucRemoveTiles();
        synchronized (outputLock) {
            System.out.println("\nYour ship board is not correct. The errors are reported on the right. Fix your ship!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "remove_tile_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to remove the tile in the given position");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "finish_build" + Font.RESET + "  if you have completed your ship");
            numLines++;
        }

    }

    /**
     * This method shows the instructions to stop repair the ship.
     */
    public void showInstrucRepairShip() {
        synchronized (stateLock) {
            this.state = State.REPAIR;
        }
        recoverAction = () -> showInstrucRepairShip();
        synchronized (outputLock) {
            System.out.println("\nYour ship board is not correct. The errors are reported on the right. Repair your ship!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "remove_tile_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to remove the tile in the given position");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "repaired" + Font.RESET + "  if you have repaired your ship");
            numLines++;
        }
    }

    /**
     * This method is used to clear the lines.
     */
    public void clearLines() {
        while (numLines != 0) {
            synchronized (outputLock) {
                System.out.print(Font.clearPreviousLine());
            }
            numLines--;
        }
    }

    @Override
    public void clearPage() {
        synchronized (outputLock) {
            System.out.print(Font.CLEAR);
            System.out.flush();
            numLines = 0;
        }
    }

    @Override
    public void showLeader(int numCards, FlightBoard flightBoard) {
        synchronized (stateLock) {
            this.state = State.LEADER;
        }

        synchronized (outputLock) {
            numLines = 0;

            for(int i = 0; i < 42; i++) {
                out.printf("\033[%d;%dH", i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            for(int i = 0; i < 20; i++) {
                out.printf("\033[%d;%dH", 80+i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            for(int i = 0; i < 40; i++) {
                out.printf("\033[%d;%dH", 40+i, 100);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[0;0H");

            System.out.println(Font.BOLD + "\nThis is the cards deck.\n" + Font.RESET);

            DrawTui drawTui = new DrawTui();
            String[] lines = drawTui.drawCardsDeck(numCards);

            for (String line : lines) {
                out.println(line);
            }

            out.print("\033[s");
            out.flush();

            drawTui.drawFlightBoard(flightBoard);

            for(Player player : flightBoard.getPlayersMap().keySet()) {
                if(player.getId().equals(clientId)) {
                    drawTui.drawPlayerParametres(player);
                }
            }

            out.print("\033[u");
            out.flush();

            numLines = 0;
            this.showInstrucPickUpCard();

        }
    }


    @Override
    public void showNotLeader(int numCards, FlightBoard flightBoard) {
        synchronized (stateLock) {
            this.state = State.NOT_LEADER;
        }

        synchronized (outputLock) {
            numLines = 0;

            for(int i = 0; i < 42; i++) {
                out.printf("\033[%d;%dH", i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            for(int i = 0; i < 20; i++) {
                out.printf("\033[%d;%dH", 80+i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            for(int i = 0; i < 40; i++) {
                out.printf("\033[%d;%dH", 40+i, 100);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[0;0H");

            System.out.println(Font.BOLD + "\nThis is the cards deck.\n" + Font.RESET);

            DrawTui drawTui = new DrawTui();
            String[] lines = drawTui.drawCardsDeck(numCards);

            for (String line : lines) {
                out.println(line);
            }

            out.print("\033[s");
            out.flush();

            drawTui.drawFlightBoard(flightBoard);

            for(Player player : flightBoard.getPlayersMap().keySet()) {
                if(player.getId().equals(clientId)) {
                    drawTui.drawPlayerParametres(player);
                }
            }

            out.print("\033[u");
            out.flush();
            System.out.println("\nYou are not the leader. You have to wait the leader to pick up the card!!");
            numLines++;
            numLines++;
        }
    }


    @Override
    public void showNotLeader2(int numCards, FlightBoard flightBoard, ArrayList<Player> players) {
        this.numCards = numCards;

        synchronized (stateLock) {
            if(state != State.SHOW_SHIPS_FOREVER) {
                this.state=State.WAIT_FOREVER;
                synchronized (outputLock) {
                    numLines = 0;

                    for (int i = 0; i < 42; i++) {
                        out.printf("\033[%d;%dH", i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }

                    for (int i = 0; i < 20; i++) {
                        out.printf("\033[%d;%dH", 80 + i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }

                    for (int i = 0; i < 40; i++) {
                        out.printf("\033[%d;%dH", 40 + i, 100);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }

                    out.print("\033[0;0H");

                    System.out.println(Font.BOLD + "\nThis is the cards deck.\n" + Font.RESET);

                    DrawTui drawTui = new DrawTui();
                    String[] lines = drawTui.drawCardsDeck(numCards);

                    for (String line : lines) {
                        out.println(line);
                    }

                    drawTui.drawFlightBoard(flightBoard);

                    for (Player player : players) {
                        if (player.getId().equals(clientId)) {
                            drawTui.drawPlayerParametres(player);
                        }
                    }

                    System.out.print("\033[83;0H");
                    System.out.flush();
                    System.out.println("\nYou are not in the flight anymore. Watch the other player!!");
                    this.showWait2(cardInUse);
                }
            }
        }
    }

    /**
     * This method shows the instructions to pick up a card.
     */
    public void showInstrucPickUpCard() {
        recoverAction = () -> showInstrucPickUpCard();
        synchronized (outputLock) {
            System.out.println("\nYou are the leader and you have to pick up the first card!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "pick_card" + Font.RESET + "  to pick up the card");
            numLines++;
        }
    }

    @Override
    public void setCardInUse(Card card) {
        this.cardInUse = card;
    }

    @Override
     public void showCard(boolean inTurn) {
        countCards++;
         synchronized (stateLock) {
             if (state != State.SHOW_SHIPS || inTurn) {
                 synchronized (outputLock) {
                     numLines = 0;

                     for (int i = 0; i < 20; i++) {
                         out.printf("\033[%d;%dH", 80+i, 0);
                         out.flush();
                         out.print("\033[K");
                         out.flush();
                     }

                     for (int i = 0; i < 47; i++) {
                         out.printf("\033[%d;%dH", i, 0);
                         out.flush();
                         out.print("\033[K");
                         out.flush();
                     }

                     out.print("\033[0;0H");
                     System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                     DrawTui drawTui = new DrawTui();
                     String[] lines = drawTui.drawCard(cardInUse);

                     for (String line : lines) {
                         out.println(line);
                     }

                     if(inTurn) {
                         switch(cardInUse.getCardType()) {
                             case PLANETS:
                                 this.showInstrucPlanets();
                                 break;

                             case SMUGGLERS:
                                 this.showInstrucSmugglers();
                                 break;

                             case SLAVERS:
                                 this.showInstrucSlavers();
                                 break;

                             case PIRATES:
                                 this.showInstrucPirates();
                                 break;

                             case ABANDONED_STATION:
                                 this.showInstrucAbandonedStation();
                                 break;

                             case ABANDONED_SHIP:
                                 this.showInstrucAbandonedShip();
                                 break;

                             case OPEN_SPACE:
                                 this.showInstrucOpenSpace();
                                 break;

                             case COMBAT_ZONE:
                                 if(cardInUse.getLevel() == 1) {
                                     CombatZone combatZone = (CombatZone) cardInUse;
                                     if(combatZone.getFaseCounter()==1) {}
                                     else if(combatZone.getFaseCounter()==2) {
                                         this.showInstrucPhase2Level1();
                                     }
                                     else if(combatZone.getFaseCounter()==3) {
                                         if(cannonFireSmall == false) {
                                             this.showInstrucPhase3Level1();
                                         }
                                         else {
                                             this.showInstrucCannonFireSmall(sum);
                                         }
                                     }
                                 }
                                 else {
                                     CombatZone combatZone = (CombatZone) cardInUse;
                                     if(combatZone.getFaseCounter()==1) {
                                         this.showInstrucPhase1Level2();
                                     }
                                     else if(combatZone.getFaseCounter()==2) {
                                         this.showInstrucPhase2Level2();
                                     }
                                     else if(combatZone.getFaseCounter()==3) {
                                         if(cannonFireSmall == false) {
                                         }
                                         else {
                                             this.showInstrucCannonFireSmall(sum);
                                         }
                                     }
                                 }
                                 break;
                             default: break;
                         }
                     }
                     else {
                         this.state = State.NOT_IN_TURN;
                         this.showWait(cardInUse);
                     }
                 }
             }
         }
     }

    @Override
    public void showCard2() {
        countCards++;
        synchronized (stateLock) {
            if (state != State.SHOW_SHIPS_FOREVER) {
                if(cardInUse != null) {
                    synchronized (outputLock) {
                        numLines = 0;

                        for (int i = 0; i < 20; i++) {
                            out.printf("\033[%d;%dH", 80+i, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }

                        for (int i = 0; i < 47; i++) {
                            out.printf("\033[%d;%dH", i, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }

                        out.print("\033[0;0H");

                        System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                        DrawTui drawTui = new DrawTui();
                        String[] lines = drawTui.drawCard(cardInUse);

                        for (String line : lines) {
                            out.println(line);
                        }

                        this.showWait2(cardInUse);
                    }
                }
                else {
                    synchronized (outputLock) {
                        numLines = 0;

                        for (int i = 0; i < 42; i++) {
                            out.printf("\033[%d;%dH", i, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }

                        for (int i = 0; i < 20; i++) {
                            out.printf("\033[%d;%dH", 80 + i, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }

                        for (int i = 0; i < 40; i++) {
                            out.printf("\033[%d;%dH", 40 + i, 100);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }

                        out.print("\033[0;0H");

                        System.out.println(Font.BOLD + "\nThis is the cards deck.\n" + Font.RESET);

                        DrawTui drawTui = new DrawTui();
                        String[] lines = drawTui.drawCardsDeck(numCards);

                        for (String line : lines) {
                            out.println(line);
                        }

                        out.print("\033[s");
                        out.flush();
                        this.showWait2(cardInUse);
                    }
                }
            }
        }
    }

     @Override
     public void setRollDice(Card card, boolean inTurn, boolean proceed) {
        this.cardInUse = card;
         synchronized (stateLock) {

             if(card.getCardType() == CardName.METEOR_SWARM) {
                 if(((MeteorSwarm) cardInUse).getCounter() == 0 || proceed) {
                     this.state = State.ROLL_DICE;

                     synchronized (outputLock) {
                         numLines = 0;

                         for (int i = 0; i < 47; i++) {
                             out.printf("\033[%d;%dH", 80+i, 0);
                             out.flush();
                             out.print("\033[K");
                             out.flush();
                         }

                         for (int i = 0; i < 47; i++) {
                             out.printf("\033[%d;%dH", i, 0);
                             out.flush();
                             out.print("\033[K");
                             out.flush();
                         }

                         out.print("\033[0;0H");

                         System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                         DrawTui drawTui = new DrawTui();
                         String[] lines = drawTui.drawCard(cardInUse);

                         for (String line : lines) {
                             out.println(line);
                         }

                         if (inTurn) {
                             switch (cardInUse.getCardType()) {
                                 case METEOR_SWARM:
                                     this.showInstrucRollDice();
                                     break;

                                 default:
                                     break;
                             }

                         } else {
                             this.state = State.WAIT_ROLL_DICE;
                             this.showInstrucWaitRollDice();
                         }
                     }
                 }
                 else {

                     if(state == State.SHOW_SHIPS) {
                         this.state = State.ROLL_DICE;

                         synchronized (outputLock) {
                             numLines = 0;

                             for (int i = 0; i < 47; i++) {
                                 out.printf("\033[%d;%dH", i, 0);
                                 out.flush();
                                 out.print("\033[K");
                                 out.flush();
                             }

                             out.print("\033[0;0H");

                             System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                             DrawTui drawTui = new DrawTui();
                             String[] lines = drawTui.drawCard(cardInUse);

                             for (String line : lines) {
                                 out.println(line);
                             }

                             if (inTurn) {
                                 switch (cardInUse.getCardType()) {
                                     case METEOR_SWARM:
                                         this.showInstrucRollDice();
                                         break;

                                     default:
                                         break;
                                 }

                             } else {
                                 this.state = State.NOT_IN_TURN;
                                 this.showInstrucWaitRollDice();
                             }
                         }
                     }
                     else {
                         this.state = State.ROLL_DICE;
                         synchronized (outputLock) {
                             numLines = 0;
                             numLines++;

                             if (inTurn) {
                                 switch (cardInUse.getCardType()) {
                                     case METEOR_SWARM:
                                         this.showInstrucRollDice();
                                         break;

                                     default:
                                         break;
                                 }
                             } else {
                                 this.state = State.NOT_IN_TURN;
                                 this.showInstrucWaitRollDice();
                             }
                         }
                     }
                 }
             }
             else if(card.getCardType() == CardName.COMBAT_ZONE) {
                 if(((CombatZone) cardInUse).getCounter() == 0 || proceed) {
                     this.state = State.ROLL_DICE;

                     synchronized (outputLock) {
                         numLines = 0;

                         for (int i = 0; i < 47; i++) {
                             out.printf("\033[%d;%dH", 80+i, 0);
                             out.flush();
                             out.print("\033[K");
                             out.flush();
                         }

                         for (int i = 0; i < 47; i++) {
                             out.printf("\033[%d;%dH", i, 0);
                             out.flush();
                             out.print("\033[K");
                             out.flush();
                         }

                         out.print("\033[0;0H");

                         System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                         DrawTui drawTui = new DrawTui();
                         String[] lines = drawTui.drawCard(cardInUse);

                         for (String line : lines) {
                             out.println(line);
                         }
                         this.showInstrucRollDice();
                     }
                 }
                 else {
                     this.state = State.ROLL_DICE;
                     synchronized (outputLock) {

                         numLines = 0;
                         numLines++;

                         this.showInstrucRollDice();
                     }
                 }

             }
             else if(card.getCardType() == CardName.PIRATES) {
                 if(((Pirates) cardInUse).getCounter() == 0 || proceed) {
                     this.state = State.ROLL_DICE;

                     synchronized (outputLock) {
                         numLines = 0;

                         for (int i = 0; i < 47; i++) {
                             out.printf("\033[%d;%dH", 80+i, 0);
                             out.flush();
                             out.print("\033[K");
                             out.flush();
                         }

                         for (int i = 0; i < 47; i++) {
                             out.printf("\033[%d;%dH", i, 0);
                             out.flush();
                             out.print("\033[K");
                             out.flush();
                         }

                         out.print("\033[0;0H");

                         System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                         DrawTui drawTui = new DrawTui();
                         String[] lines = drawTui.drawCard(cardInUse);

                         for (String line : lines) {
                             out.println(line);
                         }

                         if (inTurn) {
                             switch (cardInUse.getCardType()) {
                                 case PIRATES:
                                     this.showInstrucRollDice();
                                     break;

                                 default:
                                     break;
                             }
                         } else {
                             this.state = State.NOT_IN_TURN;
                             this.showInstrucWaitRollDice();
                         }
                     }
                 }
                 else {

                     if(state == State.SHOW_SHIPS) {
                         this.state = State.ROLL_DICE;

                         synchronized (outputLock) {
                             numLines = 0;

                             for (int i = 0; i < 47; i++) {
                                 out.printf("\033[%d;%dH", i, 0);
                                 out.flush();
                                 out.print("\033[K");
                                 out.flush();
                             }

                             out.print("\033[0;0H");

                             System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                             DrawTui drawTui = new DrawTui();
                             String[] lines = drawTui.drawCard(cardInUse);

                             for (String line : lines) {
                                 out.println(line);
                             }

                             if (inTurn) {
                                 switch (cardInUse.getCardType()) {
                                     case PIRATES:
                                         this.showInstrucRollDice();
                                         break;

                                     default:
                                         break;
                                 }
                             } else {
                                 this.state = State.NOT_IN_TURN;
                                 this.showInstrucWaitRollDice();
                             }
                         }
                     }
                     else {
                         this.state = State.ROLL_DICE;
                         synchronized (outputLock) {

                             numLines = 0;
                             numLines++;

                             if (inTurn) {
                                 switch (cardInUse.getCardType()) {
                                     case PIRATES:
                                         this.showInstrucRollDice();
                                         break;

                                     default:
                                         break;
                                 }
                             } else {
                                 this.state = State.NOT_IN_TURN;
                                 this.showInstrucWaitRollDice();
                             }
                         }
                     }
                 }
             }
         }
     }

    /**
     * This method shows the instructions for the planets card.
     */
    public void showInstrucPlanets() {
        synchronized (stateLock) {
            this.state = State.PLANETS;
        }

        recoverAction = () -> showInstrucPlanets();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to land on a planet and lose the indicated number of flight days, or you can skip the planet and not receive goods...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "land_" + Font.RESET + Font.BOLD_ITALIC + "ID" + Font.RESET + "  if you want to land on the planet with the given ID");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "skip" + Font.RESET + "  if you don't want to land on a planetD");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the smugglers card.
     */
    public void showInstrucSmugglers() {
        synchronized (stateLock) {
            this.state = State.SMUGGLERS;
        }

        recoverAction = () -> showInstrucSmugglers();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double cannons to fight the smugglers (if you don't have enough fire strength), or proceed in the fight...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_cannon_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double cannon in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "fight" + Font.RESET + "  if you want to proceed in the fight");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the slavers card.
     */
    public void showInstrucSlavers() {
        synchronized (stateLock) {
            this.state = State.SLAVERS;
        }

        recoverAction = () -> showInstrucSlavers();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double cannons to fight the slavers (if you don't have enough fire strength), or proceed in the fight...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_cannon_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double cannon in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "fight" + Font.RESET + "  if you want to proceed in the fight");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the pirates card.
     */
    public void showInstrucPirates() {
        synchronized (stateLock) {
            this.state = State.PIRATES;
        }

        recoverAction = () -> showInstrucPirates();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double cannons to fight the pirates (if you don't have enough fire strength), or proceed in the fight...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_cannon_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double cannon in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "fight" + Font.RESET + "  if you want to proceed in the fight");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the abandoned sstation card.
     */
    public void showInstrucAbandonedStation() {
        synchronized (stateLock) {
            this.state = State.ABANDONED_STATION;
        }

        recoverAction = () -> showInstrucAbandonedStation();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to dock with the space station (if you have enough figures) gaining goods and loosing flight days, or skip the opportunity...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "dock" + Font.RESET + "  if you want to dock with the space station");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "skip" + Font.RESET + "  if you want to skip the opportunity");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the abandoned ship card.
     */
    public void showInstrucAbandonedShip() {
        synchronized (stateLock) {
            this.state = State.ABANDONED_SHIP;
        }

        recoverAction = () -> showInstrucAbandonedShip();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to dock with the abandoned ship (if you have enough figures) gaining goods, loosing flight days and figures, or skip the opportunity...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "dock" + Font.RESET + "  if you want to dock with the abandoned ship");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "skip" + Font.RESET + "  if you want to skip the opportunity");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the open space card.
     */
    public void showInstrucOpenSpace() {
        synchronized (stateLock) {
            this.state = State.OPEN_SPACE;
        }

        recoverAction = () -> showInstrucOpenSpace();
        synchronized (outputLock) {
            if(flightType == FlightType.FIRST_FLIGHT) {
                System.out.println("\nIt's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength, or proceed in the open space with only your single engines. Then you move that many empty (the declared engine strength) space forward...");
            }
            else {
                System.out.println("\nIt's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength, or proceed in the open space with only your single engines. Then you move that many empty (the declared engine strength) space forward...");
                System.out.println("If your declared engine strength is 0, you will be removed from the flight!!");
                numLines++;
            }
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_engine_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double cannon in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "move" + Font.RESET + "  if you want to move in the open space");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the combat zone card, phase 2 level 1.
     */
    public void showInstrucPhase2Level1() {
        synchronized (stateLock) {
            this.state = State.PHASE2_LEVEL1;
        }

        recoverAction = () -> showInstrucPhase2Level1();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength. The player with the lowest engine strength will lose 2 crew figures...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_engine_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double engine in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed in the phase");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the combat zone card, phase 3 level 1.
     */
    public void showInstrucPhase3Level1() {
        synchronized (stateLock) {
            this.state = State.PHASE3_LEVEL1;
        }

        recoverAction = () -> showInstrucPhase3Level1();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double cannons (if you have them) to increment your power strength. The player with the lowest power strength is threatened by some dangerous cannon fire...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_cannon_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double cannon in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed in the phase");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the combat zone card, phase 1 level 2.
     */
    public void showInstrucPhase1Level2() {
        synchronized (stateLock) {
            this.state = State.PHASE1_LEVEL2;
        }

        recoverAction = () -> showInstrucPhase1Level2();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double cannons (if you have them) to increment your power strength. The player with the lowest power strength will lose flight days...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_cannon_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double cannon in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed in the phase");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the combat zone card, phase 2 level 2.
     */
    public void showInstrucPhase2Level2() {
        synchronized (stateLock) {
            this.state = State.PHASE2_LEVEL2;
        }

        recoverAction = () -> showInstrucPhase2Level2();
        synchronized (outputLock) {
            System.out.println("\nIt's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength. The player with the lowest engine strength will lose 3 goods blocks...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_engine_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the double engine in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed in the phase");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the meteor swarm small.
     */
    public void showInstrucMeteorSwarmSmall(int sum) {
        synchronized (stateLock) {
            this.state = State.METEOR_SWARM_SMALL;
        }
        this.sum = sum;
        recoverAction = () -> showInstrucMeteorSwarmSmall(sum);
        synchronized (outputLock) {
            Meteor meteor = ((MeteorSwarm) cardInUse).getMeteor().get(((MeteorSwarm) cardInUse).getCounter()-1);
            String direction = meteor.getDirection();

            if (direction.equals("nord") || direction.equals("sud")) {
                out.println("\nThe meteor will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...");
            }
            else {
                out.println("\nThe meteor will hit your shipboard at row number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...");
            }

            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_shield_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the shield in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the meteor swarm large.
     */
    public void showInstrucMeteorSwarmLarge(int sum) {
        synchronized (stateLock) {
            this.state = State.METEOR_SWARM_LARGE;
        }
        this.sum = sum;
        recoverAction = () -> showInstrucMeteorSwarmLarge(sum);
        synchronized (outputLock) {
            Meteor meteor = ((MeteorSwarm) cardInUse).getMeteor().get(((MeteorSwarm) cardInUse).getCounter()-1);
            String direction = meteor.getDirection();
            if (direction.equals("nord")) {
                out.println("\nThe meteor will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". If you have a double cannon in column " + sum + " pointing in the direction of the meteor you can activate it to prevent the catastrophe...");
            }
            else if(direction.equals("sud")) {
                out.println("\nThe meteor will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". If you have a double cannon in column " + (sum-1) + " or " + sum + " or " + (sum+1) + " pointing in the direction of the meteor you can activate it to prevent the catastrophe...");
            }
            else {
                out.println("\nThe meteor will hit your shipboard at row number " + sum + " from " + meteor.getDirection() +". If you have a double cannon in row " + (sum-1) + " or " + sum + " or " + (sum+1) + " pointing in the direction of the meteor you can activate it to prevent the catastrophe...");
            }
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_cannon_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the shield in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the cannon fire small.
     */
    public void showInstrucCannonFireSmall(int sum) {
        synchronized (stateLock) {
            this.state = State.CANNON_FIRE_SMALL;
        }
        this.cannonFireSmall = true;
        this.sum = sum;
        recoverAction = () -> showInstrucCannonFireSmall(sum);
        synchronized (outputLock) {

            CombatZone combatZoneCard = (CombatZone) cardInUse;
            ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];
            Meteor meteor = meteors.get(combatZoneCard.getCounter() - 1);
            String direction = meteor.getDirection();

            if (direction.equals("nord") || direction.equals("sud")) {
                out.println("\nThe cannon fire will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...");
            }
            else {
                out.println("\nThe cannon fire will hit your shipboard at row number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...");
            }

            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_shield_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the shield in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed");
            numLines++;
        }
    }

    /**
     * This method shows the instructions for the meteor swarm small, in the pirates card.
     */
    public void showInstrucCannonFireSmallPirates(int sum) {
        synchronized (stateLock) {
            this.state = State.CANNON_FIRE_SMALL_PIRATES;
        }
        this.cannonFireSmall = true;
        this.sum = sum;
        recoverAction = () -> showInstrucCannonFireSmallPirates(sum);
        synchronized (outputLock) {
            Pirates piratesCard = (Pirates) cardInUse;

            out.println("\nThe cannon fire will hit your shipboard at column number " + sum + " from nord. You can activate the shield in this direction (if you have it) to prevent the catastrophe...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "activate_shield_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to activate the shield in the given space");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you want to proceed");
            numLines++;
        }
    }

    /**
     * This method shows the instructions to roll the dice.
     */
    public void showInstrucRollDice() {
        synchronized (stateLock) {
            this.state = State.ROLL_DICE;
        }
        recoverAction = () -> showInstrucRollDice();
        synchronized (outputLock) {

            if(cardInUse.getCardType() == CardName.METEOR_SWARM) {
                MeteorSwarm meteorSwarm = (MeteorSwarm) cardInUse;
                Meteor meteor = (Meteor) meteorSwarm.getMeteor().get(meteorSwarm.getCounter());
                String power;
                if(meteor.getPower() == 1) {
                    power = new String("small");
                }
                else {
                    power = new String("large");
                }
                if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                    System.out.println("\nIt's your turn. You have to roll the dice to decide which number of column the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...");
                    numLines++;
                    numLines++;
                }
                else {
                    System.out.println("\nIt's your turn. You have to roll the dice to decide which number of row the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...");
                    numLines++;
                    numLines++;
                }
                System.out.println(" - write  " + Font.BOLD + "roll_dice" + Font.RESET + "  to roll the dice");
                numLines++;
            }
            else if(cardInUse.getCardType() == CardName.COMBAT_ZONE) {
                CombatZone combatZone = (CombatZone) cardInUse;
                ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZone.getFaseThree()[2];
                Meteor meteor = meteors.get(combatZone.getCounter());

                String power;
                if(meteor.getPower() == 1) {
                    power = new String("light");
                }
                else {
                    power = new String("heavy");
                }
                if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                    System.out.println("\nIt's your turn. You have to roll the dice to decide which number of column the cannon fire will hit. The cannon fire is coming from " +meteor.getDirection() + " and is a " + power + " cannon fire...");
                    numLines++;
                    numLines++;
                }
                else {
                    System.out.println("\nIt's your turn. You have to roll the dice to decide which number of row the cannon fire will hit. The cannon fire is coming from " +meteor.getDirection() + " and is a " + power + " cannon fire...");
                    numLines++;
                    numLines++;
                }
                System.out.println(" - write  " + Font.BOLD + "roll_dice" + Font.RESET + "  to roll the dice");
                numLines++;
            }
            else if(cardInUse.getCardType() == CardName.PIRATES) {
                Pirates pirates = (Pirates) cardInUse;
                int pow = pirates.getShotsPowerArray().get(pirates.getCounter());

                String power;
                if(pow == 1) {
                    power = new String("light");
                }
                else {
                    power = new String("heavy");
                }

                System.out.println("\nIt's your turn. You have to roll the dice to decide which number of column the cannon fire will hit. The cannon fire is coming from nord and is a " + power + " cannon fire...");
                numLines++;
                numLines++;

                System.out.println(" - write  " + Font.BOLD + "roll_dice" + Font.RESET + "  to roll the dice");
                numLines++;
            }
        }
    }

    /**
     * This method shows the instructions while waiting for the roll dice.
     */
    public void showInstrucWaitRollDice() {
        synchronized (stateLock) {
            this.state = State.WAIT_ROLL_DICE;
        }

        recoverAction = () -> showInstrucWaitRollDice();
        synchronized (outputLock) {
            if(cardInUse.getCardType() == CardName.METEOR_SWARM) {
                MeteorSwarm meteorSwarm = (MeteorSwarm) cardInUse;
                Meteor meteor = (Meteor) meteorSwarm.getMeteor().get(meteorSwarm.getCounter());
                String power;
                if(meteor.getPower() == 1) {
                    power = new String("small");
                }
                else {
                    power = new String("large");
                }
                if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                    System.out.println("\nWait for the leader to roll the dice to decide which number of column the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...");
                    numLines++;
                }
                else {
                    System.out.println("\nWait for the leader to roll the dice to decide which number of row the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...");
                }
                numLines++;
            }
            else if(cardInUse.getCardType() == CardName.PIRATES) {
                Pirates pirates = (Pirates) cardInUse;
                int pow = pirates.getShotsPowerArray().get(pirates.getCounter());

                String power;
                if(pow == 1) {
                    power = new String("light");
                }
                else {
                    power = new String("heavy");
                }
                System.out.println("\nWait for the leader to roll the dice to decide which number of column the meteor will hit. The cannon fire is coming from nord and is a " + power + " cannon fire...");
                numLines++;
            }
        }
    }

    @Override
    public void showMeteorHit(Card card, boolean isHit, int sum) {
        this.cardInUse = card;
        synchronized (stateLock) {
            synchronized (outputLock) {
                clearLines();

                if(card.getCardType() == CardName.METEOR_SWARM) {
                    if(isHit == false) {
                        for(int i=0; i<20; i++) {
                            out.printf("\033[%d;%dH", i+20, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }
                        out.print("\033[20;0H");
                        out.flush();

                        this.state = State.WAIT_METEOR;

                        out.println("\nThe meteor is coming from " + sum + " and it will not hit your shipboard. Wait the other players...");
                        numLines++;
                        numLines++;
                        notifyObserver(obs -> obs.onUpdateMeteorSwarmChoice(cardInUse, sum));
                    }
                    else {
                        for(int i=0; i<20; i++) {
                            out.printf("\033[%d;%dH", i+20, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }
                        out.print("\033[20;0H");
                        out.flush();

                        Meteor meteor = ((MeteorSwarm) cardInUse).getMeteor().get(((MeteorSwarm) cardInUse).getCounter()-1);
                        if(meteor.getPower() == 1) {
                            this.showInstrucMeteorSwarmSmall(sum);

                        }
                        else {
                            this.showInstrucMeteorSwarmLarge(sum);
                        }
                    }
                }
                else if (card.getCardType() == CardName.COMBAT_ZONE) {
                    if(isHit == false) {
                        for(int i=0; i<20; i++) {
                            out.printf("\033[%d;%dH", i+20, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }
                        out.print("\033[20;0H");
                        out.flush();

                        this.state = State.WAIT_METEOR;

                        out.println("\nThe cannon fire is coming from " + sum + " and it will not hit your shipboard...");
                        numLines++;
                        numLines++;
                        notifyObserver(obs -> obs.onUpdateCombatZoneChoice(cardInUse, sum));
                    }
                    else {
                        for(int i=0; i<20; i++) {
                            out.printf("\033[%d;%dH", i+20, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }
                        out.print("\033[20;0H");
                        out.flush();


                        CombatZone combatZoneCard = (CombatZone) card;
                        ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];
                        Meteor meteor = meteors.get(combatZoneCard.getCounter() - 1);

                        if(meteor.getPower() == 1) {
                            this.showInstrucCannonFireSmall(sum);

                        }
                        else {
                            notifyObserver(obs -> obs.onUpdateCombatZoneChoice(cardInUse, sum));
                        }
                    }
                }
                else if (card.getCardType() == CardName.PIRATES) {
                    if(isHit == false) {
                        for(int i=0; i<20; i++) {
                            out.printf("\033[%d;%dH", i+20, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }
                        out.print("\033[20;0H");
                        out.flush();

                        this.state = State.WAIT_METEOR;

                        out.println("\nThe cannon fire is coming from " + sum + " and it will not hit your shipboard...");
                        numLines++;
                        numLines++;
                        notifyObserver(obs -> obs.onUpdateDefeatedPiratesChoice(cardInUse, sum));
                    }
                    else {
                        for(int i=0; i<20; i++) {
                            out.printf("\033[%d;%dH", i+20, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }
                        out.print("\033[20;0H");
                        out.flush();

                        Pirates piratesCard = (Pirates) card;
                        int pow = piratesCard.getShotsPowerArray().get(piratesCard.getCounter()-1);

                        if(pow == 1) {
                            this.showInstrucCannonFireSmallPirates(sum);

                        }
                        else {
                            notifyObserver(obs -> obs.onUpdateDefeatedPiratesChoice(cardInUse, sum));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void showGoods(Card card, ArrayList<Color> tempGoodsBlock) {
        synchronized (outputLock) {
            numLines = 0;
            for(int i = 0; i < 27; i++) {
                out.printf("\033[%d;%dH", 20+i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[20;0H");
            out.flush();

            out.println("\nYou can now decide to put every block in your ship board one by one.\n");
            if(tempGoodsBlock.get(0) == Color.RED) {
                out.println("Goods block received:  " + Font.RED + "■" + Font.RESET);
                out.flush();
            }
            else if (tempGoodsBlock.get(0) == Color.BLUE) {
                out.println("Goods block received:  " + Font.BLUE + "■" + Font.RESET);
                out.flush();
            }
            else if (tempGoodsBlock.get(0) == Color.GREEN) {
                out.println("Goods block received:  " + Font.GREEN + "■" + Font.RESET);
                out.flush();
            }
            else {
                out.println("Goods block received:  " + Font.YELLOW + "■" + Font.RESET);
                out.flush();
            }

            this.showInstrucGainGoodBlock();
        }
    }


    @Override
    public void showWait(Card card) {
        synchronized (stateLock) {
            if(this.state == State.NOT_IN_TURN) {
                numLines = 0;
                recoverAction = () -> showWait(card);
                synchronized (outputLock) {

                    for (int i = 0; i < 27; i++) {
                        out.printf("\033[%d;%dH", 20 + i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }

                    out.print("\033[20;0H");
                    out.flush();

                    out.println("\n You are not in turn. Wait the other players...");
                    numLines++;
                    numLines++;
                    System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
                    numLines++;
                }
            }
        }
    }

    /**
     * Method used to show instructions to the players that are not in the flight anymore
     * @param card is the card picked
     */
    public void showWait2(Card card) {
        synchronized (stateLock) {
            if(this.state == State.WAIT_FOREVER) {
                numLines = 0;
                recoverAction = () -> showWait(card);
                synchronized (outputLock) {

                    for (int i = 0; i < 27; i++) {
                        out.printf("\033[%d;%dH", 20 + i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }

                    out.print("\033[20;0H");
                    out.flush();

                    out.println("\n You are not in the flight anymore. You can look at the other players ship boards...");
                    numLines++;
                    numLines++;
                    System.out.println(" - write  " + Font.BOLD + "show_ships" + Font.RESET + "  if you want to look at the other players ship boards");
                    numLines++;
                }
            }
        }
    }

    /**
     * This method shows the instruction to gain a good block.
     */
    public void showInstrucGainGoodBlock() {
        synchronized (stateLock) {
            this.state = State.GAIN_GOOD;
        }
        recoverAction = this::showInstrucGainGoodBlock;
        synchronized (outputLock) {
            System.out.println(" - write  " + Font.BOLD + "put_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET + "  if you want to put the goods block in the indicated position. Remember that you only can put goods block in the cargo tiles!!");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "skip" + Font.RESET + "  if you want don't want to put the goods block in the ship");
            numLines++;
        }
    }

    @Override
    public void proceedToNextCard(Card card, int num, ArrayList<Card> cardsToPlay) {

        synchronized (stateLock) {
            synchronized (outputLock) {
                if (num == 1) {
                    if (card != null && (card.getCardType() == CardName.METEOR_SWARM || card.getCardType() == CardName.COMBAT_ZONE)) {
                        if (this.state == State.SHOW_SHIPS) {
                            this.cardInUse = card;
                            numLines = 0;

                            for (int i = 0; i < 47; i++) {
                                out.printf("\033[%d;%dH", i, 0);
                                out.flush();
                                out.print("\033[K");
                                out.flush();
                            }

                            out.print("\033[0;0H");

                            System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                            DrawTui drawTui = new DrawTui();

                            String[] lines;
                            lines = drawTui.drawCard(card);

                            for (String line : lines) {
                                out.println(line);
                            }

                            System.out.println("\nThe effect of the card is finished...");

                            numLines = 0;
                            this.showInstrucProceed();
                        } else {
                            this.cardInUse = card;
                            numLines = 0;

                            if (card != null) {
                                if(this.state == State.NOT_IN_TURN) {
                                    for (int i = 0; i < 10; i++) {
                                        out.printf("\033[%d;%dH", i+20, 0);
                                        out.flush();
                                        out.print("\033[K");
                                        out.flush();
                                    }
                                    out.print("\033[20;0H");
                                    out.flush();
                                }
                                else {
                                    out.print("\033[22;0H");
                                    out.flush();
                                }

                                System.out.println("\nThe effect of the card is finished...");
                            }

                            numLines = 0;
                            this.showInstrucProceed();
                        }

                        if(num == 1) {
                            this.state = State.PROCEED;
                        }
                        else if(num == 2) {
                            this.state = State.PROCEED_PHASE;
                        }

                    } else {
                        this.cardInUse = card;
                        numLines = 0;

                        for (int i = 0; i < 47; i++) {
                            out.printf("\033[%d;%dH", i, 0);
                            out.flush();
                            out.print("\033[K");
                            out.flush();
                        }

                        out.print("\033[0;0H");

                        if (card != null) {
                            System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);
                        } else {
                            System.out.println(Font.BOLD + "\nThese are the cards.\n" + Font.RESET);
                        }

                        DrawTui drawTui = new DrawTui();

                        String[] lines;
                        if (card != null) {
                            lines = drawTui.drawCard(card);
                        } else {
                            if (this.flightType == FlightType.FIRST_FLIGHT) {
                                lines = drawTui.drawCardsDeck(8);
                            } else {
                                lines = drawTui.drawCardsDeck(12);
                            }
                        }

                        for (String line : lines) {
                            out.println(line);
                        }

                        if (card != null) {
                            System.out.println("\nThe effect of the card is finished...");
                        }

                        numLines = 0;
                        this.showInstrucProceed();

                        if(num == 1) {
                            this.state = State.PROCEED;
                        }
                        else if(num == 2) {
                            this.state = State.PROCEED_PHASE;
                        }
                    }
                } else if (num == 2) {
                    this.cardInUse = card;
                    numLines = 0;

                    for (int i = 0; i < 47; i++) {
                        out.printf("\033[%d;%dH", i, 0);
                        out.flush();
                        out.print("\033[K");
                        out.flush();
                    }

                    out.print("\033[0;0H");

                    System.out.println(Font.BOLD + "\nThis is the card picked.\n" + Font.RESET);

                    DrawTui drawTui = new DrawTui();

                    String[] lines;
                    lines = drawTui.drawCard(card);

                    for (String line : lines) {
                        out.println(line);
                    }

                    System.out.println("\nThe phase is finished...");

                    numLines = 0;
                    this.showInstrucNextPhase();

                    if(num == 1) {
                        this.state = State.PROCEED;
                    }
                    else if(num == 2) {
                        this.state = State.PROCEED_PHASE;
                    }
                }
            }
        }
    }

    public void showInstrucProceed() {
        synchronized (stateLock) {
            this.state = State.PROCEED;
        }
        recoverAction = this::showInstrucProceed;

        synchronized (outputLock) {
            System.out.println(" - write  " + Font.BOLD + "proceed" + Font.RESET + "  if you to proceed with the next card");
            numLines++;
            if(this.flightType == FlightType.STANDARD_FLIGHT) {
                System.out.println(" - write  " + Font.BOLD + "retire" + Font.RESET + "  if you to abandoned the flight");
                numLines++;
            }
        }
    }


    /**
     * This method shows the instruction for the next phase in the combat zone card.
     */
    public void showInstrucNextPhase() {
        synchronized (stateLock) {
            this.state = State.PROCEED_PHASE;
        }

        recoverAction = this::showInstrucNextPhase;
        synchronized (outputLock) {
            System.out.println(" - write  " + Font.BOLD + "next_phase" + Font.RESET + "  if you to proceed with the next phase");
            numLines++;
        }
    }

    @Override
    public void notProceed() {
        synchronized (stateLock) {
            this.state = State.NOT_PROCEED;
        }

        synchronized (outputLock) {
            this.cardInUse = null;
            for (int i = 0; i < 27; i++) {
                out.printf("\033[%d;%dH", 20 + i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[20;0H");
            out.flush();
            out.println("Waiting for other players to proceed...");
        }
    }


    @Override
    public void changeState(int num) {
        synchronized (stateLock) {
            if (num == 1) {
                this.state = State.NOT_IN_TURN;
            }
            else if (num == 2){
                this.state = State.REPAIR;
            }
            else if (num == 3){
                this.state = State.WAIT_REPAIR;
            }
        }
    }


    @Override
    public void setRemoveBattery() {
        synchronized (stateLock) {
            state = State.REMOVE_BATTERY;
        }
        synchronized (outputLock) {
            for (int i = 0; i < 27; i++) {
                out.printf("\033[%d;%dH", 20 + i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[20;0H");
            out.flush();
            numLines = 0;
            this.showInstrucRemoveBattery();
        }

    }

    /**
     * This method shows the instruction to remove a battery.
     */
    public void showInstrucRemoveBattery() {
        recoverAction = this::showInstrucRemoveBattery;
        synchronized (stateLock) {
            this.state = State.REMOVE_BATTERY;
        }

        synchronized (outputLock) {
            System.out.println("\nYou have to remove a battery...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "remove_battery_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET+ "  to remove a battery from the given position");
            numLines++;
        }
    }

    @Override
    public void setRemoveGood() {
        synchronized (stateLock) {
            state = State.REMOVE_GOOD;
        }
        synchronized (outputLock) {
            for (int i = 0; i < 27; i++) {
                out.printf("\033[%d;%dH", 20 + i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[20;0H");
            out.flush();
            numLines = 0;
            this.showInstrucRemoveGood();
        }
    }

    /**
     * This method shows the instruction to remove a goods block.
     */
    public void showInstrucRemoveGood() {
        recoverAction = this::showInstrucRemoveGood;
        synchronized (stateLock) {
            this.state = State.REMOVE_GOOD;
        }
        synchronized (outputLock) {
            System.out.println("\nYou have to remove from your ship the rarest goods block...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "remove_good_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET+ "  to remove a goods block from the given position");
            numLines++;
        }
    }


    @Override
    public void setRemoveFigure() {
        synchronized (stateLock) {
            state = State.REMOVE_FIGURE;
        }
        synchronized (outputLock) {
            for (int i = 0; i < 27; i++) {
                out.printf("\033[%d;%dH", 20 + i, 0);
                out.flush();
                out.print("\033[K");
                out.flush();
            }

            out.print("\033[20;0H");
            out.flush();
            numLines = 0;
            this.showInstrucRemoveFigure();
        }
    }

    /**
     * This method shows the instruction to remove a figure.
     */
    public void showInstrucRemoveFigure() {
        recoverAction = this::showInstrucRemoveFigure;
        synchronized (stateLock) {
            this.state = State.REMOVE_FIGURE;
        }

        synchronized (outputLock) {
            System.out.println("\nYou have to remove a figure...");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "remove_figure_" + Font.RESET + Font.BOLD_ITALIC + "numRow_numCol" + Font.RESET+ "  to remove a figure from the given position");
            numLines++;
        }
    }

    /**
     * This method shows the instruction to insert a figure in the first flight.
     */
    public void showInstructPutFigureFirst() {
        synchronized (stateLock) {
            this.state = State.PUT_FIGURES;
        }
        recoverAction = this::showInstructPutFigureFirst;
        if(countErr==1)
        {
            numLines=3;
        }
        else
        {
            numLines=0;
        }
        synchronized (outputLock) {
            System.out.print("\033[s");
            System.out.flush();
            System.out.print("\033[48;100H");
            System.out.flush();
            out.print(Font.BOLD + "INSTRUCTIONS:  " + Font.RESET);
            System.out.print("\033[49;100H");
            out.print(Font.BOLD + "you can put maximum two astronauts for every cabine of your ship!!  " + Font.RESET);
            System.out.flush();
            System.out.print("\033[u");
            System.out.flush();

            System.out.println("\nYou have to populate your own ship!!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_astronaut_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to put an astronaut in a cabine");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "finish" + Font.RESET + "  if you are ready to play");
            numLines++;
        }
    }

    /**
     * This method shows the instruction to insert a figure in the standard flight.
     */
    public void showInstructPutFigureStandard() {
        synchronized (stateLock) {
            this.state = State.PUT_FIGURES;
        }
        recoverAction = this::showInstructPutFigureStandard;
        if(countErr==1)
        {
            numLines=2;
        }
        else
        {
            numLines=0;
        }
        synchronized (outputLock) {
            System.out.print("\033[s");
            System.out.flush();
            System.out.print("\033[48;100H");
            System.out.flush();
            out.print(Font.BOLD + "INSTRUCTIONS:  " + Font.RESET);
            System.out.print("\033[49;100H");
            out.print(Font.BOLD + "A cabin that is not joined to a life support system gets 2 humans." + Font.RESET);
            System.out.flush();
            System.out.print("\033[50;100H");
            out.print(Font.BOLD + "A cabin joined to a life support system gets 2 humans or 1 alien of the corresponding color." + Font.RESET);
            System.out.flush();
            System.out.print("\033[51;100H");
            out.print(Font.BOLD + "A cabin joined to one life support system of each color gets either 2 humans or 1 purple alien or 1 brown alien." + Font.RESET);
            System.out.flush();
            System.out.print("\033[52;100H");
            out.print(Font.BOLD + "Your board can have no more than 1 alien of each color" + Font.RESET);
            System.out.flush();
            System.out.print("\033[53;100H");
            out.print(Font.BOLD + "You cannot put an alien in your starting cabine" + Font.RESET);
            System.out.flush();
            System.out.print("\033[u");
            System.out.flush();

            System.out.println("\nYou have to populate your own ship. If you don't insert astronaut, you will be automatically eliminated from the flight!!!!!");
            numLines++;
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_astronaut_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to put an astronaut in a cabine");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_purple_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to put a purple alien in a cabine");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "put_brown_" + Font.RESET + Font.BOLD + "numRow_numCol" + Font.RESET + "  if you want to put a brown alien in a cabine");
            numLines++;
            System.out.println(" - write  " + Font.BOLD + "finish" + Font.RESET + "  if you are ready to play");
            numLines++;
        }
    }

    @Override
    public void updatePlayerParametres(FlightBoard flightBoard) {
        out.print("\033[s");
        out.flush();
        DrawTui drawTui = new DrawTui();

        drawTui.drawFlightBoard(flightBoard);

        for(Player player : flightBoard.getPlayersMap().keySet()) {
            if(player.getId().equals(clientId)) {
                drawTui.drawPlayerParametres(player);
            }
        }

        out.print("\033[u");
        out.flush();
    }

    @Override
    public void showWinner(ArrayList<Player> players, boolean winner) {
        clearPage();
        if(winner) {
            out.print("\n\n" +
                    " __       __  ______  __    __  __    __  ________  _______  \n" +
                    "/  |  _  /  |/      |/  \\  /  |/  \\  /  |/        |/       \\ \n" +
                    "$$ | / \\ $$ |$$$$$$/ $$  \\ $$ |$$  \\ $$ |$$$$$$$$/ $$$$$$$  |\n" +
                    "$$ |/$  \\$$ |  $$ |  $$$  \\$$ |$$$  \\$$ |$$ |__    $$ |__$$ |\n" +
                    "$$ /$$$  $$ |  $$ |  $$$$  $$ |$$$$  $$ |$$    |   $$    $$< \n" +
                    "$$ $$/$$ $$ |  $$ |  $$ $$ $$ |$$ $$ $$ |$$$$$/    $$$$$$$  |\n" +
                    "$$$$/  $$$$ | _$$ |_ $$ |$$$$ |$$ |$$$$ |$$ |_____ $$ |  $$ |\n" +
                    "$$$/    $$$ |/ $$   |$$ | $$$ |$$ | $$$ |$$       |$$ |  $$ |\n" +
                    "$$/      $$/ $$$$$$/ $$/   $$/ $$/   $$/ $$$$$$$$/ $$/   $$/ \n" +
                    "                                                             \n" +
                    "                                                             \n" +
                    "                                                             \n");
        }
        else {
            out.print("\n\n" +
                    " __    __   ______   ________        __       __  ______  __    __  __    __  ________  _______  \n" +
                    "/  \\  /  | /      \\ /        |      /  |  _  /  |/      |/  \\  /  |/  \\  /  |/        |/       \\ \n" +
                    "$$  \\ $$ |/$$$$$$  |$$$$$$$$/       $$ | / \\ $$ |$$$$$$/ $$  \\ $$ |$$  \\ $$ |$$$$$$$$/ $$$$$$$  |\n" +
                    "$$$  \\$$ |$$ |  $$ |   $$ |         $$ |/$  \\$$ |  $$ |  $$$  \\$$ |$$$  \\$$ |$$ |__    $$ |__$$ |\n" +
                    "$$$$  $$ |$$ |  $$ |   $$ |         $$ /$$$  $$ |  $$ |  $$$$  $$ |$$$$  $$ |$$    |   $$    $$< \n" +
                    "$$ $$ $$ |$$ |  $$ |   $$ |         $$ $$/$$ $$ |  $$ |  $$ $$ $$ |$$ $$ $$ |$$$$$/    $$$$$$$  |\n" +
                    "$$ |$$$$ |$$ \\__$$ |   $$ |         $$$$/  $$$$ | _$$ |_ $$ |$$$$ |$$ |$$$$ |$$ |_____ $$ |  $$ |\n" +
                    "$$ | $$$ |$$    $$/    $$ |         $$$/    $$$ |/ $$   |$$ | $$$ |$$ | $$$ |$$       |$$ |  $$ |\n" +
                    "$$/   $$/  $$$$$$/     $$/          $$/      $$/ $$$$$$/ $$/   $$/ $$/   $$/ $$$$$$$$/ $$/   $$/ \n" +
                    "                                                                                                 \n" +
                    "                                                                                                 \n" +
                    "                                                                                                 \n");
        }

        out.print("\n\n\n\nThis are the players and their cosmic credits gained (or lost) during the game...\n");
        for(Player player : players) {
            out.println("Player: " + player.getNickname() + "\t\tCosmic credits: " + player.getCosmicCredit());
        }
    }

    @Override
    public void disconnected(String disconnectionError) {
        clearPage();
        this.state = State.DISCONNECTED;
        out.print("\n\n" + Font.RED + disconnectionError + Font.RESET);
    }

    @Override
    public void sendMessageToClient(String clientId, Message message) {}
}