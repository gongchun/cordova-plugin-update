package com.n22.thread;


public abstract class SafeThread implements Runnable {

	public Object value;

	public SafeThread() {

	}

	public SafeThread(Object value) {
		this.value = value;
	}

	public abstract void deal();

	@Override
	public void run() {
		try {
			deal();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
