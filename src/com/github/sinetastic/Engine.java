package com.github.sinetastic;

import java.awt.Dimension;
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
	// 60 FPS
	private static final int TIME_BETWEEN_FRAMES_MS = 5;

	public Engine() throws MidiUnavailableException, LineUnavailableException,
			IOException, UnsupportedAudioFileException {
		this.frame = new JFrame();
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLocationRelativeTo(null);

		this.scene = new ScaleScene(6);
		this.scene.setPreferredSize(new Dimension(600, 400));
		this.frame.setName("Sinetastic!");
		this.frame.getContentPane().add(this.scene);

		this.game = new Game(this.scene);
		this.game.setup();

		this.frame.addKeyListener(this.game);
		this.logicThread = new Thread(new Runnable() {

			private long previousDraw;
			private long currentDraw = System.nanoTime() / 1000 / 1000;

			@Override
			public void run() {
				while (true) {
					Engine.this.game.tick();
					Engine.this.scene.repaint();
					this.previousDraw = this.currentDraw;
					this.currentDraw = System.nanoTime() / 1000 / 1000;
					long timeSpanSinceLastFrame = this.currentDraw
							- this.previousDraw;
					if (timeSpanSinceLastFrame < TIME_BETWEEN_FRAMES_MS) {
						try {
							Thread.sleep(TIME_BETWEEN_FRAMES_MS
									- timeSpanSinceLastFrame);
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
	}

	public static void main(String[] args) throws MidiUnavailableException,
			LineUnavailableException, IOException,
			UnsupportedAudioFileException {
		Engine engine = new Engine();
		engine.show();
		engine.cycle();
	}

}
