package controller;

import enumerations.FlightType;
import model.card.Card;
import model.shipBoard.ShipBoard;
import network.messages.*;
import network.structure.Client;
import network.structure.ClientRMI;
import network.structure.ClientSocket;
import observer.Observable;
import observer.Observer;
import observer.ViewObserver;
import view.View;
import view.tui.Tui;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represent the generic client controller, used to receive and throw the message from and to the server.
 */
public class ClientController extends Observable implements Observer, ViewObserver {
    private final View view;
    private Client client;
    private String clientId;
    private String nickname;
    private final ExecutorService taskQueue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final boolean isSocket;

    public ClientController(View view, boolean isSocket) {
        this.view = view;
        taskQueue = Executors.newSingleThreadExecutor();
        this.isSocket = isSocket;
    }

    @Override
    public void onUpdateServerInfo(HashMap<String, String> serverInfo) {
        if(isSocket) {
            client = new ClientSocket(serverInfo.get("address"), Integer.parseInt(serverInfo.get("port")));
            ((ClientSocket)client).addObserver(this);
        } else {
            try {
                client = new ClientRMI(serverInfo.get("address"), Integer.parseInt(serverInfo.get("port")));
                ((ClientRMI)client).addObserver(this);
                try{client.connection();}
                catch(IOException e){System.err.println("Cannot connect to server, closing");}
                executorService.submit((ClientRMI) client);
            }
            catch(RemoteException e){
                System.err.println("Cannot create the client, exiting");
                System.exit(1);
            }
        }
        try {
            client.ping();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateNickname(String nickname) {
        this.nickname = nickname;
        try {
            client.sendMessage(new UsernameMessage(this.clientId, this.nickname));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createGame() {
        try {
            client.sendMessage(new CreateGameMessage(this.clientId, this.nickname));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinGame(String gameId) {
        try {
            client.sendMessage(new JoinGameMessage(this.clientId, this.nickname, gameId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshGameOnServer() {
        try {
            client.sendMessage(new RefreshGameOnServerMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateMaxPlayerAndFlightType(int maxPlayers, FlightType flightType) {
        try {
            client.sendMessage(new MaxPlayersForGameMessage(this.clientId, maxPlayers));
            client.sendMessage(new ChooseFlightTypeMessage(this.clientId, flightType));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePickTile(int tileId) {
        try {
            client.sendMessage(new PickUpTileMessage(this.clientId, tileId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePickTileFromShip(int numRow, int numCol) {
        try {
            client.sendMessage(new PickTileFromShipMessage(this.clientId, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateShowShips() {
        try {
            client.sendMessage(new ShowAllShipBoardsMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateFinishedBuild() {
        try {
            client.sendMessage(new ShowAllShipBoardsMessage(this.clientId));
            client.sendMessage(new FinishedBuildingMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateFinishedPopulate() {
        try {
            client.sendMessage(new FinishedPopulateMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRemoveTile(int numRow, int numCol) {
        try {
            client.sendMessage(new RemoveTileMessage(this.clientId, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpdateTimerMessage() {
        try {
            client.sendMessage(new TimerMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePutTileInShip(int row, int col) {
        try {
            client.sendMessage(new PutTileInShipMessage(this.clientId, row, col));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePutAstronautInShip(int row, int col) {
        try {
            client.sendMessage(new PutFigureInShipMessage(this.clientId, row, col));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePutPurpleInShip(int row, int col) {
        try {
            client.sendMessage(new PutPurpleInShipMessage(this.clientId, row, col));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePutBrownInShip(int row, int col) {
        try {
            client.sendMessage(new PutBrownInShipMessage(this.clientId, row, col));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePickCardPile(int num) {
        try {
            client.sendMessage(new PickUpCardPileMessage(this.clientId, num));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePickCard() {
        try {
            client.sendMessage(new PickUpCardMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePutTileInDeck() {
        try {
            client.sendMessage(new PutTileBackMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateSetDirection(String direction) {
        try {
            client.sendMessage(new ChangeTileDirectionMessage(this.clientId, direction));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateStopWatchingShips(Card card) {
        try {
            client.sendMessage(new StopWatchingShipsMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateStopWatchingCardPile() {
        try {
            client.sendMessage(new PutCardPileBackMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateFlightType(FlightType flightType) {
        try {
            client.sendMessage(new ChooseFlightTypeMessage(this.clientId, flightType));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePlanetChoice(Card card, String choice, int num) {
        try {
            client.sendMessage(new PlanetChoiceMessage(this.clientId, card, choice, num));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateSmugglersChoice(Card card) {
        try {
            client.sendMessage(new SmugglersChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateSlaversChoice(Card card) {
        try {
            client.sendMessage(new SlaversChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePiratesChoice(Card card) {
        try {
            client.sendMessage(new PiratesChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateAbandonedStationChoice(Card card, String choice) {
        try {
            client.sendMessage(new AbandonedStationChoiceMessage(this.clientId, card, choice));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateAbandonedShipChoice(Card card, String choice) {
        try {
            client.sendMessage(new AbandonedShipChoiceMessage(this.clientId, card, choice));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateOpenSpaceChoice(Card card) {
        try {
            client.sendMessage(new OpenSpaceChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateMeteorSwarmChoice(Card card, int sum) {
        try {
            client.sendMessage(new MeteorSwarmChoiceMessage(this.clientId, card, sum));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateCombatZoneChoice(Card card, int sum) {
        try {
            client.sendMessage(new CombatZoneChoiceMessage(this.clientId, card, sum));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateDefeatedPiratesChoice(Card card, int sum) {
        try {
            client.sendMessage(new DefeatedPiratesMessage(this.clientId, card, sum));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePhase1Choice(Card card) {
        try {
            client.sendMessage(new Fase1ChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePhase2Choice(Card card) {
        try {
            client.sendMessage(new Fase2ChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdatePhase3Choice(Card card) {
        try {
            client.sendMessage(new Fase3ChoiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateGainGood(Card card, String choice, int numRow, int numCol) {
        try {
            client.sendMessage(new GainGoodMessage(this.clientId, choice, card, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateProceed() {
        try {
            client.sendMessage(new WaitProceedMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRetire() {
        try {
            client.sendMessage(new RetireMessage(this.clientId));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateNextPhase(Card card) {
        try {
            client.sendMessage(new WaitNextPhaseMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateActivateCannon(Card card, int numRow, int numCol) {
        try {
            client.sendMessage(new ActivateCannonMessage(this.clientId, card, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateActivateEngine(Card card, int numRow, int numCol) {
        try {
            client.sendMessage(new ActivateEngineMessage(this.clientId, card, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateActivateShield(Card card, int numRow, int numCol) {
        try {
            client.sendMessage(new ActivateShieldMessage(this.clientId, card, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRemoveBattery(Card card, int numRow, int numCol, int sum) {
        try {
            client.sendMessage(new RemoveBatteryMessage(this.clientId, card, numRow, numCol, sum));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRemoveGood(Card card, int numRow, int numCol) {
        try {
            client.sendMessage(new RemoveGoodMessage(this.clientId, card, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRemoveFigure(Card card, int numRow, int numCol) {
        try {
            client.sendMessage(new RemoveFigureMessage(this.clientId, card, numRow, numCol));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRollDice(Card card) {
        try {
            client.sendMessage(new RollDiceMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateRepaired(Card card) {
        try {
            client.sendMessage(new RepairingShipMessage(this.clientId, card));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method checks if the ip is valid.
     * @param ip is the ip to verify.
     * @return {@code true} if the ip is valid, {@code false} otherwise.
     */
    public static boolean isAddressValid(String ip){
        String PATTERN = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        return ip.matches(PATTERN);
    }

    /**
     * This method checks if the port is valid.
     * @param portString is the port to verify.
     * @return {@code true} if the port is valid, {@code false} otherwise.
     */
    public static boolean isPortValid(String portString){
        try{
            int port = Integer.parseInt(portString);
            return port >= 1 && port <= 65535;
        } catch (NumberFormatException e){
            return false;
        }
    }

    @Override
    public void update(Message message) {
        if(message instanceof AskNicknameMessage) {
            AskNicknameMessage askNicknameMessage = (AskNicknameMessage) message;
            this.clientId = askNicknameMessage.getClientId();
        }

        if (message instanceof ClientDisconnectedMessage) {
            try {
                client.disconnect(false);
            } catch (RemoteException e) {
                System.err.println("Error during disconnection: " + e.getMessage());
            }
        }

        view.addServerMessage(message);

        if(message instanceof AskMaxPlayerAndFlightTypeMessage) {
            view.askMaxPlayerAndFlightType();
        }
    }
}
