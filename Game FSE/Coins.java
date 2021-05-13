// Coins.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Used to create coin objects, displayed in the game. Checked for in the main game

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

public class Coins {
    private Rectangle rect; //hitbox
    private Texture[] adding; //used for adding texture sprites
    private Animation<Texture> coinz; //used for displaying animations for coins
    public Coins(Rectangle c){
        rect = c;
        adding = new Texture[5];
        for(int i=1; i<6; i++){
            Texture frame = new Texture("coins/coin00"+i+".png");
            adding[i-1] = frame;
        }
        coinz = new Animation<Texture>(0.08f,adding); //coin animations
    }

    public Rectangle getRect() {
        return rect;

    }
    public float getX(){
        return rect.getX();
    }

    public float getY(){
        return rect.getY();
    }
    public Animation<Texture> getCoinz(){return coinz;}
}
