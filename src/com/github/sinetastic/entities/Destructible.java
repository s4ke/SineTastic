package com.github.sinetastic.entities;

import com.github.sinetastic.Game;

public interface Destructible {
	
	public void hit(Game game, Object source, double damage);
	
	public void destroy(Game game);

}
