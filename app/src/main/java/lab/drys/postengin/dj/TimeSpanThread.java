package lab.drys.postengin.dj;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.spieler.TimeDelayInterface;

/**
 * Created by lykanthrop on 1508/04/.
 */
public class TimeSpanThread extends Thread
{
	public TimeSpanThread()
	{
		sleepTime = 1000;
		free = true;
		normalRelease = true;

		lock0 = new Object();
	}

	public TimeSpanThread(long sltm)
	{
		sleepTime = sltm;
		free = true;
		normalRelease = true;

		lock0 = new Object();
	}

	public TimeSpanThread(long sltm, TimeSpanInterface tdi)
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
				if(tdobject!=null)
				{
					tdobject.cancelAction();
				}

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
				free = true;
				normalRelease = false;

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
		normalRelease = true;
		free = false;

		synchronized(lock0)
		{
			lock0.notify();
		}
	}

	public void rehit()
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
	private TimeSpanInterface tdobject;
}
