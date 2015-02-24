package com.github.sinetastic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sinetastic.entities.MoveableEntity;
import com.github.sinetastic.entities.MoveableEntity.MoverCallback;

public class MoveTick implements Game.TickListener, MoverCallback {

	private Set<MoveableEntity> entitiesToMove = new HashSet<>();
	public List<MoveableEntity> enqueue = new ArrayList<>();
	public List<MoveableEntity> dequeue = new ArrayList<>();

	@Override
	public void tick(Game game) {
		// move
		{
			for (MoveableEntity entity : this.entitiesToMove) {
				this.move(game, entity);
			}
		}
		// dequeue
		{
			MoveableEntity entity = null;
			while (this.dequeue.size() > 0
					&& (entity = this.dequeue.remove(0)) != null) {
				this.entitiesToMove.remove(entity);
			}
		}
		// enqueue
		{
			MoveableEntity entity = null;
			while (this.enqueue.size() > 0
					&& (entity = this.enqueue.remove(0)) != null) {
				this.entitiesToMove.add(entity);
			}
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	public void add(MoveableEntity entity) {
		this.enqueue.add(entity);
	}

	@Override
	public void remove(MoveableEntity entity) {
		this.dequeue.add(entity);
	}

	private void move(Game game, MoveableEntity entity) {
		double dX = game.tdT(entity.getSpeedX());
		double dY = game.tdT(entity.getSpeedY());
		double newX = entity.getX() + dX;
		double newY = entity.getY() + dY;

		entity.setX(newX);
		entity.setY(newY);
		entity.moved(game);
	}

}
