package com.github.sinetastic;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.Entity;
import com.github.sinetastic.entities.Rock;
import com.github.sinetastic.entities.Ship;
import com.github.sinetastic.entities.Sprite;

public class RockAndNonShootingEnemyTick implements Game.TickListener,
		Rock.Callback, Sprite.Callback {

	private static final int Z_INDEX = 4;
	private static final double MAX_SPEED_X = 0.4;
	private static final double MAX_SPEED_Y = 0.1;

	private static final int POINTS_PER_ROCK = 10;

	private static final float ALPHA = 1.0f;

	private static final int MIN_WIDTH = 10;
	private static final int MIN_HEIGHT = 10;

	private static final int VAR_WIDTH = 20;
	private static final int VAR_HEIGHT = 20;

	private static final double MTV_LOGO_HEIGHT = 23;
	private static final double MTV_LOGO_WIDTH = 30;
	private static final int MTV_POINTS = 200;
	private static final BufferedImage MTV_LOGO_IMAGE;
	static {
		try {
			MTV_LOGO_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/mtv.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final double VIVA_LOGO_HEIGHT = 19;
	private static final double VIVA_LOGO_WIDTH = 30;
	private static final int VIVA_POINTS = 100;
	private static final BufferedImage VIVA_LOGO_IMAGE;
	static {
		try {
			VIVA_LOGO_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/viva.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final double VIACOM_LOGO_HEIGHT = 14;
	private static final double VIACOM_LOGO_WIDTH = 30;
	private static final int VIACOM_POINTS = 300;
	private static final BufferedImage VIACOM_LOGO_IMAGE;
	static {
		try {
			VIACOM_LOGO_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/viacom.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private int aliveCount = 0;
	private final int maxRocks;
	private final int delay;
	private long lastSpawn;

	public RockAndNonShootingEnemyTick(int maxRocks, int delay) {
		this.maxRocks = maxRocks;
		this.delay = delay;
	}

	@Override
	public void tick(Game game) {
		// spawn a new rock
		{
			boolean spawnNew = game.diff(this.lastSpawn) > this.delay
					&& this.aliveCount < this.maxRocks;
			if (spawnNew) {
				final Entity rock = this.createRock(game);
				game.enqueue.add(new TickListener() {

					private boolean finished = false;

					@Override
					public void tick(Game game) {
						// move them even if they are dead.
						// we don't want to have infinite rocks spawning
						double dX = -game.tdT(game.random.nextDouble()
								* MAX_SPEED_X);
						double dY = game.tdT(game.random.nextDouble()
								* MAX_SPEED_Y);
						if (game.random.nextBoolean()) {
							dY *= -1;
						}
						rock.setX(rock.getX() + dX);
						rock.setY(rock.getY() + dY);
						if (!game.background.getCollisionShape().contains(
								rock.getX(), rock.getY())) {
							rock.setVisible(false);
							game.scene.removeEntity(Z_INDEX, rock);
							this.finished = true;
							--RockAndNonShootingEnemyTick.this.aliveCount;
						}
					}

					@Override
					public boolean isFinished() {
						return this.finished;
					}

				});
				this.lastSpawn = game.currentTick;
			}
		}
	}

	private Entity createRock(Game game) {
		Entity entity;
		switch (game.random.nextInt(5)) {
		case 0:
			entity = new Sprite(true, MTV_LOGO_WIDTH, MTV_LOGO_HEIGHT,
					MTV_LOGO_IMAGE, this, MTV_POINTS);
			break;
		case 1:
			entity = new Sprite(true, VIVA_LOGO_WIDTH, VIVA_LOGO_HEIGHT,
					VIVA_LOGO_IMAGE, this, VIVA_POINTS);
			break;
		case 2:
			entity = new Sprite(true, VIACOM_LOGO_WIDTH, VIACOM_LOGO_HEIGHT,
					VIACOM_LOGO_IMAGE, this, VIACOM_POINTS);
			break;
		case 3:
		case 4:
			entity = new Rock(true, game.random.nextInt(VAR_WIDTH) + MIN_WIDTH,
					game.random.nextInt(VAR_HEIGHT) + MIN_HEIGHT,
					game.randomColor(ALPHA), true, game.random.nextBoolean(),
					this);
			break;
		default:
			throw new AssertionError();
		}
		// Entity
		entity.setY(game.random.nextInt((int) Game.HEIGHT));
		entity.setX((int) Game.WIDTH);
		game.scene.addEntity(Z_INDEX, entity);
		++this.aliveCount;
		return entity;
	}

	@Override
	public void onDestroy(Game game, Rock rock) {
		game.addPoints(POINTS_PER_ROCK);
		game.scene.removeEntity(Z_INDEX, rock);
		--RockAndNonShootingEnemyTick.this.aliveCount;
	}

	@Override
	public void onDestroy(Game game, Sprite face) {
		game.addPoints(POINTS_PER_ROCK);
		game.scene.removeEntity(Z_INDEX, face);
		--RockAndNonShootingEnemyTick.this.aliveCount;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
