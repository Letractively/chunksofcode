package com.myapp.games.schnellen.model;

import static com.myapp.games.schnellen.model.Cards.cards;
import static com.myapp.games.schnellen.model.Card.Color.eichel;
import static com.myapp.games.schnellen.model.Card.Color.herz;
import static com.myapp.games.schnellen.model.Card.Color.laub;
import static com.myapp.games.schnellen.model.Card.Color.schell;
import static com.myapp.games.schnellen.model.Card.Value.acht;
import static com.myapp.games.schnellen.model.Card.Value.koenig;
import static com.myapp.games.schnellen.model.Card.Value.neun;
import static com.myapp.games.schnellen.model.Card.Value.ober;
import static com.myapp.games.schnellen.model.Card.Value.sau;
import static com.myapp.games.schnellen.model.Card.Value.sieben;
import static com.myapp.games.schnellen.model.Card.Value.unter;
import static com.myapp.games.schnellen.model.Card.Value.zehn;

import com.myapp.games.schnellen.model.Card;

import junit.framework.TestCase;

public class CardTest extends TestCase {


//    private static List<Card> cards = Card.newCardDeck(Config.getInstance());
    
    
    public void testCardsHardcoded() throws Exception {
//        { // print out hardcoded javacode:
//            List<Card> cards = Card.newCardDeck();
//            for (int i = 0; i < cards.size(); i++) {
//                com.myapp.games.schnellen.model.Card c = cards.get(i);
//                com.myapp.games.schnellen.model.Card.Color col = c.getColor();
//                com.myapp.games.schnellen.model.Card.Value val = c.getValue();
//                String colStr = col.toString();
//                String valStr = val.toString();
//                System.out.println(
//                                   
//                                   
////       "c = cards.get("+i+"); /* ******************** "+c+" ****/\n" +
////       "assertEquals(c+\"\", c.getColor(), "+col+");\n" +
////       "assertEquals(c+\"\", c.getValue(), "+val+");\n"
//
////       "static final Card "+ (
////           c.equals(Card.WELI) 
////           ? "weli" 
////           : valStr.toLowerCase()+
////             colStr.substring(0, 1).toUpperCase()+
////             colStr.substring(1, colStr.length()).toLowerCase()
////       )+" = cards.get("+i+");"
//           
//                );
//            }
//        }
        
        Card c;
        
        c = cards.get(0); /* ******************** [EI-7] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), sieben);

        c = cards.get(1); /* ******************** [HE-7] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), sieben);

        c = cards.get(2); /* ******************** [LA-7] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), sieben);

        c = cards.get(3); /* ******************** [SC-7] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), sieben);

        c = cards.get(4); /* ******************** [EI-8] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), acht);

        c = cards.get(5); /* ******************** [HE-8] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), acht);

        c = cards.get(6); /* ******************** [LA-8] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), acht);

        c = cards.get(7); /* ******************** [SC-8] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), acht);

        c = cards.get(8); /* ******************** [EI-9] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), neun);

        c = cards.get(9); /* ******************** [HE-9] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), neun);

        c = cards.get(10); /* ******************** [LA-9] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), neun);

        c = cards.get(11); /* ******************** [SC-9] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), neun);

        c = cards.get(12); /* ******************** [EI-10] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), zehn);

        c = cards.get(13); /* ******************** [HE-10] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), zehn);

        c = cards.get(14); /* ******************** [LA-10] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), zehn);

        c = cards.get(15); /* ******************** [SC-10] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), zehn);

        c = cards.get(16); /* ******************** [EI-unter] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), unter);

        c = cards.get(17); /* ******************** [HE-unter] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), unter);

        c = cards.get(18); /* ******************** [LA-unter] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), unter);

        c = cards.get(19); /* ******************** [SC-unter] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), unter);

        c = cards.get(20); /* ******************** [EI-ober] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), ober);

        c = cards.get(21); /* ******************** [HE-ober] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), ober);

        c = cards.get(22); /* ******************** [LA-ober] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), ober);

        c = cards.get(23); /* ******************** [SC-ober] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), ober);

        c = cards.get(24); /* ******************** [EI-koenig] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), koenig);

        c = cards.get(25); /* ******************** [papa] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), koenig);

        c = cards.get(26); /* ******************** [LA-koenig] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), koenig);

        c = cards.get(27); /* ******************** [SC-koenig] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), koenig);

        c = cards.get(28); /* ******************** [EI-sau] ****/
        assertEquals(c+"", c.getColor(), eichel);
        assertEquals(c+"", c.getValue(), sau);

        c = cards.get(29); /* ******************** [HE-sau] ****/
        assertEquals(c+"", c.getColor(), herz);
        assertEquals(c+"", c.getValue(), sau);

        c = cards.get(30); /* ******************** [LA-sau] ****/
        assertEquals(c+"", c.getColor(), laub);
        assertEquals(c+"", c.getValue(), sau);

        c = cards.get(31); /* ******************** [SC-sau] ****/
        assertEquals(c+"", c.getColor(), schell);
        assertEquals(c+"", c.getValue(), sau);

        c = cards.get(32); /* ******************** [weli] ****/
        assertEquals(c+"", c.getColor(), null);
        assertEquals(c+"", c.getValue(), null);
        
        try {
            cards.get(33);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }
}
