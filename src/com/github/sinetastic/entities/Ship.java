package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Random;

public class Ship extends BaseEntity {

	private static final Random random = new Random();

	private Polygon polygon;
	private boolean alive = true;

	public Ship(double width, double height) {
		super(true, width, height);
		this.polygon = new Polygon();
		this.polygon.addPoint(0, 0);
		this.polygon.addPoint((int) width, (int) (height / 2));
		this.polygon.addPoint(0, (int) height);
		this.polygon.addPoint((int) (width / 2), (int) (height / 2));
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(Color.RED);
		g2d.fillPolygon(this.polygon);
	}

	public void explode() {
		if (this.alive) {
			this.polygon = new Polygon();
			for (int i = 0; i < random.nextInt(15) + 3; ++i) {
				this.polygon.addPoint(
						random.nextInt((int) this.getWidth()) * 3,
						random.nextInt((int) this.getHeight()) * 2);
			}
			this.alive = false;
		}
	}
	
	public boolean isAlive() {
		return this.alive;
	}

	@Override
	public Shape getCollisionShape() {
		return this.polygon;
	}

}