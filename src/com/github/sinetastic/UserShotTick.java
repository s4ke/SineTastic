package com.github.sinetastic;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.sinetastic.Game.TickListener;
import com.github.sinetastic.entities.Entity;
import com.github.sinetastic.entities.Explodeable;
import com.github.sinetastic.entities.Ship;
import com.github.sinetastic.entities.SineWaveShot;

public class UserShotTick implements Game.TickListener {

	private static final double SHOT_SPEED = 0.25;
	private static final double SHOT_WIDTH = 50;
	private static final double SHOT_HEIGHT = 14;
	private static final int SHOT_STEPS = 42;

	private static final int DELAY = 1000 * 1000 * 300;

	public long lastShot;
	public long shotCount;

	@Override
	public void tick(Game game) {
		// trigger a new shot
		{
			boolean allowedToShoot = game.ship.isAlive()
					&& ((game.currentTick - this.lastShot) > DELAY || shotCount <= 0);
			if (game.shotButton && allowedToShoot) {
				final SineWaveShot shot = this.createShipShot(game);
				game.enqueue.add(new TickListener() {

					private boolean finished = false;

					@Override
					public void tick(Game game) {
						{
							Area shotArea = new Area(shot.getCollisionShape());
							shotArea.transform(AffineTransform
									.getTranslateInstance(shot.getX(),
											shot.getY()));
							UserShotTick.this.checkForCollision(game, shot,
									shotArea, game.scene.getChildrenEntities());
						}
						// move the shot
						{
							if (shot.isVisible()) {
								double dX = game.tdT(SHOT_SPEED);
								shot.setX(shot.getX() + dX);
								if (!game.background.getCollisionShape()
										.contains(shot.getX(), shot.getY())) {
									shot.setVisible(false);
									game.scene.removeEntity(2, shot);
									--UserShotTick.this.shotCount;
									this.finished = true;
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
				game.enqueue.add(new SoundTick(game.shipShotSound, 500));
				this.lastShot = game.currentTick;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	private SineWaveShot createShipShot(Game game) {
		SineWaveShot ret = new SineWaveShot(true, SHOT_WIDTH, SHOT_HEIGHT, (x,
				position) -> {
			x[0] = Math.sin(x[0]);
			return null;
		}, SHOT_STEPS);
		ret.setColor(Color.GREEN);
		ret.setX(game.ship.getX() + game.ship.getWidth());
		ret.setY(game.ship.getY());
		ret.setVisible(true);
		game.scene.addEntity(2, ret);
		++this.shotCount;
		return ret;
	}

	private void checkForCollision(Game game, Entity shot, Area shotArea,
			Set<Entity> entities) {
		List<Entity> toRemove = new ArrayList<>();
		Rectangle fastRect = new Rectangle((int) shot.getWidth(),
				(int) shot.getHeight());
		fastRect.translate((int) shot.getX(), (int) shot.getY());
		// check for collisions
		for (Entity entity : entities) {
			// only background and we are safe!
			if (entity != shot && entity != game.background
					&& !(entity instanceof SineWaveShot)
					&& !(entity instanceof Ship)) {
				if (entity.canCollide()) {
					Rectangle otherRect = new Rectangle(
							(int) entity.getWidth(), (int) entity.getHeight());
					otherRect.translate((int) entity.getX(),
							(int) entity.getY());
					if (fastRect.intersects(otherRect)) {
						Area otherArea = new Area(entity.getCollisionShape());
						otherArea.transform(AffineTransform
								.getTranslateInstance(entity.getX(),
										entity.getY()));
						// this destroys the area so don't use the shotArea
						// we need this again for the other checks
						otherArea.intersect(shotArea);
						if (!otherArea.isEmpty()) {
							if (entity instanceof Explodeable) {
								toRemove.add(entity);
							}
							toRemove.add(shot);
							--UserShotTick.this.shotCount;
							break;
						}
					}
				}
			}
		}
		for (Entity entity : toRemove) {
			if (entity instanceof Explodeable) {
				((Explodeable) entity).explode();
			}
			entity.setVisible(false);
			game.scene.removeEntity(2, entity);
		}
	}
}
