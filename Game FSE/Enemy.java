// Enemy.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Enemy class, flying and walking enemies are created here, with platform walkers and following players walkers

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    public static final int WALK=0, FLY=1, WALKTOPLAYER=2, WALKONPLATFORM=3; //types of enemies
    public static final int RIGHT=0, LEFT=1, WALKFRAMES=4, FLYFRAMES=3; //directions and number of frames for each enemy
    private int type, walkType;
    private float x,y,oldY; //keeps track of old y for gravity and bottom collide purposes
    private int direction;
    private double vx,vy,angle; //angle for the flying enemies
    private boolean pause,collisionX; //pausing movement and colliding on the left or right
    private TiledMapTileLayer collisionLayer; //layer of the map with collision property
    private boolean onGround;
    private Rectangle area;
    private static Texture[] adding;
    private ArrayList<Animation<Texture>> animations;
    private Random randSpeed;
    public Enemy(int x, int y, int type, TiledMapTileLayer collisionLayer){
        this.x = x;
        this.y = y;
        this.type = type;
        if (type==WALK){
            if (y==MyGdxGame.GROUNDLEVEL){ //if the enemy is on ground level, the enemy will walk to the player
                walkType = WALKTOPLAYER;
            } else{ //the enemy is not on the ground, the enemy will walk back and forth on the platform
                walkType = WALKONPLATFORM;
            }
        } else{ walkType = FLY; } //not a walking enemy, walk type will just by fly
        direction = RIGHT;
        vy = -2; //gravity
        randSpeed = new Random();
        pause = false;
        this.collisionLayer = collisionLayer;
        animations = new ArrayList<Animation<Texture>>();
        //adding texture pictures to the animation ArrayList
        for (int i=0; i<2; i++){ //for the two different directions
            if (type==WALK){
                adding = new Texture[WALKFRAMES];
                for (int j = 1; j < 5; j++) { //4 walk frames
                    if (i==0) {
                        adding[j - 1] = new Texture("WalkingEnemy/walk" + j + ".png"); //right facing
                    } else{
                        adding[j - 1] = new Texture("WalkingEnemy/walk" + (j+WALKFRAMES) + ".png"); //left facing
                    }
                }
                animations.add(new Animation<Texture>(0.1f,adding));
            }
            else if (type==FLY){
                adding = new Texture[FLYFRAMES];
                for (int j=0; j<3; j++){ //3 walk frames
                    if (i==0){
                        adding[j] = new Texture("FlyingEnemy/fly" + j + ".png"); //right facing
                    } else{
                        adding[j] = new Texture("FlyingEnemy/fly" + (j+FLYFRAMES) + ".png"); //left facing
                    }
                }
                animations.add(new Animation<Texture>(0.1f,adding));
            }
        }
    }

    public void move(float px, float py){ //move enemy
        oldY = y; //set the oldY variable to the current y
        if (walkType==WALKONPLATFORM){
            if (!collidesBottom()){ //checks under the enemy, if there's nothing under it
                if (direction==RIGHT){
                    direction = LEFT;
                } else {
                    direction = RIGHT;
                } //switch directions ^
            }
        } else { //player follower
            if (x < px) {
                pauseOff();
                direction = RIGHT;
            } else if (x > px) {
                pauseOff();
                direction = LEFT;
            } //switch directions ^ and keep enemy moving (unpaused)
        }
        if (type==WALK) { //for walking speed
            if (direction==RIGHT){
                if (walkType==WALKTOPLAYER) { //walk to player enemies
                    vx = randSpeed.nextFloat() + randSpeed.nextInt(3); //random speed
                } else{ vx = 2; } //platform walkers walk at a constant speed
            } else if (direction==LEFT){
                if (walkType==WALKTOPLAYER) { //walk to player enemies
                    vx = -(randSpeed.nextFloat() + randSpeed.nextInt(3)); //random speed
                } else{ vx = -2; } //platform walkers walk at a constant speed
            } else if (x == px){
                pauseOn();
                x=px;
                vx=0; //stop moving the enemy
            }

            y += vy;
            if (!onGround) { //go down if enemy is not on ground
                vy -= 2;
            } else {
                vy = 0; //enemy is on ground
            }

            if (y <= 320) { //doesn't fall under the map
                y = 320;
            }

            if (collidesBottom()){ //collide tile under enemy
                y = oldY; //set the enemy y position back
                onGround = true;
            } else{ //no collide tile under enemy
                onGround = false;
            }

            if(vx < 0) { // going left
                collisionX = collidesLeft();
            }
            else if(vx > 0) { // going right
                collisionX = collidesRight();
            } //check collisions ^

            if (collisionX){ //running into a wall
                vx=0;
                pauseOn();
            } else{ //not running into a wall
                x+=vx;
                pauseOff();
            }

        } else{ //flying movement
            angle = Math.atan2(py-y,px-x); //angle from enemy to player
            x+=Math.cos(angle)*randSpeed.nextFloat()*randSpeed.nextInt(5); //random speed
            y+=Math.sin(angle)*randSpeed.nextFloat()*randSpeed.nextInt(5); //random speed
        }
    }

    public float increment(){ //calculates how much space to check for collides
        float increment = collisionLayer.getTileHeight();
        increment = 32 < increment ? 32 / 2 : increment / 2;
        return increment;
    }

    private boolean isCellBlocked(float x, float y) { //checks if the tile has the collision, breakable or spikes property
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        //gets current tile and checks if it is apart of the collision layer
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked");
    }

    public boolean collidesRight() { //loops through right side of enemy
        for(float step = 0; step <= getRect().getHeight()-2; step += increment())
            //starts at 0, goes up on the height, increases at an interval
            if(isCellBlocked(x + getRect().getWidth(), y + 2 + step))
                //check on right side
                return true;
        return false;
    }

    public boolean collidesLeft() { //loops through left side of enemy
        for(float step = 0; step <= getRect().getHeight()-2; step += increment())
            //starts at 0, goes up on the height, increases at an interval
            if(isCellBlocked(x, y + 2 + step))
                //check on left side
                return true;
        return false;
    }

    public boolean collidesBottom() { //loops through top side of player
        for(float step = 0; step <= getRect().getWidth(); step += increment()) {
            //starts at 0, goes up on the width, increases at an interval
            if (isCellBlocked(x + step, y-3)) {
                //check on top side
                return true;
            }
        }
        return false;
    }

    public Rectangle getRect(){ //updated rectangle of the enemy
        if (type==WALK) {
            area = new Rectangle(x, y, 37, 42); //bigger rectangle
        } else if (type==FLY){
            area = new Rectangle(x,y,18,32); //smaller rectangle
        }
        return area;
    }

    public boolean isNotPaused(){return pause;} //checks if the enemy should be moving
    public void pauseOn(){pause = true;}
    public void pauseOff(){pause = false;}
    //sets pause to true or false ^

    public float getX(){return x;}
    public float getY(){return y;}
    public int getType(){ return type; }
    public int getDirection(){ return direction; }
    public ArrayList<Animation<Texture>> getAnimations(){return animations;} //sprites for the enemy
}
