package com.myapp.game.foxAndBunny.controller;

import static java.lang.System.currentTimeMillis;

import com.myapp.game.foxAndBunny.Logger;



/**
 * controls the sleep duration between two frames. this is the last module being
 * called from the controller each step. this timer calculates how much idle
 * time is left until estimated "next-frame-start", and sleeps until this point
 * of time
 * 
 * @author andre
 */
final class SimulationSpeedController implements IControllerModule {

    /**
     * monitors the "real" produced frames per second of the game, based on an
     * average over a number of recent frame durations.
     */
    private static final class FPSLogger {

        /**
         * the number of frames to wait until calculating an average duration.
         * (for logging)
         */
        private static final Double X = 100d;
        private static final double X_DOUBLE = X.doubleValue();
        private static final int X_INT = X.intValue();
        
        static {
            if (X_INT <= 0)
                throw new RuntimeException("not greater than zero: '"+X+"'");
        }
        
        
        private long xFramesAgo = -1;
        
        void log(Controller game) {
            long step = game.getCurrentStep();
            
            if (step % X_INT != 1L || step < X_INT)
                return;
            
            // x frames passed!
            double lastXframesMs = game.getFrameStartTime() - xFramesAgo;
            double lastXframesSec = lastXframesMs / 1000d;
            double fps = Math.ceil(X_DOUBLE / lastXframesSec * 10) / 10; // round
            
            Logger.debug(
                "SimulationSpeedController.log()      " +
                "frame number='"+step+"' - " +
                "current rate='"+fps+" frames/s' " +
                "target='"+FRAMES_PER_SEC+" frames/s'"
            );
            
            xFramesAgo = game.getFrameStartTime();
        }
    }
    

    private static final double FRAMES_PER_SEC = 40d;
    private static final double MILLIS_PER_FRAME = 1000d / FRAMES_PER_SEC;
    
    
    FPSLogger logger = new FPSLogger();
    
    
    
    @Override
    public void init(Controller game) {
        logger.xFramesAgo = System.currentTimeMillis();
    }
    
    @Override
    public void nextStep(Controller game) throws InterruptedException {
        // sleep duration depends on how fast the last iteration step 
        // was calculated to provide smooth refresh rates:
        
        long msSinceFrameStart = currentTimeMillis() - game.getFrameStartTime();
        long sleep = (long) (MILLIS_PER_FRAME - msSinceFrameStart);
        sleep = sleep < 0 ? 0 : sleep;
        
        if (Logger.IS_DEBUG) {
            logger.log(game);
            
            if (game.getCurrentStep() % 100 == 0) {
                Logger.debug(
                     "SimulationSpeedController.nextStep() " +
                     "frame number='"+game.getCurrentStep()+"' - " +
                     "duration target='"+MILLIS_PER_FRAME+" ms/frame', " +
                     "so far='"+msSinceFrameStart + " ms' -  " +
                     "will now sleep for '"+sleep+" ms'"
                 );
             }
        }
        
        Thread.sleep(sleep);
    }
}