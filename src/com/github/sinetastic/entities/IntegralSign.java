package com.github.sinetastic.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;

import com.github.sinetastic.Game;

public class IntegralSign extends BaseEntity implements Destructible {

	private static final double RESOLUTION = 64;
	private static final Stroke STROKE = new BasicStroke(2,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private final Color color;
	private final Polygon polygon;
	private final Callback callback;

	public static interface Callback {

		public void onDestroy(Game game, IntegralSign sign);

	}

	public IntegralSign(boolean canCollide, double width, double height,
			Color color, Callback callback) {
		super(canCollide, width, height);
		this.polygon = new Polygon();
		// upper part of the sign
		for (double arc = Math.PI * 1 / 4; arc <= Math.PI; arc += (Math.PI / RESOLUTION)) {
			double x = Math.cos(arc) * width / 2 + width * 3 / 2;
			double y = (-1) * (Math.sin(arc) * height / 2) + height / 2;
			// half the x because width is for the whole sign
			this.polygon.addPoint((int) x / 2, (int) y);
		}
		for (double arc = Math.PI; arc >= Math.PI * 1 / 4; arc -= (Math.PI / RESOLUTION)) {
			double x = (-1) * (Math.cos(arc) * width / 2) + width / 2;
			double y = Math.sin(arc) * height / 2 + height / 2;
			// half the x because width is for the whole sign
			this.polygon.addPoint((int) x / 2, (int) y);
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
		g2d.setStroke(STROKE);
		g2d.setColor(color);
		g2d.drawPolyline(this.polygon.xpoints, this.polygon.ypoints,
				this.polygon.npoints);
	}

	@Override
	public void hit(Game game, Object source, double damage) {
		this.destroy(game);
	}

	@Override
	public void destroy(Game game) {
		this.callback.onDestroy(game, this);
	}

}
