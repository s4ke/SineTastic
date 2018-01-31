package com.github.sinetastic.entities;

import java.awt.*;

/**
 * @author Martin Braun
 * @version 1.1.0
 * @since 1.1.0
 */
public class ImageButton extends UIEntity {

	private final Rectangle rect;
	private final Image image;

	public ImageButton(Image image, double width, double height) {
		super( width, height );
		this.image = image;
		this.rect = new Rectangle( (int) width, (int) height );
	}

	@Override
	public Shape getClickShape() {
		return this.rect;
	}

	@Override
	public void paintSub(Graphics2D g2d) {
		g2d.setColor( Color.CYAN );
		g2d.drawImage( image, 0, 0, this.rect.width, this.rect.height, null);
	}

}
