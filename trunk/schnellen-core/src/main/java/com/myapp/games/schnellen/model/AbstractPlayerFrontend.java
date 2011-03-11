package com.myapp.games.schnellen.model;

import java.util.List;
import java.util.Map;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;


/**
 *
 * @author andre
 *
 */
public abstract class AbstractPlayerFrontend implements IPlayerFrontend {

    private Game context;
    protected final String name;

    
    protected AbstractPlayerFrontend(String name) {
        String trim = name.trim();
        int length = trim.length();

        if (length > 12)
            throw new IllegalArgumentException(name+" longer than 12 chars.");
        if (length != name.length())
            throw new IllegalArgumentException(
                               "must not start or end with spaces: '"+name+"'");
        if (length == 0)
            throw new IllegalArgumentException("name must contain characters!");

        this.name = trim;
    }

    /**
     * maps the count of cards to each different card color occuring in the hand
     * of this player. the weli card will be omitted in this calculation, since
     * it has no color.
     * 
     * @return a mapping from the occuring colors to the number of its cards in
     *         this players hand
     */
    public final Map<Card.Color, Integer> colorSpread() {
        return CardRules.colorSpread(hand());
    }

    /**
     * maps the count of cards to each different card value occuring in the
     * given set of cards. the weli card will be omitted in this calculation,
     * since it has no color.
     * 
     * @return a mapping from the occuring colors to the number of its cards
     */
    public final Map<Card.Value, Integer> valueSpread() {
        return CardRules.valueSpread(hand());
    }

    /**
     * answers the game context
     * 
     * @return the game context
     */
    protected final IGameContext game() {
        return context;
    }

    @Override
    public final String getName() {
        return name;
    }

    /**
     * answers a readonly view of this player's hand
     * 
     * @return a readonly view of this player's hand
     */
    protected final List<Card> hand() {
        return context.handReadOnly(name);
    }

    @Override
    public final void setGameContext(IGameContext backend) {
        this.context = (Game) backend;
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}