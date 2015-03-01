package com.github.sinetastic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.github.sinetastic.entities.Background;
import com.github.sinetastic.entities.Entity;
import com.github.sinetastic.entities.ProceduralWall;
import com.github.sinetastic.entities.Ship;
import com.github.sinetastic.entities.Text;
import com.github.sinetastic.scene.ScaleScene;

public class Game implements KeyListener {

	public static final Font BASE_FONT;
	static {
		try (InputStream is = Text.class
				.getResourceAsStream("/com/github/sinetastic/assets/quikhand/quikhand.ttf")) {
			BASE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * FROM libGdx:
	 * 
	 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/
	 * math/MathUtils.java
	 */
	// ---
	static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
	static public final float PI = 3.1415927f;
	static public final float PI2 = PI * 2;

	static public final float E = 2.7182818f;

	static private final int SIN_BITS = 24; // 16KB. Adjust for accuracy.
	static private final int SIN_MASK = ~(-1 << SIN_BITS);
	static private final int SIN_COUNT = SIN_MASK + 1;

	static private final float radFull = PI * 2;
	static private final float degFull = 360;
	static private final float radToIndex = SIN_COUNT / radFull;
	static private final float degToIndex = SIN_COUNT / degFull;

	/** multiply by this to convert from radians to degrees */
	static public final float radiansToDegrees = 180f / PI;
	static public final float radDeg = radiansToDegrees;
	/** multiply by this to convert from degrees to radians */
	static public final float degreesToRadians = PI / 180;
	static public final float degRad = degreesToRadians;

	static private class Sin {
		static final float[] table = new float[SIN_COUNT];
		static {
			for (int i = 0; i < SIN_COUNT; i++)
				table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
			for (int i = 0; i < 360; i += 90)
				table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i
						* degreesToRadians);
		}
	}

	private static final String POINTS_STRING = "Points:  ";
	private static final String LIVES_TEXT = "Lives:    ";
	private static final int START_LIVES = 3;

	public static final double WIDTH = 600;
	public static final double HEIGHT = 400;

	private static final Rectangle SCENE_BOX = new Rectangle(0, 0, (int) WIDTH,
			(int) HEIGHT);

	private static final double SHIP_WIDTH = 18;
	private static final double SHIP_HEIGHT = 18;

	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public boolean shotButton;
	public boolean reviveButton;

	public final Clip shipShotSound;
	public final Clip shipExplodeSound;

	public ScaleScene scene;
	public Ship ship;
	public Background background;
	public ProceduralWall topWall;
	public ProceduralWall botWall;

	public long lastTick;
	public long currentTick;
	private long shipSpawnedTick;
	public long ticks = 0;
	public long lastTpsTime;
	private final Set<TickListener> tickListeners;
	public final Random random;
	public final List<TickListener> enqueue;
	public UserShotTick userShotTick;
	private UserShipTick userShipTick;
	private int lives;
	private Text livesText;
	private int points;
	private Text pointsText;

	// This is special!
	public MoveTick moveTick;

	public static interface TickListener {

		public void tick(Game game);

		public boolean isFinished();

	}

	public Game(ScaleScene scene) throws MidiUnavailableException,
			LineUnavailableException, IOException,
			UnsupportedAudioFileException {

		{
			this.shipShotSound = AudioSystem.getClip();
			BufferedInputStream bis = new BufferedInputStream(
					Game.class
							.getResourceAsStream("/com/github/sinetastic/assets/blazer.wav"));
			this.shipShotSound.open(AudioSystem.getAudioInputStream(bis));
		}

		{
			this.shipExplodeSound = AudioSystem.getClip();
			BufferedInputStream bis = new BufferedInputStream(
					Game.class
							.getResourceAsStream("/com/github/sinetastic/assets/ship_explode.wav"));
			this.shipExplodeSound.open(AudioSystem.getAudioInputStream(bis));
		}
		this.scene = scene;
		this.random = new Random();
		this.enqueue = new ArrayList<>();
		this.tickListeners = new HashSet<>();
	}
	
	public static double sin(float radians) {
		return Sin.table[(int)(radians * radToIndex) & SIN_MASK];
	}

	public void setup() {
		this.background = new Background(WIDTH, HEIGHT);
		this.scene.addEntity(5, this.background);

		this.ship = new Ship(SHIP_WIDTH, SHIP_HEIGHT);
		this.spawnShip();
		this.shipSpawnedTick = 0;

		this.tickListeners.add(new IntegralSignAndFaceEnemyTick(100, 8));

		{
			// this.topWall = new ProceduralWall(true, WIDTH, 100,
			// (step, position) -> {
			// step[0] = Math.sin(this.random.nextDouble());
			// return null;
			// }, 200, this.randomColor(1.0f), true);
			// this.topWall.setY(0);
			// this.topWall.setX(0);
			// this.scene.addEntity(1, this.topWall);
			//
			// this.botWall = new ProceduralWall(true, WIDTH, 100,
			// (step, position) -> {
			// step[0] = Math.sin(this.random.nextDouble());
			// return null;
			// }, 200, this.randomColor(1.0f), false);
			// this.botWall.setY(300);
			// this.botWall.setX(0);
			// this.scene.addEntity(1, this.botWall);
			//
			// this.tickListeners
			// .add(new BackGroundMoveTick(this.topWall, botWall));

			this.tickListeners.add(new RockTick(7, 200));
		}

		this.userShipTick = new UserShipTick();
		this.tickListeners.add(this.userShotTick = new UserShotTick());

		this.moveTick = new MoveTick();

		// Sinusal sinusal = new Sinusal(true, 200, 200,
		// this.randomColor(1.0f));
		// sinusal.setX(200);
		// sinusal.setY(200);
		// this.scene.addEntity(3, sinusal);

		this.pointsText = new Text(Color.BLUE, 20);
		this.setPoints(0);
		this.pointsText.setX(5);
		this.pointsText.setY(20);
		this.scene.addEntity(0, this.pointsText);

		this.livesText = new Text(Color.BLUE, 20);
		this.addLives(START_LIVES);
		this.livesText.setX(5);
		this.livesText.setY(40);
		this.scene.addEntity(0, this.livesText);
	}

	public void shipCrashed() {
		if (this.ship.isAlive()) {
			this.ship.explode();
			this.addLives(-1);
			this.enqueue.add(new SoundTick(this.shipExplodeSound, 1000));
		}
	}

	public void restartGame() {
		this.tickListeners.clear();
		this.enqueue.clear();
		this.scene.clear();
		this.setup();
	}

	public void addLives(long lives) {
		this.lives += lives;
		this.livesText.setText(LIVES_TEXT + this.lives);
	}

	public int getLives() {
		return this.lives;
	}

	public void addPoints(int points) {
		int newPoints = this.points;
		if (this.lives > 0) {
			newPoints += points;
		}
		this.setPoints(newPoints);
	}

	private void setPoints(int points) {
		this.points = points;
		this.pointsText.setText(POINTS_STRING + this.points);
	}

	public void respawnShip() {
		if (this.lives == 0) {
			this.restartGame();
		} else {
			this.spawnShip();
		}
	}

	public long getShipSpawnedTick() {
		return this.shipSpawnedTick;
	}

	private void spawnShip() {
		this.ship.setX(0);
		// center the ship
		this.ship.setY((HEIGHT / 2) - (SHIP_HEIGHT / 2));
		this.scene.addEntity(3, this.ship);
		this.ship.rebuild();
		this.shipSpawnedTick = this.currentTick;
	}

	public Color randomColor(float alpha) {
		return new Color(this.random.nextFloat(), this.random.nextFloat(),
				this.random.nextFloat(), alpha);
	}

	public void tick() {
		if (++this.ticks > 500) {
			long currentTime = System.nanoTime() / 1000 / 1000 / 1000;
			if (this.lastTpsTime > 0) {
				double ticksPerSecond = (((double) this.ticks) / (currentTime - this.lastTpsTime));
				System.out.println(new StringBuilder().append(ticksPerSecond)
						.append(" ticks per second").toString());
			}
			this.ticks = 0;
			this.lastTpsTime = currentTime;
		}
		this.currentTick = System.currentTimeMillis();
		if (this.lastTick > 0) {
			// first move everything
			this.moveTick.tick(this);

			List<TickListener> toRemove = new ArrayList<>();
			for (TickListener listener : this.tickListeners) {
				listener.tick(this);
				if (listener.isFinished()) {
					toRemove.add(listener);
				}
			}
			{
				TickListener listener = null;
				while (this.enqueue.size() > 0
						&& (listener = this.enqueue.remove(0)) != null) {
					listener.tick(Game.this);
					if (!listener.isFinished()) {
						this.tickListeners.add(listener);
					}
				}
			}
			for (TickListener rem : toRemove) {
				this.tickListeners.remove(rem);
			}
		}
		this.userShipTick.tick(this);
		this.lastTick = this.currentTick;
	}

	public double tdT(double value) {
		double dT = (this.currentTick - this.lastTick);
		return value * dT;
	}

	public long diff(long time) {
		return (this.currentTick - time);
	}

	public void moveAndEnsureInScene(Entity entity, double dX, double dY) {
		this.moveAndEnsureInBox(entity, dX, dY, SCENE_BOX);
	}

	public void moveAndEnsureInBox(Entity entity, double dX, double dY,
			Rectangle box) {
		double width = entity.getWidth();
		double height = entity.getHeight();
		double newX = entity.getX() + dX;
		double newY = entity.getY() + dY;
		if (newX < box.getX()) {
			newX = box.getX();
		}
		if (newX + width >= box.getWidth() + box.getX()) {
			newX = box.getWidth() - width + box.getX();
		}
		if (newY < box.getY()) {
			newY = box.getY();
		}
		if (newY + height >= box.getHeight() + box.getY()) {
			newY = box.getHeight() - height + box.getY();
		}
		entity.setX(newX);
		entity.setY(newY);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.left = true;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.up = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.down = true;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			this.shotButton = true;
		} else if (e.getKeyCode() == KeyEvent.VK_R) {
			this.reviveButton = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			this.left = false;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			this.right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.up = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			this.down = false;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			this.shotButton = false;
		} else if (e.getKeyCode() == KeyEvent.VK_R) {
			this.reviveButton = false;
		}
	}

}
