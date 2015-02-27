package com.github.sinetastic;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.Rock;

public class RockTick implements Game.TickListener, Rock.Callback {
	
	private static final int Z_INDEX = 4;
	private static final int POINTS_PER_ROCK = 10;

	private static final double MAX_SPEED_X = 0.4;
	private static final double MAX_SPEED_Y = 0.1;

	private static final float ALPHA = 1.0f;

	private static final int MIN_WIDTH = 10;
	private static final int MIN_HEIGHT = 10;

	private static final int VAR_WIDTH = 20;
	private static final int VAR_HEIGHT = 20;

	private int aliveCount = 0;
	private final int maxRocks;
	private final int delay;
	private long lastSpawn;

	public RockTick(int maxRocks, int delay) {
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
				final Rock rock = this.createRock(game);
				game.enqueue.add(new TickListener() {

					private boolean finished = false;

					@Override
					public void tick(Game game) {
						//move them even if they are dead.
						//we don't want to have infinite rocks spawning
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
							game.scene.removeEntity(2, rock);
							this.finished = true;
							--RockTick.this.aliveCount;
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

	private Rock createRock(Game game) {
		Rock rock = new Rock(true, game.random.nextInt(VAR_WIDTH) + MIN_WIDTH,
				game.random.nextInt(VAR_HEIGHT) + MIN_HEIGHT,
				game.randomColor(ALPHA), true, game.random.nextBoolean(), this);
		rock.setY(game.random.nextInt((int) Game.HEIGHT));
		rock.setX((int) Game.WIDTH);
		game.scene.addEntity(Z_INDEX, rock);
		++this.aliveCount;
		return rock;
	}

	@Override
	public void onDestroy(Game game, Rock rock) {
		game.addPoints(POINTS_PER_ROCK);
		game.scene.removeEntity(Z_INDEX, rock);
	}


	@Override
	public boolean isFinished() {
		return false;
	}

}
