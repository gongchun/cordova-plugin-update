package com.n22.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ThreadPool {

	static ExecutorService executorService;

	public synchronized static void excute(Runnable runnable) {
		if (executorService == null) {
			executorService = Executors.newScheduledThreadPool(3);
		}
		executorService.execute(runnable);
	}

	public static ExecutorService getExecutorService() {
		return executorService;
	}
}
