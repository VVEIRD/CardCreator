package vv3ird.populatecard.control;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import vv3ird.populatecard.gui.StatusListener;

public class TaskScheduler {
	
	private static int threadCount = 1;

	private static ExecutorService threads = Executors.newFixedThreadPool(1);

	private static Task activeTask = null;
	
	private static List<Future<?>> activeFutures = new ArrayList<>(1);
	
	private static boolean changeThreadCount = false;
	

	private static Queue<Task> queue = new ConcurrentLinkedQueue<>();

	private static Thread daemon = null;

	static {
		daemon = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						// Remove finished tasks
						List<Future<?>> oldFutures = new LinkedList<>();
						for (int i=0; i<activeFutures.size();i++) {
							Future<?> future = activeFutures.get(i); 
							if (future.isDone() || future.isCancelled()) {
								oldFutures.add(future);
							}
						}
						activeFutures.removeAll(oldFutures);
						// Add new tasks
						while(!changeThreadCount && activeFutures.size() < threadCount) {
							Task t = queue.poll();
							if (t != null) {
								Future<?> future = threads.submit(t.getPayload());
								activeTask = t;
								activeFutures.add(future);
							}
						}
						// Change thread count
						if (changeThreadCount && activeFutures.isEmpty()) {
							System.out.println("Changing thread count (" + threadCount + ")");
							threads = Executors.newFixedThreadPool(threadCount);
							activeFutures = new ArrayList<>(threadCount);
							System.out.println(activeFutures.size());
							changeThreadCount = false;
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
	
	public static void changeThreadCount(int threadCount) {
		System.out.println("Setting thread count (" + threadCount + ")");
		TaskScheduler.threadCount = threadCount > 0 ? threadCount : 1;
		TaskScheduler.changeThreadCount = true;
	}

}
