package com.myapp.mines.controller;

import java.awt.Color;

import com.myapp.mines.model.Baaaaaam;
import com.myapp.mines.model.Field;

/**
the controller logic of the field model and the corresponding view
is implemented in this base class which is designed to inherit view
implementations from.
@author andre
 */
public abstract class FieldController
        implements IFieldViewCtrl {

    protected IGameViewCtrl gameViewCtrl;
    protected Field model;

    @SuppressWarnings("unused")
    private FieldController() {
    }

    /**
    created a new FieldController for the given model and the given game
    view/controller.
    @param model the field
    @param gameGui the view/controller
     */
    protected FieldController(Field model, IGameViewCtrl gameGui) {
        this.gameViewCtrl = gameGui;
        this.model = model;
        gameViewCtrl.getModel().map(model, this);

    }

    /**
    handles a exception which occurs when a mine was entered.
    @param baaam the thrown exception
     */
    private final void handleBaaaaam(Baaaaaam baaam) {
        gameViewCtrl.repaintGameView();
        gameViewCtrl.setStatusText("GAME OVER!  " + baaam.getMessage());
    }

    @Override
    public final void enterField() {
        try {
            model.enter();
        }
        catch (Baaaaaam ex) {
            handleBaaaaam(ex);
        }

        repaintFieldView();

        if (model.getNeighbourBombs() == 0)
            gameViewCtrl.repaintGameView();

        gameViewCtrl.checkGameState();
    }

    @Override
    public final void solveNeighbors() {
        boolean somethingChanged = false;

        try {
            somethingChanged = model.solveNeighbourhood();
        }
        catch (Baaaaaam ex) {
            handleBaaaaam(ex);
            gameViewCtrl.repaintGameView();
            somethingChanged = false;
        }

        if (somethingChanged)
            gameViewCtrl.repaintGameView();

        gameViewCtrl.checkGameState();
    }

    @Override
    public final Field getModel() {
        return model;
    }

    /**
    get the color which should be used for the foreground
    @param f the field we want to calculate the color for.
    @return the color which should be used for the foreground
     */
    public static final Color calculateForegroundColor(Field f) {
        return Util.FieldController.calculateForegroundColor(f);
    }

    /**
    get the color which should be used for the background
    @param f the field we want to calculate the color for.
    @return the color which should be used for the background
     */
    public static final Color calculateBackgroundColor(Field f) {
        return Util.FieldController.calculateBackgroundColor(f);
    }

}
