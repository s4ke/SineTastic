package com.github.sinetastic;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.IOException;
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

	public static final double WIDTH = 600;
	public static final double HEIGHT = 400;

	private static final double SHIP_WIDTH = 25;
	private static final double SHIP_HEIGHT = 18;

	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public boolean shotButton;

	public final Clip shipShotSound;
	public final Clip shipExplodeSound;

	public ScaleScene scene;
	public Ship ship;
	public Background background;
	public ProceduralWall topWall;
	public ProceduralWall botWall;
	public long lastTick;
	public long currentTick;
	public long frames = 0;
	public long lastFpsTime;
	private final Set<TickListener> tickListeners;
	public final Random random;
	public final List<TickListener> enqueue;

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

	public void setup() {
		this.background = new Background(WIDTH, HEIGHT);
		this.scene.addEntity(5, this.background);

		Text text = new Text(200, 200, Color.WHITE);
		text.setText("Kills: ");
		this.scene.addEntity(0, text);

		this.ship = new Ship(SHIP_WIDTH, SHIP_HEIGHT);
		// center the ship
		this.ship.setY((HEIGHT / 2) - (SHIP_HEIGHT / 2));
		this.scene.addEntity(3, this.ship);

		{
			this.topWall = new ProceduralWall(true, WIDTH, 200,
					(step, position) -> {
						step[0] = Math.sin(this.random.nextDouble());
						return null;
					}, 200, this.randomColor(1.0f), true);
			this.topWall.setY(0);
			this.topWall.setX(0);
			this.scene.addEntity(1, this.topWall);

			this.botWall = new ProceduralWall(true, WIDTH, 200,
					(step, position) -> {
						step[0] = Math.sin(this.random.nextDouble());
						return null;
					}, 200, this.randomColor(1.0f), false);
			this.botWall.setY(300);
			this.botWall.setX(0);
			this.scene.addEntity(1, this.botWall);

			this.tickListeners
					.add(new BackGroundMoveTick(this.topWall, botWall));

			this.tickListeners.add(new RockTick(5000, 5));
		}

		this.tickListeners.add(new ShipTick());
		this.tickListeners.add(new UserShotTick());
	}

	public void shipCrashed() {
		if (this.ship.isAlive()) {
			this.ship.explode();
			this.enqueue.add(new SoundTick(this.shipExplodeSound, 1000));
		}
	}

	public Color randomColor(float alpha) {
		return new Color(this.random.nextFloat(), this.random.nextFloat(),
				this.random.nextFloat(), alpha);
	}

	public void tick() {
		if (++this.frames > 10) {
			long currentTime = System.nanoTime() / 1000 / 1000;
			if (this.lastFpsTime > 0) {
				double fps = (((double) this.frames) / (currentTime - this.lastFpsTime)) * 1000;
				System.out.println(new StringBuilder().append(fps)
						.append(" fps").toString());
			}
			this.frames = 0;
			this.lastFpsTime = currentTime;
		}
		this.currentTick = System.nanoTime();
		if (this.lastTick > 0) {
			List<TickListener> toRemove = new ArrayList<>();
			for (TickListener listener : this.tickListeners) {
				listener.tick(this);
				if (listener.isFinished()) {
					toRemove.add(listener);
				}
			}
			// prevents the concurrent modification exception
			int size = this.enqueue.size();
			for (int i = 0; i < size; ++i) {
				final TickListener listener = this.enqueue.get(i);
				listener.tick(Game.this);
				if (!listener.isFinished()) {
					this.tickListeners.add(listener);
				}
			}
			this.enqueue.clear();
			for (TickListener rem : toRemove) {
				this.tickListeners.remove(rem);
			}
		}

		this.lastTick = this.currentTick;
	}

	public double tdT(double value) {
		double dT = (this.currentTick - this.lastTick) / 1000 / 1000;
		return value * dT;
	}

	public long diff(long time) {
		return (this.currentTick - time) / 1000 / 1000;
	}

	public void moveAndEnsureInScene(Entity entity, double dX, double dY) {
		double width = entity.getWidth();
		double height = entity.getHeight();
		double newX = entity.getX() + dX;
		double newY = entity.getY() + dY;
		if (newX < 0) {
			newX = 0;
		}
		if (newX + width >= WIDTH) {
			newX = WIDTH - width;
		}
		if (newY < 0) {
			newY = 0;
		}
		if (newY + height >= HEIGHT) {
			newY = HEIGHT - height;
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
		}
	}

}
