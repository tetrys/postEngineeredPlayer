package lab.drys.postengin.turntable;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/26/.
 */
public class TurntableUpdater extends Thread
{
	public TurntableUpdater()
	{
		running = false;
		updating = false;
		lock = new Object();
		sleepTime = 10;

		this.setPriority(Thread.MAX_PRIORITY);
	}

	public void setTurntableGLSurface(TurntableGLSurface tbls)
	{
		if(tbls!=null)
		{
			turntableGLSurface = tbls;

			updateSwitch();
		}
		else
		{
			updating = false;
			turntableGLSurface = null;
		}
	}

	@Override
	public void run()
	{
		running = true;

		while(running)
		{
			if(updating)
			{
				turntableGLSurface.requestRender();

				try
				{
					Thread.sleep(sleepTime);
				}
				catch(InterruptedException xcpt)
				{
					PostEngin.logDebug(xcpt.getMessage());
				}
			}
			else
			{
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

	public void serve(boolean bl)
	{
		if(bl)
		{
			clients++;
		}
		else
		{
			clients--;
			if(clients<0)
			{
				clients = 0;
			}
		}

		updateSwitch();
	}

	public void updateSwitch()
	{
		if((clients>0) && (!updating))
		{
			updating = true;

			synchronized(lock)
			{
				lock.notify();
			}
		}
		else if(updating && (clients==0))
		{
			updating = false;
		}
	}

	//Variables
	private TurntableGLSurface turntableGLSurface;
	private volatile boolean running;
	private boolean updating;
	private final Object lock;
	private long sleepTime;
	private int clients;
}
