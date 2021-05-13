// BossAttack.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Circle;


public class BossAttack{
    float startX,startY,x,y,vx,vy;
    private static Texture[] adding;
    private static Animation<Texture> spriteMovement;
    public BossAttack(float x, float y, float px, float py) {
        startX = x;
        startY = y;
        this.x = x;
        this.y = y;
        float angle = (float)Math.atan2(py-y,px-x);
        vx = (float)Math.cos(angle);
        vy = (float)Math.sin(angle);
        adding = new Texture[4];
        for (int i=0; i<4; i++){
            adding[i] = new Texture("BossSprites/flameball" + i + ".png");
        }
        spriteMovement = new Animation<Texture>(0.06f, adding);
    }
    public void goAttack(){
        x+=vx*5;
        y+=vy*5;
    }
    public float[] getDistance(){
        return new float[]{Math.abs(startX-x),Math.abs(startY-y)};
    }
    public boolean offMap(){
        if (getDistance()[0]>1000 || getDistance()[1]>1000){
            return true;
        } else{
            return false;
        }
    }
    public float getX(){return x; }
    public float getY(){return y; }
    public Animation<Texture> getAnimations(){ return spriteMovement; }
    public Circle getCircle(){ return new Circle(getX(),getY(),5);}
}
