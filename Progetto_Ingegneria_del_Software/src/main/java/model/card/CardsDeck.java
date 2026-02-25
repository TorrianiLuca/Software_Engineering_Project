/*
* CARDS DECK'S CLASS
* This class represents all the cards of the game.
* In the fist flight there are eight cards, in the standard flight there are forty cards
*
* The characteristics of the cards are read from a json file
* (except for the two combat zone, which characteristics are written in this class)
*
* In the json file, the first eight cards are those that are used in the first flight. In this way,
* when the type of flight is FIRST_FLIGHT, we read only the first eight elements
* */

package model.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import JSONReader.JSONCardReader;
import enumerations.CardName;
import enumerations.Color;
import model.GameModel;
import model.card.cardsType.*;
import model.card.cardsType.ForReadJson.Meteor;
import org.json.JSONArray;
import org.json.JSONObject;
import enumerations.FlightType;

/**
 * Class that is used to construct the cards deck containing all the card used for the game.
 */
public class CardsDeck implements Serializable {
    private ArrayList<Card> cardsDeck;
    private int availableCards;

    /**
     * Creates a new deck of cards based on the given flight type.
     * Loads the cards from a JSON file and shuffles the deck.
     *
     * @param flightType the type of flight (FIRST_FLIGHT or STANDARD_FLIGHT)
     */

    public CardsDeck(FlightType flightType) {

        /** List containing all cards in the deck. */
        cardsDeck = new ArrayList<>();
        if (flightType == FlightType.FIRST_FLIGHT) {
            availableCards = 8;

            JSONCardReader jsonReader = new JSONCardReader();
            for(int i = 0; i < availableCards; i++) {
                JSONObject jsonObject = jsonReader.jsonCardReader(i);
                Card card;

                switch (jsonObject.getString("name")) {
                    case "abandoned ship":
                        card = new AbandonedShip(CardName.ABANDONED_SHIP, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("lost_figures")), Integer.parseInt(jsonObject.getString("lost_flight_days")), Integer.parseInt(jsonObject.getString("received_credits")));
                        cardsDeck.add(card);
                        break;

                    case "abandoned station":
                        JSONArray goodsArrayAbandonedStation = jsonObject.getJSONArray("received_goods");
                        ArrayList<Color> goodsColorsAbandonedStation  = new ArrayList<>();
                        for(int j = 0; j < goodsArrayAbandonedStation.length(); j++) {
                            switch (goodsArrayAbandonedStation.getString(j)) {
                                case "red":
                                    goodsColorsAbandonedStation.add(Color.RED);
                                    break;
                                case "blue":
                                    goodsColorsAbandonedStation.add(Color.BLUE);
                                    break;
                                case "yellow":
                                    goodsColorsAbandonedStation.add(Color.YELLOW);
                                    break;
                                case "green":
                                    goodsColorsAbandonedStation.add(Color.GREEN);
                            }
                        }
                        card = new AbandonedStation(CardName.ABANDONED_STATION, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("required_figures")), Integer.parseInt(jsonObject.getString("lost_flight_days")), goodsColorsAbandonedStation);
                        cardsDeck.add(card);
                        break;

                    case "smugglers":
                        JSONArray goodsArraySmugglers = jsonObject.getJSONArray("received_goods");
                        ArrayList<Color> goodsColorsSmugglers  = new ArrayList<>();
                        for(int j = 0; j < goodsArraySmugglers.length(); j++) {
                            switch (goodsArraySmugglers.getString(j)) {
                                case "red":
                                    goodsColorsSmugglers.add(Color.RED);
                                    break;
                                case "blue":
                                    goodsColorsSmugglers.add(Color.BLUE);
                                    break;
                                case "yellow":
                                    goodsColorsSmugglers.add(Color.YELLOW);
                                    break;
                                case "green":
                                    goodsColorsSmugglers.add(Color.GREEN);
                            }
                        }
                        card = new Smugglers(CardName.SMUGGLERS, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"),Integer.parseInt(jsonObject.getString("enemies_strength")), Integer.parseInt(jsonObject.getString("lost_goods")), Integer.parseInt(jsonObject.getString("lost_flight_days")), goodsColorsSmugglers);
                        cardsDeck.add(card);
                        break;

                    case "open space":
                        card = new OpenSpace(CardName.OPEN_SPACE, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"));
                        cardsDeck.add(card);
                        break;

                    case "stardust":
                        card = new Stardust(CardName.STARDUST, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"));
                        cardsDeck.add(card);
                        break;

                    case "meteor swarm":
                        JSONArray meteorJson=jsonObject.getJSONArray("meteor_shots");
                        ArrayList<Meteor> meteorShots = new ArrayList<>();

                        for(int k = 0; k < meteorJson.length(); k++) {
                            JSONObject object = meteorJson.getJSONObject(k);
                            meteorShots.add(new Meteor(object.getString("direction"),object.getInt("power")));
                        }

                        card = new MeteorSwarm(CardName.METEOR_SWARM, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), meteorShots);
                        cardsDeck.add(card);
                        break;

                    case "planets":
                        JSONArray planetGoodsArray = jsonObject.getJSONArray("planets");
                        ArrayList<ArrayList<Color>> goodsPlanets = new ArrayList<>();

                        for(int j = 0; j < planetGoodsArray.length(); j++) {
                            JSONArray subPlanetJson = planetGoodsArray.getJSONArray(j);
                            ArrayList<Color> tempGoodsPlanet = new ArrayList<>();
                            for(int k = 0; k < subPlanetJson.length(); k++) {
                                switch (subPlanetJson.getString(k)) {
                                    case "red":
                                        tempGoodsPlanet.add(Color.RED);
                                        break;
                                    case "blue":
                                        tempGoodsPlanet.add(Color.BLUE);
                                        break;
                                    case "yellow":
                                        tempGoodsPlanet.add(Color.YELLOW);
                                        break;
                                    case "green":
                                        tempGoodsPlanet.add(Color.GREEN);
                                }
                            }
                            goodsPlanets.add(tempGoodsPlanet);
                        }

                        card = new Planets(CardName.PLANETS, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("lost_flight_days")),  goodsPlanets);
                        cardsDeck.add(card);
                        break;

                    case "combat zone":
                        ArrayList<Meteor> meteorAttack= new ArrayList<>();
                        meteorAttack.add(new Meteor("sud",1));
                        meteorAttack.add(new Meteor("sud",2));

                        Object[] faseOne={"equipment", "lost_flight_days", 3};
                        Object[] faseTwo={"power", "equipment", 2};
                        Object[] faseThree={"cannon", "meteor", meteorAttack};
                        card=new CombatZone(CardName.COMBAT_ZONE, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), faseOne, faseTwo, faseThree);

                        cardsDeck.add(card);
                        break;
                    default: break;
                }
            }

            Collections.shuffle(cardsDeck);

        }
        else if(flightType == FlightType.STANDARD_FLIGHT) {
            availableCards=40;
            JSONCardReader jsonReader = new JSONCardReader();
            for(int i = 0; i < availableCards; i++) {
                JSONObject jsonObject = jsonReader.jsonCardReader(i);
                Card card;

                switch (jsonObject.getString("name")) {
                    case "abandoned ship":
                        card = new AbandonedShip(CardName.ABANDONED_SHIP, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("lost_figures")), Integer.parseInt(jsonObject.getString("lost_flight_days")), Integer.parseInt(jsonObject.getString("received_credits")));
                        cardsDeck.add(card);
                        break;

                    case "abandoned station":
                        JSONArray goodsArrayAbandonedStation = jsonObject.getJSONArray("received_goods");
                        ArrayList<Color> goodsColorsAbandonedStation  = new ArrayList<>();
                        for(int j = 0; j < goodsArrayAbandonedStation.length(); j++) {
                            switch (goodsArrayAbandonedStation.getString(j)) {
                                case "red":
                                    goodsColorsAbandonedStation.add(Color.RED);
                                    break;
                                case "blue":
                                    goodsColorsAbandonedStation.add(Color.BLUE);
                                    break;
                                case "yellow":
                                    goodsColorsAbandonedStation.add(Color.YELLOW);
                                    break;
                                case "green":
                                    goodsColorsAbandonedStation.add(Color.GREEN);
                            }
                        }
                        card = new AbandonedStation(CardName.ABANDONED_STATION, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("required_figures")), Integer.parseInt(jsonObject.getString("lost_flight_days")), goodsColorsAbandonedStation);
                        cardsDeck.add(card);
                        break;

                    case "smugglers":
                        JSONArray goodsArraySmugglers = jsonObject.getJSONArray("received_goods");
                        ArrayList<Color> goodsColorsSmugglers  = new ArrayList<>();
                        for(int j = 0; j < goodsArraySmugglers.length(); j++) {
                            switch (goodsArraySmugglers.getString(j)) {
                                case "red":
                                    goodsColorsSmugglers.add(Color.RED);
                                    break;
                                case "blue":
                                    goodsColorsSmugglers.add(Color.BLUE);
                                    break;
                                case "yellow":
                                    goodsColorsSmugglers.add(Color.YELLOW);
                                    break;
                                case "green":
                                    goodsColorsSmugglers.add(Color.GREEN);
                            }
                        }
                        card = new Smugglers(CardName.SMUGGLERS, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"),Integer.parseInt(jsonObject.getString("enemies_strength")), Integer.parseInt(jsonObject.getString("lost_goods")), Integer.parseInt(jsonObject.getString("lost_flight_days")), goodsColorsSmugglers);
                        cardsDeck.add(card);
                        break;

                    case "open space":
                        card = new OpenSpace(CardName.OPEN_SPACE, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"));
                        cardsDeck.add(card);
                        break;

                    case "stardust":
                        card = new Stardust(CardName.STARDUST, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"));
                        cardsDeck.add(card);
                        break;

                    case "meteor swarm":
                        JSONArray meteorJson=jsonObject.getJSONArray("meteor_shots");
                        ArrayList<Meteor> meteorShots = new ArrayList<>();

                        for(int k = 0; k < meteorJson.length(); k++) {
                            JSONObject object = meteorJson.getJSONObject(k);
                            meteorShots.add(new Meteor(object.getString("direction"),object.getInt("power")));
                        }

                        card = new MeteorSwarm(CardName.METEOR_SWARM, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), meteorShots);
                        cardsDeck.add(card);
                        break;

                    case "planets":
                        JSONArray planetGoodsArray = jsonObject.getJSONArray("planets");
                        ArrayList<ArrayList<Color>> goodsPlanets = new ArrayList<>();

                        for(int j = 0; j < planetGoodsArray.length(); j++) {
                            JSONArray subPlanetJson = planetGoodsArray.getJSONArray(j);
                            ArrayList<Color> tempGoodsPlanet = new ArrayList<>();
                            for(int k = 0; k < subPlanetJson.length(); k++) {
                                switch (subPlanetJson.getString(k)) {
                                    case "red":
                                        tempGoodsPlanet.add(Color.RED);
                                        break;
                                    case "blue":
                                        tempGoodsPlanet.add(Color.BLUE);
                                        break;
                                    case "yellow":
                                        tempGoodsPlanet.add(Color.YELLOW);
                                        break;
                                    case "green":
                                        tempGoodsPlanet.add(Color.GREEN);
                                }
                            }
                            goodsPlanets.add(tempGoodsPlanet);
                        }

                        card = new Planets(CardName.PLANETS, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("lost_flight_days")),goodsPlanets);
                        cardsDeck.add(card);
                        break;

                    case "combat zone":

                        int level=Integer.parseInt(jsonObject.getString("level"));
                        if(level==1)
                        {
                            ArrayList<Meteor> meteorAttack= new ArrayList<>();
                            meteorAttack.add(new Meteor("sud",1));
                            meteorAttack.add(new Meteor("sud",2));

                            Object[] faseOne={"equipment", "lost_flight_days", 3};
                            Object[] faseTwo={"power", "equipment", 2};
                            Object[] faseThree={"cannon", "meteor", meteorAttack};
                            card=new CombatZone(CardName.COMBAT_ZONE, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), faseOne, faseTwo, faseThree);
                            cardsDeck.add(card);
                        }
                        else if(level==2)
                        {
                            ArrayList<Meteor> meteorAttack= new ArrayList<>();
                            meteorAttack.add(new Meteor("nord",1));
                            meteorAttack.add(new Meteor("ovest",1));
                            meteorAttack.add(new Meteor("est",1));
                            meteorAttack.add(new Meteor("sud",2));

                            Object[] faseOne={"cannon", "lost_flight_days", 4};
                            Object[] faseTwo={"power", "goods", 3};
                            Object[] faseThree={"equipment", "meteor", meteorAttack};
                            card=new CombatZone(CardName.COMBAT_ZONE, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), faseOne, faseTwo, faseThree);
                            cardsDeck.add(card);
                        }
                        break;

                    case "slavers":
                        card = new Slavers(CardName.SLAVERS, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("enemies_strength")), Integer.parseInt(jsonObject.getString("lost_figures")), Integer.parseInt(jsonObject.getString("lost_flight_days")), Integer.parseInt(jsonObject.getString("received_credits")));
                        cardsDeck.add(card);
                        break;

                    case "pirates":
                        JSONArray shotsPower = jsonObject.getJSONArray("shots");

                        ArrayList<Integer> shotsPowerArray = new ArrayList<>();

                        for(int k = 0; k < shotsPower.length(); k++) {
                            shotsPowerArray.add(shotsPower.getInt(k));
                        }

                        card=new Pirates(CardName.PIRATES, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"), Integer.parseInt(jsonObject.getString("enemies_strength")), shotsPowerArray, Integer.parseInt(jsonObject.getString("lost_flight_days")), Integer.parseInt(jsonObject.getString("received_credits")));
                        cardsDeck.add(card);
                        break;

                    case "epidemic":
                        card = new Epidemic(CardName.EPIDEMIC, Integer.parseInt(jsonObject.getString("level")), jsonObject.getString("url"));
                        cardsDeck.add(card);
                        break;

                    default: break;
                }
            }

            Collections.shuffle(cardsDeck);
        }
    }

    /**
     * This method removes and returns the first card of the array that has the level given.
     * @param level is the level of the card
     */
    public Card removeCard(int level){
        int cardIndex = 0;
        while(cardsDeck.get(cardIndex).getLevel() != level){
            cardIndex++;
        }
        Card pickedCard = cardsDeck.get(cardIndex);
        cardsDeck.remove(cardIndex);
        return pickedCard;
    }

    /**
     * @return all the cards contained in the cards deck.
     */
    public ArrayList<Card> getCardsDeck(){
        return cardsDeck;
    }
}
