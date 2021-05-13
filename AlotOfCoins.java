// AlotOfCoins.java
// Henry Hu, Nafis Fardin
// May 14, 2019
// ICS4U
// Simple class that keeps track of hidden coins, collected by colliding with the tile on the map. Checked for game in main game

package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;

public class AlotOfCoins {
    Rectangle rect;

    public AlotOfCoins(Rectangle r){
        rect = r;
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
}
