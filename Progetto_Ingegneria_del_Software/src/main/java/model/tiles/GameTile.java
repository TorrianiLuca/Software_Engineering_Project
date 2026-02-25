package model.tiles;

import JSONReader.JSONTileReader;
import enumerations.FlightType;
import enumerations.TileName;
import model.GameModel;
import model.tiles.componentTile.*;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/*
 * The json file is ordered: the last components are the alien cabins, that are used only in the standard flight.
 * With this approach we can iterate through the json file and return the element with index i.
 * If the flight is first flight we "read" the first 140 objects in the json file (that are the tiles used for
 * the first flight); if the flight is standard we "read" all the objects (152 in total) from the json file.
 */

/**
 * This class creates and represents the 152 tiles used in the game if the flight type is standard, 140 if it is first flight.
 * It doesn't count the starting cabins.
 * The deck is shuffled before passing it to the game model.
 */
public class GameTile implements Serializable {
    private ArrayList<ComponentTile> gameTiles;
    private int numTiles;
    private FlightType flight;


    /**
     * Default constructor.
     * @param flightType is the type of flight decided for the game
     */
    public GameTile(FlightType flightType) {
        gameTiles = new ArrayList<>();

        if(flightType == FlightType.FIRST_FLIGHT) {
            flight = FlightType.FIRST_FLIGHT;
            numTiles = 140;
            JSONTileReader jsonReader = new JSONTileReader();

            for(int i = 0; i < numTiles; i++) {
                JSONObject jsonObject = jsonReader.JSONTileReader(i); // gets the i-element in the json file
                ComponentTile componentTile;

                // tile constructor based on the name read by json reader
                switch (jsonObject.getString("component_name")) {
                    case "structural_module":
                        componentTile = new StructuralModule(TileName.STRUCTURAL_MODULE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                        gameTiles.add(componentTile);
                        break;
                    case "cannon":
                        componentTile = new Cannon(TileName.CANNON, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                        gameTiles.add(componentTile);
                        break;
                    case "engine":
                        componentTile = new Engine(TileName.ENGINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                        gameTiles.add(componentTile);
                        break;
                    case "shield":
                        componentTile = new Shield(TileName.SHIELD, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                        gameTiles.add(componentTile);
                        break;
                    case "cabine":
                        componentTile = new Cabine(TileName.CABINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                        gameTiles.add(componentTile);
                        break;
                    case "cargo":
                        componentTile = new Cargo(TileName.CARGO, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")), jsonObject.getString("color"));
                        gameTiles.add(componentTile);
                        break;
                    case "battery":
                        componentTile = new Battery(TileName.BATTERY, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                        gameTiles.add(componentTile);
                        break;
                    default: break;
                }
            }
            Collections.shuffle(gameTiles);
            for(int i = 0; i < gameTiles.size(); i++) {
                gameTiles.get(i).setId(i);
            }
        }
        else if(flightType == FlightType.STANDARD_FLIGHT) {
            flight = FlightType.STANDARD_FLIGHT;
            numTiles = 152;
            JSONTileReader jsonReader = new JSONTileReader();

            for(int i = 0; i < numTiles; i++) {
                JSONObject jsonObject = jsonReader.JSONTileReader(i);
                ComponentTile componentTile;

                // tile constructor based on the name read by json reader
                switch (jsonObject.getString("component_name")) {
                    case "structural_module":
                        componentTile = new StructuralModule(TileName.STRUCTURAL_MODULE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                        gameTiles.add(componentTile);
                        break;
                    case "cannon":
                        componentTile = new Cannon(TileName.CANNON, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                        gameTiles.add(componentTile);
                        break;
                    case "engine":
                        componentTile = new Engine(TileName.ENGINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                        gameTiles.add(componentTile);
                        break;
                    case "shield":
                        componentTile = new Shield(TileName.SHIELD, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                        gameTiles.add(componentTile);
                        break;
                    case "cabine":
                        componentTile = new Cabine(TileName.CABINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                        gameTiles.add(componentTile);
                        break;
                    case "cargo":
                        componentTile = new Cargo(TileName.CARGO, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")), jsonObject.getString("color"));
                        gameTiles.add(componentTile);
                        break;
                    case "battery":
                        componentTile = new Battery(TileName.BATTERY, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                        gameTiles.add(componentTile);
                        break;
                     case "alien_cabine":
                         componentTile = new AlienCabine(TileName.ALIEN_CABINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), jsonObject.getString("color"));
                         gameTiles.add(componentTile);
                        break;
                    default: break;
                }
            }
            Collections.shuffle(gameTiles);
            for(int i = 0; i < gameTiles.size(); i++) {
                gameTiles.get(i).setId(i);
            }
        }
    }

    /**
     * Getter method that returns the component tile that has his id equals to a given id, if present.
     * @param id is the id of the component tile.
     * @return the component tile associated with id if present, null otherwise.
     */
    public ComponentTile getComponentTile(int id) {
        Optional<ComponentTile> componentTile = gameTiles.stream().filter(t -> t.getId() == id).findFirst();
        return componentTile.orElse(null);
    }

    /**
     * Method that removes the component tile that has his id equals to a given id, if present.
     * @param id is the id of the component tile.
     */
    public void removeComponentTile(int id) {
        gameTiles.removeIf(component -> component.getId() == id); // remove the tile given a specific id
    }

    /**
     * Method that returns the flight type for the game: used to know how many tiles are used..
     * @return the flight type.
     */
    public FlightType getFlightType() {
        return this.flight;
    }
}

