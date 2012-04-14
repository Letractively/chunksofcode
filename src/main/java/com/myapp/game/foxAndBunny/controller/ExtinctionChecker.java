package com.myapp.game.foxAndBunny.controller;

import com.myapp.game.foxAndBunny.model.Bunny;
import com.myapp.game.foxAndBunny.model.Fox;
import com.myapp.game.foxAndBunny.model.Population;
import com.myapp.game.foxAndBunny.model.World;

/**
 *  
 *  checks if one race died, if yes, query the user for restart or quit
 *  
 * @author andre
 */
final class ExtinctionChecker implements IControllerModule {
    
    @Override
    public void init(Controller game) {}
    
    @Override
    public void nextStep(Controller game) {
        if (game.getCurrentStep() % 100 != 2) { // every n steps only
            return; 
        }
        
        World world = game.getModel();
        Population population = world.getPopulation();
        
        int bunnies = population.actorCount(Bunny.class);
        int foxes = population.actorCount(Fox.class);
        
        if (bunnies == 0 || foxes == 0) {
            String extinctBreed = (bunnies == 0 ? "bunnies" : "foxes");
            
            if (game.getGui().askUserIfRestartOrExit(extinctBreed)) {
                world.initDefaultPopulation();    
                
            } else {
                System.exit(0);
                return;
            }
        }
    }
}