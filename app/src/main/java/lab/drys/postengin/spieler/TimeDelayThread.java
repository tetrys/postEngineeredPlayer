package lab.drys.postengin.spieler;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1508/04/.
 */
public class TimeDelayThread extends Thread
{
	public TimeDelayThread()
	{
		sleepTime = 1000;
		free = true;
		normalRelease = true;

		lock0 = new Object();
	}

	public TimeDelayThread(long sltm)
	{
		sleepTime = sltm;
		free = true;
		normalRelease = true;

		lock0 = new Object();
	}

	public TimeDelayThread(long sltm, TimeDelayInterface tdi)
	{
		sleepTime = sltm;
		free = true;
		normalRelease = true;

		lock0 = new Object();

		tdobject = tdi;
	}

	@Override
	public void run()
	{
		running = true;

		while(running)
		{
			if(free)
			{
				synchronized(lock0)
				{
					try
					{
						lock0.wait();
					}
					catch(InterruptedException xcpt)
					{
						PostEngin.logDebug(xcpt.getMessage());
					}
				}
			}

			if(running)
			{
				free = false;
				normalRelease = true;

				synchronized(lock0)
				{
					try
					{
						lock0.wait(sleepTime);
						if(normalRelease)
						{
							if(tdobject!=null)
							{
								tdobject.action();
							}

							free = true;
						}
					}
					catch(InterruptedException xcpt)
					{
						PostEngin.logDebug(xcpt.getMessage());
					}
				}
			}
		}
	}

	public void halt()
	{
		running = false;

		synchronized(lock0)
		{
			lock0.notify();
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

	public void hit()
	{
		normalRelease = false;
		free = false;

		synchronized(lock0)
		{
			lock0.notify();
		}
	}

	public boolean isFree()
	{
		return free;
	}
	//Variables
	private long sleepTime;
	private final Object lock0;
	private volatile boolean running;
	private boolean free;
	private boolean normalRelease;
	private TimeDelayInterface tdobject;
}
