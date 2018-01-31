package com.github.sinetastic.entities;

/**
 * @author Martin Braun
 * @version 1.1.0
 * @since 1.1.0
 */
public abstract class ClickableEntity extends BaseEntity {

	public ClickableEntity(boolean canCollide, double width, double height) {
		super( canCollide, width, height );
	}

}
