
package model.tiles;

import JSONReader.JSONTileReader;
import enumerations.Color;
import enumerations.FlightType;
import enumerations.TileName;
import model.tiles.componentTile.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class GameTileTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getComponentTile() {
        // Test for FIRST_FLIGHT
        GameTile gameTileFirst = new GameTile(FlightType.FIRST_FLIGHT);

        for (int i = 0; i < 140; i++) {
            ComponentTile tile = gameTileFirst.getComponentTile(i);
            assertNotNull(tile, "The tile with ID " + i + " should not be null in FIRST_FLIGHT");
            assertEquals(i, tile.getId(), "The ID of the tile taken should be " + i);
        }

        assertNull(gameTileFirst.getComponentTile(-1), "A negative ID should return null");
        assertNull(gameTileFirst.getComponentTile(140), "An ID over 139 should be null in FIRST_FLIGHT");

        //STANDARD_FLIGHT
        GameTile gameTileStandard = new GameTile(FlightType.STANDARD_FLIGHT);

        for (int i = 0; i < 152; i++) {
            ComponentTile tile = gameTileStandard.getComponentTile(i);
            assertNotNull(tile, "The tile with ID"  + i +  "should not be null in in STANDARD_FLIGHT");
            assertEquals(i, tile.getId(), "The ID of the tile taken should be " + i);
        }

        assertNull(gameTileStandard.getComponentTile(-1), "A negative ID should return null");
        assertNull(gameTileStandard.getComponentTile(152), "An ID over 151 should be null in STANDARD_FLIGHT");
    }

    @Test
    void removeComponentTile() {
        // Test for FIRST_FLIGHT
        GameTile gameTile = new GameTile(FlightType.FIRST_FLIGHT);

        assertNotNull(gameTile.getComponentTile(0), "The tile with ID 0 should exists at the start");
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should exists at the start");

        gameTile.removeComponentTile(0);
        assertNull(gameTile.getComponentTile(0), "The tile with ID 0 should be null after the remove");
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should still exists");

        gameTile.removeComponentTile(0);
        assertNull(gameTile.getComponentTile(0), "The tile with ID 0 should remain null");
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should still exists");

        gameTile.removeComponentTile(200);
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should still exists after the remove of an invalid ID");

        gameTile.removeComponentTile(1);
        assertNull(gameTile.getComponentTile(1), "The tile with ID 1 should be null after the remove");

        for (int i = 2; i < 140; i++) {
            assertNotNull(gameTile.getComponentTile(i), "The tile with ID " + i + " should still exists after the remove");
        }
    }

    @Test
    void removeComponentTile2() {
        // Test for STANDARD_FLIGHT
        GameTile gameTile = new GameTile(FlightType.STANDARD_FLIGHT);

        assertNotNull(gameTile.getComponentTile(0), "The tile with ID 0 should exists at the start");
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 0 should exists at the start");

        gameTile.removeComponentTile(0);
        assertNull(gameTile.getComponentTile(0), "The tile with ID 0 should be null after the remove");
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should still exists");

        gameTile.removeComponentTile(0);
        assertNull(gameTile.getComponentTile(0), "The tile with ID 0 should remain null");
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should still exists");

        gameTile.removeComponentTile(200);
        assertNotNull(gameTile.getComponentTile(1), "The tile with ID 1 should still exists after the remove of an invalid ID");

        gameTile.removeComponentTile(1);
        assertNull(gameTile.getComponentTile(1), "The tile with ID 1 should be null after the remove");

        for (int i = 2; i < 152; i++) {
            assertNotNull(gameTile.getComponentTile(i), "The tile with ID " + i + " should still exists after the remove");
        }
    }

    /**
     * This test verify that the correct ComponentTiles are created from the json
     */
    @Test
    void readJson(){
        ArrayList<ComponentTile> gameTiles= new ArrayList<>();

        JSONObject jsonBattery = new JSONObject(); //JSONObject for a Battery
        jsonBattery.put("component_name","battery");
        jsonBattery.put("url","001.jpg");
        jsonBattery.put("side", new JSONArray(new String[]{"3", "1", "2", "0"}));
        jsonBattery.put("num_of_comp", "2");

        JSONObject jsonCargo = new JSONObject();//JSONObject for a Cargo
        jsonCargo.put("component_name","cargo");
        jsonCargo.put("url","020.jpg");
        jsonCargo.put("side", new JSONArray(new String[]{"2", "3", "0", "1"}));
        jsonCargo.put("num_of_comp", "2");
        jsonCargo.put("color", "blue");

        JSONObject jsonCabine = new JSONObject();//JSONObject for a Cabine
        jsonCabine.put("component_name","cabine");
        jsonCabine.put("url","037.jpg");
        jsonCabine.put("side", new JSONArray(new String[]{"1", "2", "2", "1"}));

        JSONObject jsonStructuralModule = new JSONObject();//JSONObject for a StructuralModule
        jsonStructuralModule.put("component_name","structural_module");
        jsonStructuralModule.put("url","057.jpg");
        jsonStructuralModule.put("side", new JSONArray(new String[]{"2", "3", "0", "3"}));

        JSONObject jsonEngine = new JSONObject(); //JSONObject for an Engine
        jsonEngine.put("component_name","engine");
        jsonEngine.put("url","071.jpg");
        jsonEngine.put("side", new JSONArray(new String[]{"0", "3", "5", "0"}));
        jsonEngine.put("num_of_comp", "1");

        JSONObject jsonEngine2 = new JSONObject(); //JSONObject for an Engine
        jsonEngine2.put("component_name","engine");
        jsonEngine2.put("url","098.jpg");
        jsonEngine2.put("side", new JSONArray(new String[]{"0", "3", "5", "3"}));
        jsonEngine2.put("num_of_comp", "2");

        JSONObject jsonCannon = new JSONObject(); //JSONObject for a cannon
        jsonCannon.put("component_name","cannon");
        jsonCannon.put("url","125.jpg");
        jsonCannon.put("side", new JSONArray(new String[]{"5", "2", "0", "3"}));
        jsonCannon.put("num_of_comp", "1");

        JSONObject jsonCannon1 = new JSONObject(); //JSONObject for a cannon
        jsonCannon1.put("component_name","cannon");
        jsonCannon1.put("url","126.jpg");
        jsonCannon1.put("side", new JSONArray(new String[]{"5", "0", "2", "0"}));
        jsonCannon1.put("num_of_comp", "2");

        JSONObject jsonShield = new JSONObject();//JSONObject for a Shield
        jsonShield.put("component_name","shield");
        jsonShield.put("url","037.jpg");
        jsonShield.put("side", new JSONArray(new String[]{"0", "1", "3", "1"}));

        JSONObject jsonAlienCabine = new JSONObject();//JSONObject for an AlienCabine
        jsonAlienCabine.put("component_name","alien_cabine");
        jsonAlienCabine.put("url","020.jpg");
        jsonAlienCabine.put("side", new JSONArray(new String[]{"0", "0", "0", "3"}));
        jsonAlienCabine.put("color", "yellow");

        ArrayList<JSONObject> jsonTiles = new ArrayList<>();
        jsonTiles.add(jsonBattery);
        jsonTiles.add(jsonCargo);
        jsonTiles.add(jsonCabine);
        jsonTiles.add(jsonStructuralModule);
        jsonTiles.add(jsonEngine);
        jsonTiles.add(jsonEngine2);
        jsonTiles.add(jsonCannon);
        jsonTiles.add(jsonCannon1);
        jsonTiles.add(jsonShield);
        jsonTiles.add(jsonAlienCabine);

        for (JSONObject jsonObject : jsonTiles){
            ComponentTile componentTile;
            switch (jsonObject.getString("component_name")){
                case "battery": componentTile = new Battery(TileName.BATTERY, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                    gameTiles.add(componentTile);
                    break;

                case "cargo":
                    componentTile = new Cargo(TileName.CARGO, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")), jsonObject.getString("color"));
                    gameTiles.add(componentTile);
                    break;

                case "cabine":
                    componentTile = new Cabine(TileName.CABINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                    gameTiles.add(componentTile);
                    break;

                case "structural_module":
                    componentTile = new StructuralModule(TileName.STRUCTURAL_MODULE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                    gameTiles.add(componentTile);
                    break;

                case "engine":
                    componentTile = new Engine(TileName.ENGINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                    gameTiles.add(componentTile);
                    break;

                case "cannon":
                    componentTile = new Cannon(TileName.CANNON, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), Integer.parseInt(jsonObject.getString("num_of_comp")));
                    gameTiles.add(componentTile);
                    break;

                case "shield":
                    componentTile = new Shield(TileName.SHIELD, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)));
                    gameTiles.add(componentTile);
                    break;

                case "alien_cabine":
                    componentTile = new AlienCabine(TileName.ALIEN_CABINE, jsonObject.getString("url"), Integer.parseInt(jsonObject.getJSONArray("side").getString(0)), Integer.parseInt(jsonObject.getJSONArray("side").getString(1)), Integer.parseInt(jsonObject.getJSONArray("side").getString(2)), Integer.parseInt(jsonObject.getJSONArray("side").getString(3)), jsonObject.getString("color"));
                    gameTiles.add(componentTile);
                    break;

                default:break;
            }
        }

        assertEquals(10, gameTiles.size());//Number of component expected

        //Test for a Battery
        ComponentTile componentTile = gameTiles.get(0);
        assertTrue(componentTile instanceof Battery);
        Battery battery= (Battery) componentTile;
        assertEquals(TileName.BATTERY, battery.getName());
        assertEquals(3, battery.getConnectorsOnSide().get("up"));
        assertEquals(1, battery.getConnectorsOnSide().get("right"));
        assertEquals(2, battery.getConnectorsOnSide().get("down"));
        assertEquals(0, battery.getConnectorsOnSide().get("left"));
        assertEquals(2, battery.getNumMaxBatteries());

        //Test for a Cargo
        ComponentTile componentTile1 = gameTiles.get(1);
        assertTrue(componentTile1 instanceof Cargo);
        Cargo cargo= (Cargo) componentTile1;
        assertEquals(TileName.CARGO, cargo.getName());
        assertEquals(2, cargo.getConnectorsOnSide().get("up"));
        assertEquals(3, cargo.getConnectorsOnSide().get("right"));
        assertEquals(0, cargo.getConnectorsOnSide().get("down"));
        assertEquals(1, cargo.getConnectorsOnSide().get("left"));
        assertEquals(2, cargo.getNumMaxCargos());
        assertEquals(Color.BLUE, cargo.getColor());

        //Test for a Cabine
        ComponentTile componentTile2 = gameTiles.get(2);
        assertTrue(componentTile2 instanceof Cabine);
        Cabine cabine= (Cabine) componentTile2;
        assertEquals(TileName.CABINE, cabine.getName());
        assertEquals(1, cabine.getConnectorsOnSide().get("up"));
        assertEquals(2, cabine.getConnectorsOnSide().get("right"));
        assertEquals(2, cabine.getConnectorsOnSide().get("down"));
        assertEquals(1, cabine.getConnectorsOnSide().get("left"));

        //Test for a StructuralModule
        ComponentTile componentTile3 = gameTiles.get(3);
        assertTrue(componentTile3 instanceof StructuralModule);
        StructuralModule structuralModule= (StructuralModule) componentTile3;
        assertEquals(TileName.STRUCTURAL_MODULE, structuralModule.getName());
        assertEquals(2, structuralModule.getConnectorsOnSide().get("up"));
        assertEquals(3, structuralModule.getConnectorsOnSide().get("right"));
        assertEquals(0, structuralModule.getConnectorsOnSide().get("down"));
        assertEquals(3, structuralModule.getConnectorsOnSide().get("left"));

        //Test for a single Engine
        ComponentTile componentTile4 = gameTiles.get(4);
        assertTrue(componentTile4 instanceof Engine);
        Engine engine= (Engine) componentTile4;
        assertEquals(TileName.ENGINE, engine.getName());
        assertEquals(0, engine.getConnectorsOnSide().get("up"));
        assertEquals(3, engine.getConnectorsOnSide().get("right"));
        assertEquals(5, engine.getConnectorsOnSide().get("down"));
        assertEquals(0, engine.getConnectorsOnSide().get("left"));
        assertFalse(engine.isDouble());

        //Test for a double Engine
        ComponentTile componentTile5 = gameTiles.get(5);
        assertTrue(componentTile5 instanceof Engine);
        Engine engine2= (Engine) componentTile5;
        assertEquals(TileName.ENGINE, engine.getName());
        assertEquals(0, engine2.getConnectorsOnSide().get("up"));
        assertEquals(3, engine2.getConnectorsOnSide().get("right"));
        assertEquals(5, engine2.getConnectorsOnSide().get("down"));
        assertEquals(3, engine2.getConnectorsOnSide().get("left"));
        assertTrue(engine2.isDouble());

        //Test for a single Cannon
        ComponentTile componentTile6 = gameTiles.get(6);
        assertTrue(componentTile6 instanceof Cannon);
        Cannon cannon= (Cannon) componentTile6;
        assertEquals(TileName.CANNON, cannon.getName());
        assertEquals(5, cannon.getConnectorsOnSide().get("up"));
        assertEquals(2, cannon.getConnectorsOnSide().get("right"));
        assertEquals(0, cannon.getConnectorsOnSide().get("down"));
        assertEquals(3, cannon.getConnectorsOnSide().get("left"));
        assertFalse(cannon.isDouble());

        //Test for a double Cannon
        ComponentTile componentTile7 = gameTiles.get(7);
        assertTrue(componentTile7 instanceof Cannon);
        Cannon cannon1= (Cannon) componentTile7;
        assertEquals(TileName.CANNON, cannon1.getName());
        assertEquals(5, cannon1.getConnectorsOnSide().get("up"));
        assertEquals(0, cannon1.getConnectorsOnSide().get("right"));
        assertEquals(2, cannon1.getConnectorsOnSide().get("down"));
        assertEquals(0, cannon1.getConnectorsOnSide().get("left"));
        assertTrue(cannon1.isDouble());

        //Test for a Shield
        ComponentTile componentTile8 = gameTiles.get(8);
        assertTrue(componentTile8 instanceof Shield);
        Shield shield= (Shield) componentTile8;
        assertEquals(TileName.SHIELD, shield.getName());
        assertEquals(0, shield.getConnectorsOnSide().get("up"));
        assertEquals(1, shield.getConnectorsOnSide().get("right"));
        assertEquals(3, shield.getConnectorsOnSide().get("down"));
        assertEquals(1, shield.getConnectorsOnSide().get("left"));

        //Test for an AlienCabine
        ComponentTile componentTile9 = gameTiles.get(9);
        assertTrue(componentTile9 instanceof AlienCabine);
        AlienCabine alienCabine= (AlienCabine) componentTile9;
        assertEquals(TileName.ALIEN_CABINE, alienCabine.getName());
        assertEquals(0, alienCabine.getConnectorsOnSide().get("up"));
        assertEquals(0, alienCabine.getConnectorsOnSide().get("right"));
        assertEquals(0, alienCabine.getConnectorsOnSide().get("down"));
        assertEquals(3, alienCabine.getConnectorsOnSide().get("left"));
        assertEquals(Color.YELLOW, alienCabine.getColor());
    }

    @Test
    void getFlightType(){
        GameTile gameTile=new GameTile(FlightType.STANDARD_FLIGHT);
        assertEquals(FlightType.STANDARD_FLIGHT, gameTile.getFlightType());
    }

}
