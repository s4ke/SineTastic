package com.github.sinetastic;

import javax.sound.sampled.Clip;

public class SoundTick implements Game.TickListener {
	
	private final Clip clip;
	private final long lengthMs;
	private boolean finished = false;
	private long soundStartedAt;

	public SoundTick(Clip clip, long lengthMs) {
		this.clip = clip;
		this.lengthMs = lengthMs;
	}

	@Override
	public void tick(Game game) {
		// trigger a new shot
		{
			if (this.soundStartedAt == 0) {
				this.soundStartedAt = game.currentTick;
				this.clip.setFramePosition(0);
				this.clip.loop(0);
				this.clip.start();
			}
			if (game.diff(this.soundStartedAt) >= this.lengthMs) {
				this.clip.loop(0); 
				this.finished = true;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return this.finished;
	}

}
