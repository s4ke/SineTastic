package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.function.BiFunction;

import com.github.sinetastic.Game;

public class SineWaveShot extends BaseEntity implements MoveableEntity, Shot {

	private static final double SHOT_SPEED = 0.25;
	private Polygon polygon;
	private final double[] tmp;
	private final double[] tmp2;
	private final int steps;
	private Color color = Color.RED;
	private final ShotCallback shotCallback;

	public SineWaveShot(boolean canCollide, double width, double height,
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
	}

	@Override
	public Shape getCollisionShape() {
		if (!this.canCollide()) {
			throw new IllegalStateException();
		}
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

	@Override
	public double getSpeedX() {
		return SHOT_SPEED;
	}

	@Override
	public double getSpeedY() {
		return 0;
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
		game.userShotTick.removeShot(this);
		game.moveTick.remove(this);
		this.shotCallback.removeShot(this);
	}
	
}
