package lab.drys.postengin.spieler;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import lab.drys.postengin.MainActivityReceiver;
import lab.drys.postengin.PostEngin;
import lab.drys.postengin.turntable.Turntable;
import lab.drys.postengin.turntable.TurntableGLSurface;
import lab.drys.postengin.turntable.TurntableUpdater;

/**
 * Created by lykanthrop on 1507/23/.
 */
public class SpielerConnection implements ServiceConnection
{
	public SpielerConnection()
	{
		super();
		spielerService = null;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		SpielerBinder spielerBinder = (SpielerBinder)service;

		if(spielerService==null)
		{
			spielerService = spielerBinder.spielerService;

			Intent intent = new Intent(MainActivityReceiver.SPIELER_READY);
			LocalBroadcastManager.getInstance(spielerService).sendBroadcast(intent);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		spielerService = null;
	}

	public void halt()
	{
		if(spielerService!=null)
		{
			spielerService.halt();
		}
	}

	public void togglePlay()
	{
		if(spielerService!=null)
		{
			spielerService.togglePlay();
		}
	}

	public void skipNext()
	{
		if(spielerService!=null)
		{
			spielerService.skipNext();
		}
	}

	public void leap(int index)
	{
		spielerService.leap(index);
	}

	public void skipPrevious()
	{
		if(spielerService!=null)
		{
			spielerService.skipPrevious();
		}
	}

	public void stop()
	{
		if(spielerService!=null)
		{
			spielerService.stop();
		}
	}

	public void spinOn()
	{
		if(spielerService!=null)
		{
			spielerService.spinOn();
		}
	}

	public void spinOff()
	{
		if(spielerService!=null)
		{
			spielerService.spinOff();
		}
	}

	public void spinToggle()
	{
		if(spielerService!=null)
		{
			spielerService.spinToggle();
		}
	}

	public boolean isSpinning()
	{
		if(spielerService!=null)
		{
			return spielerService.isSpinning();
		}
		else
		{
			return false;
		}
	}

	public void armToggle()
	{
		if(spielerService!=null)
		{
			spielerService.armToggle();
		}
	}

	public void setSpeed(float f)
	{
		if(spielerService!=null)
		{
			spielerService.setSpeed(f);
		}
	}

	public void setTurnTableGLSurface(TurntableGLSurface tbl)
	{
		if(spielerService!=null)
		{
			spielerService.setTurntableGLSurface(tbl);
		}
	}

	public Turntable getTurntable()
	{
		Turntable turntable = null;

		if(spielerService!=null)
		{
			turntable = spielerService.getTurntable();
		}

		return turntable;
	}

	public TurntableUpdater getTurntableUpdater()
	{
		TurntableUpdater turntableUpdater = null;

		if(spielerService!=null)
		{
			turntableUpdater = spielerService.getTurntableUpdater();
		}

		return turntableUpdater;
	}

	public float getSpeed()
	{
		if(spielerService!=null)
		{
			return spielerService.getSpeed();
		}
		else
		{
			return 0;
		}
	}

	public boolean isBound()
	{
		return spielerService!=null;
	}

	public boolean isPlaying()
	{
		return spielerService!=null && spielerService.isPlaying();
	}

	//Variables
	private SpielerService spielerService;
}
