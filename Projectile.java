// Projectile.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Class for the player's projectile objects, with trajectory, methods and sprites inside

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Projectile {
    public static final int RIGHT=0, LEFT=1;
    private float triangleH,triangleB;
    private float startX,startY,x,y,vx,vy; //start position, current position and velocity of the projectile
    private int direction; //right or left
    private float power;
    private double angle; //trajectory of the projectile
    private static Texture[] adding; //adding sprites to the animation
    private static ArrayList<Animation<Texture>> animations; //element 0 is throwing right, element 1 is throwing left
    public Projectile(int mx, int my, float x, float y, float power){
        startX = x;
        startY = y;
        this.x = x;
        this.y = y;
        this.power = power; //power is taken from the Shooting class
        triangleH = Gdx.graphics.getHeight()-my; //vertical distance from height of the screen and mouse position
        triangleB = mx- Gdx.graphics.getWidth()/2;
        //horizontal distance from middle of the screen (where the player is situated) and mouse position
        angle = (float)Math.atan2(triangleH,triangleB); //find the angle between player and mouse using the two distances
        vx=(float)Math.cos(angle)*power; //x direction
        vy=(float)Math.sin(angle)*power; //y direction
        if (vx<0){
            direction=LEFT;
        } else if (vx>=0){
            direction=RIGHT;
        } //changes direction ^
        animations = new ArrayList<Animation<Texture>>();
        for (int i=0; i<2; i++) {
            adding = new Texture[8];
            for (int j=1; j<9; j++) {
                if (i == RIGHT) {
                    adding[j - 1] = new Texture("knifeSprites/knifeR00" + j + ".png"); //right throwing
                } else if (i == LEFT){
                    adding[j - 1] = new Texture("knifeSprites/knifeL00" + j + ".png"); //left throwing
                }
            }
            animations.add(new Animation<Texture>(0.06f,adding));
        }
    }

    public void shoot(){
        x+=vx;
        y+=vy;
        vy-=1; //move the projectile, increasing x and y while constantly decreasing the y speed
    }

    public boolean offMap(){ //if the projectile has gone far enough, it disappears
        if (Math.abs(startX-x)>1000 || Math.abs(startY-y)>1000){
            return true;
        } else{
            return false;
        }
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public Rectangle getRect(){ return new Rectangle(x,y,20,20); } //updated rectangle of the projectile
    public int getDirection(){ return direction; }
    public ArrayList<Animation<Texture>> getAnimations(){ return animations; } //animations for the throwing knife
}