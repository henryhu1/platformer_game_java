// menuScreen.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Displays the menu screen at the start of the game

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class menuScreen implements Screen {
    private SpriteBatch batch;
    private Menu menu;
    private Texture logo;

    public menuScreen(Menu g){
        batch = new SpriteBatch();
        menu = g;
    }

    @Override
    public void show() {
        logo = new Texture("menu.png");
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){ //enter to go to game
            menu.setScreen(menu.getGameScreen());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.I)){ //"I" to go to instructions
            menu.setScreen(menu.getInstructionsDisplay());
        }

        batch.begin();
        batch.draw(logo,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight()); //picture for main menu
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}