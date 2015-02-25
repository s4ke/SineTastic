package com.github.sinetastic.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Rock extends BaseEntity {

	private Shape shape;
	private final Color color;
	private boolean alive = true;
	private final boolean fill;

	public Rock(boolean canCollide, double width, double height, Color color, boolean fill,
			boolean rect) {
		super(canCollide, width, height);
		if (rect) {
			this.shape = new Rectangle(0, 0, (int) width, (int) height);
		} else {
			this.shape = new Ellipse2D.Double(0, 0, width, height);
		}
		this.color = color;
		this.fill = fill;
	}

	@Override
	public Shape getCollisionShape() {
		return this.shape;
	}

	public boolean isAlive() {
		return this.alive;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(this.color);
		if (this.fill) {
			g2d.setStroke(new BasicStroke(5f));
			g2d.fill(this.shape);
		} else {
			g2d.draw(this.shape);
		}
	}

}
