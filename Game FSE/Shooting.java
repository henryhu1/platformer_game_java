// Shooting.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Input class to calculate power and add Projectiles to the game. Main game extends Screen,
// this class extends ApplicationAdapter for event based input

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.*;

public class Shooting extends ApplicationAdapter implements InputProcessor {

    private static float power = 0;//amount of power charged up for each shot
    private static int up_or_down=0;//whether the power increases or decreases
    private static final int UP = 1;//directions
    private static final int DOWN = - 1;

    private int mx,my;
    private Player player;
    private ShapeRenderer sr;

    private ArrayList<Projectile> shots;

    public Shooting(ArrayList<Projectile> proj, Player p1){
        shots = proj;
        player = p1;
        sr = new ShapeRenderer();
    }

    @Override
    public void create(){
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(){
        mx = Gdx.input.getX();
        my = Gdx.input.getY();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){ //clicking the left mouse button
            powerCalc(); //calculate power
        }

        for (int i=0; i<shots.size(); i++){ //move the Projectiles
            shots.get(i).shoot();
        }
    }

    @Override
    public void dispose(){

    }

    private void powerCalc(){ //calculates power for the Projectile to use
        if (power<=0){ //once the power is at a minimum, it goes up
            up_or_down = UP;
        }
        else if (power>=30){ //once the power is at a maximum, it goes down
            up_or_down = DOWN;
        }
        if (power<=20){
            power += up_or_down;
        }
        else if (power<=25){
            power += up_or_down * 2;
        }
        else if (power<=34) {
            power += up_or_down * 3;
        }
        //if power is lower than it will take a longer amount of time for it to increase
        //if it's higher, than it will increase faster
    }

    public float getPower(){ return power; }
    public ArrayList<Projectile> getShots(){ return shots; } //the Projectile ArrayList

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    // Mouse up/touch released
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button==Input.Buttons.LEFT) { //touch up of the left mouse button
            shots.add(new Projectile(mx, my, player.getX()+Player.center_x, player.getY()+Player.center_y, power));
            //projectile based off mouse, player centre and power level
        }
        power = 0;
        return false;
    }

    @Override
    // Mouse down/touched
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
}
