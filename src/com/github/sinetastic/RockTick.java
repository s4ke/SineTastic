package com.github.sinetastic;

import java.awt.Color;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.Rock;
import com.github.sinetastic.entities.Rock.ExplodeCallback;

public class RockTick implements Game.TickListener, ExplodeCallback {

	private static final double MAX_SPEED_X = 0.4;
	private static final double MAX_SPEED_Y = 0.1;

	private static final int DELAY = 10;

	private static final int MIN_WIDTH = 10;
	private static final int MIN_HEIGHT = 10;

	private static final int VAR_WIDTH = 20;
	private static final int VAR_HEIGHT = 20;

	private int aliveCount = 0;
	private final int maxRocks;
	private long lastSpawn;

	public RockTick(int maxRocks) {
		this.maxRocks = maxRocks;
	}

	@Override
	public void tick(Game game) {
		// trigger a new shot
		{
			boolean spawnNew = game.diff(this.lastSpawn) > DELAY
					&& this.aliveCount < this.maxRocks;
			if (spawnNew) {
				final Rock rock = this.createRock(game);
				game.enqueue.add(new TickListener() {

					private boolean finished = false;

					@Override
					public void tick(Game game) {
						// move the shot
						{
							if (rock.isVisible()) {
								double dX = -game.tdT(game.random.nextDouble()
										* MAX_SPEED_X);
								double dY = (-1)
										* game.random.nextInt(2)
										* game.tdT(game.random.nextDouble()
												* MAX_SPEED_Y);
								rock.setX(rock.getX() + dX);
								rock.setY(rock.getY() + dY);
								if (!game.background.getCollisionShape()
										.contains(rock.getX(), rock.getY())) {
									rock.setVisible(false);
									game.scene.removeEntity(2, rock);
									--RockTick.this.aliveCount;
									this.finished = true;
								}
							}
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
		Rock rock = new Rock(game.random.nextInt(VAR_WIDTH) + MIN_WIDTH,
				game.random.nextInt(VAR_HEIGHT) + MIN_HEIGHT, new Color(
						game.random.nextInt(255), game.random.nextInt(255),
						game.random.nextInt(255)), game.random.nextBoolean(),
				game.random.nextBoolean(), this);
		rock.setY(game.random.nextInt((int) Game.HEIGHT));
		rock.setX((int) Game.WIDTH);
		game.scene.addEntity(2, rock);
		++this.aliveCount;
		return rock;
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void onExplode(Rock rock) {
		--this.aliveCount;
	}

}
