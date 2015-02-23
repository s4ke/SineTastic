package com.github.sinetastic;

import com.github.sinetastic.entities.ProceduralWall;

public class BackGroundMoveTick implements Game.TickListener {

	private final ProceduralWall topWall;
	private final ProceduralWall botWall;
	private static final Long COLOR_CHANGE_INTERVAL = 3L;

	private long lastShift;
	private long colorChangeCount = 0;

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
			if (this.colorChangeCount++ % COLOR_CHANGE_INTERVAL == 0L) {
				this.topWall.setColor(game.randomColor(1.0f));
				this.botWall.setColor(game.randomColor(1.0f));
			}
			this.lastShift = game.currentTick;
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
