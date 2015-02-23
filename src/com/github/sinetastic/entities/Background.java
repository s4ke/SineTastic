package com.github.sinetastic.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

public class Background extends BaseEntity {

	private final Rectangle collision;

	public Background(double width, double height) {
		super(false, width, height);
		this.collision = new Rectangle((int) width, (int) height);
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.fillRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
	}

	@Override
	public Shape getCollisionShape() {
		Rectangle clone = (Rectangle) this.collision.clone();
		clone.translate((int) this.getX(), (int) this.getY());
		return clone;
	}
	
}
