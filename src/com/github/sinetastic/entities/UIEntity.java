package com.github.sinetastic.entities;

import java.awt.*;

/**
 * @author Martin Braun
 * @version 1.1.0
 * @since 1.1.0
 */
public abstract class UIEntity extends ClickableEntity {

	public UIEntity(double width, double height) {
		super( false, width, height );
	}

	@Override
	public final Shape getCollisionShape() {
		throw new UnsupportedOperationException();
	}

	public abstract Shape getClickShape();

}
