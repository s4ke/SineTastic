package com.github.sinetastic.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import com.github.sinetastic.Game;

public class Face extends BaseEntity implements Destructible {

	private static final int POINTS_PER_FACE = 100;

	private final BufferedImage image;
	private final Callback callback;
	private final Rectangle lazyHitbox;

	public static interface Callback {

		public void onDestroy(Game game, Face face);

	}

	public Face(boolean canCollide, double width, double height,
			BufferedImage image, Callback callback) {
		super(canCollide, width, height);
		this.image = image;
		this.callback = callback;
		this.lazyHitbox = new Rectangle((int) width, (int) height);
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
		game.addPoints(POINTS_PER_FACE);
	}
	
}
