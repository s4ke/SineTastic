package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.Random;

import com.github.sinetastic.Game;

public class Ship extends BaseEntity implements Destructible {

	private static final Random random = new Random();

	private Polygon polygon;
	private boolean alive;
	private Color color;

	public Ship(double width, double height) {
		super(true, width, height);
		this.polygon = new Polygon();
		this.rebuild();
		this.setVincible();
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(this.color);
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
	
	public void setInvincible() {
		this.color = Color.BLUE;
	}
	
	public void setVincible() {
		this.color = Color.RED;
	}

	public void rebuild() {
		this.polygon = new Polygon();
		this.polygon.addPoint(0, 0);
		this.polygon.addPoint((int) this.getWidth(),
				(int) (this.getHeight() / 2));
		this.polygon.addPoint(0, (int) this.getHeight());
		this.polygon.addPoint((int) (this.getWidth() / 2),
				(int) (this.getHeight() / 2));
		this.alive = true;
	}

	public boolean isAlive() {
		return this.alive;
	}

	@Override
	public Shape getCollisionShape() {
		return this.polygon;
	}

	@Override
	public void hit(Game game, Object source, double damage) {

	}

	@Override
	public void destroy(Game game) {

	}

}
