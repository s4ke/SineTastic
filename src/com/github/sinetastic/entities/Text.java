package com.github.sinetastic.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import com.github.sinetastic.Game;

public class Text extends BaseEntity {

	private String text;

	private final Color color;
	private final Font font;

	public Text(Color color, int size) {
		super(false, 0, 0);
		this.color = color;
		this.font = Game.BASE_FONT.deriveFont(Font.PLAIN, size);
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
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(this.color);
		g2d.setFont(this.font);
		g2d.drawString(this.text, 0, 0);
	}

}
