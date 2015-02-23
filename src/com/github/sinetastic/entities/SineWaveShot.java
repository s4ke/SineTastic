package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.function.BiFunction;

public class SineWaveShot extends BaseEntity {

	private Polygon polygon;
	private final double[] tmp;
	private final double[] tmp2;
	private final int steps;
	private Color color = Color.RED;

	public SineWaveShot(boolean canCollide, double width, double height,
			BiFunction<double[], double[], Void> fx, int steps) {
		super(canCollide, width, height);
		this.tmp = new double[1];
		this.tmp2 = new double[1];
		this.polygon = new Polygon();
		this.steps = steps;
		double dX = width / this.steps;
		for (int i = 0; i < this.steps; ++i) {
			this.tmp[0] = i;
			this.tmp2[0] = dX * i;
			fx.apply(this.tmp, this.tmp2);
			double val = this.tmp[0] * height / 2;
			this.polygon.addPoint((int) (dX * i),
					(int) (height / 2 - val));
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

}
