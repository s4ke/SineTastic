package com.github.sinetastic.scene;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import com.github.sinetastic.entities.Entity;

public class ScaleScene extends JPanel {

	private final ReentrantLock lock = new ReentrantLock();

	private static final long serialVersionUID = -6316148973815286571L;
	private double scale = 1.0d;

	private Set<Entity>[] entities;
	private Set<Entity> allEntities;

	public ScaleScene(int zSize) {
		super(null);
		this.setDoubleBuffered(true);
		this.entities = new Set[zSize];
		for (int i = 0; i < this.entities.length; ++i) {
			this.entities[i] = new HashSet<>();
		}
		this.allEntities = new HashSet<>();
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void addEntity(int z, Entity entity) {
		this.lock.lock();
		try {
			ScaleScene.this.entities[z].add(entity);
			ScaleScene.this.allEntities.add(entity);
		} finally {
			this.lock.unlock();
		}
	}

	public void removeEntity(int z, Entity entity) {
		this.lock.lock();
		try {
			ScaleScene.this.entities[z].remove(entity);
			ScaleScene.this.allEntities.remove(entity);
		} finally {
			this.lock.unlock();
		}
	}

	public Set<Entity> getChildrenEntities() {
		return Collections.unmodifiableSet(this.allEntities);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(this.scale, this.scale);
		super.paintComponent(g);
		this.lock.lock();
		try {
			for (int i = this.entities.length - 1; i >= 0; --i) {
				for (Entity entity : this.entities[i]) {
					entity.paint(g2d);
				}
			}
		} finally {
			this.lock.unlock();
		}
	}

}