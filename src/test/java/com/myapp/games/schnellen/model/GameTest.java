package com.myapp.games.schnellen.model;

import static org.mockito.Mockito.*;

import java.util.List;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;
import com.myapp.games.schnellen.model.Game;
import com.myapp.games.schnellen.model.IColors;
import com.myapp.games.schnellen.model.IGameContext;
import com.myapp.games.schnellen.model.Round;
import com.myapp.games.schnellen.model.Scorings;

import junit.framework.TestCase;

public class GameTest extends TestCase {
    
    
    IGameContext gc;
    Game g;
    Round r;
    Scorings s;
    IColors c;
    IPlayerFrontend feA, feB, feC, feD;
    
    @Override
    protected void setUp() throws Exception {
        gc = g = new Game();
        r = (Round) g.round();
        s = (Scorings) g.scorings();
        c = g.colors();

        feA = mock(IPlayerFrontend.class);
        feB = mock(IPlayerFrontend.class);
        feC = mock(IPlayerFrontend.class);
        feD = mock(IPlayerFrontend.class);

        when(feA.getName()).thenReturn("feA");
        when(feB.getName()).thenReturn("feB");
        when(feC.getName()).thenReturn("feC");
        when(feD.getName()).thenReturn("feD");
    }
    
    public void testInitForNewGame() {
        g.addPlayer(feA);
        g.addPlayer(feB);
        g.addPlayer(feC);
        g.initForNewGame();
        
        List<String> players = gc.players();
        assertEquals(0, players.indexOf("feA"));
        assertEquals(1, players.indexOf("feB"));
        assertEquals(2, players.indexOf("feC"));
    }

    public void testInitRound() {
        g.addPlayer(feA);
        g.addPlayer(feB);
        g.addPlayer(feC);
        g.initForNewGame();
//        fail("Not yet implemented"); // TODO
    }

    public void testSplitDeck() {
//        fail("Not yet implemented"); // TODO
    }

    public void testDealCards() {
//        fail("Not yet implemented"); // TODO
    }

    public void testHandleUntermHund() {
//        fail("Not yet implemented"); // TODO
    }

    public void testDetermineTrumpSuit() {
//        fail("Not yet implemented"); // TODO
    }

    public void testHandleCowards() {
//        fail("Not yet implemented"); // TODO
    }

    public void testExchangeCards() {
//        fail("Not yet implemented"); // TODO
    }

    public void testPlayDealedCards() {
//        fail("Not yet implemented"); // TODO
    }

}
