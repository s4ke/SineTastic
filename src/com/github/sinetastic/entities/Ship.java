package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.github.sinetastic.Game;

public class Ship extends BaseEntity implements Destructible {
	
	private static final BufferedImage IMAGE;
	static {
		try {
			IMAGE = ImageIO.read(Ship.class.getResource("/com/github/sinetastic/assets/rocketbeans/rocket.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final Random random = new Random();

	private Polygon polygon;
	private Rectangle lazyHitBox;
	private boolean alive;
	private Color color;

	public Ship(double width, double height) {
		super(true, width, height);
		this.lazyHitBox = new Rectangle((int) width, (int) height);
		this.rebuild();
		this.setVincible();
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		if(this.alive) {
			g2d.drawImage(IMAGE, 0, 0, null);
		} else {
			g2d.setColor(this.color);
			g2d.fillPolygon(this.polygon);
		}
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
		this.alive = true;
	}

	public boolean isAlive() {
		return this.alive;
	}

	@Override
	public Shape getCollisionShape() {
		return this.lazyHitBox;
	}

	@Override
	public void hit(Game game, Object source, double damage) {

	}

	@Override
	public void destroy(Game game) {

	}

}
