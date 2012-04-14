package com.myapp.games.schnellen.frontend;

import java.util.List;

import com.myapp.games.schnellen.model.AbstractPlayerFrontend;
import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.Card.Color;
import com.myapp.games.schnellen.model.IGameContext;

/**
 * this frontend will be registered to the game.
 * it will delegate all "business" calls to this {@link IPlayerFrontend}
 * instance and provides the gamecontext with getGame() and the cards
 * of the player by getHand().
 * hands all calls to an implementor of {@link IPlayerFrontend}.
 * this may be useful for classes that cannot extend from 
 * {@link AbstractPlayerFrontend}.
 * 
 * @author andre
 */
public final class PlayerFrontendWrapper extends HumanPlayerFrontend {
    
    private IPlayerFrontend delegate;
    
    public PlayerFrontendWrapper(String name) {
        super(name);
    }
    
    /**
     * all calls of "business" methods will be delegated to this 
     * {@link IPlayerFrontend} instance
     * 
     * @param delegate the delegate
     */
    public void setDelegate(IPlayerFrontend delegate) {
        this.delegate = delegate;
    }

    /**
     * answers a readonly view of the registered instance's hand
     * 
     *@return a readonly view of the registered instance's hand
     */
    public List<Card> getHand() {
        return hand();
    }

    /**
     * answers the gamecontext of the registered instance
     * 
     * @return the gamecontext of the registered instance
     */
    public IGameContext getGame() {
        return game();
    }

    @Override
    public void fireGameEvent(Event id)    {delegate.fireGameEvent(id);}
    @Override
    public void notifyCardLifted(Card c)   {delegate.notifyCardLifted(c);}
    @Override
    public boolean wantToBeUntermHund()    {return delegate.wantToBeUntermHund();}
    @Override
    public Color spellTrumpSuit()          {return delegate.spellTrumpSuit();}
    @Override
    public Card playNextCard(List<Card> p) {return delegate.playNextCard(p);}
    @Override
    public int offerPunch()                {return delegate.offerPunch();}
    @Override
    public int askSplitDeckAt(int d)       {return delegate.askSplitDeckAt(d);}
    @Override
    public Card askDropOneOfSixCards()     {return delegate.askDropOneOfSixCards();}
    @Override
    public List<Card> askCardsForExchange(){return delegate.askCardsForExchange();}
    @Override
    public boolean askForSkipRound()       {return delegate.askForSkipRound();}
    @Override
    public String toString()               {return super.toString();}
}