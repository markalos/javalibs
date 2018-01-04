

class DaemonThread implements Runnable {
	private volatile boolean isAlive;
	private volatile boolean pauseThread;
	private Thread mThread;
	private Action action;

	DaemonThread(Action action) {
		mThread = new Thread(this);
		System.out.println(action);
		this.action = action;
		mThread.start();
	}

	synchronized public void start() {
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
		isAlive = true;
		try {
			System.out.println(isAlive);
			while (isAlive) {
				
				if (pauseThread) {
					System.out.println("pauseThread " + pauseThread);
					synchronized (this) {
						while (pauseThread && isAlive) {
							System.out.println("isAlive " + isAlive);
							wait();
						}
						
					}
					System.out.println("end if");
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
		mThread.pause();
		mThread.finish();
		System.out.println("end test");
	}
}