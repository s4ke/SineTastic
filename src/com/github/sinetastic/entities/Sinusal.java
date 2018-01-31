package com.github.sinetastic.entities;

import java.awt.*;

import com.github.sinetastic.Game;

public class Sinusal extends BaseEntity {

	private final Polygon polygon;
	private final Color color;

	public Sinusal(boolean canCollide, double width, double height, Color color) {
		super( canCollide, width, height );
		double[] tmp = new double[1];
		double[] tmp2 = new double[1];
		this.polygon = FxUtil.makeArea(
				FxUtil.createPolygonFromFunction(
						(x, position) -> x[0] = Game.sin( position[0] ),
						150,
						width,
						height,
						tmp,
						tmp2
				), 150, 1, 2 );
		this.color = color;
	}

	@Override
	public Shape getCollisionShape() {
		return this.polygon;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d = (Graphics2D) g2d.create();
		g2d.setColor( color );
		g2d.fillPolygon( this.polygon );
		g2d.dispose();
	}

}
