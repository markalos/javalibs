

class DaemonThread implements Runnable {
	private volatile boolean isAlive;
	private volatile boolean pauseThread;
	private Thread mThread;
	private Action action;

	DaemonThread(Action action) {
		mThread = new Thread(this);
		this.action = action;
		isAlive = true;
		mThread.start();
	}

	synchronized public void restart() {
		pauseThread = false;
		notify();
	}

	synchronized public void pause() {
		pauseThread = true;
		notify();
	}

	synchronized public void finish() {
		isAlive = false;
		pauseThread = true;
		notify();
	}

	public interface Action {
		void act();
	}

	@Override
	public void run() {
		try {
			while (isAlive) {
				
				if (pauseThread) {
					synchronized (this) {
						while (pauseThread && isAlive) {
							wait();
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
		for (int i = 0; i < 3; ++i) {
			
		}
		mThread.restart();
		mThread.pause();
		mThread.finish();
		System.out.println("end test");
	}
}