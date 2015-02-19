package controller;

import java.util.concurrent.locks.ReentrantLock;

import model.GridModel;

public class ResourceManager implements Runnable {

	private GridModel model;
	private boolean interruptFlag = false;
	private ReentrantLock lock = new ReentrantLock();

	public ResourceManager(GridModel model) {
		this.model = model;
	}

	public void run() {
		boolean interrupt = false;
		while (!interrupt) {
			model.updateSquares();
			try {
				Thread.sleep(1000);
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
