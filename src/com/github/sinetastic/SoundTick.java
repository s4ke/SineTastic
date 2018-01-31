package com.github.sinetastic;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundTick implements Game.TickListener {

	private final Clip clip;
	private final FloatControl floatControl;
	private final long lengthMs;
	private boolean finished = false;
	private long soundStartedAt;

	public SoundTick(Clip clip, long lengthMs, float volume) {
		this.clip = clip;
		this.lengthMs = lengthMs;
		this.floatControl = (FloatControl) this.clip.getControl( FloatControl.Type.MASTER_GAIN );
		setVolume( this.floatControl, volume );
	}

	private static void setVolume(FloatControl control, float level) {
		float range = control.getMaximum() - control.getMinimum();
		float gain = (range * level) + control.getMinimum();
		control.setValue(gain);
	}

	@Override
	public void tick(Game game) {
		// trigger a new shot
		{
			if ( this.soundStartedAt == 0 ) {
				this.soundStartedAt = game.currentTick;
				this.clip.setFramePosition( 0 );
				this.clip.loop( 0 );
				this.clip.start();
			}
			if ( game.diff( this.soundStartedAt ) >= this.lengthMs ) {
				this.clip.loop( 0 );
				this.finished = true;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return this.finished;
	}

}
