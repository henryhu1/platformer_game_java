// Boss.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
//

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.*;

public class Boss {
    private static final int STAGE1 = 0, STAGE2 = 2, STAGE3 = 4; //different stages of the boss based on health
    private static final float STAGE1SPEED = 0.2f, STAGE2SPEED = 0.5f, STAGE3SPEED = 1f; //speed of the boss based on stage
    private static final int LEFT=0, RIGHT = 1; //facing directions
    int health;
    float x,y,vx,vy,speed; //vx and vy are for moving, speed determines by how much
    int stage,direction;
    ArrayList<BossAttack> bossAttackList; //boss's attacks
    Random rand; //for random movement
    private static String[] stages = new String[]{"Stage1_","Stage1_","Stage2_","Stage2_","Stage3_","Stage3_"};
    //for loading sprites
    private static Texture[] adding;
    private static ArrayList<Animation<Texture>> animations;
    public Boss(){
        //creating the boss with these starting values
        health = 20;
        x=6360;
        y=600;
        vx=0;
        vy=0;
        stage = STAGE1;
        speed = STAGE1SPEED;
        rand = new Random();
        bossAttackList = new ArrayList<BossAttack>();
        animations = new ArrayList<Animation<Texture>>();
        for (int i=0; i<6; i++) {
            adding = new Texture[8];
            for (int j = 1; j < 9; j++) {
                if (i%2==LEFT) {
                    adding[j - 1] = new Texture("BossSprites/boss" + stages[i] + "L" + j + ".png");
                } else if (i%2==RIGHT){
                    adding[j - 1] = new Texture("BossSprites/boss" + stages[i] + "R" + j + ".png");
                }
            }
            animations.add(new Animation<Texture>(0.06f, adding));
        }
    }
    public void move(float px) { //move the boss
        if (!isDead()) { //not dead
            x += vx;
            y += vy;
            if (px < x) {
                direction = LEFT;
            } else {
                direction = RIGHT;
            } //switches directions ^
            if (stage == STAGE2) {
                speed = STAGE2SPEED;
            } else if (stage == STAGE3) {
                speed = STAGE3SPEED;
            } //switches speeds ^
            int chance = rand.nextInt(4) % 4;
            //random direction ^
            if (chance == 0 && x < 6800 && vx < 3) {
                vx += speed;
            } else if (chance == 1 && x > 6200 && vx > -3) {
                vx -= speed;
            } else if (chance == 2 && y < 600 && vy < 3) {
                vy += speed;
            } else if (chance == 3 && y > 320 && vy > -3) {
                vy -= speed;
            } //if the boss is within the boundaries, then the boss will move in the randomly determined direction
            //as long as if it's not going too fast in that direction too
        }
    }

    public void attack(float px, float py) { //use boss attacks
        if (!isDead()) { //not dead
            checkAttack(px, py);
            for (int i = 0; i < bossAttackList.size(); i++) {
                bossAttackList.get(i).goAttack(); //loops through the ArrayList and uses BossAttack methods
                if (bossAttackList.get(i).offMap()) {
                    bossAttackList.remove(bossAttackList.get(i));
                }
            }
        }
    }

    public void checkAttack(float px, float py){ //check if can attack
        if (stage==STAGE1) {
            if (bossAttackList.size() == 0) {
                addAttack(px,py,3); //at stage 1, only 3 attacks are sent out
            }
        } else if (stage==STAGE2){
            if (bossAttackList.size() == 0) {
                addAttack(px,py,3); //first set
            }
            if (bossAttackList.get(0).getDistance()[0] > 300 && bossAttackList.get(0).getDistance()[1] > 300) {
                if (bossAttackList.size() < 4) { //less than 4 attacks are out
                    addAttack(px,py,3); //second set
                }
            }
        } else if (stage==STAGE3){
            if (bossAttackList.size() == 0) {
                addAttack(px,py,3); //first set
            }
            if (bossAttackList.get(0).getDistance()[0] > 300 && bossAttackList.get(0).getDistance()[1] > 300) {
                if (bossAttackList.size() < 6) { //less than 6 attacks are out
                    addAttack(px, py, 5); //second set
                }
            }
        }
    }

    public void addAttack(float px, float py, int num){ //adds attacks to ArrayList based on player and amount of attacks
        for (int i=-num/2; i<num/2+1; i++) {
            bossAttackList.add(new BossAttack(x, y, px, py+120*i));
        }
    }

    public void getAttacked(Rectangle playerRect, int pDirection){ //checks if attacked by player's right click
        //doesn't do damage, only moves the boss
        if (Intersector.overlaps(getRect(),playerRect)){
            if (pDirection==Player.RIGHT){
                vx=5;
            } else{
                vx=-5;
            }
        }
    }
    public float getX(){ return x; }
    public float getY(){ return y; }
    public int getDirection(){ return direction; }
    public void getHit(){ //checks if it's hit by player's projectiles (which does damage)
        health-=1;
        if (health<5){
            stage = STAGE3;
        } else if (health<10){
            stage = STAGE2;
        }
    }

    public boolean isDead(){ if (health<=0){ return true; } else { return false; } } //if the boss's health is 0
    public ArrayList<Animation<Texture>> getAnimations(){ return animations;} //the boss's sprites
    public int getStage(){return stage;}
    public Rectangle getRect(){ return new Rectangle(x+20,y+10,50,60); } //updated boss rect
    public ArrayList<BossAttack> getAttacks(){ return bossAttackList; } //boss's attack ArrayList
}
