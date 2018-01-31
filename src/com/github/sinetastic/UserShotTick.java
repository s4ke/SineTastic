package com.github.sinetastic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sinetastic.entities.Destructible;
import com.github.sinetastic.entities.Entity;
import com.github.sinetastic.entities.FxShot;
import com.github.sinetastic.entities.Ship;
import com.github.sinetastic.entities.Shot;

public class UserShotTick implements Game.TickListener, ShotTick {

	private static final double SHOT_SPEED = 0.25;
	private static final double SHOT_WIDTH = 50;
	private static final double SHOT_HEIGHT = 14;
	private static final int SHOT_STEPS = 42;
	private static final Color SHOT_COLOR = new Color( 0, 0x99, 0 );

	private static final int MAX_DELAY = 1000 * 1000 * 300;
	private static final int MIN_DELAY = 1000 * 1000 * 100;

	public long lastShot;
	private Set<Shot> shots = new HashSet<>();
	private List<Shot> removeShots = new ArrayList<>();
	private Polygon polygon;

	@Override
	public void tick(Game game) {
		for ( Shot shot : this.shots ) {
			{
				Area shotArea = new Area( shot.getCollisionShape() );
				shotArea.transform( AffineTransform.getTranslateInstance(
						shot.getX(), shot.getY() ) );
				UserShotTick.this.checkForCollision( game, shot, shotArea,
													 game.scene.getChildrenEntities()
				);
			}
		}
		int size = this.removeShots.size();
		for ( int i = 0; i < size; ++i ) {
			Shot shot = this.removeShots.remove( 0 );
			game.moveTick.remove( shot );
			game.scene.removeEntity( FxShot.Z_INDEX, shot );
			this.shots.remove( shot );
		}
		// trigger a new shot
		{
			boolean allowedToShoot = game.ship.isAlive()
					&& (((game.currentTick - this.lastShot) > MAX_DELAY) || (this.shots
					.size() == 0 && ((game.currentTick - this.lastShot) > MIN_DELAY)));
			if ( game.shotButton && allowedToShoot ) {
				final FxShot shot = this.createShipShot( game );
				game.enqueue.add( new SoundTick( game.shipShotSound, 500, game.fxVolume ) );
				game.moveTick.add( shot );
				this.lastShot = game.currentTick;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void removeShot(Shot shot) {
		this.removeShots.add( shot );
	}

	private FxShot createShipShot(Game game) {
		FxShot ret;
		if ( this.polygon == null ) {
			ret = new FxShot(
					false,
					SHOT_WIDTH,
					SHOT_HEIGHT,
					(x, position) -> x[0] = Game.sin( x[0] ),
					SHOT_STEPS,
					this
			);
			this.polygon = ret.getPolygon();
		}
		else {
			ret = new FxShot(
					false,
					SHOT_WIDTH,
					SHOT_HEIGHT,
					(x, position) -> x[0] = Game.sin( x[0] ),
					SHOT_STEPS,
					this,
					FxShot.DEFAULT_STROKE,
					this.polygon
			);
		}
		ret.setColor( SHOT_COLOR );
		ret.setSpeedX( SHOT_SPEED );
		ret.setX( game.ship.getX() + game.ship.getWidth() );
		ret.setY( game.ship.getY() );
		ret.setVisible( true );
		game.scene.addEntity( FxShot.Z_INDEX, ret );
		this.shots.add( ret );
		return ret;
	}

	private void checkForCollision(
			Game game, Shot shot, Area shotArea,
			Set<Entity> entities) {
		List<Destructible> toRemove = new ArrayList<>();
		Rectangle fastRect = new Rectangle(
				(int) shot.getWidth(),
				(int) shot.getHeight()
		);
		fastRect.translate( (int) shot.getX(), (int) shot.getY() );
		// check for collisions
		for ( Entity entity : entities ) {
			// only background and we are safe!
			if ( entity != shot && entity != game.background
					&& !(entity instanceof FxShot) && !(entity instanceof Ship) ) {
				if ( entity.canCollide() ) {
					Rectangle otherRect = new Rectangle(
							(int) entity.getWidth(), (int) entity.getHeight() );
					otherRect.translate(
							(int) entity.getX(),
							(int) entity.getY()
					);
					if ( fastRect.intersects( otherRect ) ) {
						Area otherArea = new Area( entity.getCollisionShape() );
						otherArea.transform( AffineTransform
													 .getTranslateInstance(
															 entity.getX(),
															 entity.getY()
													 ) );
						// this destroys the area so don't use the shotArea
						// we need this again for the other checks
						otherArea.intersect( shotArea );
						if ( !otherArea.isEmpty() ) {
							if ( entity instanceof Destructible ) {
								toRemove.add( (Destructible) entity );
							}
							this.removeShots.add( shot );
							break;
						}
					}
				}
			}
		}
		for ( Destructible entity : toRemove ) {
			if ( entity != null ) {
				entity.hit( game, this, 1 );
			}
		}
	}
}
