package com.github.sinetastic.entities;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

public class PythagorasBlob extends BaseEntity {
	
	private final Polygon polygon;

	public PythagorasBlob(boolean canCollide, double width, double height) {
		super(canCollide, width, height);
		this.polygon = new Polygon();
		
	}

	@Override
	public Shape getCollisionShape() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
	}

}
