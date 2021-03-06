package com.github.sinetastic;

import java.awt.*;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.FxShot;
import com.github.sinetastic.entities.IntegralSign;

public class IntegralSignEnemyTick implements Game.TickListener,
											  IntegralSign.Callback {

	private static final Rectangle CONTAINING_BOX = new Rectangle( 100, 0,
																   (int) Game.WIDTH - 100, (int) Game.HEIGHT
	);

	public static final int Z_INDEX = 4;

	private static final int MIN_WIDTH = 15;
	private static final int MIN_HEIGHT = 30;

	private static final double MAX_SPEED_X = 0.3;
	private static final double MIN_SPEED_Y = 0.09;
	private static final double VAR_SPEED_Y = 0.05;

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
			boolean spawnNew = game.diff( this.lastSpawn ) > this.delay
					&& this.aliveCount < this.maxCount;
			if ( spawnNew ) {
				final IntegralSign sign = this.createSign( game );
				game.enqueue.add( new TickListener() {

					private static final int SHOT_WIDTH = 10;
					private static final int SHOT_HEIGHT = 3;

					private long shotDelay = (long) (game.random.nextDouble() * 3000 + 1000);
					private double yScale = game.random.nextDouble() * 4;
					private double speedY = game.random.nextDouble()
							* VAR_SPEED_Y + MIN_SPEED_Y;
					private double sinSpeedY = this.speedY * 0.3;

					private long lastShot;

					private boolean finished = false;
					private double sinPosX;
					private double sinPosY;
					private double signSin;
					private FxShot shot;

					@Override
					public void tick(Game game) {
						{
							if ( sign.isVisible() ) {
								double signToShip = 0;
								// move the entity
								double dX = -game.tdT( Game
															   .sin( (this.sinPosX += 0.004) )
															   * MAX_SPEED_X );
								double dY = game.tdT( this.speedY
															  * this.yScale
															  + this.sinSpeedY
										* Math.abs( Game
															.sin( this.sinPosY += 0.01 ) ) );
								if ( game.ship.isAlive() ) {
									dY *= signToShip = Math.signum( (game.ship
											.getY() + game.ship.getHeight() / 2)
																			- (sign.getY() + sign.getHeight() / 2) );
								}
								else {
									dY *= Math.signum( Game
															   .sin( this.signSin += game.tdT( 0.01 ) ) );
								}
								game.moveAndEnsureInBox( sign, dX, dY,
														 CONTAINING_BOX
								);

								if ( this.lastShot == 0 ) {
									this.lastShot = (long) (game.currentTick + this.shotDelay);
								}
								// trigger a shot if we don't have one at the
								// moment
								boolean canShoot = game.diff( this.lastShot ) > this.shotDelay
										&& this.shot == null;
								if ( canShoot ) {
									this.shot = new FxShot( true, SHOT_WIDTH,
															SHOT_HEIGHT, (x, position) -> x[0] = 0, 2, shot_ -> {
										if ( shot != shot_ ) {
											throw new AssertionError();
										}
										shot = null;
										shot_.setVisible( false );
										game.scene.removeEntity(
												FxShot.Z_INDEX,
												shot_
										);
										game.moveTick.remove( shot_ );
									}
									);
									shot.setVisible( true );
									shot.setSpeedX( -0.5 );
									shot.setX( sign.getX() - 5 );
									// mid of the sign
									double y = sign.getY() + sign.getHeight()
											/ 2;
									// if the ship is alive shoot in the
									// direction of it
									y += signToShip
											* Math.abs( (game.ship.getY() + game.ship
											.getHeight() / 2)
																- (sign.getY() + sign
											.getHeight() / 2) );
									shot.setY( y );
									game.scene.addEntity( FxShot.Z_INDEX, shot );
									game.moveTick.add( shot );
									this.lastShot = game.currentTick;
								}
							}
							else {
								this.finished = true;
							}
						}
					}

					@Override
					public boolean isFinished() {
						return this.finished;
					}

				} );
				this.lastSpawn = game.currentTick;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	private IntegralSign createSign(Game game) {
		IntegralSign integralSign = new IntegralSign( true,
													  game.random.nextInt( VAR_WIDTH ) + MIN_WIDTH,
													  game.random.nextInt( VAR_HEIGHT ) + MIN_HEIGHT,
													  game.randomColor( 1.0f ), this
		);
		game.moveAndEnsureInBox( integralSign,
								 Game.WIDTH - integralSign.getWidth(),
								 game.random.nextInt( (int) Game.HEIGHT ), CONTAINING_BOX
		);
		game.scene.addEntity( Z_INDEX, integralSign );
		++this.aliveCount;
		return integralSign;
	}

	@Override
	public void onDestroy(Game game, IntegralSign sign) {
		--this.aliveCount;
		sign.setVisible( false );
		game.scene.removeEntity( Z_INDEX, sign );
	}

}
