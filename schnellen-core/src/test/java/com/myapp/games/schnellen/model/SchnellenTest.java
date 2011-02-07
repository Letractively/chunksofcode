package com.myapp.games.schnellen.model;

import static org.mockito.Mockito.*;

import static com.myapp.games.schnellen.model.Cards.*;

import java.util.List;

import com.myapp.games.schnellen.frontend.Bot;
import com.myapp.games.schnellen.frontend.IPlayerFrontend;
import com.myapp.games.schnellen.model.Card.Color;

import junit.framework.TestCase;





public class SchnellenTest extends TestCase {

    private IPlayerFrontend a, b, c, x;
    
    @Override
    protected void setUp() {
//        a = mock(IPlayerFrontend.class);
//        b = mock(IPlayerFrontend.class);
//        c = mock(IPlayerFrontend.class);
//        x = mock(IPlayerFrontend.class);
//        when(a.getName()).thenReturn("a");
//        when(b.getName()).thenReturn("b");
//        when(c.getName()).thenReturn("c");
//        when(x.getName()).thenReturn("x");
//        when(a.wantToBeUntermHund()).thenReturn(true);
//        when(b.wantToBeUntermHund()).thenReturn(true);
//        when(c.wantToBeUntermHund()).thenReturn(true);
//        when(x.wantToBeUntermHund()).thenReturn(true);
        a = new Bot("a");
        b = new Bot("b");
        c = new Bot("c");
        x = new Bot("x");
    }

    public void test1() throws Exception {
        List<Card> possible = new Card.CardList();
        possible.add(sauHerz);
        possible.add(sauSchell);
        possible.add(oberSchell);
        possible.add(achtSchell);
        
        when(a.playNextCard(possible)).thenReturn(achtSchell);
        
        assertEquals(achtSchell, a.playNextCard(possible));
    }

    public void testColorSelector() {
        GameFactory gf = new GameFactory();
        
        for (IPlayerFrontend pf : new IPlayerFrontend[]{a ,b, c}) {
            gf.putPlayer(pf);
            when(pf.offerPunch()).thenReturn(2);
            when(pf.spellTrumpSuit()).thenReturn(Color.herz);
        }

        IGameContext game = gf.createGame(); // TODO: set game status persistent to cards static
        game.playGame();
        
//        fail("Not yet implemented"); // TODO
    }

    public void testGetRankings() {
//        fail("Not yet implemented"); // TODO
    }

    public void testGetScore() {
//        fail("Not yet implemented"); // TODO
    }

    public void testIsGameFinal() {
//        fail("Not yet implemented"); // TODO
    }

    public void testPlayGame() {
//        fail("Not yet implemented"); // TODO
    }

    public void testUncoverNextCardForColorChoosing() {
//        fail("Not yet implemented"); // TODO
    }

}
