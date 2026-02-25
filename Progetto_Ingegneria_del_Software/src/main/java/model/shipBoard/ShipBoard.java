package model.shipBoard;

import enumerations.*;
import exceptions.DisconnectedPartsException;
import exceptions.InvalidConnectorException;
import exceptions.InvalidTilePlacementException;
import exceptions.MultipleValidationErrorsException;
import model.card.Card;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.card.cardsType.MeteorSwarm;
import model.player.Player;
import model.tiles.ComponentTile;
import model.GameModel;
import model.tiles.componentTile.*;
import network.messages.GenericErrorMessage;
import network.structure.ServerMain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * This clas represents the shipboard
 */
public class ShipBoard implements Serializable {
    private final ShipBoardSpace[][] shipBoardSpaces;
    private HashMap<ShipBoardSpace, int[]> spacePositions;
    private final int shiftRow = 5; // in the game the rows goes from 5 to 9
    private final int shiftCol = 4; // in the game the columns goes from 4 to 10
    private boolean hit;
    private HashMap<String, Boolean> coveredDirection; // covered directions by the shields
    private int numLostTiles;
    private boolean correctShip;
    private boolean hasPurpleAlien;
    private boolean hasBrownAlien;

    /**
     * Constructor.
     * @param flightType is the flight type chosen for the game.
     * @param color is the color of the player.
     */
    public ShipBoard(FlightType flightType, Color color) {
        this.shipBoardSpaces = new ShipBoardSpace[5][7];
        this.spacePositions = new HashMap<>();
        this.hit = false;
        this.coveredDirection = new HashMap<>();
        coveredDirection.put("nord", false);
        coveredDirection.put("sud", false);
        coveredDirection.put("est", false);
        coveredDirection.put("ovest", false);
        this.numLostTiles = 0;
        this.correctShip = false;

        if(flightType == FlightType.FIRST_FLIGHT) {
            // Create the First Flight ShipBoard
            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 7; j++) {
                    if((i == 0 && j == 0) || (i == 0 && j == 1) || (i == 0 && j == 2) || (i == 0 && j == 4) || (i == 0 && j == 5) || (i == 0 && j == 6) || (i == 1 && j == 0) || (i == 1 && j == 1) || (i == 1 && j == 5) || (i == 1 && j == 6) || (i == 2 && j == 0) || (i == 2 && j == 6) || (i == 3 && j == 0) || (i == 3 && j == 6) || (i == 4 && j == 0) || (i == 4 && j == 3) || (i == 4 && j == 6)) {
                        shipBoardSpaces[i][j] = new ShipBoardSpace(TypeSpace.UNUSABLE);
                        spacePositions.put(shipBoardSpaces[i][j], new int[]{i, j});
                    }
                    else {
                        shipBoardSpaces[i][j] = new ShipBoardSpace(TypeSpace.USABLE);
                        spacePositions.put(shipBoardSpaces[i][j], new int[]{i, j});
                    }
                }
            }
        }
        else if(flightType == FlightType.STANDARD_FLIGHT) {
            // Create the Standard Flight ShipBoard
            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 7; j++) {
                    if((i == 0 && j == 0) || (i == 0 && j == 1) || (i == 0 && j == 3) || (i == 1 && j == 0) || (i == 1 && j == 6) || (i == 4 && j == 3)) {
                        shipBoardSpaces[i][j] = new ShipBoardSpace(TypeSpace.UNUSABLE);
                        spacePositions.put(shipBoardSpaces[i][j], new int[]{i, j});
                    }
                    else if((i == 0 && j == 5) || (i == 0 && j == 6)) {
                        shipBoardSpaces[i][j] = new ShipBoardSpace(TypeSpace.RESERVE);
                        spacePositions.put(shipBoardSpaces[i][j], new int[]{i, j});
                    }
                    else {
                        shipBoardSpaces[i][j] = new ShipBoardSpace(TypeSpace.USABLE);
                        spacePositions.put(shipBoardSpaces[i][j], new int[]{i, j});
                    }
                }
            }
        }

        switch (color) {
            case RED:
                this.putObjectIn(getSpace(2,3), new StartingCabine(TileName.STARTING_CABINE, "52.jpg", 3, 3, 3,3, color));
                break;
            case YELLOW:
                this.putObjectIn(getSpace(2,3), new StartingCabine(TileName.STARTING_CABINE, "61.jpg", 3, 3, 3,3, color));
                break;
            case GREEN:
                this.putObjectIn(getSpace(2,3), new StartingCabine(TileName.STARTING_CABINE, "34.jpg", 3, 3, 3,3, color));
                break;
            case BLUE:
                this.putObjectIn(getSpace(2,3), new StartingCabine(TileName.STARTING_CABINE, "33.jpg", 3, 3, 3,3, color));
                break;
            default: break;
        }
    }

    /**
     * @return {@code true} if the player has the purple alien in his ship, {@code false} otherwise.
     */
    public boolean getHasPurpleAlien()
    {
        return hasPurpleAlien;
    }

    /**
     * Set the purple alien.
     * @param hasPurpleAlien is the value that has to be set.
     */
    public void setHasPurpleAlien(boolean hasPurpleAlien)
    {
        this.hasPurpleAlien = hasPurpleAlien;
    }

    /**
     * @return {@code true} if the player has the brown alien in his ship, {@code false} otherwise.
     */
    public boolean getHasBrownAlien()
    {
        return hasBrownAlien;
    }

    /**
     * Set the brown alien.
     * @param hasBrownAlien is the value that has to be set.
     */
    public void setHasBrownAlien(boolean hasBrownAlien)
    {
        this.hasBrownAlien = hasBrownAlien;
    }

    /**
     * Method that returns the specified {@link ShipBoardSpace}.
     * @param x is the x position on the ship board.
     * @param y is the y position in the ship board.
     * @return the ship board space associated.
     */
    public ShipBoardSpace getSpace(int x, int y) {
        return shipBoardSpaces[x][y];
    }

    /**
     * This method allows the player to put a component tile in a specified space (if that's possible).
     * @param shipBoardSpace is the space in consideration.
     * @param objectContained is the tile that the player wants to put in.
     */
    public void putObjectIn(ShipBoardSpace shipBoardSpace, ComponentTile objectContained) {
        if (shipBoardSpace.getTypeSpace() != TypeSpace.UNUSABLE) {
            if(shipBoardSpace.getComponent() == null) {
                shipBoardSpace.insertComponent(objectContained);
            }
        }
    }

    /**
     * This method allows the player to pick up the object contained in the reserve spaces on the ShipBoard
     * @param row is the row on the ship board.
     * @param col is the col of the ship board
     * @return the component tile contained or null if there is no tile in the space.
     */
    public ComponentTile pickUpObjectFrom(int row, int col) {
        ShipBoardSpace space = getSpace(row, col);
        if (space.getTypeSpace() == TypeSpace.RESERVE) {
            ComponentTile componentPicked = space.getComponent();
            if (componentPicked != null) {
                space.removeComponent();
            }
            return componentPicked;
        }
        else {
            return null;
        }
    }

    /**
     * @return if the shipboard is hit
     */
    public boolean isHit() {
        return hit;
    }

    /**
     * Setter method that sets the hit attribute
     * @param hit is the value of the attribute
     */
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    /**
     * Getter method that counts and returns the number of exposed connectors on this shipboard.
     * @return the number of exposed connectors.
     */
    public int numExposedConnectors(){
        int exposedConnectors=0;
        for(int i=0; i<5; i++) {
            for(int j=0; j<7; j++) {
                if(this.getSpace(i,j).getComponent()!=null && this.getSpace(i,j).getTypeSpace()== TypeSpace.USABLE) {
                    switch (this.getSpace(i,j).getComponent().getDirection()) { //Switch based on the direction of the component considered
                        case "nord":
                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=5) { //If it's true it means that there is a connector on that side
                                if(i==0 || this.getSpace(i-1,j).getComponent()==null) { //If tre it means that there is nothing above the tile
                                    exposedConnectors++;
                                }
                            }//End if for the up side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=5) { //If it's true it means that there is a connector on that side
                                if(j==6 || this.getSpace(i,j+1).getComponent()==null) { //If tre it means that there is nothing on the right tile
                                    exposedConnectors++;
                                }
                            }//End if for the right side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=5) { //If it's true it means that there is a connector on that side
                                if(i==4 || this.getSpace(i+1,j).getComponent()==null) { //If tre it means that there is nothing below the tile
                                    exposedConnectors++;
                                }
                            }//End if for the down side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=5) { //If it's true it means that there is a connector on that side
                                if(j==0 || this.getSpace(i,j-1).getComponent()==null) { //If tre it means that there is nothing on the left of the tile
                                    exposedConnectors++;
                                }
                            }//End if for the left side
                            break;

                        case "est":
                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=5) { //If it's true it means that there is a connector on that side
                                if(i==0 || this.getSpace(i-1,j).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the up side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=5) { //If it's true it means that there is a connector on that side
                                if(j==6 || this.getSpace(i,j+1).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the right side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=5) { //If it's true it means that there is a connector on that side
                                if(i==4 || this.getSpace(i+1,j).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the down side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=5) { //If it's true it means that there is a connector on that side
                                if(j==0 || this.getSpace(i,j-1).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the left side
                            break;

                        case"sud":
                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=5) { //If it's true it means that there is a connector on that side
                                if(i==0 || this.getSpace(i-1,j).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the up side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=5) { //If it's true it means that there is a connector on that side
                                if(j==6 || this.getSpace(i,j+1).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the right side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=5) { //If it's true it means that there is a connector on that side
                                if(i==4 || this.getSpace(i+1,j).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the down side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=5) { //If it's true it means that there is a connector on that side
                                if(j==0 || this.getSpace(i,j-1).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the left side
                            break;

                        case "ovest":
                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=5) { //If it's true it means that there is a connector on that side
                                if(i==0 || this.getSpace(i-1,j).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the up side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=5) { //If it's true it means that there is a connector on that side
                                if(j==6 || this.getSpace(i,j+1).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the right side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=5) { //If it's true it means that there is a connector on that side
                                if(i==4 || this.getSpace(i+1,j).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the down side

                            if(this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 && this.getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=5) { //If it's true it means that there is a connector on that side
                                if(j==0 || this.getSpace(i,j-1).getComponent()==null) {
                                    exposedConnectors++;
                                }
                            }//End if for the left side
                            break;

                        default: break;
                    }
                }//End if component != null
            }//End for for the columns
        }//End for for the rows

        return exposedConnectors;
    }

    /**
     * Method that activates a cannon.
     * @param row is the row selected by the player.
     * @param col is the column selected by the player.
     * @return {@code 1} if the activation is ok, {@code 2} if the cannon is already active, {@code 3} if the tile is not correct.
     */
    public int activateCannon(int row, int col) {
        if(this.getSpace(row-shiftRow, col-shiftCol).getComponent() == null || (!(this.getSpace(row - shiftRow, col - shiftCol).getComponent().getName().equals(TileName.CANNON)))) {
            return 3;
        }
        Cannon cannon = (Cannon) this.getSpace(row - shiftRow, col - shiftCol).getComponent();
        if(cannon.getActive()) {
            return 2;
        }
        cannon.setActive(true);
        return 1;
    }

    /**
     * Method that calculates the fire strength of the shipboard.
     * @return a double that represents the fire strength.
     */
    public double calculateFireStrength() {
        double fireStrengh = 0;
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 7; j++) {
                if(this.getSpace(i,j).getComponent() != null && this.getSpace(i,j).getComponent().getName().equals(TileName.CANNON)) {
                    Cannon cannon = (Cannon) this.getSpace(i,j).getComponent();
                    if(cannon.getDirection().equals("nord")) {
                        if(!cannon.isDouble()) {
                            fireStrengh = fireStrengh + 1;
                        }
                        else {
                            if(cannon.getActive()) {
                                fireStrengh = fireStrengh + 2;
                            }
                        }
                    }
                    else {
                        if(!cannon.isDouble()) {
                            fireStrengh = fireStrengh + 0.5;
                        }
                        else {
                            if(cannon.getActive()) {
                                fireStrengh = fireStrengh + 1;
                            }
                        }
                    }
                }
            }
        }
        if(getHasPurpleAlien() && fireStrengh>0) {
            fireStrengh=fireStrengh+2;
        }
        return fireStrengh;
    }

    /**
     * Method that restore the double cannons state to inactive.
     */
    public void restoreCannons() {
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 7; j++) {
                if(this.getSpace(i,j).getComponent() != null && this.getSpace(i,j).getComponent().getName().equals(TileName.CANNON)) {
                    Cannon cannon = (Cannon) this.getSpace(i,j).getComponent();
                    if(cannon.isDouble()) {
                        cannon.setActive(false);
                    }
                }
            }
        }
    }

    /**
     * Method that activates an engine.
     * @param row is the row selected by the player.
     * @param col is the column selected by the player.
     * @return {@code 1} if the activation is ok, {@code 2} if the engine is already active, {@code 3} if the tile is not correct.
     */
    public int activateEngine(int row, int col) {
        if(this.getSpace(row-shiftRow, col-shiftCol).getComponent() == null || (!(this.getSpace(row - shiftRow, col - shiftCol).getComponent().getName().equals(TileName.ENGINE)))) {
            return 3;
        }
        Engine engine = (Engine) this.getSpace(row - shiftRow, col - shiftCol).getComponent();
        if(engine.getActive()) {
            return 2;
        }
        engine.setActive(true);
        return 1;
    }

    /**
     * Method that calculates the engine strength of the shipboard.
     * @return an int that represents the engine strength.
     */
    public int calculateEngineStrength() {
        int engineStrengh = 0;
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 7; j++) {
                if(this.getSpace(i,j).getComponent() != null && this.getSpace(i,j).getComponent().getName().equals(TileName.ENGINE)) {
                    Engine engine = (Engine) this.getSpace(i,j).getComponent();
                    if(!engine.isDouble()) {
                        engineStrengh = engineStrengh + 1;
                    }
                    else {
                        if(engine.getActive()) {
                            engineStrengh = engineStrengh + 2;
                        }
                    }
                }
            }
        }
        if(getHasBrownAlien() && engineStrengh>0) {
            engineStrengh=engineStrengh+2;
        }
        return engineStrengh;
    }
    /**
     * Method that restore the double engines state to inactive.
     */
    public void restoreEngines() {
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 7; j++) {
                if(this.getSpace(i,j).getComponent() != null && this.getSpace(i,j).getComponent().getName().equals(TileName.ENGINE)) {
                    Engine engine = (Engine) this.getSpace(i,j).getComponent();
                    if(engine.isDouble()) {
                        engine.setActive(false);
                    }
                }
            }
        }
    }

    /**
     * Method that activates a shield.
     * @param row is the row selected by the player.
     * @param col is the column selected by the player.
     * @return {@code 1} if the activation is ok, {@code 2} if the shield is already active, {@code 3} if the tile is not correct.
     */
    public int activateShield(int row, int col) {
        if(this.getSpace(row-shiftRow, col-shiftCol).getComponent() == null || (!(this.getSpace(row - shiftRow, col - shiftCol).getComponent().getName().equals(TileName.SHIELD)))) {
            return 3;
        }
        Shield shield = (Shield) this.getSpace(row - shiftRow, col - shiftCol).getComponent();
        if(shield.getActive()) {
            return 2;
        }
        shield.setActive(true);
        switch (shield.getDirection()) {
            case "nord":
                coveredDirection.put("nord", true);
                coveredDirection.put("est", true);
                break;
            case "est":
                coveredDirection.put("est", true);
                coveredDirection.put("sud", true);
                break;
            case "sud":
                coveredDirection.put("sud", true);
                coveredDirection.put("ovest", true);
                break;
            case "ovest":
                coveredDirection.put("ovest", true);
                coveredDirection.put("nord", true);
                break;
            default: break;
        }

        return 1;
    }

    /**
     * Method that restore the shield state to inactive, and the covered direction to false.
     */
    public void restoreShields() {
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 7; j++) {
                if(this.getSpace(i,j).getComponent() != null && this.getSpace(i,j).getComponent().getName().equals(TileName.SHIELD)) {
                    Shield shield = (Shield) this.getSpace(i,j).getComponent();
                    shield.setActive(false);
                }
            }
        }
        coveredDirection.put("nord", false);
        coveredDirection.put("est", false);
        coveredDirection.put("sud", false);
        coveredDirection.put("ovest", false);
    }

    /**
     * @return the covered directions by the activation of the shield.
     */
    public HashMap<String, Boolean> getCoveredDirection() {
        return coveredDirection;
    }

    /**
     * @return the number of lost components.
     */
    public int getNumLostTiles() {
        return numLostTiles;
    }

    /**
     * @return the number of lost components.
     */
    public void incrementNumLostTiles() {
        this.numLostTiles++;
    }

    /**
     * Method that removes the external component in the hit shipbaord.
     * @param direction is the direction of the meteor
     * @param sum is the number of the col/row
     */
    public void removeComponent(String direction, int sum) {
        switch(direction) {
            case "nord":
                for(int j = 0; j < 5; j++) {
                    if(getSpace(j,sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (!(getSpace(j,sum-shiftCol).getComponent() == null)) {
                            numLostTiles++;
                            getSpace(j,sum-shiftCol).insertComponent(null);
                            break;
                        }
                    }
                }
                break;
            case "sud":
                for(int j = 4; j >= 0; j--) {
                    if(getSpace(j,sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (!(getSpace(j,sum-shiftCol).getComponent() == null)) {
                            numLostTiles++;
                            getSpace(j,sum-shiftCol).insertComponent(null);
                            break;
                        }
                    }
                }
                break;
            case "est":
                for(int i = 6; i >= 0; i--) {
                    if(getSpace(sum-shiftRow,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (!(getSpace(sum-shiftRow,i).getComponent() == null)) {
                            numLostTiles++;
                            getSpace(sum-shiftRow,i).insertComponent(null);
                            break;
                        }
                    }
                }
                break;
            case "ovest":
                for(int i = 0; i < 7; i++) {
                    if(getSpace(sum-shiftRow,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (!(getSpace(sum-shiftRow, i).getComponent() == null)) {
                            numLostTiles++;
                            getSpace(sum-shiftRow,i).insertComponent(null);
                            break;
                        }
                    }
                }
                break;
            default: break;
        }
    }

    /**
     * Method that checks if there is an active double cannon in the direction of the meteor.
     * If the meteor comes from est/sud/ovest, also checks the adjacent columns/rows.
     * @param direction is the direction of the meteor.
     * @param sum is the number of col/row from which arrives the meteor.
     * @return {@code true} if there is an active double cannon, {@code false} otherwise.
     */
    public boolean checkDoubleCannonMeteor(String direction, int sum) {
        switch(direction) {
            case "nord":
                for(int j = 0; j < 5; j++) {
                    if(getSpace(j,sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (getSpace(j,sum-shiftCol).getComponent() != null && getSpace(j,sum-shiftCol).getComponent().getName().equals(TileName.CANNON)) {
                            Cannon cannon = (Cannon) getSpace(j,sum-shiftCol).getComponent();
                            if(cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case "sud":
                for(int j = 4; j >= 0; j--) {
                    if(getSpace(j,sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (getSpace(j, sum - shiftCol).getComponent() != null && getSpace(j, sum - shiftCol).getComponent().getName().equals(TileName.CANNON)) {
                            Cannon cannon = (Cannon) getSpace(j, sum - shiftCol).getComponent();
                            if (cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                return true;
                            }
                        }
                    }
                    if((sum-shiftCol) != 0) {
                        if (getSpace(j, sum - shiftCol-1).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (getSpace(j, sum - shiftCol - 1).getComponent() != null && getSpace(j, sum - shiftCol - 1).getComponent().getName().equals(TileName.CANNON)) {
                                Cannon cannon = (Cannon) getSpace(j, sum - shiftCol - 1).getComponent();
                                if (cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                    return true;
                                }
                            }
                        }
                    }
                    if((sum-shiftCol) != 6) {
                        if(getSpace(j,sum-shiftCol+1).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (getSpace(j,sum-shiftCol+1).getComponent() != null && getSpace(j,sum-shiftCol+1).getComponent().getName().equals(TileName.CANNON)) {
                                Cannon cannon = (Cannon) getSpace(j,sum-shiftCol+1).getComponent();
                                if(cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                break;
            case "est":
                for(int i = 6; i >= 0; i--) {
                    if(getSpace(sum-shiftRow,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (getSpace(sum - shiftRow, i).getComponent() != null && getSpace(sum - shiftRow, i).getComponent().getName().equals(TileName.CANNON)) {
                            Cannon cannon = (Cannon) getSpace(sum - shiftRow, i).getComponent();
                            if (cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                return true;
                            }
                        }
                    }
                    if((sum-shiftRow) != 0) {
                        if (getSpace(sum - shiftRow-1, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (getSpace(sum - shiftRow - 1, i).getComponent() != null && getSpace(sum - shiftRow - 1, i).getComponent().getName().equals(TileName.CANNON)) {
                                Cannon cannon = (Cannon) getSpace(sum - shiftRow - 1, i).getComponent();
                                if (cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                    return true;
                                }
                            }
                        }
                    }
                    if((sum-shiftRow) != 4) {
                        if(getSpace(sum-shiftRow+1,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (getSpace(sum-shiftRow+1,i).getComponent() != null && getSpace(sum-shiftRow+1,i).getComponent().getName().equals(TileName.CANNON)) {
                                Cannon cannon = (Cannon) getSpace(sum-shiftRow+1,i).getComponent();
                                if(cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                break;
            case "ovest":
                for(int i = 0; i < 7; i++) {
                    if(getSpace(sum-shiftRow,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                        if (getSpace(sum - shiftRow, i).getComponent() != null && getSpace(sum-shiftRow,i).getComponent().getName().equals(TileName.CANNON)) {
                            Cannon cannon = (Cannon) getSpace(sum-shiftRow,i).getComponent();
                            if(cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                return true;
                            }
                        }
                    }
                    if((sum-shiftRow) != 0) {
                        if (getSpace(sum - shiftRow-1, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (getSpace(sum - shiftRow - 1, i).getComponent() != null && getSpace(sum - shiftRow - 1, i).getComponent().getName().equals(TileName.CANNON)) {
                                Cannon cannon = (Cannon) getSpace(sum - shiftRow - 1, i).getComponent();
                                if (cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                    return true;
                                }
                            }
                        }
                    }
                    if((sum-shiftRow) != 4) {
                        if(getSpace(sum-shiftRow+1,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (getSpace(sum-shiftRow+1,i).getComponent() != null && getSpace(sum-shiftRow+1,i).getComponent().getName().equals(TileName.CANNON)) {
                                Cannon cannon = (Cannon) getSpace(sum-shiftRow+1,i).getComponent();
                                if(cannon.isDouble() == true && cannon.getDirection().equals(direction) && cannon.getActive() == true) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                break;
            default: break;
        }
        return false;
    }

    /**
     * @param shipBoardSpace is a space in the ShipBoard
     * @return the coordinate of the space
     */
    public int[] getSpacePositions(ShipBoardSpace shipBoardSpace) {
        return spacePositions.get(shipBoardSpace);
    }

    /**
     * Method that sets the boolean flag if the ship is hit by a meteor.
     * Hit means that the ship cannot resolve by itself the meteor hit (needs cannon activation or shield activation).
     */
    public void calculateIfHit(Card card, int sum) {

        if (card.getCardType().equals(CardName.METEOR_SWARM)) {
            ComponentTile componentTile = null;
            MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;

            // if power = 1
            if (meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter() - 1).getPower() == 1) {
                switch (meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter() - 1).getDirection()) {
                    case "nord":
                        for (int j = 0; j < 5; j++) {
                            if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                                    componentTile = getSpace(j, sum-shiftCol).getComponent();
                                    break;
                                }
                            }
                        }
                        if (componentTile != null) {
                            switch (componentTile.getDirection()) {
                                case "nord":
                                    if (componentTile.getConnector("up") != 0 && componentTile.getConnector("up") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "est":
                                    if (componentTile.getConnector("left") != 0 && componentTile.getConnector("left") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "sud":
                                    if (componentTile.getConnector("down") != 0 && componentTile.getConnector("down") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "ovest":
                                    if (componentTile.getConnector("right") != 0 && componentTile.getConnector("right") != 5) {
                                        hit = true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;

                    case "sud":
                        for (int j = 4; j >= 0; j--) {
                            if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                                    componentTile = getSpace(j, sum-shiftCol).getComponent();
                                    break;
                                }
                            }
                        }
                        if (componentTile != null) {
                            switch (componentTile.getDirection()) {
                                case "nord":
                                    if (componentTile.getConnector("down") != 0 && componentTile.getConnector("down") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "est":
                                    if (componentTile.getConnector("right") != 0 && componentTile.getConnector("right") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "sud":
                                    if (componentTile.getConnector("up") != 0 && componentTile.getConnector("up") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "ovest":
                                    if (componentTile.getConnector("left") != 0 && componentTile.getConnector("left") != 5) {
                                        hit = true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;

                    case "est":
                        for (int i = 6; i >= 0; i--) {
                            if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(sum-shiftRow, i).getComponent() == null)) {
                                    componentTile = getSpace(sum-shiftRow, i).getComponent();
                                    break;
                                }
                            }
                        }
                        if (componentTile != null) {
                            switch (componentTile.getDirection()) {
                                case "nord":
                                    if (componentTile.getConnector("right") != 0 && componentTile.getConnector("right") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "est":
                                    if (componentTile.getConnector("up") != 0 && componentTile.getConnector("up") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "sud":
                                    if (componentTile.getConnector("left") != 0 && componentTile.getConnector("left") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "ovest":
                                    if (componentTile.getConnector("down") != 0 && componentTile.getConnector("down") != 5) {
                                        hit = true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;

                    case "ovest":
                        for (int i = 0; i < 7; i++) {
                            if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(sum-shiftRow, i).getComponent() == null)) {
                                    componentTile = getSpace(sum-shiftRow, i).getComponent();
                                    break;
                                }
                            }
                        }
                        if (componentTile != null) {
                            switch (componentTile.getDirection()) {
                                case "nord":
                                    if (componentTile.getConnector("left") != 0 && componentTile.getConnector("left") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "est":
                                    if (componentTile.getConnector("down") != 0 && componentTile.getConnector("down") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "sud":
                                    if (componentTile.getConnector("right") != 0 && componentTile.getConnector("right") != 5) {
                                        hit = true;
                                    }
                                    break;
                                case "ovest":
                                    if (componentTile.getConnector("up") != 0 && componentTile.getConnector("up") != 5) {
                                        hit = true;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else { // if power = 2

                switch (meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter() - 1).getDirection()) {
                    case "nord":
                        for (int j = 0; j < 5; j++) {
                            if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                                    hit = true;
                                    break;
                                }
                            }
                        }
                        if (hit == true) {
                            // cerca un cannone
                            for (int j = 0; j < 5; j++) {
                                if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                                    if (getSpace(j,sum-shiftCol).getComponent() != null && getSpace(j, sum-shiftCol).getComponent().getName().equals(TileName.CANNON)) {
                                        Cannon cannon = (Cannon) getSpace(j, sum-shiftCol).getComponent();
                                        if (cannon.isDouble() == false) {
                                            if (cannon.getDirection().equals("nord")) {
                                                hit = false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    case "sud":
                        for (int j = 4; j >= 0; j--) {
                            if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                                    hit = true;
                                    break;
                                }
                            }
                        }
                        if (hit == true) {
                            for (int j = 4; j >= 0; j--) {
                                if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                                    if (getSpace(j, sum-shiftCol).getComponent() != null && getSpace(j, sum-shiftCol).getComponent().getName().equals(TileName.CANNON)) {
                                        Cannon cannon = (Cannon) getSpace(j, sum-shiftCol).getComponent();
                                        if (cannon.isDouble() == false) {
                                            if (cannon.getDirection().equals("sud")) {
                                                hit = false;
                                            }
                                        }
                                    }
                                }
                                if((sum-shiftCol) != 0) {
                                    if (getSpace(j, sum - shiftCol-1).getTypeSpace().equals(TypeSpace.USABLE)) {
                                        if (getSpace(j, sum - shiftCol - 1).getComponent() != null && getSpace(j, sum - shiftCol - 1).getComponent().getName().equals(TileName.CANNON)) {
                                            Cannon cannon = (Cannon) getSpace(j, sum - shiftCol - 1).getComponent();
                                            if (cannon.isDouble() == false) {
                                                if (cannon.getDirection().equals("sud")) {
                                                    hit = false;
                                                }
                                            }
                                        }
                                    }
                                }
                                if((sum-shiftCol) != 6) {
                                    if (getSpace(j, sum - shiftCol+1).getTypeSpace().equals(TypeSpace.USABLE)) {
                                        if (getSpace(j, sum - shiftCol +1).getComponent() != null && getSpace(j, sum - shiftCol+ 1).getComponent().getName().equals(TileName.CANNON)) {
                                            Cannon cannon = (Cannon) getSpace(j, sum - shiftCol + 1).getComponent();
                                            if (cannon.isDouble() == false) {
                                                if (cannon.getDirection().equals("sud")) {
                                                    hit = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    case "est":
                        for (int i = 6; i >= 0; i--) {
                            if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(sum-shiftRow, i).getComponent() == null)) {
                                    hit = true;
                                    break;
                                }
                            }
                        }
                        if (hit == true) {
                            for (int i = 6; i >= 0; i--) {
                                if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                    if (getSpace(sum-shiftRow,i).getComponent() != null && getSpace(sum-shiftRow, i).getComponent().getName().equals(TileName.CANNON)) {
                                        Cannon cannon = (Cannon) getSpace(sum-shiftRow, i).getComponent();
                                        if (cannon.isDouble() == false) {
                                            if (cannon.getDirection().equals("est")) {
                                                hit = false;
                                            }
                                        }
                                    }
                                }
                                if((sum-shiftRow) != 0) {
                                    if (getSpace(sum - shiftRow-1,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                        if (getSpace(sum - shiftRow-1,i).getComponent() != null && getSpace(sum - shiftRow- 1,i).getComponent().getName().equals(TileName.CANNON)) {
                                            Cannon cannon = (Cannon) getSpace(sum - shiftRow-1,i).getComponent();
                                            if (cannon.isDouble() == false) {
                                                if (cannon.getDirection().equals("est")) {
                                                    hit = false;
                                                }
                                            }
                                        }
                                    }
                                }
                                if((sum-shiftRow) != 4) {
                                    if (getSpace(sum - shiftRow+1,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                        if (getSpace(sum - shiftRow+1,i).getComponent() != null && getSpace(sum - shiftRow+1,i).getComponent().getName().equals(TileName.CANNON)) {
                                            Cannon cannon = (Cannon) getSpace(sum - shiftRow+1,i).getComponent();
                                            if (cannon.isDouble() == false) {
                                                if (cannon.getDirection().equals("est")) {
                                                    hit = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    case "ovest":
                        for (int i = 0; i < 7; i++) {
                            if (getSpace( sum-shiftRow,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                if (!(getSpace(sum-shiftRow,i).getComponent() == null)) {
                                    hit = true;
                                    break;
                                }
                            }
                        }
                        if (hit == true) {
                            for (int i = 0; i < 7; i++) {
                                if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                    if (getSpace(sum-shiftRow,i).getComponent() != null && getSpace(sum-shiftRow, i).getComponent().getName().equals(TileName.CANNON)) {
                                        Cannon cannon = (Cannon) getSpace(sum-shiftRow, i).getComponent();
                                        if (cannon.isDouble() == false) {
                                            if (cannon.getDirection().equals("ovest")) {
                                                hit = false;
                                            }
                                        }
                                    }
                                }
                                if((sum-shiftRow) != 0) {
                                    if (getSpace(sum - shiftRow-1,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                        if (getSpace(sum - shiftRow-1,i).getComponent() != null && getSpace(sum - shiftRow- 1,i).getComponent().getName().equals(TileName.CANNON)) {
                                            Cannon cannon = (Cannon) getSpace(sum - shiftRow-1,i).getComponent();
                                            if (cannon.isDouble() == false) {
                                                if (cannon.getDirection().equals("ovest")) {
                                                    hit = false;
                                                }
                                            }
                                        }
                                    }
                                }
                                if((sum-shiftRow) != 4) {
                                    if (getSpace(sum - shiftRow+1,i).getTypeSpace().equals(TypeSpace.USABLE)) {
                                        if (getSpace(sum - shiftRow+1,i).getComponent() != null && getSpace(sum - shiftRow+1,i).getComponent().getName().equals(TileName.CANNON)) {
                                            Cannon cannon = (Cannon) getSpace(sum - shiftRow+1,i).getComponent();
                                            if (cannon.isDouble() == false) {
                                                if (cannon.getDirection().equals("ovest")) {
                                                    hit = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }

            }
        }

        if (card.getCardType().equals(CardName.COMBAT_ZONE)) {
            CombatZone combatZoneCard = (CombatZone) card;
            ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];

            switch (meteors.get(combatZoneCard.getCounter() - 1).getDirection()) {
                case "nord":
                    for (int j = 0; j < 5; j++) {
                        if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                                hit = true;
                                break;
                            }
                        }
                    }
                    break;

                case "sud":
                    for (int j = 4; j >= 0; j--) {
                        if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                                hit = true;
                                break;
                            }
                        }
                    }
                    break;

                case "est":
                    for (int i = 6; i >= 0; i--) {
                        if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (!(getSpace(sum-shiftRow, i).getComponent() == null)) {
                                hit = true;
                                break;
                            }
                        }
                    }
                    break;

                case "ovest":
                    for (int i = 0; i < 7; i++) {
                        if (getSpace(sum-shiftRow, i).getTypeSpace().equals(TypeSpace.USABLE)) {
                            if (!(getSpace(sum-shiftRow, i).getComponent() == null)) {
                                hit = true;
                                break;
                            }
                        }
                    }
                    break;
                default: break;
            }
        }

        if (card.getCardType().equals(CardName.PIRATES)) {

            for (int j = 0; j < 5; j++) {
                if (getSpace(j, sum-shiftCol).getTypeSpace().equals(TypeSpace.USABLE)) {
                    if (!(getSpace(j, sum-shiftCol).getComponent() == null)) {
                        hit = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Method that controls if there are connected cabins in the shipboard.
     */
    public void applyEpidemic() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {

                if(getSpace(i,j).getTypeSpace().equals(TypeSpace.USABLE)) {
                    if(getSpace(i,j).getComponent() != null && (getSpace(i,j).getComponent().getName().equals(TileName.CABINE) || getSpace(i,j).getComponent().getName().equals(TileName.STARTING_CABINE))) {

                        if(getSpace(i,j).getComponent().getName().equals(TileName.CABINE)) {
                            Cabine cabine = (Cabine) getSpace(i,j).getComponent();
                            if(cabine.getNumFigures() > 0 || cabine.getHasPurpleAlien() || cabine.getHasBrownAlien()) {
                                if(i != 0) {
                                    if(getSpace(i-1,j).getComponent() != null && (getSpace(i-1,j).getComponent().getName().equals(TileName.CABINE) || getSpace(i-1,j).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;
                                        if(getSpace(i-1,j).getComponent().getName().equals(TileName.CABINE)) {
                                            Cabine cabine1 = (Cabine) getSpace(i-1,j).getComponent();
                                            numFigures = cabine1.getNumFigures();
                                            if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                                numFigures++;
                                            }
                                        }
                                        else {
                                            StartingCabine startingCabine1 = (StartingCabine) getSpace(i-1,j).getComponent();
                                            numFigures = startingCabine1.getNumFigures();
                                        }
                                        if(numFigures > 0) {
                                            cabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                                if(j != 0) {
                                    if(getSpace(i,j-1).getComponent() != null && (getSpace(i,j-1).getComponent().getName().equals(TileName.CABINE) || getSpace(i,j-1).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;
                                        if(getSpace(i,j-1).getComponent().getName().equals(TileName.CABINE)) {
                                            Cabine cabine1 = (Cabine) getSpace(i,j-1).getComponent();
                                            numFigures = cabine1.getNumFigures();
                                            if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                                numFigures++;
                                            }
                                        }
                                        else {
                                            StartingCabine startingCabine1 = (StartingCabine) getSpace(i,j-1).getComponent();
                                            numFigures = startingCabine1.getNumFigures();
                                        }
                                        if(numFigures > 0) {
                                            cabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                                if(i != 4) {
                                    if(getSpace(i+1,j).getComponent() != null && (getSpace(i+1,j).getComponent().getName().equals(TileName.CABINE) || getSpace(i+1,j).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;
                                        if(getSpace(i+1,j).getComponent().getName().equals(TileName.CABINE)) {
                                            Cabine cabine1 = (Cabine) getSpace(i+1,j).getComponent();
                                            numFigures = cabine1.getNumFigures();
                                            if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                                numFigures++;
                                            }
                                        }
                                        else {
                                            StartingCabine startingCabine1 = (StartingCabine) getSpace(i+1,j).getComponent();
                                            numFigures = startingCabine1.getNumFigures();
                                        }
                                        if(numFigures > 0) {
                                            cabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                                if(j != 6) {
                                    if(getSpace(i,j+1).getComponent() != null && (getSpace(i,j+1).getComponent().getName().equals(TileName.CABINE) || getSpace(i,j+1).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;
                                        if(getSpace(i,j+1).getComponent().getName().equals(TileName.CABINE)) {
                                            Cabine cabine1 = (Cabine) getSpace(i,j+1).getComponent();
                                            numFigures = cabine1.getNumFigures();
                                            if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                                numFigures++;
                                            }
                                        }
                                        else {
                                            StartingCabine startingCabine1 = (StartingCabine) getSpace(i,j+1).getComponent();
                                            numFigures = startingCabine1.getNumFigures();
                                        }
                                        if(numFigures > 0) {
                                            cabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            StartingCabine startingCabine = (StartingCabine) getSpace(i,j).getComponent();
                            if(startingCabine.getNumFigures() > 0) {
                                if(i != 0) {
                                    if(getSpace(i-1,j).getComponent() != null && (getSpace(i-1,j).getComponent().getName().equals(TileName.CABINE) || getSpace(i-1,j).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;

                                        Cabine cabine1 = (Cabine) getSpace(i-1,j).getComponent();
                                        numFigures = cabine1.getNumFigures();
                                        if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                            numFigures++;
                                        }

                                        if(numFigures > 0) {
                                            startingCabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                                if(j != 0) {
                                    if(getSpace(i,j-1).getComponent() != null && (getSpace(i,j-1).getComponent().getName().equals(TileName.CABINE) || getSpace(i,j-1).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;

                                        Cabine cabine1 = (Cabine) getSpace(i,j-1).getComponent();
                                        numFigures = cabine1.getNumFigures();
                                        if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                            numFigures++;
                                        }

                                        if(numFigures > 0) {
                                            startingCabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                                if(i != 4) {
                                    if(getSpace(i+1,j).getComponent() != null && (getSpace(i+1,j).getComponent().getName().equals(TileName.CABINE) || getSpace(i+1,j).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;

                                        Cabine cabine1 = (Cabine) getSpace(i+1,j).getComponent();
                                        numFigures = cabine1.getNumFigures();
                                        if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                            numFigures++;
                                        }

                                        if(numFigures > 0) {
                                            startingCabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                                if(j != 6) {
                                    if(getSpace(i,j+1).getComponent() != null && (getSpace(i,j+1).getComponent().getName().equals(TileName.CABINE) || getSpace(i,j+1).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                                        int numFigures;

                                        Cabine cabine1 = (Cabine) getSpace(i,j+1).getComponent();
                                        numFigures = cabine1.getNumFigures();
                                        if(cabine1.getHasPurpleAlien() || cabine1.getHasBrownAlien()) {
                                            numFigures++;
                                        }

                                        if(numFigures > 0) {
                                            startingCabine.setConnectedWithOccupiedCabine(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method that removes a figure in every cabine that has the arribute connectedWithOccupiedCabine = true.
     * Reset the attribute to false.
     */
    public void removeEpidemicFigures() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (getSpace(i,j).getTypeSpace().equals(TypeSpace.USABLE)) {
                    if (getSpace(i,j).getComponent() != null && (getSpace(i,j).getComponent().getName().equals(TileName.CABINE) || getSpace(i,j).getComponent().getName().equals(TileName.STARTING_CABINE))) {
                        if (getSpace(i,j).getComponent().getName().equals(TileName.CABINE)) {
                            Cabine cabine = (Cabine) getSpace(i,j).getComponent();
                            if(cabine.isConnectedWithOccupiedCabine()) {
                                if(cabine.getNumFigures() > 0) {
                                    cabine.decrementNumFigures();
                                }
                                else {
                                    if(cabine.getHasBrownAlien()) {
                                        cabine.setHasBrownAlien(false);
                                        setHasBrownAlien(false);
                                    }
                                    else {
                                        cabine.setHasPurpleAlien(false);
                                        setHasPurpleAlien(false);
                                    }
                                }
                                cabine.setConnectedWithOccupiedCabine(false);
                            }
                        }
                        else {
                            StartingCabine startingCabine = (StartingCabine) getSpace(i,j).getComponent();
                            if(startingCabine.isConnectedWithOccupiedCabine()) {
                                startingCabine.decrementNumFigures();
                                startingCabine.setConnectedWithOccupiedCabine(false);
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Method that checks if a shipboard has some parts separated from the rest of the shipboard.
     * @param x is the x position of the starting tile for the control.
     * @param y is the y position of the starting tile for the control.
     */
    public void checkShipBoardParts(int x, int y) {
        Stack<ShipBoardSpace> spacesToCheck = new Stack<>(); //For the number of component to check
        getSpace(x,y).setCheck(1);

        ShipBoardSpace nextSpace=getSpace(x,y);
        int connectorForTile=0; //If the controller has found more than one connector, there will be some tile to put in the stack

        while(nextSpace!=null || !spacesToCheck.isEmpty())
        {
            if(nextSpace!=null) //If there is a space to check, x and y has to be updated
            {
                int[] position = getSpacePositions(nextSpace);
                x = position[0];
                y = position[1];
                nextSpace = null;
            }
            else { //If there isn't a valid nextSpace, then take the next space from the stack
                ShipBoardSpace spaceToCheck = spacesToCheck.pop();
                int [] position = getSpacePositions(spaceToCheck);
                x = position[0];
                y = position[1];
            }

            switch (getSpace(x,y).getComponent().getDirection()) { //Switch based on the direction of the component considered

                case "nord":

                    if(x>0 && getSpace(x-1,y).getComponent()!=null && getSpace(x-1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x-1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile above

                    if(y<6 && getSpace(x,y+1).getComponent()!=null && getSpace(x,y+1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y+1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile on the right

                    if(x<4 && getSpace(x+1,y).getComponent()!=null && getSpace(x+1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x+1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile below

                    if(y>0 && getSpace(x,y-1).getComponent()!=null && getSpace(x,y-1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y-1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            default: break;
                        }//End second switch
                    }//End if for the tile on the left
                    break;

                case "est":
                    if(x>0 && getSpace(x-1,y).getComponent()!=null && getSpace(x-1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x-1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile above

                    if(y<6 && getSpace(x,y+1).getComponent()!=null && getSpace(x,y+1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y+1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile on the right

                    if(x<4 && getSpace(x+1,y).getComponent()!=null && getSpace(x+1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x+1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile below

                    if(y>0 && getSpace(x,y-1).getComponent()!=null && getSpace(x,y-1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y-1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            default: break;
                        }//End second switch
                    }//End if for the tile on the left
                    break;

                case "sud":
                    if(x>0 && getSpace(x-1,y).getComponent()!=null && getSpace(x-1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x-1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile above

                    if(y<6 && getSpace(x,y+1).getComponent()!=null && getSpace(x,y+1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y+1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile on the right

                    if(x<4 && getSpace(x+1,y).getComponent()!=null && getSpace(x+1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x+1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile below

                    if(y>0 && getSpace(x,y-1).getComponent()!=null && getSpace(x,y-1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y-1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            default: break;
                        }//End second switch
                    }//End if for the tile on the left
                    break;

                case "ovest":
                    if(x>0 && getSpace(x-1,y).getComponent()!=null && getSpace(x-1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x-1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x-1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x-1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x-1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x-1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile above

                    if(y<6 && getSpace(x,y+1).getComponent()!=null && getSpace(x,y+1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y+1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y+1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y+1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y+1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y+1));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile on the right

                    if(x<4 && getSpace(x+1,y).getComponent()!=null && getSpace(x+1,y).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x+1,y).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x+1,y).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x+1,y).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x+1,y);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x+1,y));
                                    }
                                }
                                break;

                        }//End second switch
                    }//End if for the tile below

                    if(y>0 && getSpace(x,y-1).getComponent()!=null && getSpace(x,y-1).getCheck()==0 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                    {
                        switch(getSpace(x,y-1).getComponent().getDirection())
                        {
                            case "nord":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("right")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "est":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("up")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "sud":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("left")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            case "ovest":
                                if(getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=5 && getSpace(x,y-1).getComponent().getConnectorsOnSide().get("down")!=0) //If it's true it means that the component has a connector on that side
                                {
                                    connectorForTile++;
                                    getSpace(x,y-1).setCheck(1);
                                    if(connectorForTile==1) //It means that for now the controller has found only one tile connected
                                    {
                                        nextSpace=getSpace(x,y-1);
                                    }
                                    else{
                                        spacesToCheck.push(getSpace(x,y-1));
                                    }
                                }
                                break;

                            default: break;
                        }//End second switch
                    }//End if for the tile on the left
                    break;

                default: break;
            } //End fist switch
            connectorForTile=0;
        } //End while for side and stack
    }

    /**
     * Method that sets all the flag check to 0
     */
    public void resetCheck() {
        for(int i=0; i<5; i++) {
            for(int j=0; j<7; j++) {
                getSpace(i,j).setCheck(0);
            }
        }
    }

    /**
     * Method that verifies if the ship is assembled well
     * @throws MultipleValidationErrorsException if the ship board is not correct.
     */
    public void validateShipBoard() throws MultipleValidationErrorsException {
        ArrayList<String> errors = new ArrayList<>();

        if(this.getSpace(0,5).getTypeSpace() == TypeSpace.RESERVE && this.getSpace(0,5).getComponent() != null) {
            this.getSpace(0,5).insertComponent(null);
            this.numLostTiles++;
        }
        if(this.getSpace(0,6).getTypeSpace() == TypeSpace.RESERVE && this.getSpace(0,6).getComponent() != null) {
            this.getSpace(0,6).insertComponent(null);
            this.numLostTiles++;
        }

        int x=-1;
        int y=-1;
        outerloop:
        for(int i=0; i<5; i++) {
            for (int j=0; j<7; j++) {
                if(getSpace(i,j).getComponent()!=null) {
                    x=i;
                    y=j;
                    break outerloop;
                }
            }
        }

        if(x == -1  && y == -1) {
        }
        else {
            checkShipBoardParts(x,y);
        }
        outerloop:
        for (int i=0; i<5; i++) {
            for (int j=0; j<7; j++) {
                if(getSpace(i,j).getComponent()!=null && getSpace(i,j).getCheck()==0) {
                    resetCheck();
                    errors.add("Your ship board has a part not attached to the rest.");
                    break outerloop;
                }
            }
        }

        for(int i=0; i<5; i++) {
            for (int j=0; j<7; j++) {
                if (getSpace(i,j).getTypeSpace() == TypeSpace.UNUSABLE && getSpace(i,j).getComponent() != null) {
                     errors.add("The space ("+(i+5)+","+(j+4)+") is unusable. You have to remove the tile.");
                }
                else if(getSpace(i,j).getTypeSpace() == TypeSpace.USABLE) {
                    if(getSpace(i,j).getComponent()!=null) {
                        if(getSpace(i,j).getComponent().getName().equals(TileName.ENGINE)) { //An engine must have the exhaust pipe ont the rear and behind it the space must be free
                            if(!getSpace(i,j).getComponent().getDirection().equals("nord")) {
                                errors.add("The engine at ("+(i+5)+","+(j+4)+") is in the wrong direction.");
                            }
                            switch(getSpace(i,j).getComponent().getDirection()) {
                                case "nord":
                                    if(i<4 && getSpace(i+1,j).getComponent()!=null && getSpace(i+1,j).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                        errors.add("The engine at ("+(i+5)+","+(j+4)+") has a component behind it.");
                                    }
                                    break;
                                case "est":
                                    if(j>0 && getSpace(i,j-1).getComponent()!=null && getSpace(i,j-1).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                        errors.add("The engine at ("+(i+5)+","+(j+4)+") has a component behind it.");
                                    }
                                    break;
                                case "sud":
                                    if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                        errors.add("The engine at ("+(i+5)+","+(j+4)+") has a component behind it.");
                                    }
                                    break;
                                case "ovest":
                                    if(j<6 && getSpace(i,j+1).getComponent()!=null && getSpace(i,j +1).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                        errors.add("The engine at ("+(i+5)+","+(j+4)+") has a component behind it.");
                                    }
                                    break;
                                default: break;
                            }
                        }

                        if(getSpace(i,j).getComponent().getName().equals(TileName.CANNON)) { //No component can sit on the square in front of the cannon’s barrel
                            switch(getSpace(i,j).getComponent().getDirection()) {
                                case "nord":
                                    if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                         errors.add("The cannon at ("+(i+5)+","+(j+4)+") has a component in front of it.");
                                    }
                                    break;
                                case "est":
                                    if(j<6 && getSpace(i,j+1).getComponent()!=null && getSpace(i,j +1).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                         errors.add("The cannon at ("+(i+5)+","+(j+4)+") has a component in front of it.");
                                    }
                                    break;
                                case "sud":
                                    if(i<4 && getSpace(i+1,j).getComponent()!=null && getSpace(i+1,j).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                         errors.add("The cannon at ("+(i+5)+","+(j+4)+") has a component in front of it.");
                                    }
                                    break;
                                case "ovest":
                                    if(j>0 && getSpace(i,j-1).getComponent()!=null && getSpace(i,j-1).getTypeSpace()!=TypeSpace.UNUSABLE) {
                                         errors.add("The cannon at ("+(i+5)+","+(j+4)+") has a component in front of it.");
                                    }
                                    break;
                                default: break;
                            }
                        }

                        switch (getSpace(i,j).getComponent().getDirection()) { //Switch based on the direction of the component considered
                            case "nord":
                                if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space above the tile considered
                                    switch(getSpace(i-1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if(getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 && getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=1 && getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;
                                        default: break;
                                    }//end second switch
                                } //End if of the tile above

                                if(j<6 &&  getSpace(i,j+1).getComponent()!=null &&  getSpace(i,j +1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the right of the tile considered
                                    switch( getSpace(i,j+1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                System.out.println(getSpace(i,j).getComponent().getName());
                                                errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the right

                                if(i<4 &&  getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space below the tile considered
                                    switch( getSpace(i+1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile below

                                if(j>0 &&  getSpace(i,j-1).getComponent()!=null &&  getSpace(i,j-1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the left of the tile considered
                                    switch( getSpace(i,j-1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the left
                                break; //End of the nord case for the tile considered

                            case "est":
                                if(i>0 &&  getSpace(i-1,j).getComponent()!=null &&  getSpace(i-1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space above the tile considered
                                    switch( getSpace(i-1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;
                                        default: break;
                                    }//end second switch
                                } //End if of the tile above

                                if(j<6 &&  getSpace(i,j+1).getComponent()!=null &&  getSpace(i,j +1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the right of the tile considered
                                    switch( getSpace(i,j+1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the right

                                if(i<4 &&  getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space below the tile considered
                                    switch( getSpace(i+1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile below

                                if(j>0 &&  getSpace(i,j-1).getComponent()!=null &&  getSpace(i,j-1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the left of the tile considered
                                    switch( getSpace(i,j-1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the left
                                break; //End of the est case for the tile considered

                            case "sud":
                                if(i>0 &&  getSpace(i-1,j).getComponent()!=null &&  getSpace(i-1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space above the tile considered
                                    switch( getSpace(i-1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;
                                        default: break;
                                    }//end second switch
                                } //End if of the tile above

                                if(j<6 &&  getSpace(i,j+1).getComponent()!=null &&  getSpace(i,j +1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the right of the tile considered
                                    switch( getSpace(i,j+1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the right

                                if(i<4 &&  getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space below the tile considered
                                    switch( getSpace(i+1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile below

                                if(j>0 &&  getSpace(i,j-1).getComponent()!=null &&  getSpace(i,j-1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the left of the tile considered
                                    switch( getSpace(i,j-1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the left

                                break; //End of the sud case for the tile considered

                            case "ovest":
                                if(i>0 &&  getSpace(i-1,j).getComponent()!=null &&  getSpace(i-1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space above the tile considered
                                    switch( getSpace(i-1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")==3 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile above it.");
                                            }
                                            break;
                                        default: break;
                                    }//end second switch
                                } //End if of the tile above

                                if(j<6 &&  getSpace(i,j+1).getComponent()!=null &&  getSpace(i,j +1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the right of the tile considered
                                    switch( getSpace(i,j+1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")==3 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its right.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the right

                                if(i<4 &&  getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space below the tile considered
                                    switch( getSpace(i+1,j).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")==3 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile below it.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile below

                                if(j>0 &&  getSpace(i,j-1).getComponent()!=null &&  getSpace(i,j-1).getTypeSpace()!=TypeSpace.UNUSABLE) { //This in for the space on the left of the tile considered
                                    switch( getSpace(i,j-1).getComponent().getDirection()) {
                                        case "nord":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "est":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "sud":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;

                                        case "ovest":
                                            if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=1 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=2 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=3) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            else if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")==3 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")==0) {
                                                 errors.add("The tile at ("+(i+5)+","+(j+4)+") has mismatched connectors with the tile to its left.");
                                            }
                                            break;
                                        default: break;
                                    }//End switch
                                }//End if for the tile on the left
                                break; //End of the ovest case for the tile considered
                            default: break;
                        }//end first switch

                    }//end if spaceContent != null
                } //end if usable

            } //end for columns
        }//end for rows

        resetCheck();

        if (!errors.isEmpty()) {
            this.correctShip = false;
            throw new MultipleValidationErrorsException(errors);
        }
        else {
            this.correctShip = true;
        }
    }

    /**
     * Method that checks if the alien cabin is connected to a support module, and sets a parameter.
     */
    public void defineAlienCabine()
    {
        for(int i=0; i<5; i++)
        {
            for(int j=0; j<7; j++)
            {
                if(getSpace(i,j).getComponent()!=null && getSpace(i,j).getComponent().getName()==TileName.CABINE)
                {
                    Cabine cabin = (Cabine) getSpace(i,j).getComponent();
                    cabin.unsetConnectedWithAlienCabine();
                    cabin.clearListAlien();

                    switch (cabin.getDirection())
                    {
                        case "nord":
                            if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space above the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i-1,j).getComponent();
                                switch(getSpace(i-1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if(getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 && getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//end second switch
                            } //End if of the tile above

                            if(j<6 && getSpace(i,j+1).getComponent()!=null && getSpace(i,j+1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the right of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j+1).getComponent();
                                switch( getSpace(i,j+1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the right

                            if(i<4 && getSpace(i+1,j).getComponent()!=null && getSpace(i+1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space below the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i+1, j).getComponent();
                                switch( getSpace(i+1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile below

                            if(j>0 && getSpace(i,j-1).getComponent()!=null &&  getSpace(i,j-1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the left of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j-1).getComponent();
                                switch( getSpace(i,j-1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the left

                            break; //End of the nord case for the tile considered


                        case "est":
                            if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space above the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i-1,j).getComponent();
                                switch(getSpace(i-1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if(getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 && getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//end second switch
                            } //End if of the tile above

                            if(j<6 && getSpace(i,j+1).getComponent()!=null && getSpace(i,j+1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the right of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j+1).getComponent();
                                switch( getSpace(i,j+1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the right

                            if(i<4 && getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space below the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i+1, j).getComponent();
                                switch( getSpace(i+1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile below

                            if(j>0 && getSpace(i,j-1).getComponent()!=null && getSpace(i,j-1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the left of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j-1).getComponent();
                                switch( getSpace(i,j-1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the left
                            break; //End case est

                        case "sud":
                            if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space above the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i-1,j).getComponent();
                                switch(getSpace(i-1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if(getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 && getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//end second switch
                            } //End if of the tile above

                            if(j<6 && getSpace(i,j+1).getComponent()!=null && getSpace(i,j+1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the right of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j+1).getComponent();
                                switch( getSpace(i,j+1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the right

                            if(i<4 && getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space below the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i+1, j).getComponent();
                                switch( getSpace(i+1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile below

                            if(j>0 && getSpace(i,j-1).getComponent()!=null && getSpace(i,j-1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the left of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j-1).getComponent();
                                switch( getSpace(i,j-1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the left
                            break;//End the case sud

                        case "ovest":
                            if(i>0 && getSpace(i-1,j).getComponent()!=null && getSpace(i-1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space above the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i-1,j).getComponent();
                                switch(getSpace(i-1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if(getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 && getSpace(i-1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("right")!=0 &&  getSpace(i-1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//end second switch
                            } //End if of the tile above

                            if(j<6 && getSpace(i,j+1).getComponent()!=null && getSpace(i,j+1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the right of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j+1).getComponent();
                                switch( getSpace(i,j+1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("down")!=0 &&  getSpace(i,j+1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the right

                            if(i<4 && getSpace(i+1,j).getComponent()!=null &&  getSpace(i+1,j).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space below the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i+1, j).getComponent();
                                switch( getSpace(i+1,j).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("left")!=0 &&  getSpace(i+1,j).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile below

                            if(j>0 && getSpace(i,j-1).getComponent()!=null && getSpace(i,j-1).getComponent().getName()==TileName.ALIEN_CABINE) { //This in for the space on the left of the tile considered
                                AlienCabine alienCabine = (AlienCabine) getSpace(i, j-1).getComponent();
                                switch( getSpace(i,j-1).getComponent().getDirection()) {
                                    case "nord":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("right")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "est":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("up")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "sud":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("left")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;

                                    case "ovest":
                                        if( getSpace(i,j).getComponent().getConnectorsOnSide().get("up")!=0 &&  getSpace(i,j-1).getComponent().getConnectorsOnSide().get("down")!=0) {
                                            cabin.setConnectedWithAlienCabine();
                                            cabin.setAlienCabineConnected(alienCabine);
                                        }
                                        break;
                                    default: break;
                                }//End switch
                            }//End if for the tile on the left
                            break;//End the case sud

                        default: break;
                    }
                    if(cabin.getHasPurpleAlien())
                    {

                        if(cabin.getAlienCabineConnected().isEmpty()) {
                            cabin.setHasPurpleAlien(false);
                            setHasPurpleAlien(false);
                        }
                        else {
                            boolean found = cabin.getAlienCabineConnected().stream().anyMatch(c -> c.getColor()==Color.PURPLE);
                            if(!found)
                            {
                                cabin.setHasPurpleAlien(false);
                                setHasPurpleAlien(false);
                            }
                        }

                    }
                    if(cabin.getHasBrownAlien())
                    {
                        if(cabin.getAlienCabineConnected().isEmpty()) {
                            cabin.setHasBrownAlien(false);
                            setHasBrownAlien(false);
                        }
                        else {
                            boolean found = cabin.getAlienCabineConnected().stream().anyMatch(c -> c.getColor()==Color.YELLOW);
                            if(!found)
                            {
                                cabin.setHasBrownAlien(false);
                                setHasBrownAlien(false);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method that return the color of the earest goods block in the shipboard.
     * @return the color of the rarest goods block.
     */
    public Color rarestGoodsBlock() {
        ArrayList<Color> temp = new ArrayList<>();
        for(int i=0; i<5; i++) {
            for(int j=0; j<7; j++) {
                if(getSpace(i,j).getComponent() != null && getSpace(i,j).getComponent().getName() == TileName.CARGO) {
                    Cargo cargo = (Cargo) getSpace(i,j).getComponent();
                    ArrayList<Color> temp2 = cargo.getCargosIn();
                    for(Color color : temp2) {
                        temp.add(color);
                    }
                }
            }
        }
        if(temp.size() == 0) {
            return null;
        }
        if(temp.contains(Color.RED))  {
            return Color.RED;
        }
        if(temp.contains(Color.YELLOW))  {
            return Color.YELLOW;
        }
        if(temp.contains(Color.GREEN))  {
            return Color.GREEN;
        }
        return Color.BLUE;
    }

    /**
     * @return {@code true} if the ship board is correct, {@code false} otherwise.
     */
    public boolean getCorrectShip() {
        return this.correctShip;
    }

    /**
     * Method that returns the goods block on the ship, at the end of the game.
     * @return the goods block.
     */
    public ArrayList<Color> getGoodsBlockOnShipBoard() {
        ArrayList<Color> temp = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (getSpace(i, j).getTypeSpace().equals(TypeSpace.USABLE)) {
                    if (getSpace(i, j).getComponent() != null && (getSpace(i, j).getComponent().getName().equals(TileName.CARGO))) {
                        Cargo cargo = (Cargo) getSpace(i, j).getComponent();
                        temp.addAll(cargo.getCargosIn());
                    }
                }
            }
        }
        return temp;
    }

    /**
     *
     * @return the number of batteries in a Battery Tile
     */
    public int getNumBatteries() {
        int numBatteries = 0;
        for(int i=0; i<5; i++) {
            for(int j=0; j<7; j++) {
                if(this.getSpace(i,j).getComponent() != null && getSpace(i,j).getComponent().getName() == TileName.BATTERY) {
                    Battery battery =  (Battery) getSpace(i,j).getComponent();
                    numBatteries = numBatteries + battery.getNumBatteriesInUse();
                }
            }

        }
        return numBatteries;
    }

    /**
     *
     * @return the num of figures in a tile
     */
    public int getNumFigures() {
        int numFigures = 0;
        for(int i=0; i<5; i++) {
            for(int j=0; j<7; j++) {
                if(this.getSpace(i,j).getComponent() != null && getSpace(i,j).getComponent().getName() == TileName.CABINE) {
                    Cabine cabine =  (Cabine) getSpace(i,j).getComponent();
                    numFigures = numFigures + cabine.getNumFigures();
                }
                else if(this.getSpace(i,j).getComponent() != null && getSpace(i,j).getComponent().getName() == TileName.STARTING_CABINE) {
                    StartingCabine startingCabine =  (StartingCabine) getSpace(i,j).getComponent();
                    numFigures = numFigures + startingCabine.getNumFigures();
                }
            }

        }
        return numFigures;
    }
}