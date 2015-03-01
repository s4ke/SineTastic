package com.github.sinetastic.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.function.BiFunction;

import com.github.sinetastic.Game;

public class FxShot extends BaseEntity implements MoveableEntity, Shot {

	public static final int Z_INDEX = 2;

	public static Stroke DEFAULT_STROKE = new BasicStroke(1.6f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	private Polygon polygon;
	private final double[] tmp = new double[1];
	private final double[] tmp2 = new double[1];
	private Color color = Color.RED;
	private final ShotCallback shotCallback;
	private double speedX;
	private double speedY;
	private final Stroke stroke;

	public FxShot(boolean canCollide, double width, double height,
			BiFunction<double[], double[], Void> fx, int steps,
			ShotCallback shotCallback, Stroke stroke, Polygon polygon) {
		super(canCollide, width, height);
		this.stroke = stroke;
		this.shotCallback = shotCallback;
		this.polygon = polygon;
	}

	public FxShot(boolean canCollide, double width, double height,
			BiFunction<double[], double[], Void> fx, int steps,
			ShotCallback shotCallback) {
		this(canCollide, width, height, fx, steps, shotCallback, DEFAULT_STROKE);
	}

	public FxShot(boolean canCollide, double width, double height,
			BiFunction<double[], double[], Void> fx, int steps,
			ShotCallback shotCallback, Stroke stroke) {
		super(canCollide, width, height);
		this.stroke = stroke;
		this.shotCallback = shotCallback;
		this.polygon = FxUtil.makeArea(FxUtil.createPolygonFromFunction(fx,
				steps, width, height, this.tmp, this.tmp2), steps, 0, 1);
	}
	
	public Polygon getPolygon() {
		return this.polygon;
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
		g2d = (Graphics2D) g2d.create();
		g2d.setStroke(this.stroke);
		g2d.setColor(this.color);
		g2d.drawPolyline(this.polygon.xpoints, this.polygon.ypoints,
				this.polygon.npoints);
		g2d.dispose();
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
		this.shotCallback.removeShot(this);
	}

}
