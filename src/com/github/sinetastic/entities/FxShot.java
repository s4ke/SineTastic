package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.function.BiFunction;

import com.github.sinetastic.Game;

public class FxShot extends BaseEntity implements MoveableEntity, Shot {

	private Polygon polygon;
	private final double[] tmp;
	private final double[] tmp2;
	private final int steps;
	private Color color = Color.RED;
	private final ShotCallback shotCallback;
	private double speedX;
	private double speedY;

	public FxShot(boolean canCollide, double width, double height,
			BiFunction<double[], double[], Void> fx, int steps,
			ShotCallback shotCallback) {
		super(canCollide, width, height);
		this.tmp = new double[1];
		this.tmp2 = new double[1];
		this.polygon = new Polygon();
		this.shotCallback = shotCallback;
		this.steps = steps;
		double dX = width / this.steps;
		for (int i = 0; i < this.steps; ++i) {
			this.tmp[0] = i;
			this.tmp2[0] = dX * i;
			fx.apply(this.tmp, this.tmp2);
			double val = this.tmp[0] * height / 2;
			this.polygon.addPoint((int) (dX * i), (int) (height / 2 - val));
		}
		// make this an area!
		for (int i = 0; i < this.steps; ++i) {
			this.polygon.addPoint(this.polygon.xpoints[i] + 1,
					this.polygon.ypoints[i] + 1);
		}
	}

	@Override
	public Shape getCollisionShape() {
		return this.polygon;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(this.color);
		g2d.drawPolyline(this.polygon.xpoints, this.polygon.ypoints,
				this.polygon.npoints);
	}

	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}

	@Override
	public double getSpeedX() {
		return this.speedX;
	}

	@Override
	public double getSpeedY() {
		return this.speedY;
	}

	@Override
	public void moved(Game game) {
		double newX = this.getX();
		double newY = this.getY();
		boolean leftScreen = false;
		if (newX < 0) {
			leftScreen = true;
		}
		if (newX >= Game.WIDTH) {
			leftScreen = true;
		}
		if (newY < 0) {
			leftScreen = true;
		}
		if (newY >= Game.HEIGHT) {
			leftScreen = true;
		}
		if (leftScreen) {
			this.destroy(game);
		}
	}

	@Override
	public void hit(Game game, Object source, double damage) {
		this.destroy(game);
	}

	@Override
	public void destroy(Game game) {
		this.setVisible(false);
		game.scene.removeEntity(2, this);
		game.moveTick.remove(this);
		if (this.shotCallback != null) {
			this.shotCallback.removeShot(this);
		}
	}

}
