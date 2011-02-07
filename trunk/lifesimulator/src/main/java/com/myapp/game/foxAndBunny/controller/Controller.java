package com.myapp.game.foxAndBunny.controller;

import java.util.ArrayList;
import java.util.List;

import com.myapp.game.foxAndBunny.Logger;
import com.myapp.game.foxAndBunny.gui.SimulatorFrame;
import com.myapp.game.foxAndBunny.model.World;


public final class Controller implements IControllerModule  {
    
    private List<IControllerModule> modules = new ArrayList<IControllerModule>();
    private World model;
    private SimulatorFrame gui;
    private long currentStep = 0;
    private long frameStartTime = -1;
    
    public Controller() {
        addControllerModule(this);
        addControllerModule(new ExtinctionChecker());
        addControllerModule(new SimulationSpeedController());
    }

    public void startGame() {
        Logger.info("Controller.start() - starting game instance...");

        for (IControllerModule listener : modules) {
            listener.init(this);
        }
        
        for (;;currentStep++) {
            frameStartTime = System.currentTimeMillis();

            for (IControllerModule listener : modules) {
                try {
                    listener.nextStep(this);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void init(Controller game) {
        Logger.info("Controller.init() - initializing game...");
        
        if (game != this) {
            throw new IllegalStateException("must be self! :"+game);
        }
        
        initModel();
        initGui();
        
        Logger.info("Controller.init() - game initialized!");
    }

    private void initModel() {
        Logger.debug("Controller.initModel() - create world...");
        model = new World();
        
        Logger.debug("Controller.initModel() - populating world...");
        model.initDefaultPopulation();
        
        Logger.info("Controller.initModel() - world ready!");
    }
    
    private void initGui() {
        Logger.debug("Controller.initGui() - create gui...");
        gui = new SimulatorFrame(model);

        Logger.debug("Controller.initGui() - draw for the first time...");
        gui.draw();
        
        Logger.info("Controller.initGui() - gui ready!");
    }
    
    public void addControllerModule(IControllerModule module) {
        if (modules.contains(module)) {
            assert false : "already contained: "+module;
            return;
        }
        modules.add(module);
    }
    
    public void removeControllerModule(IControllerModule module) {
        modules.remove(module);
        assert ! modules.contains(module);
    }
    
    
    @Override
    public void nextStep(Controller game) {
        if (game != this) {
            throw new IllegalStateException("must be self! :"+game);
        }
        game.getModel().letActorsAct();
        game.getGui().draw();
    }

    World getModel() {
        return model;
    }
    SimulatorFrame getGui() {
        return gui;
    }
    long getCurrentStep() {
        return currentStep;
    }
    long getFrameStartTime() {
        return frameStartTime;
    }
}
