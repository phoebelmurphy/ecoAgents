package controller;

import java.util.concurrent.locks.ReentrantLock;

import model.GridModel;
import model.GridRowModel;
import model.GridSquareModel;

public class ResourceManager implements Runnable {

	private GridModel model;
	private boolean interruptFlag = false;
	private ReentrantLock lock = new ReentrantLock();

	public ResourceManager(GridModel model) {
		this.model = model;
	}

	public void run() {
		boolean interrupt = false;
		int y = model.getY();
		int x = model.getX();
		while (!interrupt) {
			for (int i = 0; i < y; i++) {
				GridRowModel currentRow = model.getRow(i);
				for (int n = 0; n < x; n++) {
					GridSquareModel currentSquare = currentRow.getSquare(n);
					currentSquare.lock();
					currentSquare.growGrass();
					currentSquare.unlock();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.lock();
			interrupt = interruptFlag;
			lock.unlock();
		}
	}

	/**
	 * Sets an interrupt flag to tell the running thread to stop.
	 */
	public void stop() {
		lock.lock();
		interruptFlag = true;
		lock.unlock();

	}

}
