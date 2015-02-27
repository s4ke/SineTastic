package com.github.sinetastic.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;

import com.github.sinetastic.Game;

public class IntegralSign extends BaseEntity implements Destructible {

	private static final int POINTS_PER_SIGN = 50;
	
	private static final double RESOLUTION = 64;
	private static final double PAINT_POLY_SCALE = 10;
	private static final Stroke STROKE = new BasicStroke(
			(int) (2 * PAINT_POLY_SCALE), BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER);
	
	private final Color color;
	private final Polygon polygon;
	private final Polygon paintPolygon;
	private final Callback callback;

	public static interface Callback {

		public void onDestroy(Game game, IntegralSign sign);

	}

	public IntegralSign(boolean canCollide, double width, double height,
			Color color, Callback callback) {
		super(canCollide, width, height);
		this.polygon = new Polygon();
		this.paintPolygon = new Polygon();
		// upper part of the sign
		for (double arc = Math.PI * 1 / 4; arc <= Math.PI; arc += (Math.PI / RESOLUTION)) {
			double x = Math.cos(arc) * width / 2 + width * 3 / 2;
			double y = (-1) * (Math.sin(arc) * height / 2) + height / 2;
			// half the x because width is for the whole sign
			this.polygon.addPoint((int) x / 2, (int) y);
			this.paintPolygon.addPoint((int) (x / 2 * PAINT_POLY_SCALE),
					(int) (y * PAINT_POLY_SCALE));
		}
		for (double arc = Math.PI; arc >= Math.PI * 1 / 4; arc -= (Math.PI / RESOLUTION)) {
			double x = (-1) * (Math.cos(arc) * width / 2) + width / 2;
			double y = Math.sin(arc) * height / 2 + height / 2;
			// half the x because width is for the whole sign
			this.polygon.addPoint((int) x / 2, (int) y);
			this.paintPolygon.addPoint((int) (x / 2 * PAINT_POLY_SCALE),
					(int) (y * PAINT_POLY_SCALE));
		}
		// make this an area!
		for (int i = this.polygon.npoints - 1; i >= 0; --i) {
			this.polygon.addPoint(this.polygon.xpoints[i] + 1,
					this.polygon.ypoints[i] + 1);
		}
		this.color = color;
		this.callback = callback;
	}

	@Override
	public Shape getCollisionShape() {
		return this.polygon;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d = (Graphics2D) g2d.create();
		g2d.scale(1 / PAINT_POLY_SCALE, 1 / PAINT_POLY_SCALE);
		g2d.setStroke(STROKE);
		g2d.setColor(color);
		g2d.drawPolyline(this.paintPolygon.xpoints, this.paintPolygon.ypoints,
				this.paintPolygon.npoints);
		g2d.dispose();
	}

	@Override
	public void hit(Game game, Object source, double damage) {
		this.destroy(game);
	}

	@Override
	public void destroy(Game game) {
		this.callback.onDestroy(game, this);
		game.addPoints(POINTS_PER_SIGN);
	}

}
