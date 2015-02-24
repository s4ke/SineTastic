package com.github.sinetastic.entities;

import com.github.sinetastic.Game;

public interface MoveableEntity extends Entity {
	
	public static interface MoverCallback {
		
		public void remove(MoveableEntity entity);
		
	}
	
	public double getSpeedX();
	
	public double getSpeedY();
	
	public void moved(Game game);

}
