// instructonsScreen.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Simply displays the instructions screen at the beginning

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class instructionsScreen implements Screen{
    private SpriteBatch batch;
    private Menu menu;
    private Texture instructions;

        public instructionsScreen(Menu g){
            menu = g;
        }

        @Override
        public void show() {
            batch = new SpriteBatch();
            instructions = new Texture("instructions.png");
        }

        @Override
        public void render(float delta) {

            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){ //escape to go back to the main menu
                menu.setScreen(menu.getMenuScreen());
            }

            batch.begin();
            batch.draw(instructions,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight()); //instructions picture
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
