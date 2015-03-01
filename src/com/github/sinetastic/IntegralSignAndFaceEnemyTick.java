package com.github.sinetastic;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.Entity;
import com.github.sinetastic.entities.IntegralSign;
import com.github.sinetastic.entities.Ship;
import com.github.sinetastic.entities.Shot;
import com.github.sinetastic.entities.ShotCallback;
import com.github.sinetastic.entities.FxShot;
import com.github.sinetastic.entities.Face;

public class IntegralSignAndFaceEnemyTick implements Game.TickListener,
		IntegralSign.Callback, Face.Callback {

	private static final Rectangle CONTAINING_BOX = new Rectangle(100, 0,
			(int) Game.WIDTH - 100, (int) Game.HEIGHT);

	private static final double NILS_FACE_HEIGHT = 50;
	private static final double NILS_FACE_WIDTH = 35;
	private static final BufferedImage NILS_IMAGE;
	static {
		try {
			NILS_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/nils.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final double EDE_FACE_HEIGHT = 50;
	private static final double EDE_FACE_WIDTH = 38;
	private static final BufferedImage EDE_IMAGE;
	static {
		try {
			EDE_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/ede.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final double BUDI_FACE_HEIGHT = 50;
	private static final double BUDI_FACE_WIDTH = 42;
	private static final BufferedImage BUDI_IMAGE;
	static {
		try {
			BUDI_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/budi.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final double SIMON_FACE_HEIGHT = 50;
	private static final double SIMON_FACE_WIDTH = 42;
	private static final BufferedImage SIMON_IMAGE;
	static {
		try {
			SIMON_IMAGE = ImageIO
					.read(Ship.class
							.getResource("/com/github/sinetastic/assets/rocketbeans/simon.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static final int Z_INDEX = 4;

	private static final int MIN_WIDTH = 15;
	private static final int MIN_HEIGHT = 30;

	private static final double MAX_SPEED_X = 0.25;
	private static final double MIN_SPEED_Y = 0.13;
	private static final double VAR_SPEED_Y = 0.05;

	private static final int VAR_WIDTH = 1;
	private static final int VAR_HEIGHT = 20;

	private int aliveCount = 0;
	private long lastSpawn;

	private final int delay;
	private final int maxCount;

	public IntegralSignAndFaceEnemyTick(int delay, int maxCount) {
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
				final Entity sign;
				// if (game.random.nextBoolean()) {
				sign = this.createFace(game);
				// } else {
				// sign = this.createSign(game);
				// }
				game.enqueue.add(new TickListener() {

					private static final int SHOT_WIDTH = 10;
					private static final int SHOT_HEIGHT = 3;

					private long shotDelay = (long) 2000;
					private double yScale = game.random.nextDouble() * 4 + 0.2;
					private double speedY = game.random.nextDouble()
							* VAR_SPEED_Y + MIN_SPEED_Y;

					private long lastShot;

					private boolean finished = false;
					private float sinPosX;
					private float sinPosY;
					private float signSin;
					private FxShot shot;

					@Override
					public void tick(Game game) {
						{
							if (sign.isVisible()) {
								double signToShip = 0;
								double differenceBetweenMids = Math
										.abs((game.ship.getY() + game.ship
												.getHeight() / 2)
												- (sign.getY() + sign
														.getHeight() / 2));
								// move the entity
								double dX = -Game.sin((this.sinPosX += game
										.tdT(0.0015))) * MAX_SPEED_X;
								double dY = this.speedY
										* this.yScale * Math.abs(Game
												.sin(this.sinPosY += game
														.tdT((0.005))));
								if (game.ship.isAlive()) {
									dY *= signToShip = Math.signum((game.ship
											.getY() + game.ship.getHeight() / 2)
											- (sign.getY() + sign.getHeight() / 2));
								} else {
									dY *= Math.signum(Game
											.sin(this.signSin += game
													.tdT(0.005)));
								}
								dX = game.tdT(dX);
								dY = game.tdT(dY);
								if (game.ship.isAlive() && Math.abs(dY) > differenceBetweenMids) {
									dY = signToShip * differenceBetweenMids;
								}
								game.moveAndEnsureInBox(sign, dX, dY,
										CONTAINING_BOX);

								if (this.lastShot == 0) {
									this.lastShot = (long) (game.currentTick + this.shotDelay);
								}
								// trigger a shot if we don't have one at the
								// moment
								boolean canShoot = game.diff(this.lastShot) > this.shotDelay
										&& this.shot == null;
								if (canShoot) {
									this.shot = new FxShot(true, SHOT_WIDTH,
											SHOT_HEIGHT, (x, position) -> {
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
													game.scene.removeEntity(
															FxShot.Z_INDEX,
															shot_);
													game.moveTick.remove(shot_);
												}

											});
									shot.setVisible(true);
									shot.setSpeedX(-0.45);
									shot.setX(sign.getX() - 5);
									// mid of the sign
									double y = sign.getY() + sign.getHeight()
											/ 2;
									// if the ship is alive shoot in the
									// direction of it
									y += signToShip * differenceBetweenMids;
									shot.setY(y);
									game.scene.addEntity(FxShot.Z_INDEX, shot);
									game.moveTick.add(shot);
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

	private Face createFace(Game game) {
		Face face;
		switch (game.random.nextInt(4)) {
		case 0:
			face = new Face(true, SIMON_FACE_WIDTH, SIMON_FACE_HEIGHT,
					SIMON_IMAGE, this);
			break;
		case 1:
			face = new Face(true, BUDI_FACE_WIDTH, BUDI_FACE_HEIGHT,
					BUDI_IMAGE, this);
			break;
		case 2:
			face = new Face(true, EDE_FACE_WIDTH, EDE_FACE_HEIGHT, EDE_IMAGE,
					this);
			break;
		case 3:
			face = new Face(true, NILS_FACE_WIDTH, NILS_FACE_HEIGHT,
					NILS_IMAGE, this);
			break;
		default:
			throw new AssertionError();
		}
		game.moveAndEnsureInBox(face, Game.WIDTH - face.getWidth(),
				game.random.nextInt((int) Game.HEIGHT), CONTAINING_BOX);
		game.scene.addEntity(4, face);
		++this.aliveCount;
		return face;
	}

	private IntegralSign createSign(Game game) {
		IntegralSign integralSign = new IntegralSign(true,
				game.random.nextInt(VAR_WIDTH) + MIN_WIDTH,
				game.random.nextInt(VAR_HEIGHT) + MIN_HEIGHT,
				game.randomColor(1.0f), this);
		game.moveAndEnsureInBox(integralSign,
				Game.WIDTH - integralSign.getWidth(),
				game.random.nextInt((int) Game.HEIGHT), CONTAINING_BOX);
		game.scene.addEntity(Z_INDEX, integralSign);
		++this.aliveCount;
		return integralSign;
	}

	@Override
	public void onDestroy(Game game, IntegralSign sign) {
		--this.aliveCount;
		sign.setVisible(false);
		game.scene.removeEntity(Z_INDEX, sign);
	}

	@Override
	public void onDestroy(Game game, Face face) {
		--this.aliveCount;
		face.setVisible(false);
		game.scene.removeEntity(4, face);
	}

}
