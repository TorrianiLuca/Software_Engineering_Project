package view.tui;

import enumerations.*;
import model.card.Card;
import model.card.CardPile;
import model.card.cardsType.*;
import model.card.cardsType.ForReadJson.Meteor;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.tiles.componentTile.*;
import support.Couple;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.out;

/**
 * Class that contains the methods for draw the Tui
 */
public class DrawTui {


    /**
     * Method used to draw the shipboard of a player
     * @param shipBoard is the player's shipboard
     * @return the draw of the shipboard
     */
    public String[] drawShipBoard(ShipBoard shipBoard) {
        String[] lines = new String[32];
        for (int i = 0; i < 32; i++) {
            lines[i] = "";
        }

        lines[0] = lines[0] + ("        4         5         6         7         8         9        10     ");


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {

                if (i == 0) {
                    if (j == 0) {
                        lines[1] = lines[1] + ("   ┌╌╌╌╌╌╌╌╌╌┬");
                        lines[2] = lines[2] + ("   ┆         ┆");
                        lines[3] = lines[3] + ("   ┆         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4] = lines[4] + (" " + (i + 5) + " ┆  " + Font.BOLD + "USAB." + Font.RESET + "  ┆");
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4] = lines[4] + (" " + (i + 5) + " ┆ UNUSAB. ┆");
                        } else {
                            lines[4] = lines[4] + (" " + (i + 5) + " ┆ " + Font.BOLD + "RESERVE" + Font.RESET + " ┆");
                        }
                        lines[5] = lines[5] + ("   ┆         ┆");
                        lines[6] = lines[6] + ("   ┆         ┆");
                        lines[7] = lines[7] + ("   ├╌╌╌╌╌╌╌╌╌┼");
                    } else if (j == 6) {
                        lines[1] = lines[1] + ("╌╌╌╌╌╌╌╌╌┐");
                        lines[2] = lines[2] + ("         ┆");
                        lines[3] = lines[3] + ("         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4] = lines[4] + ("  " + Font.BOLD + "USAB." + Font.RESET + "  ┆");
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4] = lines[4] + (" UNUSAB. ┆");
                        } else {
                            lines[4] = lines[4] + (" " + Font.BOLD + "RESERVE" + Font.RESET + " ┆");
                        }
                        lines[5] = lines[5] + ("         ┆");
                        lines[6] = lines[6] + ("         ┆");
                        lines[7] = lines[7] + ("╌╌╌╌╌╌╌╌╌┤");
                    } else {
                        lines[1] = lines[1] + ("╌╌╌╌╌╌╌╌╌┬");
                        lines[2] = lines[2] + ("         ┆");
                        lines[3] = lines[3] + ("         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4] = lines[4] + ("  " + Font.BOLD + "USAB." + Font.RESET + "  ┆");
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4] = lines[4] + (" UNUSAB. ┆");
                        } else {
                            lines[4] = lines[4] + (" " + Font.BOLD + "RESERVE" + Font.RESET + " ┆");
                        }
                        lines[5] = lines[5] + ("         ┆");
                        lines[6] = lines[6] + ("         ┆");
                        lines[7] = lines[7] + ("╌╌╌╌╌╌╌╌╌┼");
                    }
                } else if (i == 4) {
                    if (j == 0) {
                        lines[2 + i * 6] = lines[2 + i * 6] + ("   ┆         ┆");
                        lines[3 + i * 6] = lines[3 + i * 6] + ("   ┆         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + (i + 5) + " ┆  " + Font.BOLD + "USAB." + Font.RESET + "  ┆");
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + (i + 5) + " ┆ UNUSAB. ┆");
                        } else {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + (i + 5) + " ┆ " + Font.BOLD + "RESERVE" + Font.RESET + " ┆");
                        }
                        lines[5 + i * 6] = lines[5 + i * 6] + ("   ┆         ┆");
                        lines[6 + i * 6] = lines[6 + i * 6] + ("   ┆         ┆");
                        lines[7 + i * 6] = lines[7 + i * 6] + ("   └╌╌╌╌╌╌╌╌╌┴");
                    } else if (j == 6) {
                        lines[2 + i * 6] = lines[2 + i * 6] + ("         ┆");
                        lines[3 + i * 6] = lines[3 + i * 6] + ("         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + ("  " + Font.BOLD + "USAB." + Font.RESET + "  ┆");
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" UNUSAB. ┆");
                        } else {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + Font.BOLD + "RESERVE" + Font.RESET + " ┆");
                        }
                        lines[5 + i * 6] = lines[5 + i * 6] + ("         ┆");
                        lines[6 + i * 6] = lines[6 + i * 6] + ("         ┆");
                        lines[7 + i * 6] = lines[7 + i * 6] + ("╌╌╌╌╌╌╌╌╌┘");
                    } else {
                        lines[2 + i * 6] = lines[2 + i * 6] + ("         ┆");
                        lines[3 + i * 6] = lines[3 + i * 6] + ("         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + ("  " + Font.BOLD + "USAB." + Font.RESET + "  ┆");
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" UNUSAB. ┆");
                        } else {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + Font.BOLD + "RESERVE" + Font.RESET + " ┆");
                        }
                        lines[5 + i * 6] = lines[5 + i * 6] + ("         ┆");
                        lines[6 + i * 6] = lines[6 + i * 6] + ("         ┆");
                        lines[7 + i * 6] = lines[7 + i * 6] + ("╌╌╌╌╌╌╌╌╌┴");
                    }
                } else {
                    if (j == 0) {
                        lines[2 + i * 6] = lines[2 + i * 6] + ("   ┆         ┆");
                        lines[3 + i * 6] = lines[3 + i * 6] + ("   ┆         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + (i + 5) + " ┆  " + Font.BOLD + "USAB." + Font.BOLD + "  ┆" + Font.RESET);
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + (i + 5) + " ┆ UNUSAB. ┆");
                        } else {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + (i + 5) + " ┆ " + Font.BOLD + "RESERVE" + Font.BOLD + " ┆" + Font.RESET);
                        }
                        lines[5 + i * 6] = lines[5 + i * 6] + ("   ┆         ┆");
                        lines[6 + i * 6] = lines[6 + i * 6] + ("   ┆         ┆");
                        lines[7 + i * 6] = lines[7 + i * 6] + ("   ├╌╌╌╌╌╌╌╌╌┼");
                    } else if (j == 6) {
                        lines[2 + i * 6] = lines[2 + i * 6] + ("         ┆");
                        lines[3 + i * 6] = lines[3 + i * 6] + ("         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + ("  " + Font.BOLD + "USAB." + Font.BOLD + "  ┆" + Font.RESET);
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" UNUSAB. ┆");
                        } else {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + Font.BOLD + "RESERVE" + Font.BOLD + " ┆" + Font.RESET);
                        }
                        lines[5 + i * 6] = lines[5 + i * 6] + ("         ┆");
                        lines[6 + i * 6] = lines[6 + i * 6] + ("         ┆");
                        lines[7 + i * 6] = lines[7 + i * 6] + ("╌╌╌╌╌╌╌╌╌┤");
                    } else {
                        lines[2 + i * 6] = lines[2 + i * 6] + ("         ┆");
                        lines[3 + i * 6] = lines[3 + i * 6] + ("         ┆");
                        if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.USABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + ("  " + Font.BOLD + "USAB." + Font.BOLD + "  ┆" + Font.RESET);
                        } else if (shipBoard.getSpace(i, j).getTypeSpace() == TypeSpace.UNUSABLE) {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" UNUSAB. ┆");
                        } else {
                            lines[4 + i * 6] = lines[4 + i * 6] + (" " + Font.BOLD + "RESERVE" + Font.BOLD + " ┆" + Font.RESET);
                        }
                        lines[5 + i * 6] = lines[5 + i * 6] + ("         ┆");
                        lines[6 + i * 6] = lines[6 + i * 6] + ("         ┆");
                        lines[7 + i * 6] = lines[7 + i * 6] + ("╌╌╌╌╌╌╌╌╌┼");
                    }
                }
            }
        }
        return lines;
    }

    /**
     * Method used to draw in the player's shipboard the components that he has picked
     * @param shipBoard is the shipboard of the player
     */
    public void componentInShipBoard(ShipBoard shipBoard) {
        String[] lines = new String[5];
        for (int i = 0; i < 5; i++) {
            lines[i] = "";
        }

        int k = 0;

        int startRow = 50;
        int startCol = 5;

        System.out.print("\033[s");
        System.out.flush();


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (shipBoard.getSpace(i, j).getComponent() != null) {
                    String[] lines2 = this.drawComponentTile(shipBoard.getSpace(i, j).getComponent());
                    for (String line2 : lines2) {
                        System.out.printf("\033[%d;%dH", startRow + k + i * 6, startCol + j * 10);
                        System.out.flush();
                        System.out.println(line2);
                        k++;
                    }
                    k = 0;
                }
            }
        }

        System.out.print("\033[u");
        System.out.flush();

    }


    /**
     * Method used to draw the card pile for the standard flight
     * @param cardPile is one of the three card piles
     * @return the draw of the cards pile
     */
    public String[] drawCardsPile(CardPile cardPile) {
        String[] lines = new String[16];
        for (int i = 0; i < 16; i++) {
            lines[i] = "";
        }

        for(Card card : cardPile.getCards()) {
            String[] lines2 = this.drawCard(card);
            for (int i = 0; i < 16; i++) {
                lines[i] = lines[i] + lines2[i] + ("     ");
            }
        }

        return lines;
    }


    /**
     * Method used to draw a specific card
     * @param card is the card considered
     * @return the draw of the card
     */
    public String[] drawCard(Card card) {
        String[] lines = new String[16];
        for (int i = 0; i < 16; i++) {
            lines[i] = "";
        }

        lines[0] = lines[0] + ("╭─────────────────────╮");
        lines[15] = lines[15] + ("╰─────────────────────╯");

        if(card.getCardType() == CardName.PLANETS) {
            Planets planet = (Planets) card;
            lines[1] = lines[1] + ("│       PLANETS       │");
            lines[14] = lines[14] + ("│ LOST FLIGHT DAYS: "+planet.getLoseFlightDays()+" │") ;
            if(planet.getNumberOfPlanets() == 4) {
                lines[2] = lines[2] + ("│   ╭───╮             │");
                lines[4] = lines[4] + ("│   ╰───╯             │") ;
                lines[5] = lines[5] + ("│   ╭───╮             │") ;
                lines[7] = lines[7] + ("│   ╰───╯             │") ;
                lines[8] = lines[8] + ("│   ╭───╮             │") ;
                lines[10] = lines[10] + ("│   ╰───╯             │") ;
                lines[11] = lines[11] + ("│   ╭───╮             │") ;
                lines[13] = lines[13] + ("│   ╰───╯             │") ;

                for(int j = 1; j <= 4; j++) {
                    Color color = planet.playerOnPlanet(j);
                    if(color == null) {
                        lines[j*3] = lines[j*3] + ("│  "+j+"│   │ ") ;
                    }
                    else if(color.equals(Color.RED)) {
                        lines[j*3] = lines[j*3] + ("│  "+j+"│"+Font.RED+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else if(color.equals(Color.BLUE)) {
                        lines[j*3] = lines[j*3] + ("│  "+j+"│"+Font.BLUE+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else if(color.equals(Color.GREEN)) {
                        lines[j*3] = lines[j*3] + ("│  "+j+"│"+Font.GREEN+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else {
                        lines[j*3] = lines[j*3] + ("│  "+j+"│"+Font.YELLOW+"▦▦▦"+Font.RESET+"│ ") ;
                    }

                    ArrayList<Color> goodsBlock = planet.choosePlanetGoods(j);
                    int num = 0;
                    for(Color c : goodsBlock) {
                        num++;
                        if(c == Color.RED) {
                            lines[j*3] = lines[j*3] + (Font.RED+"■ "+Font.RESET) ;
                        }
                        else if(c == Color.BLUE) {
                            lines[j*3] = lines[j*3] + (Font.BLUE+"■ "+Font.RESET) ;
                        }
                        else if(c == Color.GREEN) {
                            lines[j*3] = lines[j*3] + (Font.GREEN+"■ "+Font.RESET) ;
                        }
                        else {
                            lines[j*3] = lines[j*3] + (Font.YELLOW+"■ "+Font.RESET) ;
                        }
                    }
                    for(;num < 6; num++) {
                        lines[j*3] = lines[j*3] + ("  ");
                    }
                    lines[j*3] = lines[j*3] + ("│");
                }
            }
            else if(planet.getNumberOfPlanets() == 3) {
                lines[2] = lines[2] + ("│                     │");
                lines[12] = lines[12] + ("│                     │");
                lines[13] = lines[13] + ("│                     │");
                lines[3] = lines[3] + ("│   ╭───╮             │");
                lines[5] = lines[5] + ("│   ╰───╯             │") ;
                lines[6] = lines[6] + ("│   ╭───╮             │") ;
                lines[8] = lines[8] + ("│   ╰───╯             │") ;
                lines[9] = lines[9] + ("│   ╭───╮             │") ;
                lines[11] = lines[11] + ("│   ╰───╯             │") ;

                for(int j = 1; j <= 3; j++) {
                    Color color = planet.playerOnPlanet(j);
                    if(color == null) {
                        lines[(j*3)+1] = lines[(j*3)+1] + ("│  "+j+"│   │ ") ;
                    }
                    else if(color.equals(Color.RED)) {
                        lines[(j*3)+1] = lines[(j*3)+1] + ("│  "+j+"│"+Font.RED+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else if(color.equals(Color.BLUE)) {
                        lines[(j*3)+1] = lines[(j*3)+1] + ("│  "+j+"│"+Font.BLUE+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else if(color.equals(Color.GREEN)) {
                        lines[(j*3)+1] = lines[(j*3)+1] + ("│  "+j+"│"+Font.GREEN+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else {
                        lines[(j*3)+1] = lines[(j*3)+1] + ("│  "+j+"│"+Font.YELLOW+"▦▦▦"+Font.RESET+"│ ") ;
                    }

                    ArrayList<Color> goodsBlock = planet.choosePlanetGoods(j);
                    int num = 0;
                    for(Color c : goodsBlock) {
                        num++;
                        if(c == Color.RED) {
                            lines[(j*3)+1] = lines[(j*3)+1] + (Font.RED+"■ "+Font.RESET) ;
                        }
                        else if(c == Color.BLUE) {
                            lines[(j*3)+1] = lines[(j*3)+1] + (Font.BLUE+"■ "+Font.RESET) ;
                        }
                        else if(c == Color.GREEN) {
                            lines[(j*3)+1] = lines[(j*3)+1] + (Font.GREEN+"■ "+Font.RESET) ;
                        }
                        else {
                            lines[(j*3)+1] = lines[(j*3)+1] + (Font.YELLOW+"■ "+Font.RESET) ;
                        }
                    }
                    for(;num < 6; num++) {
                        lines[(j*3)+1] = lines[(j*3)+1] + ("  ");
                    }
                    lines[(j*3)+1] = lines[(j*3)+1] + ("│");
                }


            }
            else if(planet.getNumberOfPlanets() == 2) {
                lines[2] = lines[2] + ("│                     │");
                lines[3] = lines[3] + ("│                     │");
                lines[4] = lines[4] + ("│                     │");
                lines[11] = lines[11] + ("│                     │");
                lines[12] = lines[12] + ("│                     │");
                lines[13] = lines[13] + ("│                     │");

                lines[5] = lines[5] + ("│   ╭───╮             │");
                lines[7] = lines[7] + ("│   ╰───╯             │") ;
                lines[8] = lines[8] + ("│   ╭───╮             │") ;
                lines[10] = lines[10] + ("│   ╰───╯             │") ;


                for(int j = 1; j <= 2; j++) {
                    Color color = planet.playerOnPlanet(j);
                    if(color == null) {
                        lines[(j*3)+3] = lines[(j*3)+3] + ("│  "+j+"│   │ ") ;
                    }
                    else if(color.equals(Color.RED)) {
                        lines[(j*3)+3] = lines[(j*3)+3] + ("│  "+j+"│"+Font.RED+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else if(color.equals(Color.BLUE)) {
                        lines[(j*3)+3] = lines[(j*3)+3] + ("│  "+j+"│"+Font.BLUE+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else if(color.equals(Color.GREEN)) {
                        lines[(j*3)+3] = lines[(j*3)+3] + ("│  "+j+"│"+Font.GREEN+"▦▦▦"+Font.RESET+"│ ") ;
                    }
                    else {
                        lines[(j*3)+3] = lines[(j*3)+3] + ("│  "+j+"│"+Font.YELLOW+"▦▦▦"+Font.RESET+"│ ") ;
                    }

                    ArrayList<Color> goodsBlock = planet.choosePlanetGoods(j);
                    int num = 0;
                    for(Color c : goodsBlock) {
                        num++;
                        if(c == Color.RED) {
                            lines[(j*3)+3] = lines[(j*3)+3] + (Font.RED+"■ "+Font.RESET) ;
                        }
                        else if(c == Color.BLUE) {
                            lines[(j*3)+3] = lines[(j*3)+3] + (Font.BLUE+"■ "+Font.RESET) ;
                        }
                        else if(c == Color.GREEN) {
                            lines[(j*3)+3] = lines[(j*3)+3] + (Font.GREEN+"■ "+Font.RESET) ;
                        }
                        else {
                            lines[(j*3)+3] = lines[(j*3)+3] + (Font.YELLOW+"■ "+Font.RESET) ;
                        }
                    }
                    for(;num < 6; num++) {
                        lines[(j*3)+3] = lines[(j*3)+3] + ("  ");
                    }
                    lines[(j*3)+3] = lines[(j*3)+3] + ("│");
                }
            }
        }
        else if(card.getCardType() == CardName.COMBAT_ZONE) {
            if(card.getLevel() == 1) {
                lines[1] = lines[1] + ("│     COMBAT ZONE     │");
                lines[2] = lines[2] + ("│                     │");
                lines[3] = lines[3] + ("│ FEWEST CREW FIGURES │");
                lines[4] = lines[4] + ("│ LOST FLIGHT DAYS: 3 │");
                lines[5] = lines[5] + ("│─────────────────────│");
                lines[6] = lines[6] + ("│ WEAKEST ENGINES     │");
                lines[7] = lines[7] + ("│ LOST CREW FIGURES: 2│") ;
                lines[8] = lines[8] + ("│─────────────────────│") ;
                lines[9] = lines[9] + ("│ WEAKEST CANNONS     │") ;
                lines[10] = lines[10] + ("│ CANNON FIRE         │") ;
                lines[11] = lines[11] + ("│ DIRECTION   POWER   │") ;
                lines[12] = lines[12] + ("│ SUD         LIGHT   │") ;
                lines[13] = lines[13] + ("│ SUD         HEAVY   │") ;
                lines[14] = lines[14] + ("│                     │");
            }
            else {
                lines[1] = lines[1] + ("│     COMBAT ZONE     │");
                lines[2] = lines[2] + ("│ WEAKEST CANNONS     │");
                lines[3] = lines[3] + ("│ LOST FLIGHT DAYS: 4 │");
                lines[4] = lines[4] + ("│─────────────────────│");
                lines[5] = lines[5] + ("│ WEAKEST ENGINES     │");
                lines[6] = lines[6] + ("│ LOST GOODS BLOCKS: 3│");
                lines[7] = lines[7] + ("│─────────────────────│") ;
                lines[8] = lines[8] + ("│ FEWEST CREW FIGURES │") ;
                lines[9] = lines[9] + ("│ CANNON FIRE         │") ;
                lines[10] = lines[10] + ("│ DIRECTION   POWER   │") ;
                lines[11] = lines[11] + ("│ NORD        LIGHT   │") ;
                lines[12] = lines[12] + ("│ OVEST       LIGHT   │") ;
                lines[13] = lines[13] + ("│ EST         LIGHT   │") ;
                lines[14] = lines[14] + ("│ SUD         HEAVY   │");
            }
        }
        else if(card.getCardType() == CardName.SLAVERS) {
            Slavers slavers = (Slavers) card;
            lines[1] = lines[1] + ("│       SLAVERS       │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│ CANNON STRENGTH: "+slavers.getEnemyStrength()+"  │");
            lines[5] = lines[5] + ("│                     │");
            lines[6] = lines[6] + ("│ WIN                 │");
            lines[7] = lines[7] + ("│ GAINED CREDITS: "+slavers.getNumOfCreditsTaken()+"   │") ;
            lines[8] = lines[8] + ("│ LOST FLIGHT DAYS: "+slavers.getLoseFlightDays()+" │") ;
            lines[9] = lines[9] + ("│─────────────────────│") ;
            lines[10] = lines[10] + ("│ DEFEAT              │") ;
            lines[11] = lines[11] + ("│ LOST CREW FIGURES: "+slavers.getNumOfLoseFigures()+"│") ;
            lines[12] = lines[12] + ("│                     │") ;
            lines[13] = lines[13] + ("│                     │") ;
            lines[14] = lines[14] + ("│                     │");
        }
        else if(card.getCardType() == CardName.SMUGGLERS) {
            Smugglers smugglers = (Smugglers) card;
            lines[1] = lines[1] + ("│      SMUGGLERS      │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│ CANNON STRENGTH: "+smugglers.getEnemyStrength()+"  │");
            lines[5] = lines[5] + ("│                     │");
            lines[6] = lines[6] + ("│ WIN                 │");
            lines[7] = lines[7] + ("│ GAINED: ") ;
            lines[8] = lines[8] + ("│ LOST FLIGHT DAYS: "+smugglers.getLoseFlightDays()+" │") ;
            lines[9] = lines[9] + ("│─────────────────────│") ;
            lines[10] = lines[10] + ("│ DEFEAT              │") ;
            lines[11] = lines[11] + ("│ LOST GOODS BLOCKS: "+smugglers.getGoodsLose()+"│") ;
            lines[12] = lines[12] + ("│                     │") ;
            lines[13] = lines[13] + ("│                     │") ;
            lines[14] = lines[14] + ("│                     │");

            ArrayList<Color> goodsBlock = smugglers.getColorOfGoodsTaken();
            int num = 0;
            for(Color c : goodsBlock) {
                num++;
                if(c == Color.RED) {
                    lines[7] = lines[7] + (Font.RED+"■ "+Font.RESET) ;
                }
                else if(c == Color.BLUE) {
                    lines[7] = lines[7] + (Font.BLUE+"■ "+Font.RESET) ;
                }
                else if(c == Color.GREEN) {
                    lines[7] = lines[7] + (Font.GREEN+"■ "+Font.RESET) ;
                }
                else {
                    lines[7] = lines[7] + (Font.YELLOW+"■ "+Font.RESET) ;
                }
            }
            for(;num < 6 ; num++) {
                lines[7] = lines[7] + ("  ");
            }
            lines[7] = lines[7] + ("│");
        }
        else if(card.getCardType() == CardName.PIRATES) {
            Pirates pirates = (Pirates) card;
            lines[1] = lines[1] + ("│       PIRATES       │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│ CANNON STRENGTH: "+pirates.getEnemyStrength()+"  │");
            lines[4] = lines[4] + ("│                     │");
            lines[5] = lines[5] + ("│ WIN                 │");
            lines[6] = lines[6] + ("│ GAINED CREDITS: "+pirates.getNumOfCreditsTaken()+"   │");
            lines[7] = lines[7] + ("│ LOST FLIGHT DAYS: 3 │");
            lines[8] = lines[8] + ("│─────────────────────│") ;
            lines[9] = lines[9] + ("│ DEFEAT              │") ;
            lines[10] = lines[10] + ("│ CANNON FIRE         │") ;
            lines[11] = lines[11] + ("│ DIRECTION   POWER   │") ;
            lines[12] = lines[12] + ("│ NORD        ") ;
            lines[13] = lines[13] + ("│ NORD        ") ;
            lines[14] = lines[14] + ("│ NORD        ");

            ArrayList<Integer> shots = pirates.getShotsPowerArray();
            for(int i = 0; i < shots.size(); i++) {
                if(shots.get(i) == 1) {
                    lines[12+i] = lines[12+i] + ("LIGHT   │");
                }
                else {
                    lines[12+i] = lines[12+i] + ("HEAVY   │");
                }
            }
        }
        else if(card.getCardType() == CardName.STARDUST) {
            lines[1] = lines[1] + ("│       STARDUST      │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│                     │");
            lines[5] = lines[5] + ("│                     │");
            lines[6] = lines[6] + ("│ EVERY PLAYER        │");
            lines[7] = lines[7] + ("│ LOSES 1 FLIGHT DAY  │");
            lines[8] = lines[8] + ("│ FOR EVERY           │");
            lines[9] = lines[9] + ("│ EXPOSED CONNECTORS  │") ;
            lines[10] = lines[10] + ("│                     │") ;
            lines[11] = lines[11] + ("│                     │") ;
            lines[12] = lines[12] + ("│                     │") ;
            lines[13] = lines[13] + ("│                     │") ;
            lines[14] = lines[14] + ("│                     │") ;
        }
        else if(card.getCardType() == CardName.OPEN_SPACE)
        {
            OpenSpace openSpace = (OpenSpace) card;
            lines[1] = lines[1] + ("│     OPEN SPACE      │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│                     │");
            lines[5] = lines[5] + ("│                     │");
            lines[6] = lines[6] + ("│ EVERY PLAYER        │");
            lines[7] = lines[7] + ("│ GAIN A NUMBER OF    │");
            lines[8] = lines[8] + ("│ FLIGHT DAYS EQUAL TO│");
            lines[9] = lines[9] + ("│ HIS ENGINE STRENGTH │");
            lines[10] = lines[10] + ("│                     │");
            lines[11] = lines[11] + ("│                     │");
            lines[12] = lines[12] + ("│                     │");
            lines[13] = lines[13] + ("│                     │");
            lines[14] = lines[14] + ("│                     │");

        }

        else if(card.getCardType() == CardName.EPIDEMIC)
        {
            Epidemic epidemic = (Epidemic) card;
            lines[1] = lines[1] + ("│       EPIDEMIC      │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│                     │");
            lines[5] = lines[5] + ("│ EVERY PLAYER HAS TO │");
            lines[6] = lines[6] + ("│ REMOVE 1 CREW       │");
            lines[7] = lines[7] + ("│ FIGURES FOR EVERY   │");
            lines[8] = lines[8] + ("│ OCCUPIED CABINE THAT│");
            lines[9] = lines[9] + ("│ IS JOINED TO ANOTHER│");
            lines[10] = lines[10] + ("│ OCCUPIED CABINE     │");
            lines[11] = lines[11] + ("│                     │");
            lines[12] = lines[12] + ("│                     │");
            lines[13] = lines[13] + ("│                     │");
            lines[14] = lines[14] + ("│                     │");

        }

        else if(card.getCardType() == CardName.ABANDONED_STATION) {
            AbandonedStation abandonedStation = (AbandonedStation) card;
            lines[1] = lines[1] + ("│  ABANDONED STATION  │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│                     │");
            lines[5] = lines[5] + ("│                     │");
            lines[6] = lines[6] + ("│ REQUIRED            │");
            lines[7] = lines[7] + ("│ CREW FIGURES: " + abandonedStation.getNumOfFigureRequired() + "     │");
            lines[8] = lines[8] + ("│                     │");
            lines[9] = lines[9] + ("│ GAINED: ");
            lines[10] = lines[10] + ("│ LOST FLIGHT DAYS: " + abandonedStation.getLoseFlightDays() + " │") ;
            lines[11] = lines[11] + ("│                     │");
            lines[12] = lines[12] + ("│                     │");
            lines[13] = lines[13] + ("│                     │");
            lines[14] = lines[14] + ("│                     │");

            ArrayList<Color> goodsBlock = abandonedStation.getColorOfGoodsTaken();
            int num = 0;
            for(Color c : goodsBlock) {
                num++;
                if(c == Color.RED) {
                    lines[9] = lines[9] + (Font.RED+"■ "+Font.RESET) ;
                }
                else if(c == Color.BLUE) {
                    lines[9] = lines[9] + (Font.BLUE+"■ "+Font.RESET) ;
                }
                else if(c == Color.GREEN) {
                    lines[9] = lines[9] + (Font.GREEN+"■ "+Font.RESET) ;
                }
                else {
                    lines[9] = lines[9] + (Font.YELLOW+"■ "+Font.RESET) ;
                }
            }
            for(;num < 6; num++) {
                lines[9] = lines[9] + ("  ");
            }
            lines[9] = lines[9] + ("│");
        }

        else if(card.getCardType() == CardName.ABANDONED_SHIP) {
            AbandonedShip abandonedShip = (AbandonedShip) card;
            lines[1] = lines[1] + ("│   ABANDONED SHIP    │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│                     │");
            lines[4] = lines[4] + ("│                     │");
            lines[5] = lines[5] + ("│ REQUIRED            │");
            lines[6] = lines[6] + ("│ CREW FIGURES: " + abandonedShip.getNumOfLoseFigures()+ "     │");
            lines[7] = lines[7] + ("│                     │");
            lines[8] = lines[8] + ("│ GAINED CREDITS: " + abandonedShip.getNumOfCreditsTaken()+ "   │");
            lines[9] = lines[9] + ("│ LOST FLIGHT DAYS: " + abandonedShip.getLoseFlightDays()+ " │");
            lines[10] = lines[10] + ("│ LOST CREW FIGURES: " + abandonedShip.getNumOfLoseFigures()+ "│");
            lines[11] = lines[11] + ("│                     │");
            lines[12] = lines[12] + ("│                     │");
            lines[13] = lines[13] + ("│                     │");
            lines[14] = lines[14] + ("│                     │");
        }
        else if(card.getCardType() == CardName.METEOR_SWARM) {
            MeteorSwarm meteorSwarm = (MeteorSwarm) card;
            lines[1] = lines[1] + ("│    METEOR SWARM     │");
            lines[2] = lines[2] + ("│                     │");
            lines[3] = lines[3] + ("│ METEOR HIT          │");
            lines[4] = lines[4] + ("│ DIRECTION   SIZE    │");
            lines[10] = lines[10] + ("│                     │");
            lines[11] = lines[11] + ("│                     │");
            lines[12] = lines[12] + ("│                     │");
            lines[13] = lines[13] + ("│                     │");
            lines[14] = lines[14] + ("│                     │");

            if(meteorSwarm.getMeteor().size()==3) {
                ArrayList<Meteor> meteors = meteorSwarm.getMeteor();
                int i=0;
                for(Meteor meteor : meteors)
                {
                    if(meteor.getPower()==1) {
                        if(meteor.getDirection().equals("nord")) {
                            lines[5+i] = lines[5+i] + ("│ NORD        SMALL   │");
                        }
                        else if(meteor.getDirection().equals("est")) {
                            lines[5+i] = lines[5+i] + ("│ EST         SMALL   │");
                        }
                        else if(meteor.getDirection().equals("sud")) {
                            lines[5+i] = lines[5+i] + ("│ SUD         SMALL   │");
                        }
                        else if(meteor.getDirection().equals("ovest")) {
                            lines[5+i] = lines[5+i] + ("│ OVEST       SMALL   │");
                        }

                    }
                    else{
                        if(meteor.getDirection().equals("nord")) {
                            lines[5+i] = lines[5+i] + ("│ NORD        LARGE   │");
                        }
                        else if(meteor.getDirection().equals("est")) {
                            lines[5+i] = lines[5+i] + ("│ EST         LARGE   │");
                        }
                        else if(meteor.getDirection().equals("sud")) {
                            lines[5+i] = lines[5+i] + ("│ SUD         LARGE   │");
                        }
                        else if(meteor.getDirection().equals("ovest")) {
                            lines[5+i] = lines[5+i] + ("│ OVEST       LARGE   │");
                        }
                    }
                    i++;
                }
                lines[8] = lines[8] + ("│                     │");
                lines[9] = lines[9] + ("│                     │");
            }
            else if(meteorSwarm.getMeteor().size()==4) {
                ArrayList<Meteor> meteors = meteorSwarm.getMeteor();
                int i=0;
                for(Meteor meteor : meteors)
                {
                    if(meteor.getPower()==1) {
                        if(meteor.getDirection().equals("nord")) {
                            lines[5+i] = lines[5+i] + ("│ NORD        SMALL   │");
                        }
                        else if(meteor.getDirection().equals("est")) {
                            lines[5+i] = lines[5+i] + ("│ EST         SMALL   │");
                        }
                        else if(meteor.getDirection().equals("sud")) {
                            lines[5+i] = lines[5+i] + ("│ SUD         SMALL   │");
                        }
                        else if(meteor.getDirection().equals("ovest")) {
                            lines[5+i] = lines[5+i] + ("│ OVEST       SMALL   │");
                        }

                    }
                    else{
                        if(meteor.getDirection().equals("nord")) {
                            lines[5+i] = lines[5+i] + ("│ NORD        LARGE   │");
                        }
                        else if(meteor.getDirection().equals("est")) {
                            lines[5+i] = lines[5+i] + ("│ EST         LARGE   │");
                        }
                        else if(meteor.getDirection().equals("sud")) {
                            lines[5+i] = lines[5+i] + ("│ SUD         LARGE   │");
                        }
                        else if(meteor.getDirection().equals("ovest")) {
                            lines[5+i] = lines[5+i] + ("│ OVEST       LARGE   │");
                        }
                    }
                    i++;
                }
                lines[9] = lines[9] + ("│                     │");
            }
            else if(meteorSwarm.getMeteor().size()==5) {
                ArrayList<Meteor> meteors = meteorSwarm.getMeteor();
                int i=0;
                for(Meteor meteor : meteors)
                {
                    if(meteor.getPower()==1) {
                        if(meteor.getDirection().equals("nord")) {
                            lines[5+i] = lines[5+i] + ("│ NORD        SMALL   │");
                        }
                        else if(meteor.getDirection().equals("est")) {
                            lines[5+i] = lines[5+i] + ("│ EST         SMALL   │");
                        }
                        else if(meteor.getDirection().equals("sud")) {
                            lines[5+i] = lines[5+i] + ("│ SUD         SMALL   │");
                        }
                        else if(meteor.getDirection().equals("ovest")) {
                            lines[5+i] = lines[5+i] + ("│ OVEST       SMALL   │");
                        }

                    }
                    else{
                        if(meteor.getDirection().equals("nord")) {
                            lines[5+i] = lines[5+i] + ("│ NORD        LARGE   │");
                        }
                        else if(meteor.getDirection().equals("est")) {
                            lines[5+i] = lines[5+i] + ("│ EST         LARGE   │");
                        }
                        else if(meteor.getDirection().equals("sud")) {
                            lines[5+i] = lines[5+i] + ("│ SUD         LARGE   │");
                        }
                        else if(meteor.getDirection().equals("ovest")) {
                            lines[5+i] = lines[5+i] + ("│ OVEST       LARGE   │");
                        }
                    }
                    i++;
                }
            }
        }









        return lines;
    }

    /**
     * Method used to draw the legend that appears when a player picks a component tile
     * @param flightType is the type of the flight
     * @return the draw of the legend
     */
    public String[] drawComponentLegend(FlightType flightType) {
        String[] lines = new String[7];
        for (int i = 0; i < 7; i++) {
            lines[i] = "";
        }

        if (flightType == FlightType.FIRST_FLIGHT) {
            lines[0] = lines[0] + ("╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮");
            lines[1] = lines[1] + ("│ ╭───╮ │     │ " + Font.RED + "╺━━━┓" + Font.RESET + " │     │ " + Font.GOLD + "     " + Font.RESET + " │     │ " + Font.RED + "╭───╮" + Font.RESET + " │     │ " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " │     │ " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " │     │  " + Font.RED + "┌─┐" + Font.RESET + "  │     │ " + Font.RED + "┌─┬─┐" + Font.RESET + " │     │ " + Font.BLUE + "┌─┬─┐" + Font.RESET + " │     │" + Font.BLUE + "┌─┬─┬─┐" + Font.RESET + "│     │  " + Font.GREEN + "▄ ▄" + Font.RESET + "  │     │ " + Font.GREEN + "▄ ▄ ▄" + Font.RESET + " │     │ ╭─╮─╮╮│");
            lines[2] = lines[2] + ("│ │ ○ │ │     │     " + Font.RED + "┃" + Font.RESET + " │     │ " + Font.RED + "╭───╮" + Font.RESET + " │     │ " + Font.RED + "╞═══╡" + Font.RESET + " │     │ " + Font.PURPLE + "│   │" + Font.RESET + " │     │ " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " │     │  " + Font.RED + "│ │" + Font.RESET + "  │     │ " + Font.RED + "│ │ │" + Font.RESET + " │     │ " + Font.BLUE + "│ │ │" + Font.RESET + " │     │" + Font.BLUE + "│ │ │ │" + Font.RESET + "│     │  " + Font.GREEN + "█ █" + Font.RESET + "  │     │ " + Font.GREEN + "█ █ █" + Font.RESET + " │     │╰──┴─┤││");
            lines[3] = lines[3] + ("│ ╰───╯ │     │     " + Font.RED + "╹" + Font.RESET + " │     │ " + Font.RED + "╘═══╛" + Font.RESET + " │     │ " + Font.RED + "╘═══╛" + Font.RESET + " │     │ " + Font.PURPLE + "└───┘" + Font.RESET + " │     │ " + Font.PURPLE + "└───┘" + Font.RESET + " │     │  " + Font.RED + "└─┘" + Font.RESET + "  │     │ " + Font.RED + "└─┴─┘" + Font.RESET + " │     │ " + Font.BLUE + "└─┴─┘" + Font.RESET + " │     │" + Font.BLUE + "└─┴─┴─┘" + Font.RESET + "│     │  " + Font.GREEN + "▀ ▀" + Font.RESET + "  │     │ " + Font.GREEN + "▀ ▀ ▀" + Font.RESET + " │     │ ╰┴─╰─╯│");
            lines[4] = lines[4] + ("╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯");
            lines[5] = lines[6] + (" CABINE        SHIELD        SINGLE        DOUBLE        SINGLE        DOUBLE        SINGLE        DOUBLE        DOUBLE        TRIPLE        DOUBLE        TRIPLE        STRUCTURAL");
            lines[6] = lines[6] + ("                             ENGINE        ENGINE        CANNON        CANNON        RED CARGO     RED CARGO     BLUE CARGO    BLUE CARGO    BATTERY       BATTERY       MODULE    ");
        }

        if (flightType == FlightType.STANDARD_FLIGHT) {
            lines[0] = lines[0] + ("╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮     ╭───────╮ ");
            lines[1] = lines[1] + ("│ ╭───╮ │     │ " + Font.RED + "╺━━━┓" + Font.RESET + " │     │ " + Font.GOLD + "     " + Font.RESET + " │     │ " + Font.RED + "╭───╮" + Font.RESET + " │     │ " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " │     │ " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " │     │  " + Font.RED + "┌─┐" + Font.RESET + "  │     │ " + Font.RED + "┌─┬─┐" + Font.RESET + " │     │ " + Font.BLUE + "┌─┬─┐" + Font.RESET + " │     │" + Font.BLUE + "┌─┬─┬─┐" + Font.RESET + "│     │  " + Font.GREEN + "▄ ▄" + Font.RESET + "  │     │ " + Font.GREEN + "▄ ▄ ▄" + Font.RESET + " │     │ ╭─╮─╮╮│     │" + Font.GOLD + " ◉═╦═◉ " + Font.RESET + "│     │" + Font.PURPLE + " ◉═╦═◉ " + Font.RESET + "│ ");
            lines[2] = lines[2] + ("│ │ ○ │ │     │     " + Font.RED + "┃" + Font.RESET + " │     │ " + Font.RED + "╭───╮" + Font.RESET + " │     │ " + Font.RED + "╞═══╡" + Font.RESET + " │     │ " + Font.PURPLE + "│   │" + Font.RESET + " │     │ " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " │     │  " + Font.RED + "│ │" + Font.RESET + "  │     │ " + Font.RED + "│ │ │" + Font.RESET + " │     │ " + Font.BLUE + "│ │ │" + Font.RESET + " │     │" + Font.BLUE + "│ │ │ │" + Font.RESET + "│     │  " + Font.GREEN + "█ █" + Font.RESET + "  │     │ " + Font.GREEN + "█ █ █" + Font.RESET + " │     │╰──┴─┤││     │" + Font.GOLD + " ◉═○═◉ " + Font.RESET + "│     │" + Font.PURPLE + " ◉═○═◉ " + Font.RESET + "│ ");
            lines[3] = lines[3] + ("│ ╰───╯ │     │     " + Font.RED + "╹" + Font.RESET + " │     │ " + Font.RED + "╘═══╛" + Font.RESET + " │     │ " + Font.RED + "╘═══╛" + Font.RESET + " │     │ " + Font.PURPLE + "└───┘" + Font.RESET + " │     │ " + Font.PURPLE + "└───┘" + Font.RESET + " │     │  " + Font.RED + "└─┘" + Font.RESET + "  │     │ " + Font.RED + "└─┴─┘" + Font.RESET + " │     │ " + Font.BLUE + "└─┴─┘" + Font.RESET + " │     │" + Font.BLUE + "└─┴─┴─┘" + Font.RESET + "│     │  " + Font.GREEN + "▀ ▀" + Font.RESET + "  │     │ " + Font.GREEN + "▀ ▀ ▀" + Font.RESET + " │     │ ╰┴─╰─╯│     │" + Font.GOLD + " ◉═╩═◉ " + Font.RESET + "│     │" + Font.PURPLE + " ◉═╩═◉ " + Font.RESET + "│ ");
            lines[4] = lines[4] + ("╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯     ╰───────╯ ");
            lines[5] = lines[6] + (" CABINE        SHIELD        SINGLE        DOUBLE        SINGLE        DOUBLE        SINGLE        DOUBLE        DOUBLE        TRIPLE        DOUBLE        TRIPLE        STRUCTURAL    YELLOW        PURPLE");
            lines[6] = lines[6] + ("                             ENGINE        ENGINE        CANNON        CANNON        RED CARGO     RED CARGO     BLUE CARGO    BLUE CARGO    BATTERY       BATTERY       MODULE        SUPPORT       SUPPORT");
        }



        return lines;
    }


    /**
     * Method used to draw the tiles deck
     * @param gameTile is the deck of the component tile
     * @return the draw of the deck
     */
    public String[] drawTilesDeck(GameTile gameTile) {
        String[] lines = new String[42];
        for (int i = 0; i < 42; i++) {
            lines[i] = "";
        }

        if (gameTile.getFlightType() == FlightType.FIRST_FLIGHT) {
            for (int k = 0; k < 140; k++) {
                ComponentTile componentTile = gameTile.getComponentTile(k);

                if (componentTile == null) {
                    lines[0 + (k / 27) * 7] = lines[0 + (k / 27) * 7] + ("          ");
                    lines[1 + (k / 27) * 7] = lines[1 + (k / 27) * 7] + ("          ");
                    lines[2 + (k / 27) * 7] = lines[2 + (k / 27) * 7] + ("          ");
                    lines[3 + (k / 27) * 7] = lines[3 + (k / 27) * 7] + ("          ");
                    lines[4 + (k / 27) * 7] = lines[4 + (k / 27) * 7] + ("          ");
                    lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("          ");
                    lines[6 + (k / 27) * 7] = lines[6 + (k / 27) * 7] + ("          ");
                } else if (componentTile.isFaceDown()) {
                    lines[0 + (k / 27) * 7] = lines[0 + (k / 27) * 7] + ("╭───────╮ ");
                    lines[1 + (k / 27) * 7] = lines[1 + (k / 27) * 7] + ("│       │ ");
                    lines[2 + (k / 27) * 7] = lines[2 + (k / 27) * 7] + ("│       │ ");
                    lines[3 + (k / 27) * 7] = lines[3 + (k / 27) * 7] + ("│       │ ");
                    lines[4 + (k / 27) * 7] = lines[4 + (k / 27) * 7] + ("╰───────╯ ");
                    if (k < 10) {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("    " + (k) + "     ");
                    } else if (k >= 100) {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "    ");
                    } else {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "     ");
                    }
                    lines[6 + (k / 27) * 7] = lines[6 + (k / 27) * 7] + ("          ");
                } else if (!componentTile.isFaceDown()) {
                    String[] lines3 = this.drawComponentTile(componentTile);

                    lines[0 + (k / 27) * 7] = lines[0 + (k / 27) * 7] + lines3[0] + (" ");
                    lines[1 + (k / 27) * 7] = lines[1 + (k / 27) * 7] + lines3[1] + (" ");
                    lines[2 + (k / 27) * 7] = lines[2 + (k / 27) * 7] + lines3[2] + (" ");
                    lines[3 + (k / 27) * 7] = lines[3 + (k / 27) * 7] + lines3[3] + (" ");
                    lines[4 + (k / 27) * 7] = lines[4 + (k / 27) * 7] + lines3[4] + (" ");
                    if (k < 10) {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("    " + (k) + "     ");
                    } else if (k >= 100) {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "    ");
                    } else {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "     ");
                    }
                    lines[6 + (k / 27) * 7] = lines[6 + (k / 27) * 7] + ("          ");
                }
            }
        }

        if (gameTile.getFlightType() == FlightType.STANDARD_FLIGHT) {
            for (int k = 0; k < 152; k++) {
                ComponentTile componentTile = gameTile.getComponentTile(k);

                if (componentTile == null) {
                    lines[0 + (k / 27) * 7] = lines[0 + (k / 27) * 7] + ("          ");
                    lines[1 + (k / 27) * 7] = lines[1 + (k / 27) * 7] + ("          ");
                    lines[2 + (k / 27) * 7] = lines[2 + (k / 27) * 7] + ("          ");
                    lines[3 + (k / 27) * 7] = lines[3 + (k / 27) * 7] + ("          ");
                    lines[4 + (k / 27) * 7] = lines[4 + (k / 27) * 7] + ("          ");
                    lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("          ");
                    lines[6 + (k / 27) * 7] = lines[6 + (k / 27) * 7] + ("          ");
                } else if (componentTile.isFaceDown()) {
                    lines[0 + (k / 27) * 7] = lines[0 + (k / 27) * 7] + ("╭───────╮ ");
                    lines[1 + (k / 27) * 7] = lines[1 + (k / 27) * 7] + ("│       │ ");
                    lines[2 + (k / 27) * 7] = lines[2 + (k / 27) * 7] + ("│       │ ");
                    lines[3 + (k / 27) * 7] = lines[3 + (k / 27) * 7] + ("│       │ ");
                    lines[4 + (k / 27) * 7] = lines[4 + (k / 27) * 7] + ("╰───────╯ ");
                    if (k < 10) {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("    " + (k) + "     ");
                    } else if (k >= 100) {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "    ");
                    } else {
                        lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "     ");
                    }
                    lines[6 + (k / 27) * 7] = lines[6 + (k / 27) * 7] + ("          ");
                } else if (!componentTile.isFaceDown()) {
                String[] lines3 = this.drawComponentTile(componentTile);

                lines[0 + (k / 27) * 7] = lines[0 + (k / 27) * 7] + lines3[0] + (" ");
                lines[1 + (k / 27) * 7] = lines[1 + (k / 27) * 7] + lines3[1] + (" ");
                lines[2 + (k / 27) * 7] = lines[2 + (k / 27) * 7] + lines3[2] + (" ");
                lines[3 + (k / 27) * 7] = lines[3 + (k / 27) * 7] + lines3[3] + (" ");
                lines[4 + (k / 27) * 7] = lines[4 + (k / 27) * 7] + lines3[4] + (" ");
                if (k < 10) {
                    lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("    " + (k) + "     ");
                } else if (k >= 100) {
                    lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "    ");
                } else {
                    lines[5 + (k / 27) * 7] = lines[5 + (k / 27) * 7] + ("   " + (k) + "     ");
                }
                lines[6 + (k / 27) * 7] = lines[6 + (k / 27) * 7] + ("          ");
            }

            }
        }

        return lines;
    }

    /**
     * Method used to draw a specific component tile
     * @param componentTile is the tile considered
     * @return the draw of the tile
     */
    public String[] drawComponentTile(ComponentTile componentTile) {
        String[] lines = new String[5];
        for (int i = 0; i < 5; i++) {
            lines[i] = "";
        }

        switch (componentTile.getDirection()) {
            case "nord":
                switch (componentTile.getConnector("up")) {
                    case 0:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    case 1:
                        lines[0] = lines[0] + ("╭───│───╮");
                        break;
                    case 2:
                        lines[0] = lines[0] + ("╭─│───│─╮");
                        break;
                    case 3:
                        lines[0] = lines[0] + ("╭─│─│─│─╮");
                        break;
                    case 5:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    default:
                        break;
                }

                switch (componentTile.getConnector("down")) {
                    case 0:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    case 1:
                        lines[4] = lines[4] + ("╰───│───╯");
                        break;
                    case 2:
                        lines[4] = lines[4] + ("╰─│───│─╯");
                        break;
                    case 3:
                        lines[4] = lines[4] + ("╰─│─│─│─╯");
                        break;
                    case 5:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    default:
                        break;
                }

                switch (componentTile.getConnector("left")) {
                    case 0:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 1:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 2:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 3:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 5:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    default:
                        break;
                }
                break;

            case "est":
                switch (componentTile.getConnector("left")) {
                    case 0:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    case 1:
                        lines[0] = lines[0] + ("╭───│───╮");
                        break;
                    case 2:
                        lines[0] = lines[0] + ("╭─│───│─╮");
                        break;
                    case 3:
                        lines[0] = lines[0] + ("╭─│─│─│─╮");
                        break;
                    case 5:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    default:
                        break;
                }

                switch (componentTile.getConnector("right")) {
                    case 0:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    case 1:
                        lines[4] = lines[4] + ("╰───│───╯");
                        break;
                    case 2:
                        lines[4] = lines[4] + ("╰─│───│─╯");
                        break;
                    case 3:
                        lines[4] = lines[4] + ("╰─│─│─│─╯");
                        break;
                    case 5:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    default:
                        break;
                }

                switch (componentTile.getConnector("down")) {
                    case 0:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 1:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 2:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 3:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 5:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    default:
                        break;
                }
                break;
            case "sud":
                switch (componentTile.getConnector("down")) {
                    case 0:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    case 1:
                        lines[0] = lines[0] + ("╭───│───╮");
                        break;
                    case 2:
                        lines[0] = lines[0] + ("╭─│───│─╮");
                        break;
                    case 3:
                        lines[0] = lines[0] + ("╭─│─│─│─╮");
                        break;
                    case 5:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    default:
                        break;
                }

                switch (componentTile.getConnector("up")) {
                    case 0:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    case 1:
                        lines[4] = lines[4] + ("╰───│───╯");
                        break;
                    case 2:
                        lines[4] = lines[4] + ("╰─│───│─╯");
                        break;
                    case 3:
                        lines[4] = lines[4] + ("╰─│─│─│─╯");
                        break;
                    case 5:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    default:
                        break;
                }
                switch (componentTile.getConnector("right")) {
                    case 0:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 1:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 2:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 3:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 5:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    default:
                        break;
                }
                break;
            case "ovest":
                switch (componentTile.getConnector("right")) {
                    case 0:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    case 1:
                        lines[0] = lines[0] + ("╭───│───╮");
                        break;
                    case 2:
                        lines[0] = lines[0] + ("╭─│───│─╮");
                        break;
                    case 3:
                        lines[0] = lines[0] + ("╭─│─│─│─╮");
                        break;
                    case 5:
                        lines[0] = lines[0] + ("╭───────╮");
                        break;
                    default:
                        break;
                }

                switch (componentTile.getConnector("left")) {
                    case 0:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    case 1:
                        lines[4] = lines[4] + ("╰───│───╯");
                        break;
                    case 2:
                        lines[4] = lines[4] + ("╰─│───│─╯");
                        break;
                    case 3:
                        lines[4] = lines[4] + ("╰─│─│─│─╯");
                        break;
                    case 5:
                        lines[4] = lines[4] + ("╰───────╯");
                        break;
                    default:
                        break;
                }
                switch (componentTile.getConnector("up")) {
                    case 0:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 1:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("│");
                        break;
                    case 2:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 3:
                        lines[1] = lines[1] + ("─");
                        lines[2] = lines[2] + ("─");
                        lines[3] = lines[3] + ("─");
                        break;
                    case 5:
                        lines[1] = lines[1] + ("│");
                        lines[2] = lines[2] + ("│");
                        lines[3] = lines[3] + ("│");
                        break;
                    default:
                        break;
                }
                break;
        }

        if (componentTile.getName() == TileName.STARTING_CABINE) {
            StartingCabine startingCabine = (StartingCabine) componentTile;

            if(startingCabine.getNumFigures()==0)
            {
                if (startingCabine.getColor() == Color.RED) {
                    lines[1] = lines[1] + (Font.RED + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.RED + " │ ○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.RED + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.BLUE) {
                    lines[1] = lines[1] + (Font.BLUE + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.BLUE + " │ ○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.BLUE + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.GREEN) {
                    lines[1] = lines[1] + (Font.GREEN + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.GREEN + " │ ○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.GREEN + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.YELLOW) {
                    lines[1] = lines[1] + (Font.YELLOW + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.YELLOW + " │ ○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.YELLOW + " ╰───╯ " + Font.RESET + "─");
                }
            }
            else if(startingCabine.getNumFigures()==1)
            {
                if (startingCabine.getColor() == Color.RED) {
                    lines[1] = lines[1] + (Font.RED + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.RED + " │" + Font.GRAY + "◉" + Font.RED + "○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.RED + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.BLUE) {
                    lines[1] = lines[1] + (Font.BLUE + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.BLUE + " │" + Font.GRAY + "◉" + Font.BLUE + "○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.BLUE + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.GREEN) {
                    lines[1] = lines[1] + (Font.GREEN + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.GREEN + " │" + Font.GRAY + "◉" + Font.GREEN + "○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.GREEN + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.YELLOW) {
                    lines[1] = lines[1] + (Font.YELLOW + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.YELLOW + " │" + Font.GRAY + "◉" + Font.YELLOW + "○ │ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.YELLOW + " ╰───╯ " + Font.RESET + "─");
                }
            }
            else if(startingCabine.getNumFigures()==2)
            {
                if (startingCabine.getColor() == Color.RED) {
                    lines[1] = lines[1] + (Font.RED + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.RED + " │" + Font.GRAY + "◉" + Font.RED + "○" + Font.GRAY + "◉" + Font.RED + "│ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.RED + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.BLUE) {
                    lines[1] = lines[1] + (Font.BLUE + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.BLUE + " │" + Font.GRAY + "◉" + Font.BLUE + "○" + Font.GRAY + "◉" + Font.BLUE + "│ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.BLUE + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.GREEN) {
                    lines[1] = lines[1] + (Font.GREEN + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.GREEN + " │" + Font.GRAY + "◉" + Font.GREEN + "○" + Font.GRAY + "◉" + Font.GREEN + "│ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.GREEN + " ╰───╯ " + Font.RESET + "─");
                } else if (startingCabine.getColor() == Color.YELLOW) {
                    lines[1] = lines[1] + (Font.YELLOW + " ╭───╮ " + Font.RESET + "─");
                    lines[2] = lines[2] + (Font.YELLOW + " │" + Font.GRAY + "◉" + Font.YELLOW + "○" + Font.GRAY + "◉" + Font.YELLOW + "│ " + Font.RESET + "─");
                    lines[3] = lines[3] + (Font.YELLOW + " ╰───╯ " + Font.RESET + "─");
                }
            }
        }
        if (componentTile.getName() == TileName.CABINE) {
            Cabine cabine = (Cabine) componentTile;
            switch (componentTile.getDirection()) {
                case "nord":
                    if(cabine.getNumFigures()==0 && !cabine.getHasPurpleAlien() && !cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getNumFigures()==1)
                    {
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY +  "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getNumFigures()==2)
                    {
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasPurpleAlien())
                    {
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE +  "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD +  "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }

                    break;

                case "est":
                    if(cabine.getNumFigures()==0 && !cabine.getHasPurpleAlien() && !cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getNumFigures()==1)
                    {
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getNumFigures()==2)
                    {
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasPurpleAlien())
                    {
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }

                    break;

                case "sud":
                    if(cabine.getNumFigures()==0 && !cabine.getHasBrownAlien() && !cabine.getHasPurpleAlien())
                    {
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getNumFigures()==1)
                    {
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getNumFigures()==2)
                    {
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasPurpleAlien())
                    {
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE+ "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE+ "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD+ "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD+ "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }

                    break;
                case "ovest":
                    if(cabine.getNumFigures()==0 && !cabine.getHasPurpleAlien() && !cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │ ○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }

                    else if(cabine.getNumFigures()==1)
                    {
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }

                    else if(cabine.getNumFigures()==2)
                    {
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GRAY + "◉" + Font.RESET + "○" + Font.GRAY + "◉" + Font.RESET +"│ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasPurpleAlien())
                    {
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.PURPLE + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    else if(cabine.getHasBrownAlien())
                    {
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("─");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("─");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" ╭───╮ ") + ("│");
                                lines[2] = lines[2] + (" │" + Font.GOLD + "◉" + Font.RESET + "○ │ ") + ("│");
                                lines[3] = lines[3] + (" ╰───╯ ") + ("│");
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
        }
        if (componentTile.getName() == TileName.ALIEN_CABINE) {
            AlienCabine alienCabine = (AlienCabine) componentTile;
            if(alienCabine.getColor() == Color.YELLOW) {
                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.GOLD + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.GOLD + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.GOLD + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
            else {
                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("─");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("─");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (Font.PURPLE + " ◉═╦═◉ " + Font.RESET) + ("│");
                                lines[2] = lines[2] + (Font.PURPLE + " ◉═○═◉ " + Font.RESET) + ("│");
                                lines[3] = lines[3] + (Font.PURPLE + " ◉═╩═◉ " + Font.RESET) + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }


        if (componentTile.getName() == TileName.SHIELD) {
            switch (componentTile.getDirection()) {
                case "nord":
                    switch (componentTile.getConnector("right")) {
                        case 0:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╺━━━┓" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "    ╹" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╺━━━┓" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "    ╹" + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 1:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╺━━━┓" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "    ╹" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╺━━━┓" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "    ╹" + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 2:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╺━━━┓" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "    ╹" + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╺━━━┓" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "    ╹" + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 3:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╺━━━┓" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "    ╹" + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╺━━━┓" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "    ╹" + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 5:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╺━━━┓" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "    ╹" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╺━━━┓" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "    ╹" + Font.RESET + " ") + ("│");
                            }
                            break;
                        default:
                            break;
                    }
                    break;

                case "est":
                    switch (componentTile.getConnector("up")) {
                        case 0:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "    ╻" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╺━━━┛" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "    ╻" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╺━━━┛" + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 1:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "    ╻" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╺━━━┛" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "    ╻" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╺━━━┛" + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 2:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "    ╻" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╺━━━┛" + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "    ╻" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╺━━━┛" + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 3:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "    ╻" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╺━━━┛" + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "    ╻" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╺━━━┛" + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 5:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "    ╻" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╺━━━┛" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "    ╻" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "    ┃" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╺━━━┛" + Font.RESET + " ") + ("│");
                            }
                            break;
                        default:
                            break;
                    }
                    break;

                case "sud":
                    switch (componentTile.getConnector("left")) {
                        case 0:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╻    " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "┗━━━╸" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╻    " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "┗━━━╸" + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 1:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╻    " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "┗━━━╸" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╻    " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "┗━━━╸" + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 2:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╻    " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "┗━━━╸" + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╻    " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "┗━━━╸" + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 3:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╻    " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "┗━━━╸" + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╻    " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "┗━━━╸" + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 5:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "╻    " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "┗━━━╸" + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "╻    " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "┗━━━╸" + Font.RESET + " ") + ("│");
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case "ovest":
                    switch (componentTile.getConnector("down")) {
                        case 0:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "┏━━━╸" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╹    " + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "┏━━━╸" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╹    " + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 1:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "┏━━━╸" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╹    " + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "┏━━━╸" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╹    " + Font.RESET + " ") + ("│");
                            }
                            break;
                        case 2:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "┏━━━╸" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╹    " + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "┏━━━╸" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╹    " + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 3:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "┏━━━╸" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╹    " + Font.RESET + " ") + ("─");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "┏━━━╸" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╹    " + Font.RESET + " ") + ("─");
                            }
                            break;
                        case 5:
                            if(((Shield) componentTile).getActive()) {
                                lines[1] = lines[1] + (" " + Font.RED + "┏━━━╸" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╹    " + Font.RESET + " ") + ("│");
                            }
                            else {
                                lines[1] = lines[1] + (" " + Font.GREEN + "┏━━━╸" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.GREEN + "┃    " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.GREEN + "╹    " + Font.RESET + " ") + ("│");
                            }
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

        if (componentTile.getName() == TileName.ENGINE) {
            Engine engine = (Engine) componentTile;
            if (!engine.isDouble()) {
                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.RED + "     " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.RED + "     " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.RED + "     " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.RED + "     " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.RED + "     " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.RED + "╓─╮  " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "║ │  " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╙─╯  " + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.RED + "╓─╮  " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "║ │  " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╙─╯  " + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.RED + "╓─╮  " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "║ │  " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╙─╯  " + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.RED + "╓─╮  " + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "║ │  " + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "╙─╯  " + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.RED + "╓─╮  " + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "║ │  " + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "╙─╯  " + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "     " + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "     " + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "     " + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "     " + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "     " + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.RED + "  ╭─╖" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "  │ ║" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "  ╰─╜" + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.RED + "  ╭─╖" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "  │ ║" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "  ╰─╜" + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.RED + "  ╭─╖" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "  │ ║" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "  ╰─╜" + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.RED + "  ╭─╖" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "  │ ║" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.RED + "  ╰─╜" + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.RED + "  ╭─╖" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "  │ ║" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.RED + "  ╰─╜" + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }
            } else {
                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╘═══╛" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╘═══╛" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╘═══╛" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╘═══╛" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╘═══╛" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╘═══╛" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╓─╥─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "║ ║ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╙─╨─╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╓─╥─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "║ ║ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╙─╨─╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╓─╥─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "║ ║ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╙─╨─╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╓─╥─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "║ ║ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╙─╨─╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╓─╥─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "║ ║ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╙─╨─╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╓─╥─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "║ ║ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╙─╨─╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╓─╥─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "║ ║ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╙─╨─╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╓─╥─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "║ ║ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╙─╨─╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╓─╥─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "║ ║ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╙─╨─╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╓─╥─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "║ ║ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╙─╨─╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╒═══╕" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰───╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╒═══╕" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰───╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╒═══╕" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰───╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╒═══╕" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰───╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╒═══╕" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰───╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╒═══╕" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "╞═══╡" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰───╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭─╥─╖" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "│ ║ ║" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰─╨─╜" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭─╥─╖" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "│ ║ ║" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰─╨─╜" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭─╥─╖" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "│ ║ ║" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰─╨─╜" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭─╥─╖" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "│ ║ ║" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰─╨─╜" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭─╥─╖" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "│ ║ ║" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰─╨─╜" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭─╥─╖" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "│ ║ ║" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰─╨─╜" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭─╥─╖" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "│ ║ ║" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰─╨─╜" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭─╥─╖" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "│ ║ ║" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰─╨─╜" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Engine) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.RED + "╭─╥─╖" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "│ ║ ║" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.RED + "╰─╨─╜" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.GOLD + "╭─╥─╖" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.GOLD + "│ ║ ║" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.GOLD + "╰─╨─╜" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                }

            }
        }


        if (componentTile.getName() == TileName.CANNON) {
            Cannon cannon = (Cannon) componentTile;
            if (!cannon.isDouble()) {
                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   " + Font.RESET + Font.RED + "▶" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   " + Font.RESET + Font.RED + "▶" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   " + Font.RESET + Font.RED + "▶" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   " + Font.RESET + Font.RED + "▶" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   " + Font.RESET + Font.RED + "▶" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.PURPLE + "│   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "◀" + Font.RESET + Font.PURPLE + "   │" + Font.RESET + " ") + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "◀" + Font.RESET + Font.PURPLE + "   │" + Font.RESET + " ")  + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "◀" + Font.RESET + Font.PURPLE + "   │" + Font.RESET + " ")  + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("─");
                                lines[2] = lines[2] + (" " + Font.RED + "◀" + Font.RESET + Font.PURPLE + "   │" + Font.RESET + " ")  + ("─");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                lines[2] = lines[2] + (" " + Font.RED + "◀" + Font.RESET + Font.PURPLE + "   │" + Font.RESET + " ")  + ("│");
                                lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }
            } else {
                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─▲─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▲ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─▲─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▲ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                }

                                break;
                            case 2:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─▲─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▲ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─▲─╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▲ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─" + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + "─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▲" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭─▲─╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▲ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───┘" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▶ ▶" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▶ ▶" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▶ ▶" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▶ ▶" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▶ ▶" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▶ ▶" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▶ ▶" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▶ ▶" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▶ ▶" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───╮" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▶ ▶" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "└───╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▼ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─▼─╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▼ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─▼─╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▼ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─▼─╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▼ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─▼─╯" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ " + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + " │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─" + Font.RESET + Font.RED + "▼" + Font.RESET + Font.PURPLE + "─╯" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "┌───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "│ ▼ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰─▼─╯" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "◀ ◀" + Font.RESET + Font.PURPLE +" │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "◀ ◀ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 1:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "◀ ◀" + Font.RESET + Font.PURPLE +" │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "◀ ◀ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                }
                                break;
                            case 2:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "◀ ◀" + Font.RESET + Font.PURPLE +" │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "◀ ◀ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 3:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.RED + "◀ ◀" + Font.RESET + Font.PURPLE +" │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("─");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("─");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "◀ ◀ │" + Font.RESET + " ") + ("─");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("─");
                                }
                                break;
                            case 5:
                                if(((Cannon) componentTile).getActive()) {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.RED + "◀ ◀" + Font.RESET + Font.PURPLE +" │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                }
                                else {
                                    lines[1] = lines[1] + (" " + Font.PURPLE + "╭───┐" + Font.RESET + " ") + ("│");
                                    lines[2] = lines[2] + (" " + Font.PURPLE + "◀ ◀ │" + Font.RESET + " ") + ("│");
                                    lines[3] = lines[3] + (" " + Font.PURPLE + "╰───┘" + Font.RESET + " ") + ("│");
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                }

            }
        }

        if (componentTile.getName() == TileName.CARGO) {
            Cargo cargo = (Cargo) componentTile;
            ArrayList<Color> cargosIn = cargo.getCargosIn();
            if (cargo.getNumMaxCargos() == 3) {
                if (cargosIn.size() == 0) {
                    lines[1] = lines[1] + (Font.BLUE + "┌─┬─┬─┐" + Font.RESET);
                    lines[2] = lines[2] + (Font.BLUE + "│ │ │ │" + Font.RESET);
                    lines[3] = lines[3] + (Font.BLUE + "└─┴─┴─┘" + Font.RESET);
                } else if (cargosIn.size() == 1) {
                    lines[1] = lines[1] + (Font.BLUE + "┌─┬─┬─┐" + Font.RESET);
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    Color c1 = cargosIn.get(0);
                    if (c1 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c1 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c1 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.BLUE + "│ │ │" + Font.RESET);
                    lines[3] = lines[3] + (Font.BLUE + "└─┴─┴─┘" + Font.RESET);
                } else if (cargosIn.size() == 2) {
                    lines[1] = lines[1] + (Font.BLUE + "┌─┬─┬─┐" + Font.RESET);
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    Color c1 = cargosIn.get(0);
                    if (c1 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c1 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c1 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    Color c2 = cargosIn.get(1);
                    if (c2 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c2 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c2 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.BLUE + "│ │" + Font.RESET);
                    lines[3] = lines[3] + (Font.BLUE + "└─┴─┴─┘" + Font.RESET);
                } else {
                    lines[1] = lines[1] + (Font.BLUE + "┌─┬─┬─┐" + Font.RESET);
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    Color c1 = cargosIn.get(0);
                    if (c1 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c1 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c1 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    Color c2 = cargosIn.get(1);
                    if (c2 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c2 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c2 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    Color c3 = cargosIn.get(2);
                    if (c3 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c3 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c3 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                    lines[3] = lines[3] + (Font.BLUE + "└─┴─┴─┘" + Font.RESET);
                }

                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }
            } else if (cargo.getNumMaxCargos() == 2) {
                Color cargoColor = cargo.getColor();
                if (cargoColor == Color.RED) {
                    if (cargosIn.size() == 0) {
                        lines[1] = lines[1] + (Font.RED + " ┌─┬─┐ " + Font.RESET);
                        lines[2] = lines[2] + (Font.RED + " │ │ │ " + Font.RESET);
                        lines[3] = lines[3] + (Font.RED + " └─┴─┘ " + Font.RESET);
                    } else if (cargosIn.size() == 1) {
                        lines[1] = lines[1] + (Font.RED + " ┌─┬─┐ " + Font.RESET);
                        lines[2] = lines[2] + (Font.RED + " │" + Font.RESET);
                        Color c1 = cargosIn.get(0);
                        if (c1 == Color.RED) {
                            lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                        } else if (c1 == Color.BLUE) {
                            lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                        } else if (c1 == Color.GREEN) {
                            lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                        } else {
                            lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                        }
                        lines[2] = lines[2] + (Font.RED + "│ │ " + Font.RESET);
                        lines[3] = lines[3] + (Font.RED + " └─┴─┘ " + Font.RESET);
                    } else {
                        lines[1] = lines[1] + (Font.RED + " ┌─┬─┐ " + Font.RESET);
                        lines[2] = lines[2] + (Font.RED + " │" + Font.RESET);
                        Color c1 = cargosIn.get(0);
                        if (c1 == Color.RED) {
                            lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                        } else if (c1 == Color.BLUE) {
                            lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                        } else if (c1 == Color.GREEN) {
                            lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                        } else {
                            lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                        }
                        lines[2] = lines[2] + (Font.RED + "│" + Font.RESET);
                        Color c2 = cargosIn.get(1);
                        if (c2 == Color.RED) {
                            lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                        } else if (c2 == Color.BLUE) {
                            lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                        } else if (c2 == Color.GREEN) {
                            lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                        } else {
                            lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                        }
                        lines[2] = lines[2] + (Font.RED + "│ " + Font.RESET);
                        lines[3] = lines[3] + (Font.RED + " └─┴─┘ " + Font.RESET);
                    }
                } else {
                    if (cargosIn.size() == 0) {
                        lines[1] = lines[1] + (Font.BLUE + " ┌─┬─┐ " + Font.RESET);
                        lines[2] = lines[2] + (Font.BLUE + " │ │ │ " + Font.RESET);
                        lines[3] = lines[3] + (Font.BLUE + " └─┴─┘ " + Font.RESET);
                    } else if (cargosIn.size() == 1) {
                        lines[1] = lines[1] + (Font.BLUE + " ┌─┬─┐ " + Font.RESET);
                        lines[2] = lines[2] + (Font.BLUE + " │" + Font.RESET);
                        Color c1 = cargosIn.get(0);
                        if (c1 == Color.RED) {
                            lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                        } else if (c1 == Color.BLUE) {
                            lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                        } else if (c1 == Color.GREEN) {
                            lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                        } else {
                            lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                        }
                        lines[2] = lines[2] + (Font.BLUE + "│ │ " + Font.RESET);
                        lines[3] = lines[3] + (Font.BLUE + " └─┴─┘ " + Font.RESET);
                    } else {
                        lines[1] = lines[1] + (Font.BLUE + " ┌─┬─┐ " + Font.RESET);
                        lines[2] = lines[2] + (Font.BLUE + " │" + Font.RESET);
                        Color c1 = cargosIn.get(0);
                        if (c1 == Color.RED) {
                            lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                        } else if (c1 == Color.BLUE) {
                            lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                        } else if (c1 == Color.GREEN) {
                            lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                        } else {
                            lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                        }
                        lines[2] = lines[2] + (Font.BLUE + "│" + Font.RESET);
                        Color c2 = cargosIn.get(1);
                        if (c2 == Color.RED) {
                            lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                        } else if (c2 == Color.BLUE) {
                            lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                        } else if (c2 == Color.GREEN) {
                            lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                        } else {
                            lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                        }
                        lines[2] = lines[2] + (Font.BLUE + "│ " + Font.RESET);
                        lines[3] = lines[3] + (Font.BLUE + " └─┴─┘ " + Font.RESET);
                    }
                }

                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }
            } else {
                if (cargosIn.size() == 0) {
                    lines[1] = lines[1] + (Font.RED + "  ┌─┐  " + Font.RESET);
                    lines[2] = lines[2] + (Font.RED + "  │ │  " + Font.RESET);
                    lines[3] = lines[3] + (Font.RED + "  └─┘  " + Font.RESET);
                } else {
                    lines[1] = lines[1] + (Font.RED + "  ┌─┐  " + Font.RESET);
                    lines[2] = lines[2] + (Font.RED + "  │" + Font.RESET);
                    Color c1 = cargosIn.get(0);
                    if (c1 == Color.RED) {
                        lines[2] = lines[2] + (Font.RED + "█" + Font.RESET);
                    } else if (c1 == Color.BLUE) {
                        lines[2] = lines[2] + (Font.BLUE + "█" + Font.RESET);
                    } else if (c1 == Color.GREEN) {
                        lines[2] = lines[2] + (Font.GREEN + "█" + Font.RESET);
                    } else {
                        lines[2] = lines[2] + (Font.YELLOW + "█" + Font.RESET);
                    }
                    lines[2] = lines[2] + (Font.RED + "│  " + Font.RESET);
                    lines[3] = lines[3] + (Font.RED + "  └─┘  " + Font.RESET);
                }

                switch (componentTile.getDirection()) {
                    case "nord":
                        switch (componentTile.getConnector("right")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "est":
                        switch (componentTile.getConnector("up")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;

                    case "sud":
                        switch (componentTile.getConnector("left")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "ovest":
                        switch (componentTile.getConnector("down")) {
                            case 0:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 1:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("│");
                                break;
                            case 2:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 3:
                                lines[1] = lines[1] + ("─");
                                lines[2] = lines[2] + ("─");
                                lines[3] = lines[3] + ("─");
                                break;
                            case 5:
                                lines[1] = lines[1] + ("│");
                                lines[2] = lines[2] + ("│");
                                lines[3] = lines[3] + ("│");
                                break;
                            default:
                                break;
                        }
                        break;
                }

            }
        }


        if (componentTile.getName() == TileName.BATTERY) {
            Battery battery = (Battery) componentTile;
            if (battery.getNumMaxBatteries() == 3) {
                if (battery.getNumBatteriesInUse() == 3) {
                    lines[1] = lines[1] + (Font.GREEN + " ▄ ▄ ▄ " + Font.RESET);
                    lines[2] = lines[2] + (Font.GREEN + " █ █ █ " + Font.RESET);
                    lines[3] = lines[3] + (Font.GREEN + " ▀ ▀ ▀ " + Font.RESET);
                } else if (battery.getNumBatteriesInUse() == 2) {
                    lines[1] = lines[1] + (Font.GREEN + " ▄ ▄" + Font.RESET + " ▄ ");
                    lines[2] = lines[2] + (Font.GREEN + " █ █" + Font.RESET + " █ ");
                    lines[3] = lines[3] + (Font.GREEN + " ▀ ▀" + Font.RESET + " ▀ ");
                } else if (battery.getNumBatteriesInUse() == 1) {
                    lines[1] = lines[1] + (Font.GREEN + " ▄" + Font.RESET + " ▄ ▄ ");
                    lines[2] = lines[2] + (Font.GREEN + " █" + Font.RESET + " █ █ ");
                    lines[3] = lines[3] + (Font.GREEN + " ▀" + Font.RESET + " ▀ ▀ ");
                } else {
                    lines[1] = lines[1] + (" ▄ ▄ ▄  ");
                    lines[2] = lines[2] + (" █ █ █ ");
                    lines[3] = lines[3] + (" ▀ ▀ ▀ ");
                }
            } else {
                if (battery.getNumBatteriesInUse() == 2) {
                    lines[1] = lines[1] + (Font.GREEN + "  ▄ ▄  " + Font.RESET);
                    lines[2] = lines[2] + (Font.GREEN + "  █ █  " + Font.RESET);
                    lines[3] = lines[3] + (Font.GREEN + "  ▀ ▀  " + Font.RESET);
                } else if (battery.getNumBatteriesInUse() == 1) {
                    lines[1] = lines[1] + (Font.GREEN + "  ▄" + Font.RESET + " ▄  ");
                    lines[2] = lines[2] + (Font.GREEN + "  █" + Font.RESET + " █  ");
                    lines[3] = lines[3] + (Font.GREEN + "  ▀" + Font.RESET + " ▀  ");
                } else {
                    lines[1] = lines[1] + ("  ▄ ▄  ");
                    lines[2] = lines[2] + ("  █ █  ");
                    lines[3] = lines[3] + ("  ▀ ▀  ");
                }
            }


            switch (componentTile.getDirection()) {
                case "nord":
                    switch (componentTile.getConnector("right")) {
                        case 0:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        default:
                            break;
                    }
                    break;

                case "est":
                    switch (componentTile.getConnector("up")) {
                        case 0:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        default:
                            break;
                    }
                    break;

                case "sud":
                    switch (componentTile.getConnector("left")) {
                        case 0:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        default:
                            break;
                    }
                    break;
                case "ovest":
                    switch (componentTile.getConnector("down")) {
                        case 0:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + ("─");
                            lines[2] = lines[2] + ("─");
                            lines[3] = lines[3] + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + ("│");
                            lines[2] = lines[2] + ("│");
                            lines[3] = lines[3] + ("│");
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }

        if (componentTile.getName() == TileName.STRUCTURAL_MODULE) {
            switch (componentTile.getDirection()) {
                case "nord":
                    switch (componentTile.getConnector("right")) {
                        case 0:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        default:
                            break;
                    }
                    break;

                case "est":
                    switch (componentTile.getConnector("up")) {
                        case 0:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        default:
                            break;
                    }
                    break;

                case "sud":
                    switch (componentTile.getConnector("left")) {
                        case 0:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        default:
                            break;
                    }
                    break;
                case "ovest":
                    switch (componentTile.getConnector("down")) {
                        case 0:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 1:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        case 2:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 3:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("─");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("─");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("─");
                            break;
                        case 5:
                            lines[1] = lines[1] + (" ╭─╮─╮╮") + ("│");
                            lines[2] = lines[2] + ("╰──┴─┤│") + ("│");
                            lines[3] = lines[3] + (" ╰┴─╰─╯") + ("│");
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }


        return lines;
    }


    /**
     * Method used to draw the ship boards of all the players
     * @param shipBoards contains the nicknames of players associated with their shipboards
     * @return the draw of the ship boards
     */
    public String[] drawAllShips(HashMap<String, ShipBoard> shipBoards) {
        String[] lines = new String[33];
        for (int i = 0; i < 33; i++) {
            lines[i] = "";
        }
        lines[0] = lines[0] + ("    ");

        for(String nickname : shipBoards.keySet()) {
            lines[0] = lines[0] + Font.BOLD + nickname + Font.RESET;
            for(int i = 85; i >= nickname.length(); i--) {
                lines[0] = lines[0] + (" ");
            }
            ShipBoard shipBoard = shipBoards.get(nickname);
            String[] lines2 = this.drawShipBoard(shipBoard);
            for (int i = 0; i < 32; i++) {
                lines[i+1] = lines[i+1] + lines2[i] + ("            ");
            }
        }

        return lines;
    }


    /**
     * Method used to draw the component in the ship boards of every player while they are showing together
     * @param shipBoards contains the nicknames of players associated with their shipboards
     * @param count indicates if it's the first time that the method is called
     */
    public void componentInAllShipBoards(HashMap<String, ShipBoard> shipBoards, int count) {
        String[] lines = new String[5];
        for (int i = 0; i < 5; i++) {
            lines[i] = "";
        }

        int k = 0;

        int startRow = 7;
        int startCol = 5;

        if(count == 0) {
            System.out.print("\033[s");
            System.out.flush();
        }

        for(ShipBoard shipBoard : shipBoards.values()) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    if (shipBoard.getSpace(i, j).getComponent() != null) {
                        String[] lines2 = this.drawComponentTile(shipBoard.getSpace(i, j).getComponent());
                        for (String line2 : lines2) {
                            System.out.printf("\033[%d;%dH", startRow + k + i * 6, startCol + j * 10);
                            System.out.flush();
                            System.out.println(line2);
                            k++;
                        }
                        k = 0;
                    }
                }
            }
            startCol = startCol + 86;
        }

        System.out.print("\033[u");
        System.out.flush();
    }

    /**
     *  Method used to draw the deck of the cards
     * @param numCards is the number of cards contained in the deck
     * @return the draw of the deck
     */
    public String[] drawCardsDeck(int numCards) {
        String[] lines = new String[16];
        for (int i = 0; i < 16; i++) {
            lines[i] = "";
        }

        lines[0] = lines[0] + ("╭─────────────────────╮");
        lines[1] = lines[1] + ("│                     │");
        lines[2] = lines[2] + ("│                     │");
        lines[3] = lines[3] + ("│                     │");
        lines[4] = lines[4] + ("│                     │");
        lines[5] = lines[5] + ("│                     │");
        lines[6] = lines[6] + ("│                     │");
        lines[7] = lines[7] + ("│                     │");
        lines[8] = lines[8] + ("│                     │");
        lines[9] = lines[9] + ("│                     │");
        lines[10] = lines[10] + ("│                     │");
        lines[11] = lines[11] + ("│                     │");
        lines[12] = lines[12] + ("│                     │");
        lines[13] = lines[13] + ("│                     │");
        lines[14] = lines[14] + ("│                     │");
        lines[15] = lines[15] + ("╰─────────────────────╯");

        for(int i = 1; i < numCards; i++) {
            lines[0] = lines[0] + ("╮");
            lines[1] = lines[1] + ("│");
            lines[2] = lines[2] + ("│");
            lines[3] = lines[3] + ("│");
            lines[4] = lines[4] + ("│");
            lines[5] = lines[5] + ("│");
            lines[6] = lines[6] + ("│");
            lines[7] = lines[7] + ("│");
            lines[8] = lines[8] + ("│");
            lines[9] = lines[9] + ("│");
            lines[10] = lines[10] + ("│");
            lines[11] = lines[11] + ("│");
            lines[12] = lines[12] + ("│");
            lines[13] = lines[13] + ("│");
            lines[14] = lines[14] + ("│");
            lines[15] = lines[15] + ("╯");

        }

        return lines;
    }


    /**
     *  Method used to draw the flight board
     * @param flightBoard is the flight board used in the flight
     */
    public void drawFlightBoard(FlightBoard flightBoard) {

        String[] lines = new String[16];
        for (int i = 0; i < 16; i++) {
            lines[i] = "";
        }


        if(flightBoard.getTotalPositions() == 18) {
            lines[0] = lines[0] + ("      1          2          3          4          5          6          7          8");
            lines[1] = lines[1] + ("   ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮");
            lines[2] = lines[2] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[3] = lines[3] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[4] = lines[4] + ("   ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯");
            lines[6] = lines[6] + ("   ╭─────╮                                                                      ╭─────╮");
            lines[7] = lines[7] + ("18 │     │                                                                      │     │ 9");
            lines[8] = lines[8] + ("   │     │                                                                      │     │");
            lines[9] = lines[9] + ("   ╰─────╯                                                                      ╰─────╯");
            lines[11] = lines[11] + ("   ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮");
            lines[12] = lines[12] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[13] = lines[13] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[14] = lines[14] + ("   ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯");
            lines[15] = lines[15] + ("     17         16         15         14         13         12         11         10");
        }
        else {
            lines[0] = lines[0] + ("      1          2          3          4          5          6          7          8          9         10         11");
            lines[1] = lines[1] + ("   ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮");
            lines[2] = lines[2] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[3] = lines[3] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[4] = lines[4] + ("   ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯");
            lines[6] = lines[6] + ("   ╭─────╮                                                                                                       ╭─────╮");
            lines[7] = lines[7] + ("24 │     │                                                                                                       │     │ 12");
            lines[8] = lines[8] + ("   │     │                                                                                                       │     │");
            lines[9] = lines[9] + ("   ╰─────╯                                                                                                       ╰─────╯");
            lines[11] = lines[11] + ("   ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮    ╭─────╮");
            lines[12] = lines[12] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[13] = lines[13] + ("   │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │    │     │");
            lines[14] = lines[14] + ("   ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯    ╰─────╯");
            lines[15] = lines[15] + ("     23         22         21         20         19         18         17         16         15         14         13");
        }


        out.print("\033[48;100H");
        out.flush();
        out.print("\033[K");
        out.flush();
        out.print(Font.BOLD +"This is the flight board:" + Font.RESET);


        for(int j = 0; j < 16; j++) {
            out.printf("\033[%d;%dH", 50+j,100);
            out.flush();
            out.print("\033[K");
            out.flush();
            out.print(lines[j]);
            out.flush();
        }

        HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
        for(Player p : map.keySet()) {
            int num = map.get(p).getFirst();
            Color color = p.getColor();

            if(flightBoard.getTotalPositions() == 18) {
                if(num >= 0 && num <= 7) {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                }
                else if(num == 17) {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                }
                else if(num == 8) {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 57, 182);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 182);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 57, 182);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 182);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 57, 182);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 182);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 57, 182);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 182);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                }
                else {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 62, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 62, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 62, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 62, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (16-num)*11);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                }
            }
            else {

                if(num >= 0 && num <= 10) {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 52, 105 + num*11);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 53, 105 + num*11);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                }
                else if(num == 23) {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 57, 105);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 105);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                }
                else if(num == 11) {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 57, 215);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 215);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 57, 215);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 215);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 57, 215);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 215);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 57, 215);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 58, 215);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                }
                else {
                    if(color == Color.RED) {
                        out.printf("\033[%d;%dH", 62, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.RED + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.RED + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                    else if(color == Color.BLUE) {
                        out.printf("\033[%d;%dH", 62, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.BLUE + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.BLUE + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else if(color == Color.GREEN) {
                        out.printf("\033[%d;%dH", 62, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.GREEN + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.GREEN + "▙▄▟" + Font.RESET);
                        out.flush();

                    }
                    else {
                        out.printf("\033[%d;%dH", 62, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.YELLOW + "▛▀▜" + Font.RESET);
                        out.flush();
                        out.printf("\033[%d;%dH", 63, 105 + (22-num)*11);
                        out.flush();
                        out.print(Font.YELLOW + "▙▄▟" + Font.RESET);
                        out.flush();
                    }
                }

            }
        }
    }

    /**
     *  Method used to draw the parameters of a player
     * @param player is the player considered
     */
    public void drawPlayerParametres(Player player) {

        String[] lines = new String[9];
        for (int i = 0; i < 9; i++) {
            lines[i] = "";
        }

        int count = player.getShipBoard().getNumFigures()
                + (player.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                + (player.getShipBoard().getHasBrownAlien() ? 1 : 0);


        lines[0] = lines[0] + (Font.BOLD +"These are your parametres:" + Font.RESET);
        lines[1] = lines[1] + ("  Color:  " + player.getColor());
        lines[2] = lines[2] + ("  Cosmic credits:  " + player.getCosmicCredit());
        lines[3] = lines[3] + ("  Number of figures:  " + count);
        lines[4] = lines[4] + ("  Number of batteries:  " + player.getShipBoard().getNumBatteries());
        lines[5] = lines[5] + ("  Cannon strength:  " + player.getShipBoard().calculateFireStrength());
        lines[6] = lines[6] + ("  Engine strength:  " + player.getShipBoard().calculateEngineStrength());
        lines[7] = lines[7] + ("  Exposed Connectors:  " + player.getShipBoard().numExposedConnectors());
        lines[8] = lines[8] + ("  Lost tiles:  " + player.getShipBoard().getNumLostTiles());

        for(int j = 0; j < 9; j++) {
            out.printf("\033[%d;%dH", 69+j,100);
            out.flush();
            out.print("\033[K");
            out.flush();
            out.print(lines[j]);
            out.flush();
        }

    }










}

