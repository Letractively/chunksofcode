package com.myapp.games.schnellen.model;

import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.Card.Color;

import java.util.*;
import junit.framework.TestCase;

import static com.myapp.games.schnellen.model.Cards.*;
import static com.myapp.games.schnellen.model.Card.Color.*;
import static com.myapp.games.schnellen.model.Card.Value.*;
import static com.myapp.games.schnellen.model.CardRules.*;


public class CardRulesTest extends TestCase {

    public void testLowlevel1() {
        List<Card> hand = new Card.CardList();
        hand.add(siebenEichel);
        hand.add(siebenSchell);
        hand.add(achtEichel);
        hand.add(zehnHerz);
        hand.add(weli);
        
        assertEquals((Integer)2, valueSpread(hand).get(sieben));
        assertEquals((Integer)1, valueSpread(hand).get(acht));
        assertEquals(null,       valueSpread(hand).get(neun));
        assertEquals((Integer)1, valueSpread(hand).get(zehn));
        assertEquals(null,       valueSpread(hand).get(unter));
        assertEquals(null,       valueSpread(hand).get(ober));
        assertEquals(null,       valueSpread(hand).get(koenig));
        assertEquals(null,       valueSpread(hand).get(sau));
        
        assertEquals((Integer)7,  pointsPerColor(hand).get(schell));
        assertEquals((Integer)15, pointsPerColor(hand).get(eichel));
        assertEquals((Integer)10, pointsPerColor(hand).get(herz));
        assertEquals(null,        pointsPerColor(hand).get(laub));
        
        assertTrue(isTechnicallyUntermHund(hand));
        assertEquals(eichel, suggestColor(hand));
    }

    public void testLowlevel2() {
        List<Card> hand = new Card.CardList();
        hand.add(siebenEichel);
        hand.add(siebenSchell);
        hand.add(zehnHerz);
        hand.add(koenigEichel);
        hand.add(weli);

        assertEquals((Integer)2, valueSpread(hand).get(sieben));
        assertEquals(null,       valueSpread(hand).get(acht));
        assertEquals(null,       valueSpread(hand).get(neun));
        assertEquals((Integer)1, valueSpread(hand).get(zehn));
        assertEquals(null,       valueSpread(hand).get(unter));
        assertEquals(null,       valueSpread(hand).get(ober));
        assertEquals((Integer)1, valueSpread(hand).get(koenig));
        assertEquals(null,       valueSpread(hand).get(sau));

        assertEquals((Integer)7,  pointsPerColor(hand).get(schell));
        assertEquals((Integer)20, pointsPerColor(hand).get(eichel));
        assertEquals((Integer)10, pointsPerColor(hand).get(herz));
        assertEquals(null,        pointsPerColor(hand).get(laub));
        
        assertFalse(isTechnicallyUntermHund(hand));
        assertEquals(eichel, suggestColor(hand));
    }

    public void testLowlevel3() {
        List<Card> hand = new Card.CardList();
        hand.add(achtEichel);
        hand.add(achtHerz);
        hand.add(achtSchell);
        hand.add(koenigSchell);
        hand.add(sauEichel);

        assertEquals(null,       valueSpread(hand).get(sieben));
        assertEquals((Integer)3, valueSpread(hand).get(acht));
        assertEquals(null,       valueSpread(hand).get(neun));
        assertEquals(null,       valueSpread(hand).get(zehn));
        assertEquals(null,       valueSpread(hand).get(unter));
        assertEquals(null,       valueSpread(hand).get(ober));
        assertEquals((Integer)1, valueSpread(hand).get(koenig));
        assertEquals((Integer)1, valueSpread(hand).get(sau));

        assertEquals((Integer)21, pointsPerColor(hand).get(schell));
        assertEquals((Integer)22, pointsPerColor(hand).get(eichel));
        assertEquals((Integer)8,  pointsPerColor(hand).get(herz));
        assertEquals(null,        pointsPerColor(hand).get(laub));
        
        assertTrue(isTechnicallyUntermHund(hand));
        assertEquals(eichel, suggestColor(hand));
    }

    public void testCalcPossibleCards1() {
        List<Card> played = new Card.CardList();
        played.add(koenigLaub);
        played.add(siebenLaub);
        played.add(achtHerz);

        List<Card> hand = new Card.CardList();
        hand.add(neunEichel);
        hand.add(neunHerz);
        hand.add(zehnLaub);
        hand.add(oberHerz);
        hand.add(sauSchell);
        
        assertFalse(isTechnicallyUntermHund(hand));
        assertEquals(herz, suggestColor(hand));
        
        Card.Color trump; List<Card> poss; String m;
        
        poss = calcPossibleCards(played, hand, (trump = schell));
        m = "played="+played+", hand="+hand+", trump=" + trump+", poss="+poss;
        assertTrue(m, poss.contains(zehnLaub));
        assertEquals(m, 1, poss.size());

        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, herz).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, laub).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, eichel).toArray()));
    }

    public void testCalcPossibleCards2() {
        List<Card> played = new Card.CardList();
        played.add(unterSchell);
        played.add(achtEichel);

        List<Card> hand = new Card.CardList();
        hand.add(oberSchell);
        hand.add(siebenLaub);
        hand.add(unterHerz);
        hand.add(unterEichel);
        hand.add(achtHerz);
        assertFalse(isTechnicallyUntermHund(hand));
        assertEquals(herz, suggestColor(hand));
        
        Card.Color trump; List<Card> poss; String m;
        
        poss = calcPossibleCards(played, hand, (trump = schell));
        m = "played="+played+", hand="+hand+", trump=" + trump+", poss="+poss;
        assertTrue(m, poss.contains(oberSchell));
        assertEquals(m, 1, poss.size());

        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, herz).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, laub).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, eichel).toArray()));
    }

    public void testCalcPossibleCards3() {
        List<Card> played = new Card.CardList();
        played.add(unterSchell);
        played.add(sauHerz);

        List<Card> hand = new Card.CardList();
        hand.add(oberSchell);
        hand.add(siebenLaub);
        hand.add(unterHerz);
        hand.add(unterEichel);
        hand.add(weli);
        assertFalse(isTechnicallyUntermHund(hand));
        assertEquals(schell, suggestColor(hand));
        
        Card.Color trump; List<Card> poss; String m;
        
        poss = calcPossibleCards(played, hand, (trump = schell));
        m = "played="+played+", hand="+hand+", trump=" + trump+", poss="+poss;
        assertTrue(m, poss.contains(oberSchell));
        assertTrue(m, poss.contains(weli));
        assertEquals(m, 2, poss.size());

        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, herz).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, laub).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, eichel).toArray()));
    }

    public void testCalcPossibleCards4() {
        List<Card> played = new Card.CardList();
        played.add(zehnHerz);

        List<Card> hand = new Card.CardList();
        hand.add(achtLaub);
        hand.add(siebenLaub);
        hand.add(unterHerz);
        hand.add(unterEichel);
        hand.add(weli);
        assertFalse(isTechnicallyUntermHund(hand));
        assertEquals(laub, suggestColor(hand));
        
        Card.Color trump; List<Card> poss; String m;
        
        poss = calcPossibleCards(played, hand, (trump = schell));
        m = "played="+played+", hand="+hand+", trump=" + trump+", poss="+poss;
        assertTrue(m, poss.contains(unterHerz));
        assertTrue(m, poss.contains(weli));
        assertEquals(m, 2, poss.size());

        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, herz).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, laub).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, eichel).toArray()));
    }
    


    public void testCalcPossibleCards5() {
        List<Card> played = new Card.CardList();
        played.add(oberSchell);

        List<Card> hand = new Card.CardList();
        hand.add(achtLaub);
        hand.add(siebenLaub);
        hand.add(unterHerz);
        hand.add(unterEichel);
        hand.add(weli);
        assertFalse(isTechnicallyUntermHund(hand));
        assertEquals(laub, suggestColor(hand));
        
        Card.Color trump; List<Card> poss; String m;
        
        poss = calcPossibleCards(played, hand, (trump = schell));
        m = "played="+played+", hand="+hand+", trump=" + trump+", poss="+poss;
        assertTrue(m, poss.contains(achtLaub));
        assertTrue(m, poss.contains(siebenLaub));
        assertTrue(m, poss.contains(unterHerz));
        assertTrue(m, poss.contains(unterEichel));
        assertTrue(m, poss.contains(weli));
        assertEquals(m, 5, poss.size());

        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, herz).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, laub).toArray()));
        assertTrue(Arrays.equals(poss.toArray(), calcPossibleCards(played, hand, eichel).toArray()));
    }

    public void testPunches1() {
        List<Card> played = new Card.CardList();
        played.add(weli);
        played.add(oberSchell);
        
        assertPunchedByNothing(played, schell, sauSchell, Card.PAPA);
        assertPunchedByNothing(played, eichel, sauEichel, Card.PAPA);
        assertPunchedByNothing(played, herz,   sauHerz,   Card.PAPA);
        assertPunchedByNothing(played, laub,   sauLaub,   Card.PAPA);
    }

    public void testPunches2() {
        List<Card> played = new Card.CardList();
        played.add(unterEichel);
        played.add(oberSchell);
        
        assertPunchedByNothing(played, 
                               schell, 
                               koenigSchell, sauSchell, 
                               Card.PAPA, weli);
        assertPunchedByNothing(played, 
                               eichel, 
                               oberEichel, koenigEichel, sauEichel,
                               Card.PAPA, weli);
        assertPunchedByNothing(played, 
                               herz,   
                               siebenHerz, achtHerz, neunHerz, zehnHerz, unterHerz, oberHerz, koenigHerz, sauHerz, 
                               oberEichel, koenigEichel, sauEichel, 
                               Card.PAPA, weli);
        assertPunchedByNothing(played,
                               laub,
                               siebenLaub, achtLaub, neunLaub, zehnLaub, unterLaub, oberLaub, koenigLaub, sauLaub,
                               oberEichel, koenigEichel, sauEichel,
                               Card.PAPA, weli);
    }

    public void testPunches3() {
        List<Card> played = new Card.CardList();
        played.add(siebenLaub);
        
        assertPunchedByNothing(played, 
                               schell, 
                               siebenSchell, achtSchell, neunSchell, zehnSchell, unterSchell, oberSchell, koenigSchell, sauSchell, 
                               achtLaub, neunLaub, zehnLaub, unterLaub, oberLaub, koenigLaub, sauLaub,
                               Card.PAPA, weli);
        assertPunchedByNothing(played,
                               eichel,
                               siebenEichel, achtEichel, neunEichel, zehnEichel, unterEichel, oberEichel, koenigEichel, sauEichel, 
                               achtLaub, neunLaub, zehnLaub, unterLaub, oberLaub, koenigLaub, sauLaub,
                               Card.PAPA, weli);
        assertPunchedByNothing(played,
                               herz,
                               achtLaub, neunLaub, zehnLaub, unterLaub, oberLaub, koenigLaub, sauLaub,
                               siebenHerz, achtHerz, neunHerz, zehnHerz, unterHerz, oberHerz, koenigHerz, sauHerz,
                               Card.PAPA, weli);
        assertPunchedByNothing(played,
                               laub,
                               achtLaub, neunLaub, zehnLaub, unterLaub, oberLaub, koenigLaub, sauLaub,
                               Card.PAPA, weli);
    }
    
    static void assertPunchedByNothing(List<Card> played, Color trump, Card... exceptions) {
        Arrays.sort(exceptions);
        List<Card> fails = null;
        
        for (Card c : cards) {
            if (played.contains(c)) {
                continue;
            }
            
            int index = Arrays.binarySearch(exceptions, c);
            boolean punches = punches(played, c, trump);
            
            if (index >= 0) {
                if (! punches) {
                    if (fails == null) fails = new Card.CardList();
                    fails.add(c);
                }
            } else {
                if (punches) {
                    if (fails == null) fails = new Card.CardList();
                    fails.add(c);
                }
            }
        }
        
        assertNull("color="+trump+", fails="+fails, fails);
    }
    
    static void assertPunchedByEverything(List<Card> played, Color trump, Card... exceptions) {
        Arrays.sort(exceptions);
        List<Card> fails = null;
        
        for (Card c : cards) {
            if (played.contains(c)) {
                continue;
            }
            
            int index = Arrays.binarySearch(exceptions, c);
            boolean punches = punches(played, c, trump);
            
            if (index >= 0) {
                if (punches) {
                    if (fails == null) fails = new Card.CardList();
                    fails.add(c);
                }
            } else {
                if (! punches) {
                    if (fails == null) fails = new Card.CardList();
                    fails.add(c);
                }
            }
        }
        
        assertNull("color="+trump+", fails="+fails, fails);
    }
}
