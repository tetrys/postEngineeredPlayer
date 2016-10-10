package lab.drys.postengin.spieler;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;
import lab.drys.postengin.dj.DJActivityReceiver;
import lab.drys.postengin.turntable.Turntable;
import lab.drys.postengin.turntable.TurntableGLSurface;
import lab.drys.postengin.turntable.TurntableSetter;
import lab.drys.postengin.turntable.TurntableSpinner;
import lab.drys.postengin.turntable.TurntableUpdater;

/**
 * Created by lykanthrop on 1507/23/.
 */
public class SpielerService extends IntentService implements TimeDelayInterface, AudioManager.OnAudioFocusChangeListener
{
	public SpielerService()
	{
		super("Spieler");

		spielerBinder = new SpielerBinder(this);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		init = false;
		serving = false;

		turntable = new Turntable();
		turntable.setThumb(this.getResources().getDisplayMetrics().density*40f);
		turntableUpdater = new TurntableUpdater();
		turntableSpinner = new TurntableSpinner(turntable,turntableUpdater);
		turntableSetter = new TurntableSetter(turntable,turntableUpdater);

		timeDelayThread = new TimeDelayThread(5000,this);

		spieler0 = new Spieler(this);
		spieler1 = new Spieler(this);
		spieler0.setSwapSpieler(spieler1);
		spieler1.setSwapSpieler(spieler0);

		current = null;

		repeat = REPEAT.NONE;

		localBroadcastManager = LocalBroadcastManager.getInstance(this);

		AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
		{

		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.init();

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		if(turntableSpinner!=null)
		{
			turntableSpinner.halt();
			turntableSpinner = null;
		}

		if(turntableSetter!=null)
		{
			turntableSetter.halt();
			turntableSetter = null;
		}

		if(turntableUpdater!=null)
		{
			turntableUpdater.halt();
			turntableUpdater = null;
		}

		if(timeDelayThread!=null)
		{
			timeDelayThread.halt();
			timeDelayThread = null;
		}

		current = null;
		spieler0.release();
		spieler1.release();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		super.onBind(intent);

		return spielerBinder;
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		COMS com = COMS.values()[intent.getIntExtra(COMMAND,0)];

		switch(com)
		{
			case NONE:
				break;

			case PLAY:
				this.play();
				break;

			case PAUSE:
				this.pause();
				break;

			case TOGGLE:
				this.togglePlay();
				break;

			case STOP:
				this.stop();
				break;

			case PREVIOUS:
				this.skipPrevious();
				break;

			case NEXT:
				this.skipNext();
				break;

			case REPEATONE:
				break;

			case REPEATALL:

				break;
		}
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		turntableUpdater.setTurntableGLSurface(null);

		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent)
	{
		super.onRebind(intent);
	}

	@Override
	public void onAudioFocusChange(int focusChange)
	{
		switch (focusChange)
		{
			case AudioManager.AUDIOFOCUS_GAIN:
				this.play();
				if(current!=null)
				{
					current.setVolume(1f,1f);
				}
				break;

			case AudioManager.AUDIOFOCUS_LOSS:
				this.stop();
				this.halt();
				break;

			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				this.pause();
				break;

			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				if(current!=null)
				{
					current.setVolume(0.1f,0.1f);
				}
				break;
		}
	}

	@Override
	public void action()
	{
		if(current!=null)
		{
			current.prepareSwap();
		}
	}

	private void init()
	{
		if(!init)
		{
			Notification notification = new NotificationCompat.Builder(this)
					.setContentTitle("Post-Engineered Player")
					.setSmallIcon(R.mipmap.ic_stat_notify)
					.build();

			this.startForeground(SpielerID, notification);

			if(timeDelayThread.getState()==Thread.State.NEW)
			{
				turntableSetter.start();
			}

			if(timeDelayThread.getState()==Thread.State.NEW)
			{
				turntableSpinner.start();
			}

			if(timeDelayThread.getState()==Thread.State.NEW)
			{
				turntableUpdater.start();
			}

			if(timeDelayThread.getState()==Thread.State.NEW)
			{
				timeDelayThread.start();
			}

			init = true;
		}
	}

	public void setTurntableGLSurface(TurntableGLSurface tgls)
	{
		turntableUpdater.setTurntableGLSurface(tgls);
	}

	public void setSpeed(float f)
	{
		turntableSpinner.setSpeed(f);
	}

	public void halt()
	{
		if(!serving)
		{
			this.stopForeground(true);
			this.stopSelf();
		}
	}

	public void spinOn()
	{
		turntableSpinner.spinOn();
	}

	public void spinOff()
	{
		turntableSpinner.spinOff();
	}

	public void spinToggle()
	{
		turntableSpinner.spinToggle();
	}

	public void armToggle()
	{
		turntableSetter.toggle();
	}

	public void play()
	{
		if(paused && current!=null)
		{
			current.start();
			localBroadcastManager.sendBroadcast(intent_update_icons);
		}
	}

	public void pause()
	{
		if(!paused && current!=null && current.isPlaying())
		{
			current.pause();
			localBroadcastManager.sendBroadcast(intent_update_icons);
		}
	}

	public void togglePlay()
	{
		if(current!=null)
		{
			if(current.isPlaying())
			{
				current.pause();
				localBroadcastManager.sendBroadcast(intent_update_icons);
			}
			else if(paused)
			{
				current.start();
				localBroadcastManager.sendBroadcast(intent_update_icons);
			}
			else
			{
				current.prepare();
			}
		}
		else
		{
			this.update();

			if(current!=null)
			{
				current.prepare();
			}
		}

		turntable.setVinyl(true);
	}

	public void skipNext()
	{
		if(current!=null)
		{
			if(current.isPlaying())
			{
				current.stop();

				if(PostEngin.playQueue.potentialNext()!=null)
				{
					PostEngin.playQueue.forward();
					current.prepare();
				}
			}
		}

		timeDelayThread.hit();
	}

	public void skipPrevious()
	{
		if(timeDelayThread.isFree())
		{
			if(current!=null)
			{
				current.restart();
			}
		}
		else if(current!=null)
		{
			current.stop();
			if(PostEngin.playQueue.potentialPrevious()!=null)
			{
				PostEngin.playQueue.backward();
				current.prepare();
			}
		}

		timeDelayThread.hit();
	}

	public void leap(int index)
	{
		if(current!=null)
		{
			if(current.isPlaying())
			{
				current.stop();
			}

			PostEngin.playQueue.set(index);
			current.prepare();
		}
		else
		{
			current=spieler0;
			PostEngin.playQueue.set(index);
			current.prepare();
		}
	}

	public void stop()
	{
		if(current!=null)
		{
			current.stop();
			current = null;
		}

		serving = false;
		paused = false;

		localBroadcastManager.sendBroadcast(intent_update_icons);
		localBroadcastManager.sendBroadcast(intent_update_list);

		PostEngin.playQueue.hardReset();
		turntable.setVinyl(false);
	}

	public void toggleRepeat()
	{
		if(repeat==REPEAT.NONE)
		{
			repeat = REPEAT.ONE;
		}
		else if(repeat==REPEAT.ONE)
		{
			repeat = REPEAT.ALL;
		}
		else if(repeat==REPEAT.ALL)
		{
			repeat = REPEAT.NONE;
		}

		localBroadcastManager.sendBroadcast(intent_update_icons);
	}

	public Turntable getTurntable()
	{
		return turntable;
	}

	public TurntableUpdater getTurntableUpdater()
	{
		return turntableUpdater;
	}

	public float getSpeed()
	{
		return turntableSpinner.getSpeed();
	}

	public boolean isSpinning()
	{
		return turntableSpinner.isSpinning();
	}

	public boolean isPlaying()
	{
		return serving && (!paused);
	}

	public REPEAT getRepetition()
	{
		return repeat;
	}

	protected void currentReady()
	{
		serving = true;
		paused = false;

		localBroadcastManager.sendBroadcast(intent_update_list);
		localBroadcastManager.sendBroadcast(intent_update_icons);
	}

	protected void playEnd()
	{
		serving = false;
		paused = false;

		current = null;

		localBroadcastManager.sendBroadcast(intent_update_list);
		localBroadcastManager.sendBroadcast(intent_update_icons);

		PostEngin.playQueue.hardReset();
		turntable.setVinyl(false);
	}

	protected void swap()
	{
		if(current==spieler0)
		{
			current = spieler1;
		}
		else if(current==spieler1)
		{
			current = spieler0;
		}

		localBroadcastManager.sendBroadcast(intent_update_list);
		timeDelayThread.hit();
	}

	private void update()
	{
		if(PostEngin.playQueue.isEmpty())
		{
			if(current!=null)
			{
				current.stop();
				current = null;
			}
		}
		else
		{
			if(current==null)
			{
				current = spieler0;
				PostEngin.playQueue.reset();
			}
		}
	}

	//Variables
	private static boolean init;

	private final SpielerBinder spielerBinder;

	protected boolean serving;
	protected boolean paused;

	private Spieler current;
	private TimeDelayThread timeDelayThread;

	private Spieler spieler0;
	private Spieler spieler1;

	private Turntable turntable;
	private TurntableUpdater turntableUpdater;
	private TurntableSpinner turntableSpinner;
	private TurntableSetter turntableSetter;

	private LocalBroadcastManager localBroadcastManager;

	public static final String COMMAND = "com";
	private static final int SpielerID = 1000;;

	public enum COMS{NONE, PLAY, PAUSE, TOGGLE, STOP, NEXT, PREVIOUS, REPEATONE, REPEATALL}
	public enum REPEAT{NONE,ONE,ALL}

	public REPEAT repeat;

	private static final Intent intent_update_list = new Intent(DJActivityReceiver.UPDATE_LIST);
	private static final Intent intent_update_icons = new Intent(DJActivityReceiver.UPDATE_ICONS);
}
