package vv3ird.populatecard.control;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import vv3ird.populatecard.gui.StatusListener;

public class TaskScheduler {

	private static ExecutorService threads = Executors.newFixedThreadPool(1);

	private static Task activeTask = null;
	
	private static Future<?> activeFuture = null;

	private static Queue<Task> queue = new ConcurrentLinkedQueue<>();

	private static Thread daemon = null;

	static {
		daemon = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						if (activeFuture == null || activeFuture.isDone()) {
							Task t = queue.poll();
							if (t != null) {
								activeFuture = threads.submit(t.getPayload());
							}
							activeTask = t;
						}
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		daemon.setDaemon(true);
		daemon.start();
	}
	
	public void  addTask(Task t) {
		t = Objects.requireNonNull(t);
		queue.add(t);
	}
	
	public static void addTask(String description, Runnable payload, StatusListener listener) {
		queue.add(new Task(description, payload, listener));
	}
	
	public static Task getActiveTask() {
		return activeTask;
	}
	
	public static Task[] getQueue() {
		return queue.toArray(new Task[0]);
	}
	
	public static void removeTask(Task task) {
		queue.remove(task);
	}

	public static boolean hasActiveTask() {
		return getActiveTask() != null;
	}

}
