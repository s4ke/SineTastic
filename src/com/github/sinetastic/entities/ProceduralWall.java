package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.function.BiFunction;

public class ProceduralWall extends BaseEntity {

	private final boolean top;
	private final int steps;
	private final double[] tmp;
	private final double[] tmp2;
	private final BiFunction<double[], double[], Void> function;
	private final double dX;
	private final Polygon functionPolygon;
	private final Polygon boxPolygon;
	private Color color;

	public ProceduralWall(boolean canCollide, double width, double height,
			BiFunction<double[], double[], Void> function, int steps,
			Color color, boolean top) {
		super(canCollide, width, height);
		if (steps < 2) {
			throw new IllegalArgumentException("at least 2 steps are expected!");
		}
		this.tmp = new double[1];
		this.tmp2 = new double[1];
		this.function = function;
		this.steps = steps;
		this.dX = width / this.steps;
		this.functionPolygon = new Polygon();
		this.boxPolygon = new Polygon();
		this.color = color;
		this.top = top;
		this.initPolygon();
	}

	private void initPolygon() {
		if (this.top) {
			this.boxPolygon.addPoint(0, 0);
		}
		// add one more to fill the polygon up correctly
		for (int i = 0; i <= this.steps; ++i) {
			Point2D point = this.calcPoint(i);
			this.functionPolygon.addPoint((int) point.getX(),
					(int) point.getY());
			this.boxPolygon.addPoint((int) point.getX(), (int) point.getY());
		}
		// and add the rest of the box
		if (this.top) {
			this.boxPolygon.addPoint((int) this.getWidth(), 0);
		} else {
			this.boxPolygon.addPoint((int) this.getWidth(),
					(int) this.getHeight());
			this.boxPolygon.addPoint(0, (int) this.getHeight());
		}
	}

	private Point2D calcPoint(int index) {
		this.tmp[0] = index;
		this.tmp2[0] = this.dX * index;
		this.function.apply(this.tmp, this.tmp2);
		double val = this.tmp[0] * this.getHeight();
		return new Point((int) (this.dX * index),
				(int) (this.getHeight() - val));
	}

	private int totalInc = 0;

	public void shift(int stepIncrement) {
		this.totalInc += stepIncrement;
		for (int j = 0; j < stepIncrement; ++j) {
			for (int i = 0; i < this.steps; ++i) {
				this.functionPolygon.ypoints[i] = this.functionPolygon.ypoints[i + 1];

				int boxIndex = i;
				if (this.top) {
					boxIndex += 1;
				}
				this.boxPolygon.ypoints[boxIndex] = this.boxPolygon.ypoints[boxIndex + 1];
			}
			int newIndex = this.steps + this.totalInc;
			Point2D newPoint = this.calcPoint(newIndex);
			int bla = this.steps;
			if (this.top) {
				bla += 1;
			}
			this.functionPolygon.ypoints[this.steps] = (int) newPoint.getY();
			this.boxPolygon.ypoints[bla] = (int) newPoint.getY();
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public Shape getCollisionShape() {
		return this.boxPolygon;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(this.color);
		g2d.fillPolygon(this.boxPolygon.xpoints, this.boxPolygon.ypoints,
				this.boxPolygon.npoints);
	}

}
