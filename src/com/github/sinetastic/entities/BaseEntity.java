package com.github.sinetastic.entities;

import java.awt.Graphics2D;

public abstract class BaseEntity implements Entity {

	private double scale = 1.0;
	private double x;
	private double y;
	private double rotate;
	private double width;
	private double height;
	private final boolean canCollide;
	private boolean visible = true;

	public BaseEntity(boolean canCollide, double width, double height) {
		this.canCollide = canCollide;
		this.width = width;
		this.height = height;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public boolean canCollide() {
		return this.canCollide;
	}

	@Override
	public double getRotate() {
		return this.rotate;
	}

	@Override
	public void setRotate(double rotate) {
		this.rotate = rotate;
	}

	@Override
	public double getScale() {
		return this.scale;
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public double getWidth() {
		return this.width;
	}

	@Override
	public double getHeight() {
		return this.height;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public final void paint(Graphics2D g2d) {
		if (this.visible) {
			g2d = (Graphics2D) g2d.create();
			g2d.translate(x, y);
			g2d.scale(scale, scale);
			this.paintSub(g2d);
			g2d.translate(-x, -y);
			g2d.dispose();
		}
	}

	public abstract void paintSub(Graphics2D g2d);

}
