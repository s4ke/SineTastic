package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

public class Background extends BaseEntity {

	private static final Color PAPER_COLOR = new Color(50, 50, 50, 30);
	private static final Color LINE_COLOR = new Color(0, 0, 255, 30);
	private static final int BOX_SIZE = 20;
	private final Rectangle collision;

	public Background(double width, double height) {
		super(false, width, height);
		this.collision = new Rectangle((int) width, (int) height);
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(PAPER_COLOR);
		g2d.fillRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
		for (int x = 0; x < this.getWidth(); x += BOX_SIZE) {
			g2d.setColor(LINE_COLOR);
			g2d.drawLine(x, 0, x, (int) this.getHeight());
		}
		for (int y = 0; y < this.getHeight(); y += BOX_SIZE) {
			g2d.setColor(LINE_COLOR);
			g2d.drawLine(0, y, (int) this.getWidth(), y);
		}
	}

	@Override
	public Shape getCollisionShape() {
		Rectangle clone = (Rectangle) this.collision.clone();
		clone.translate((int) this.getX(), (int) this.getY());
		return clone;
	}

}
