package com.github.sinetastic.entities;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public interface Entity {

	public void paint(Graphics2D g2d);

	public void setX(double x);

	public void setY(double y);

	public double getX();

	public double getY();

	public double getWidth();

	public double getHeight();

	public void setScale(double scale);

	public double getScale();

	public boolean canCollide();

	public Shape getCollisionShape();

	public void setVisible(boolean visible);

	public boolean isVisible();

	/**
	 * ATTENTION: SLOW!
	 */
	public default boolean intersects(Entity other) {
		if (!this.canCollide()) {
			return false;
		}
		if (!other.canCollide()) {
			return false;
		}
		Area a = new Area(this.getCollisionShape());
		a.transform(AffineTransform.getTranslateInstance(this.getX(),
				this.getY()));
		Area b = new Area(other.getCollisionShape());
		b.transform(AffineTransform.getTranslateInstance(other.getX(),
				other.getY()));
		b.intersect(a);
		return !b.isEmpty();
	}

}
