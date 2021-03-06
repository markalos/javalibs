

class DaemonThread implements Runnable {
	private volatile boolean keepRuning;
	private volatile boolean pauseThread;
	private Thread mThread;
	private Action action;

	DaemonThread(Action action) {
		mThread = new Thread(this);
		this.action = action;
		keepRuning = true;
	}

	synchronized public void start() {
		if (mThread.getState() == Thread.State.NEW) {
			mThread.start();
			System.out.println("start the Thread " + mThread);
		} else {
			System.out.println(mThread + " has been started");
			pauseThread = false;
			notify();
		}
	}

	synchronized public void pause() {
		pauseThread = true;
		notify();
	}

	synchronized public void finish() {
		keepRuning = false;
		pauseThread = true;
		notify();
	}

	public interface Action {
		void act();
	}

	@Override
	public void run() {
		try {
			while (keepRuning) {
				if (pauseThread) {
					synchronized (this) {
						while (pauseThread && keepRuning) {
							wait();
							System.out.println("wait");
						}
					}
				} else {
					action.act();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DaemonThread mThread = new DaemonThread(new DaemonThread.Action() {
			@Override
			public void act() {
				System.out.println("enter act function");
				
			}
		});

		mThread.start();
		mThread.start();
		mThread.finish();
		System.out.println("end test");
	}
}