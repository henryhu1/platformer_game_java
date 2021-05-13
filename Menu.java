// Menu.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// What DesktopLauncher opens, switches screens to start menu, instructions or to play the game

package com.mygdx.game;

import com.badlogic.gdx.Game;


public class Menu extends Game {
    private MyGdxGame gameScreen;
    private menuScreen menuScreen;
    private instructionsScreen instructionsDisplay;

    @Override
    public void create () {
        gameScreen = new MyGdxGame(this);
        menuScreen = new menuScreen(this);
        instructionsDisplay = new instructionsScreen(this);
        //switching screens, in-game, start menu or instructions
        setScreen(new menuScreen(this)); //sets screen to what the player is on
    }

    @Override
    public void dispose () {
    }

    public menuScreen getMenuScreen(){ return menuScreen; }
    public MyGdxGame getGameScreen(){ return gameScreen; }
    public instructionsScreen getInstructionsDisplay(){ return instructionsDisplay; }
}