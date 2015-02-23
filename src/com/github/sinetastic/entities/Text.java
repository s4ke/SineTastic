package com.github.sinetastic.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

public class Text extends BaseEntity {

	private String text;
	private final Color color;

	public Text(double width, double height, Color color) {
		super(false, width, height);
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public Shape getCollisionShape() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor(this.color);
		g2d.drawString(this.text, 0, 0);
	}

}
