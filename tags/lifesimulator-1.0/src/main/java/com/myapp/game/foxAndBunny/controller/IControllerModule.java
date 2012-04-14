package com.myapp.game.foxAndBunny.controller;

interface IControllerModule {
    void init(Controller game);
    void nextStep(Controller game) throws Exception;
}