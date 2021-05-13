// MyGdxGame.java
// Henry Hu, Nafis Molla
// May 14, 2019
// ICS4U
// Main game class, creates all objects and runs their methods during the game.
// The player starts on the left of the map, progressing to the right by defeating enemies and maneuvering the map
// On the right side, the final boss awaits, and to defeat the boss, projectiles must be thrown to hit it
// Once the boss is defeated, the game is won, otherwise, if the player loses all their health, the game is over.

package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;


public class MyGdxGame implements Screen{
	static final int RIGHT = 0; //used for directions
	static final int LEFT = 5;
	public static final int GROUNDLEVEL = 320; //level of the ground for enemies
	private SpriteBatch batch;
	private ShapeRenderer sr;
	private Player p1;
	private float stateTime; //used for sprite timing
	private ArrayList<Enemy> enemies;
	private ArrayList<Projectile> proj; //player projectiles
	private Boss boss;
	private ArrayList<Coins> coins;
	private ArrayList<AlotOfCoins> alotCoins;
    private Texture healthBar;
    private Random rand;

    private BitmapFont font;
    private Music gameTheme;

    private boolean isGameOver,isGameWon;

    private Shooting shooting; //input class used for projectiles

	/////////////////////////
	TiledMap map;
	OrthogonalTiledMapRenderer Renderer;
	OrthographicCamera camera;
	TiledMapTileLayer collisionLayer;
	TiledMapTileLayer tile;
	//////////////////////////

	Menu menu; //used switching screens, from start menu, instructions and in-game screen

	public MyGdxGame(Menu g){
		menu = g;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		rand = new Random();

		///////////////////////////////////////////////////////
		camera = new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//positions the screen on the player using this class

        //load the map and its layers
		map = new TmxMapLoader().load("Map Stuff/map2.tmx");
		Renderer = new OrthogonalTiledMapRenderer(map);
		collisionLayer = (TiledMapTileLayer) map.getLayers().get("collision");
        tile = (TiledMapTileLayer) map.getLayers().get("Tile Layer 1");
        ///////////////////////////////////////////////////////

		p1 = new Player(60,collisionLayer,tile); //pass in the starting x position and map tile layers
		boss = new Boss();

		proj = new ArrayList<Projectile>(); //holds player projectiles

		enemies = new ArrayList<Enemy>();
		enemies.add(new Enemy(600,GROUNDLEVEL,Enemy.WALK,collisionLayer)); //walking enemies
		enemies.add(new Enemy(1392,417,Enemy.WALK,collisionLayer));
		enemies.add(new Enemy(320,450,Enemy.WALK,collisionLayer));
		enemies.add(new Enemy(1584,GROUNDLEVEL,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(1907,GROUNDLEVEL,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(2545,529,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(2774,417,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(2915,641,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(2922,481,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(3443,432,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(4579,528,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(4261,432,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(4423,GROUNDLEVEL,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(5393,608,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(5087,481,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(4848,416,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(5660,496,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(5838,577,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(6229,640,Enemy.WALK,collisionLayer));
        enemies.add(new Enemy(6440,608,Enemy.WALK,collisionLayer));

        for (int i=0; i<15; i++) {
            enemies.add(new Enemy(rand.nextInt(7000), 600, Enemy.FLY, collisionLayer)); //flying enemies
        }

		coins = new ArrayList<Coins>(); //coins
		alotCoins = new ArrayList<AlotOfCoins>(); //special hidden coins

		healthBar = new Texture("healthBar.png");

		isGameOver = false; //game over boolean
		isGameWon = false; //game won boolean

		shooting = new Shooting(proj,p1); //pass in the Projectile ArrayList and a Player

        font = new BitmapFont(Gdx.files.internal("pokemonGB.fnt"));//gets specialized font
        font.getData().setScale(1f);//changes the size of the font

        gameTheme = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));//loads up the background music
        gameTheme.setLooping(true);//makes it so if the music ends it loops again
        gameTheme.setVolume(0.5f);//sets the volume so it's not too loud
        gameTheme.play();//plays the music as soon as the program begins

		Gdx.input.setInputProcessor(shooting); //input goes to shooting
		readAllObejects(); //create all objects from the map
	}

	@Override
	public void render(float delta) {
        if (isGameOver) { //if the player is out of health
            batch.begin();
            batch.draw(new Texture("gameover.jpg"), p1.getX()-320, p1.getY()-180);
            batch.end();
        } else if (isGameWon){ //if the boss has been defeated
            batch.begin();
            batch.draw(new Texture("gamewon.jpg"),p1.getX()-160, p1.getY()-90);
            batch.end();
        }
        else { //if the player is alive, the game continues
            Gdx.gl.glClearColor(0.258f, 0.407f, 0.956f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            Renderer.render();
            Renderer.setView(camera);

            batch.setProjectionMatrix(camera.combined);

            camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.position.set(p1.getX(), p1.getY(), 0);
            camera.update();

            sr.setProjectionMatrix(camera.combined);

            stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

            shooting.render(); //use everything that happens in shooting's render
///////////////////////////////////////////////////////
            p1.move(); //move player
///////////////////////////////////////////////////////
            if (enemies.size() > 0) { //everything for the enemies
                for (int i = 0; i < enemies.size(); i++) {
                    enemyLogic(enemies.get(i));
                }
            }
///////////////////////////////////////////////////////
            proj = shooting.getShots(); //from shooting, take the projectiles created there
            for (int i = 0; i < proj.size(); i++) {
                if (proj.get(i).offMap()) { //remove the projectile if it goes too far
                    proj.remove(i);
                }
            }
///////////////////////////////////////////////////////
            boss.move(p1.getX()); //move the boss
            boss.attack(p1.getX(), p1.getY()); //boss's attack
            if (p1.getMove() == Player.ATTACK) {
                boss.getAttacked(p1.getAttackRect(), p1.getDirection()); //if the boss gets hit by right click
            }
///////////////////////////////////////////////////////
            if (proj.size() > 0) { //projectile and enemy collision
                for (int j = 0; j < proj.size(); j++) {
                    if (enemies.size() > 0) {
                        for (int i = 0; i < enemies.size(); i++) {
                            if (proj.size() > j && Intersector.overlaps(proj.get(j).getRect(), enemies.get(i).getRect())) {
                                // projectile rect and enemy rect overlaps
                                p1.pointsFromEnemy(enemies.get(i));
                                proj.remove(proj.get(j));
                                enemies.remove(enemies.get(i));
                            }
                        }
                    }
                }
            }
///////////////////////////////////////////////////////
            if (proj.size() > 0) { //projectile and boss collision
                for (int j = 0; j < proj.size(); j++) {
                    if (Intersector.overlaps(proj.get(j).getRect(), boss.getRect())) {
                        //projectile and boss overlaps
                        proj.remove(proj.get(j));
                        boss.getHit();
                    }
                }
            }
///////////////////////////////////////////////////////
            for (int i = 0; i < boss.getAttacks().size(); i++) { //boss attack and player collision
                if (Intersector.overlaps(boss.getAttacks().get(i).getCircle(), p1.getRect())) {
                    p1.bossHits();
                    boss.getAttacks().remove(boss.getAttacks().get(i));
                }
            }
///////////////////////////////////////////////////////
            if (p1.isDead()) { //p1 has no health
                isGameOver = true;
            }
///////////////////////////////////////////////////////
            batch.begin();

            font.draw(batch, Integer.toString(p1.getPoints()), p1.getX() + 550, p1.getY() + 350); //player's points
///////////////////////////////////////////////////////
            if (p1.getBossReached()) {
                Texture s = p1.getSaiyan().getKeyFrame(stateTime, true); //super saiyan sprites when the player reaches the boss
                batch.draw(s, p1.getX() - 10, p1.getY() - 3);
            }
///////////////////////////////////////////////////////
            Texture currentFrame = p1.getAnimations().get(p1.getMove() + p1.getDirection()).getKeyFrame(stateTime, true);
            //player sprites
            if (p1.getDirection() == RIGHT) {
                batch.draw(currentFrame, p1.getX() + Player.sprite_xr, p1.getY() + Player.sprite_y);
            } else {
                batch.draw(currentFrame, p1.getX() + Player.sprite_xl, p1.getY() + Player.sprite_y);
            }
///////////////////////////////////////////////////////
            for (Projectile p : proj) { //projectile sprites
                Texture projectileFrame = p.getAnimations().get(p.getDirection()).getKeyFrame(stateTime, true);
                batch.draw(projectileFrame, p.getX(), p.getY());
            }
///////////////////////////////////////////////////////
            for (Enemy e : enemies) { //enemy sprites
                Texture enemyFrame = e.getAnimations().get(e.getDirection()).getKeyFrame(stateTime, true);
                batch.draw(enemyFrame, e.getX(), e.getY());
            }
///////////////////////////////////////////////////////
            if (!boss.isDead()) { //boss sprites, as long as he's not dead
                Texture bossFrame = boss.getAnimations().get(boss.getStage() + boss.getDirection()).getKeyFrame(stateTime, true);
                batch.draw(bossFrame, boss.getX(), boss.getY());

                for (BossAttack ba : boss.getAttacks()) { //boss attack sprites
                    Texture bossAttackFrame = ba.getAnimations().getKeyFrame(stateTime, true);
                    batch.draw(bossAttackFrame, ba.getX(), ba.getY());
                }
            } else{ //boss has no health
                isGameWon = true;
            }
///////////////////////////////////////////////////////
            for (Coins c : coins) { //coin sprites
                Texture coinFrame = c.getCoinz().getKeyFrame(stateTime, true);
                batch.draw(coinFrame, c.getX(), c.getY());
            }

            batch.end();
///////////////////////////////////////////////////////
            sr.begin(ShapeRenderer.ShapeType.Filled);

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) { //power bar shapes
                sr.setColor(Color.BLACK);
                sr.rect(p1.getX() - 4, p1.getY() + 24, 8, 35);
                Color powerColor = new Color(shooting.getPower() / 30, 1 - shooting.getPower() / 30, 0, 1);
                sr.setColor(powerColor);
                sr.rect(p1.getX() - 2, p1.getY() + 26, 4, shooting.getPower());
            }
///////////////////////////////////////////////////////
            sr.setColor(Color.RED); //player's health bar
            sr.rect(p1.getX() - 530, p1.getY() + 275, (float) (p1.getHealth()) / 100f * 413, 67);

            sr.end();
///////////////////////////////////////////////////////
            batch.begin();
            batch.draw(healthBar, p1.getX() - 600, p1.getY() + 250); //image for the player's health bar in HUD
            batch.end();
//////////////////////////////////////////////////////
            ObjectCollide(p1.getRect()); //objects from map collision with the player
        }
    }

	public void enemyLogic(Enemy e) { //simple methods for enemies
		if (e.isNotPaused()) { //enemy is in movement
			e.move(p1.getX() + Player.center_x, p1.getY() + Player.center_y); //they move to the player
		}
		p1.getsHit(e); //checks if the player is hit by the enemy
		if (p1.getMove() == Player.ATTACK) {
			if (p1.successHit(e)) { //checks if the player hits the enemy
				enemies.remove(e);
			}
		}
	}

    public void readAllObejects(){ //the special tiles on the map are read and objects are created
        for(RectangleMapObject object: map.getLayers().get("coins").getObjects().getByType(RectangleMapObject.class)) {
            //for coin objects
            if (object instanceof RectangleMapObject) {
                com.badlogic.gdx.math.Rectangle r = object.getRectangle();
                coins.add(new Coins(r));
            }
        }
        for(RectangleMapObject object5: map.getLayers().get("alot of coins").getObjects().getByType(RectangleMapObject.class)){
            //for hidden coins
            if(object5 instanceof RectangleMapObject){
                com.badlogic.gdx.math.Rectangle r = object5.getRectangle();
                alotCoins.add(new AlotOfCoins(r));
            }
        }

    }

    public boolean ObjectCollide(Rectangle player){ //map tile objects collision with player
        for(Coins c:coins){ //collect coins
            if(player.overlaps(c.getRect())){
                coins.remove(c);
                p1.addPoint(5);
                return true;
            }
        }
        for(AlotOfCoins a : alotCoins){ //hidden coins collected
            if(player.overlaps(a.getRect())){
                p1.addPoint(30);
                alotCoins.remove(a);
                return true;
            }
        }
        return false;
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
	public void dispose () {
		batch.dispose();
		sr.dispose();
	}
}