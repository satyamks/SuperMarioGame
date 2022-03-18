package com.android.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SuperMario extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture dizzyMan;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manX;
	int manY;
	Rectangle manRectangle;
	int gold = 0;
	int energy = 1;
	int gameState = 0;
	BitmapFont fontGold;
	BitmapFont fontEnergy;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Integer> redBullXs = new ArrayList<>();
	ArrayList<Integer> redBullYs = new ArrayList<>();
	ArrayList<Integer> macDXs = new ArrayList<>();
	ArrayList<Integer> macDYs = new ArrayList<>();
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Integer> redBullCloudXs = new ArrayList<>();
	ArrayList<Integer> redBullCloudYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	ArrayList<Rectangle> redBullRectangles = new ArrayList<Rectangle>();
	ArrayList<Rectangle> macDRectangles = new ArrayList<Rectangle>();
	ArrayList<Rectangle> redBullCloudRectangles = new ArrayList<Rectangle>();
	Texture coin;
	Texture redBull;
	Texture macD;
	Texture bomb;
	Texture redBullCloud;
	Random random;
	int coinTimer = 0;
	int bombTimer = 0;
	int redBullTimer = 0;
	int macDTimer = 0;
	int redBullCloudTimer = 0;
	int newCoinWaitCount = 500;
	int newBombWaitCount = 500;
	int newRedBullWaitCount = 700;
	int newMacDWaitCount = 700;
	int newRedBullCloudWaitCount = 1000;
	int coinSpeed = 5;
	int bombSpeed = 5;
	int redBullSpeed = 5;
	int macDSpeed = 5;
	int redBullCloudSpeed = 2;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.jpeg");
		man = new Texture[4];
		dizzyMan = new Texture("dizzyFrame.png");
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manY = Gdx.graphics.getHeight()/2;
		manX = (Gdx.graphics.getWidth()/2) - (man[manState].getWidth()/2);
		coin = new Texture("dollar.png");
		redBull = new Texture("red-bull.png");
		macD = new Texture("mac-d.png");
		bomb = new Texture("bomb.png");
		redBullCloud = new Texture("redbull-clouds-overlay.png");
		random = new Random();
		fontGold = new BitmapFont();
		fontEnergy = new BitmapFont();
		fontGold.setColor(Color.GOLD);
		fontGold.getData().setScale(5);
		fontEnergy.setColor(Color.MAROON);
		fontEnergy.getData().setScale(5);
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 0) {
			//Waiting for game to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 1) {
			//Game is live
			//Render RedBull Cloud
			renderGameObject(redBullCloud, redBullCloudRectangles, redBullCloudXs, redBullCloudYs,
					redBullCloudTimer++, newRedBullCloudWaitCount, redBullCloudSpeed);
			//Render Coin
			renderGameObject(coin, coinRectangles, coinXs, coinYs, coinTimer++, newCoinWaitCount, coinSpeed);
			//Render Bomb
			renderGameObject(bomb, bombRectangles, bombXs, bombYs, bombTimer++, newBombWaitCount, bombSpeed);
			//Render RedBull
			renderGameObject(redBull, redBullRectangles, redBullXs, redBullYs, redBullTimer++,
					newRedBullWaitCount, redBullSpeed);
			//Render MacD French Fries
			renderGameObject(macD, macDRectangles, macDXs, macDYs, macDTimer++,
					newMacDWaitCount, macDSpeed);
			resetCounter();

			//Move hero vertically
			moveHeroVertically();
		} else if (gameState == 2) {
			//Game is over
			resetGame();
		}

		if (gameState == 2) {
			batch.draw(dizzyMan, manX, manY);
		} else {
			batch.draw(man[manState], manX, manY);
		}
		manRectangle = new Rectangle(manX, manY, man[manState].getWidth(), man[manState].getHeight());

		handleCoinCollision();
		handleBombCollision();
		handleAdCharacterCollision(redBullRectangles, redBullXs, redBullYs);
		handleAdCharacterCollision(macDRectangles, macDXs, macDYs);

		fontGold.draw(batch, "Score: " + gold, 100, 2000);
		fontEnergy.draw(batch, "Energy: " + energy, 100, 1900);

		batch.end();
	}

	public void makeGameObject(ArrayList<Integer> gameObjectXs, ArrayList<Integer> gameObjectYs) {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		gameObjectXs.add(Gdx.graphics.getWidth());
		gameObjectYs.add((int) height);
	}

	public void renderGameObject(Texture gameObject, ArrayList<Rectangle> gameObjectRectangles,
								 ArrayList<Integer> gameObjectXs, ArrayList<Integer> gameObjectYs,
								 int gameObjectTimer, int newGameObjectWaitCount, int gameObjectSpeed) {
		try {
			if ((gameObjectTimer % newGameObjectWaitCount) == 0) {
				if (gameObject.equals(redBullCloud)) {
					gameObjectXs.add(Gdx.graphics.getWidth());
					float cloudHeight = 200 + (random.nextFloat() * 200);
					gameObjectYs.add(Gdx.graphics.getHeight() - (int) cloudHeight);
				} else {
					makeGameObject(gameObjectXs, gameObjectYs);
				}
			}
			gameObjectRectangles.clear();
			for (int i = 0; i < gameObjectXs.size(); i++) {
				batch.draw(gameObject, gameObjectXs.get(i), gameObjectYs.get(i));
				gameObjectXs.set(i, gameObjectXs.get(i) - gameObjectSpeed);
				gameObjectRectangles.add(new Rectangle(gameObjectXs.get(i), gameObjectYs.get(i),
						gameObject.getWidth(), gameObject.getHeight()));
			}
		} catch (Exception ex) {
			//Need to log the exception.
		}
	}

	public void moveHeroVertically() {
		if (Gdx.input.justTouched()) {
			velocity -= 10;
		}

		if (pause < 8) {
			pause++;
		} else {
			pause = 0;
			if (manState < 3) {
				manState++;
			} else {
				manState = 0;
			}
		}

		velocity += gravity;
		manY -= velocity;

		if (manY < 0) {
			manY = 0;
		}

		if (manY > (Gdx.graphics.getHeight() - man[manState].getHeight())) {
			manY = Gdx.graphics.getHeight() - man[manState].getHeight();
			velocity = 0;
		}
	}

	public void handleCoinCollision() {
		for (int i = 0; i < coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				gold++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
	}

	public void handleBombCollision() {
		for (int i = 0; i < bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				energy = energy - 2;
				bombRectangles.remove(i);
				bombXs.remove(i);
				bombYs.remove(i);
				if (energy <= 0) {
					gameState = 2;
				}
				if (gameState == 2) {
					energy = 0;
				}
			}
		}
	}

	public void handleAdCharacterCollision(ArrayList<Rectangle> adCharacterRectangles,
										   ArrayList<Integer> adCharacterXs,
										   ArrayList<Integer> adCharacterYs) {
		for (int i = 0; i < adCharacterRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, adCharacterRectangles.get(i))) {
				energy++;
				adCharacterRectangles.remove(i);
				adCharacterXs.remove(i);
				adCharacterYs.remove(i);
				break;
			}
		}
	}

	public void resetCounter() {
		if(coinTimer >= newCoinWaitCount) coinTimer = 0;
		if(bombTimer >= newBombWaitCount) bombTimer = 0;
		if(redBullTimer >= newRedBullWaitCount) redBullTimer = 0;
		if(macDTimer >= newMacDWaitCount) macDTimer = 0;
	}

	public void resetGame() {
		if (Gdx.input.justTouched()) {
			gameState = 1;
			manY = Gdx.graphics.getHeight()/2;
			gold = 0;
			energy = 1;
			velocity = 0;
			coinXs.clear();
			coinYs.clear();
			coinRectangles.clear();
			bombXs.clear();
			bombYs.clear();
			bombRectangles.clear();
			redBullXs.clear();
			redBullYs.clear();
			redBullRectangles.clear();
			macDXs.clear();
			macDYs.clear();
			macDRectangles.clear();
			redBullCloudXs.clear();
			redBullCloudYs.clear();
			redBullCloudRectangles.clear();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
