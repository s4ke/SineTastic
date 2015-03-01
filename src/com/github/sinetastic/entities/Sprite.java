package com.github.sinetastic.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import com.github.sinetastic.Game;

public class Sprite extends BaseEntity implements Destructible {
	
	private final int points;
	private final BufferedImage image;
	private final Callback callback;
	private final Rectangle lazyHitbox;

	public static interface Callback {

		public void onDestroy(Game game, Sprite face);

	}

	public Sprite(boolean canCollide, double width, double height,
			BufferedImage image, Callback callback, int points) {
		super(canCollide, width, height);
		this.image = image;
		this.callback = callback;
		this.lazyHitbox = new Rectangle((int) width, (int) height);
		this.points = points;
	}

	@Override
	public Shape getCollisionShape() {
		return this.lazyHitbox;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.drawImage(this.image, 0, 0, null);
	}

	@Override
	public void hit(Game game, Object source, double damage) {
		this.destroy(game);
	}

	@Override
	public void destroy(Game game) {
		this.callback.onDestroy(game, this);
		game.addPoints(this.points);
	}
	
}
