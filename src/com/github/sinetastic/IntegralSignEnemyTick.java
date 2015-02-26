package com.github.sinetastic;

import java.awt.Rectangle;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.IntegralSign;
import com.github.sinetastic.entities.Shot;
import com.github.sinetastic.entities.ShotCallback;
import com.github.sinetastic.entities.FxShot;

public class IntegralSignEnemyTick implements Game.TickListener,
		IntegralSign.Callback {

	private static final Rectangle CONTAINING_BOX = new Rectangle(100, 50,
			(int) Game.WIDTH - 100, (int) Game.HEIGHT - 100);

	private static final int MIN_WIDTH = 15;
	private static final int MIN_HEIGHT = 30;

	private static final double MAX_SPEED_X = 0.3;
	private static final double MAX_SPEED_Y = 0.1;

	private static final int VAR_WIDTH = 1;
	private static final int VAR_HEIGHT = 20;

	private int aliveCount = 0;
	private long lastSpawn;

	private final int delay;
	private final int maxCount;

	public IntegralSignEnemyTick(int delay, int maxCount) {
		this.delay = delay;
		this.maxCount = maxCount;
	}

	@Override
	public void tick(Game game) {
		// spawn a sign
		{
			boolean spawnNew = game.diff(this.lastSpawn) > this.delay
					&& this.aliveCount < this.maxCount;
			if (spawnNew) {
				final IntegralSign sign = this.createSign(game);
				game.enqueue.add(new TickListener() {

					// 1 SEC
					private static final int SHOT_DELAY = 1000;
					private static final int SHOT_WIDTH = 10;
					private static final int SHOT_HEIGHT = 3;

					private long lastShot;

					private boolean finished = false;
					private double sinPosX;
					private double sinPosY;
					private FxShot shot;

					@Override
					public void tick(Game game) {
						{
							if (sign.isVisible()) {
								// move the entity
								double dX = -game.tdT(Math
										.sin((this.sinPosX += 0.004))
										* MAX_SPEED_X);
								double dY = game.tdT(Math
										.sin((this.sinPosY += 0.02))
										* MAX_SPEED_Y);
								game.moveAndEnsureInBox(sign, dX, dY,
										CONTAINING_BOX);

								// trigger a shot if we don't have one at the
								// moment
								boolean canShoot = game.diff(this.lastShot) > SHOT_DELAY
										&& this.shot == null;
								if (canShoot) {
									this.shot = new FxShot(true,
											SHOT_WIDTH, SHOT_HEIGHT, (x,
													position) -> {
												x[0] = 0;
												return null;
											}, 2, new ShotCallback() {
												@Override
												public void removeShot(
														Shot shot_) {
													if (shot != shot_) {
														throw new AssertionError();
													}
													shot = null;
													shot_.setVisible(false);
													game.scene.removeEntity(4,
															shot_);
													game.moveTick.dequeue
															.add(shot_);
												}

											});
									shot.setVisible(true);
									shot.setSpeedX(-0.5);
									shot.setX(sign.getX() - 5);
									shot.setY(sign.getY() + sign.getHeight()
											/ 2);
									game.scene.addEntity(4, shot);
									game.moveTick.enqueue.add(shot);
									this.lastShot = game.currentTick;
								}
							} else {
								this.finished = true;
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

	@Override
	public boolean isFinished() {
		return false;
	}

	private IntegralSign createSign(Game game) {
		IntegralSign integralSign = new IntegralSign(true,
				game.random.nextInt(VAR_WIDTH) + MIN_WIDTH,
				game.random.nextInt(VAR_HEIGHT) + MIN_HEIGHT,
				game.randomColor(1.0f), this);
		game.moveAndEnsureInBox(integralSign,
				Game.WIDTH - integralSign.getWidth(),
				game.random.nextInt((int) Game.HEIGHT), CONTAINING_BOX);
		game.scene.addEntity(4, integralSign);
		++this.aliveCount;
		return integralSign;
	}

	@Override
	public void onDestroy(Game game, IntegralSign sign) {
		--this.aliveCount;
		sign.setVisible(false);
		game.scene.removeEntity(4, sign);
	}

}
