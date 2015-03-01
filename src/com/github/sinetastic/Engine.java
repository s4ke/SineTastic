package com.github.sinetastic;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import com.github.sinetastic.scene.ScaleScene;

public class Engine {

	private Game game;
	private final JFrame frame;
	private final ScaleScene scene;
	private final Thread logicThread;

	public long frames = 0;
	public long lastFpsTime = 0;

	private static final int TIME_BETWEEN_TICKS_MS = 5;
	private static final int TIME_BETWEEN_REPAINTS_MS = 10;

	public Engine() throws MidiUnavailableException, LineUnavailableException,
			IOException, UnsupportedAudioFileException {
		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLocationRelativeTo(null);

		this.scene = new ScaleScene(6);
		this.frame.setName("Rocktastic!");
		this.frame.setTitle("RockTastic!");
		this.frame.getContentPane().add(this.scene);
		this.scene.setPreferredSize(new Dimension((int) Game.WIDTH,
				(int) Game.HEIGHT));
		this.scene.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {
				double newWidth = Engine.this.scene.getWidth();
				double newHeight = Engine.this.scene.getHeight();
				Engine.this.scene.setScaleX(newWidth / Game.WIDTH);
				Engine.this.scene.setScaleY(newHeight / Game.HEIGHT);
			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}

		});

		this.game = new Game(this.scene);
		this.game.setup();

		this.frame.addKeyListener(this.game);
		this.logicThread = new Thread(new Runnable() {

			private long previousTick;
			private long currentTick = System.currentTimeMillis();

			@Override
			public void run() {
				while (true) {
					Engine.this.game.tick();
					this.previousTick = this.currentTick;
					this.currentTick = System.currentTimeMillis();
					long timeSpanSinceLastTick = this.currentTick
							- this.previousTick;
					if (timeSpanSinceLastTick < TIME_BETWEEN_TICKS_MS) {
						try {
							Thread.sleep(TIME_BETWEEN_TICKS_MS
									- timeSpanSinceLastTick);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}

		});
	}

	public void show() {
		this.frame.pack();
		this.frame.setVisible(true);
		this.frame.repaint();
	}

	public void cycle() {
		this.logicThread.start();
		long currentRepaint = 0;
		long previousRepaint = 0;
		while (true) {
			if (++this.frames > 200) {
				long currentTime = System.nanoTime() / 1000 / 1000 / 1000;
				if (this.lastFpsTime > 0) {
					double fps = (((double) this.frames) / (currentTime - this.lastFpsTime));
					System.out.println(new StringBuilder().append(fps)
							.append(" fps").toString());
				}
				this.frames = 0;
				this.lastFpsTime = currentTime;
			}
			Engine.this.frame.repaint();
			previousRepaint = currentRepaint;
			currentRepaint = System.currentTimeMillis();
			long timeSpanSinceLastFrame = currentRepaint - previousRepaint;
			if (timeSpanSinceLastFrame < TIME_BETWEEN_REPAINTS_MS) {
				try {
					Thread.sleep(TIME_BETWEEN_REPAINTS_MS - timeSpanSinceLastFrame);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static void main(String[] args) throws MidiUnavailableException,
			LineUnavailableException, IOException,
			UnsupportedAudioFileException {
		Engine engine = new Engine();
		engine.show();
		for (int i = 0; i < 100; ++i) {
			for (float d = 0; d < Math.PI; d += 0.00001f) {
				Game.sin(d);
			}
		}
		engine.cycle();
	}
}
