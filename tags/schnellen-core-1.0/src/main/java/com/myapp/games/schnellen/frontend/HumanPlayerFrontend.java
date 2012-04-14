package com.myapp.games.schnellen.frontend;

import com.myapp.games.schnellen.model.AbstractPlayerFrontend;
import com.myapp.games.schnellen.model.Card;



public abstract class HumanPlayerFrontend extends AbstractPlayerFrontend {

    public HumanPlayerFrontend(String name) {
        super(name);
    }

    @Override
    public abstract boolean askForSkipRound();

    @Override
    public abstract void notifyCardLifted(Card card);
}
