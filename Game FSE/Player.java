// Player.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// The player class, sprites, movement, and logic methods are in here

package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;

public class Player {
    public static final int RIGHT = 0, LEFT = 4; //separate right facing sprites and left facing
    public static final int WALKSPEED = 4, RUNSPEED = 6;
    public static final int STAND = 0, WALK = 1, RUN = 2, ATTACK = 3, HIT = 4; //the player's moves
    public static final int sprite_xr = -60, sprite_y = -55, sprite_xl = -12; //offset positions for the sprites
    public static final int center_x = 25, center_y = 30; //approximate center of the player
    private float x, y, vx, vy;
    private float oldX, oldY; //old position to put the player back during collision with the map tiles
    private boolean onGround,bossReached;
    private TiledMapTileLayer collisionLayer; //layer of map with collision property
    private int[] leftAttack, rightAttack, currentAttack; //holding coordinates, width and length for attack rectangles
    private boolean collisionX, collisionY; //checks collision in the x and y
    private int move, newMove; //current player move and updated move
    private int points, direction;
    private int health;
    private TiledMapTileLayer tile; //another layer from the map that's used to get coin spaces
    private static String[] moves = new String[]{"Stand", "Walk", "Run", "Attack"}; //used to get sprites
    private static Texture[] adding; //textures to be added for animations
    private static ArrayList<Animation<Texture>> animations; //holds all the animations for each move
    private static Texture[] addSaiyan; //saiyan power up sprite
    private static Animation<Texture> saiyan;

    public Player(int x, TiledMapTileLayer collisionLayer, TiledMapTileLayer tileL) {
        //the starting values for the player
        this.x = x;
        this.y = 320; //ground level
        this.collisionLayer = collisionLayer;
        tile = tileL;
        vx = 0;
        vy = 0;
        move = STAND;
        health = 100;
        bossReached=false;
        points = 0;
        leftAttack = new int[]{-30, 0, 30, 60}; //dimensions for the attack rects
        rightAttack = new int[]{50, 0, 30, 60};
        currentAttack = rightAttack;
        onGround = true;
//        haveShield = false;
        animations = new ArrayList<Animation<Texture>>();
        for (int i = 0; i < 8; i++) { //4 moves, 2 directions for 8 different animations
            adding = new Texture[10]; //ten different frames for each move
            for (int j = 0; j < 10; j++) {
                if (i < 4) { //right facing sprites
                    adding[j] = new Texture(moves[i] + "R/" + j + ".png");
                    //get the moves from the static array and goes to path
                } else {
                    adding[j] = new Texture(moves[i - LEFT] + "L/" + j + ".png"); //left facing sprites
                }
            }
            if (i == 3 || i == 7) {
                animations.add(new Animation<Texture>(0.03f, adding)); //attack sprites are faster
            } else {
                animations.add(new Animation<Texture>(0.06f, adding));
            }
        }

        addSaiyan = new Texture[7];
        for (int n = 0; n < 7; n++) {
            addSaiyan[n] = new Texture("saiyan/saiyan" + n + ".png");
        }
        saiyan = new Animation<Texture>(0.03f, addSaiyan);
    }

    public void move() {
        oldX = x; //old positions are updated
        oldY = y;

        newMove = STAND; //new move is reset
        x += vx; //movement in x
        if (x>5500 && !bossReached){
            resetHealth();
            bossReached = true;
        }
        if (move != HIT) { //the player is not getting hit
            vx = 0;
        }
        y += vy; //movement in y
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D) && x<7900) { //start movement in x
            if (Gdx.input.isKeyPressed((Input.Keys.SHIFT_LEFT))) { //running
                vx = RUNSPEED;
                newMove = RUN;
            } else { //not running
                vx = WALKSPEED;
                newMove = WALK;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) { //move left (negative x)
                direction = LEFT;
                currentAttack = leftAttack;
                vx = -vx;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) { //move right (positive x)
                direction = RIGHT;
                currentAttack = rightAttack;
            }
        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) { //cannot attack while moving in the x
            newMove = ATTACK;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) { //pressing space (jumping is separate from x movement)
            if (onGround) { //pressing space while on the ground
                vy += 15; //vy increases
            } else { //pressing space while not on the ground
                vy -= 1; //vy goes down
            }
        } else { //space is not pressed
            if (!onGround) { //player is not on the ground
                vy -= 2; //vy simply decreases
            }
        }

        if (vx < 0) // going left
            collisionX = collidesLeft(); //check left collision
        else if (vx > 0) // going right
            collisionX = collidesRight(); //check right collision
        // react to x collision
        if (collisionX) { //collision layer preventing player to move in the x
            x = oldX; //x gets set back
            vx = 0; //stop x movement
        }

        if (vy <= 0) // going down
            collisionY = collidesBottom(); //check bottom collision
        else if (vy > 0) // going up
            collisionY = collidesTop(); //check top collision

        // react to y collision
        if (collisionY) { //collision layer preventing player to move up and down
            if (vy <= 0) { //player moving down or staying level
                y = oldY; //y gets set back
                onGround = true; //player is on a platform or ground
            }
            vy = 0; //stop y movement
        } else {
            onGround = false; //not colliding with anything in the y, player is not on the ground
        }

        if (onGround) { //if player is on the ground,
            vy = 0; //y movement should stop
        }

        if (move != newMove) { //update what the player is doing
            move = newMove;
        }
    }

    /////////////////////////////////////////////////////////////////

    public float increment(){ //calculates how much space to check for collides
        float increment = collisionLayer.getTileHeight();
        increment = 32 < increment ? 32 / 2 : increment / 2;
        return increment;
    }

    private boolean isCellBlocked(float x, float y) { //checks if the tile has the collision, breakable or spikes property
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        //gets current tile
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            breakable(x, y); //if applicable, breaks block
        }
        if (vy > 0) {
            checkSpike(x, y + getRect().getHeight()); //check above player for spikes
        } else {
            checkSpike(x, y); //check below player for spikes or hazards
        }
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked") || cell.getTile().getProperties().containsKey("break") || cell.getTile().getProperties().containsKey("spiked"));
        //returns if tile is actually colliding or not
    }

    private void breakable(float x, float y) { //breaks the tile
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("break")) {
            //gets the tile and checks the property
            collisionLayer.setCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()), null);
            tile.setCell((int) (x / tile.getTileWidth()), (int) (y / tile.getTileHeight()), null);
            //takes away the breakable cell
        }
    }

    private boolean checkSpike(float x, float y) { //checks if player collides with a spike
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        //getting the cell
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spiked")) { //on spike
            minusHealth(1); //takes away health
            return true;
        } else{
            return false;
        }
    }

    public boolean collidesRight() { //loops through right side of player
        for (float step = 0; step <= getRect().getHeight() - 2; step += increment()) {
            //starts at 0, goes up on the height, increases at an interval
            if (isCellBlocked(x + getRect().getWidth(), y + 2 + step)) {
                //check on right side
                return true;
            }
        }
        return false;
    }

    public boolean collidesLeft() { //loops through left side of player
        for (float step = 0; step <= getRect().getHeight() - 2; step += increment()) {
            //starts at 0, goes up on the height, increases at an interval
            if (isCellBlocked(x, y + 2 + step)) {
                //check on left side
                return true;
            }
        }
        return false;
    }

    public boolean collidesTop() { //loops through top side of player
        for (float step = 0; step <= getRect().getWidth(); step += increment()) {
            //starts at 0, goes up on the width, increases at an interval
            if (isCellBlocked(x + step, y + getRect().getHeight())) {
                //check on top side
                return true;
            }
        }
        return false;

    }

    public boolean collidesBottom() { //loops through bottom side of player
        for (float step = 0; step <= getRect().getWidth(); step += increment()) {
            //starts at 0, goes up on the width, increases at an interval
            if (isCellBlocked(x + step, y)) {
                //check on bottom side
                return true;
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////


    public boolean successHit(Enemy e) { //checks if the enemy is hit by right clicking
        if (Intersector.overlaps(e.getRect(), getAttackRect())) { //use the Intersector class in LibGDX
            pointsFromEnemy(e);
            return true;
        } else {
            return false;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setY(float m) {
        y = m;
    }

    public void setX(float n) {
        x = n;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, 50, 60); //return a updated rect of the player
    }

    public Rectangle getAttackRect() { //get the attack rect, corresponding to the player's direction
        return new Rectangle(x + currentAttack[0], y + currentAttack[1],
                currentAttack[2], currentAttack[3]);
    }

    public int getMove() {
        return move;
    } //what the player is doing

    public boolean getBossReached(){return bossReached;} //boss has been reached

    public void resetHealth(){ health = 100; } //when the boss is reached, health is reset
    public void minusHealth(int v){ if (health>0){ health-=v; } } //take away health
    public int getHealth(){ return health; }
    public boolean isDead(){if (health<=0){ return true; } else{ return false; } } //checks if health is 0

    public void pointsFromEnemy(Enemy e) { //getting poitns from killing enemies
        if (e.getType() == Enemy.FLY) {
            addPoint(1);
        } else if (e.getType() == Enemy.WALK) {
            addPoint(5);
        }
    }

    public void addPoint(int p) {
        points += p;
    }

    public int getPoints(){ return points; }

    public void getsHit(Enemy e) { //checks if hit by the enemy
        if (Intersector.overlaps(e.getRect(), getRect())) {
            minusHealth(1);
            e.pauseOff(); //enemy stops
            if (e.getX() < x && !collidesRight()) {
                x += 1;
            } else if (e.getX() > x && !collidesLeft()) {
                x -= 1;
            }
            //player is pushed ^
        } else {
            e.pauseOn(); //enemy is not colliding, enemy moves
        }
    }

    public void bossHits() { //gets hit by the boss attacks
        minusHealth(2);
        if (direction == RIGHT) {
            x -= 5;
        } else {
            x += 5;
        } //gets pushed ^
    }

    public int getDirection() {
        return direction;
    }

    public ArrayList<Animation<Texture>> getAnimations() {
        return animations;
    }

    public Animation<Texture> getSaiyan() {
        return saiyan;
    }
}
