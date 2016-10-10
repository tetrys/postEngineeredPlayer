package lab.drys.postengin.turntable;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/24/.
 */
public class TurntableThread extends Thread
{
	protected TurntableThread(Turntable tbl, TurntableUpdater tblupd)
	{
		this.turntable = tbl;
		this.turntableUpdater = tblupd;

		this.running = false;
		this.updating = false;
		this.sleepTime = 50;

		this.lock = new Object();

		this.setPriority(Thread.MAX_PRIORITY);
	}

	@Override
	public void run()
	{
		running = true;

		while(running)
		{
			if(updating)
			{
				this.updateLoop();
			}
			else
			{
				turntableUpdater.serve(false);

				synchronized(lock)
				{
					try
					{
						lock.wait();
					}
					catch(InterruptedException xcpt)
					{
						PostEngin.logDebug(xcpt.getMessage());
					}
				}

				if(updating)
				{
					turntableUpdater.serve(true);
				}
			}
		}
	}

	public void halt()
	{
		running = false;

		synchronized(lock)
		{
			lock.notify();
		}

		try
		{
			this.join();
		}
		catch(InterruptedException xcpt)
		{
			PostEngin.logDebug(xcpt.getMessage());
		}

	}

	protected void updateLoop()
	{
		try
		{
			Thread.sleep(sleepTime);
		}
		catch(InterruptedException xcpt)
		{
			PostEngin.logDebug(xcpt.getMessage());
		}
	}

	protected void awake()
	{
		this.updating = true;

		synchronized(lock)
		{
			lock.notify();
		}
	}

	//Variables
	protected Turntable turntable;
	protected TurntableUpdater turntableUpdater;
	protected volatile boolean running;
	protected boolean updating;
	protected long sleepTime;
	protected final Object lock;
}
