package com.github.sinetastic;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import com.github.sinetastic.entities.Entity;
import com.github.sinetastic.entities.Ship;

public class UserShipTick implements Game.TickListener {

	private static final long INVINCIBLE_TIME = 500;

	private static final double SHIP_SPEED_X = 0.17;
	private static final double SHIP_SPEED_Y = 0.17;

	@Override
	public void tick(Game game) {
		if (game.ship.isAlive()) {
			// move the ship
			{
				double dX = 0;
				double dY = 0;
				if (game.left) {
					dX -= game.tdT(SHIP_SPEED_X);
				}
				if (game.right) {
					dX += game.tdT(SHIP_SPEED_X);
				}
				if (game.up) {
					dY -= game.tdT(SHIP_SPEED_Y);
				}
				if (game.down) {
					dY += game.tdT(SHIP_SPEED_Y);
				}
				game.moveAndEnsureInScene(game.ship, dX, dY);
			}
			if (game.diff(game.getShipSpawnedTick()) > INVINCIBLE_TIME) {
				game.ship.setVincible();
				Ship ship = game.ship;
				if (ship.canCollide()) {
					Area shipArea = new Area(ship.getCollisionShape());
					shipArea.transform(AffineTransform.getTranslateInstance(
							ship.getX(), ship.getY()));
					this.checkForCollision(game, ship, shipArea,
							game.scene.getChildrenEntities());
				}
			} else {
				game.ship.setInvincible();
			}
		} else {
			if (game.reviveButton) {
				game.respawnShip();
			}
		}
	}

	private void checkForCollision(Game game, Ship ship, Area shipArea,
			Iterable<Entity> entities) {
		Rectangle fastRect = new Rectangle((int) ship.getWidth(),
				(int) ship.getHeight());
		fastRect.translate((int) ship.getX(), (int) ship.getY());
		// check for collisions
		for (Entity entity : entities) {
			// only background and we are safe!
			if (entity != ship && entity != game.background) {
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
						// this destroys the area so don't use the shipArea
						// we need this again for the other checks
						otherArea.intersect(shipArea);
						if (!otherArea.isEmpty()) {
							game.shipCrashed();
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
