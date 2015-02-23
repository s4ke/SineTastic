package com.github.sinetastic;

import com.github.sinetastic.entities.ProceduralWall;

public class BackGroundMoveTick implements Game.TickListener {

	private final ProceduralWall topWall;
	private final ProceduralWall botWall;
	private long lastShift;

	private static final int DELAY = 300;

	public BackGroundMoveTick(ProceduralWall topWall, ProceduralWall botWall) {
		super();
		this.topWall = topWall;
		this.botWall = botWall;
	}

	@Override
	public void tick(Game game) {
		boolean allowedToTick = game.diff(this.lastShift) > DELAY;
		if (allowedToTick) {
			this.topWall.shift(1);
			this.botWall.shift(1);
			this.lastShift = game.currentTick;
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
