package lab.drys.postengin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.File;

import lab.drys.postengin.spieler.SpielerService;
import lab.drys.postengin.spieler.SpielerConnection;
import lab.drys.postengin.turntable.TurntableGLSurface;

/**
 * Created by lykanthrop on 1507/19/.
 */
public class MainActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_main);

		this.turntableGLSurface = (TurntableGLSurface) this.findViewById(R.id.turntable);
		this.rotationSwitch = (ImageButton) this.findViewById(R.id.rotation_switch);
		this.rotationSpeedSetter = (SeekBar) this.findViewById(R.id.speed_setter);
		this.progressGear = (ProgressBar) this.findViewById(R.id.progress_gear);
		this.curtain = this.findViewById(R.id.curtain);

		this.spielerConnection = new SpielerConnection();

		speedBarListener = new SpeedBarListener(spielerConnection);

		this.rotationSpeedSetter.setOnSeekBarChangeListener(speedBarListener);

		this.receiver = new MainActivityReceiver(this);

		this.commonPreferences = this.getSharedPreferences(PostEngin.COMMON_PREFERENCES,MODE_PRIVATE);

		if(!commonPreferences.getBoolean(PostEngin.ready,false))
		{
			this.firstRun();
		}

		this.firstCreation = true;

		Intent intent = new Intent(this,SpielerService.class);
		this.startService(intent);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		leftDrawer = new MainDrawer(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, receiver.getIntentFilter());

		Intent intent = new Intent(this,SpielerService.class);
		this.bindService(intent, spielerConnection, Context.BIND_AUTO_CREATE);

		this.init();
	}

	@Override
	public void onPause()
	{
		this.setBusy(true);

		this.unbindService(spielerConnection);

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		spielerConnection.halt();

		super.onDestroy();
	}

	public void setBusy(boolean bln)
	{
		if(bln)
		{
			curtain.setVisibility(View.VISIBLE);
			progressGear.setVisibility(View.VISIBLE);
		}
		else
		{
			try
			{
				Thread.sleep(500);
			}
			catch(InterruptedException xcpt)
			{
				PostEngin.logDebug(xcpt.getMessage());
			}

			progressGear.setVisibility(View.GONE);
			curtain.setVisibility(View.GONE);
		}
	}

	public void loadVinyl(String filepath)
	{
		turntableGLSurface.loadVinyl(filepath);
	}

	public void unloadVinyl()
	{
		turntableGLSurface.unloadVinyl();
	}

	public void init()
	{
		if(spielerConnection.isBound())
		{
			spielerConnection.setTurnTableGLSurface(turntableGLSurface);
			turntableGLSurface.initRenderer(spielerConnection.getTurntable(), spielerConnection.getTurntableUpdater());

			this.rotationSpeedSetter.setProgress(speedBarListener.getProgress());
			this.setSwitch();
		}
	}

	public void openDrawer()
	{
		leftDrawer.openDrawer();
	}

	public void closeDrawer(View vw)
	{
		leftDrawer.closeDrawer();
	}

	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.rotation_switch :

				spielerConnection.spinToggle();
				this.setSwitch();
				break;
		}
	}

	public void armToggle()
	{
		spielerConnection.armToggle();
	}

	private void firstRun()
	{
		SharedPreferences.Editor editor = commonPreferences.edit();
		editor.putBoolean(PostEngin.ready, true);
		editor.apply();

		File newFile = new File(this.getFilesDir().getAbsolutePath() + "/discs");
		if(newFile.mkdir())
		{

		}

		newFile = new File(this.getFilesDir().getAbsolutePath()+"/artists");

		if(newFile.mkdir())
		{

		}
	}
	private void setSwitch()
	{
		if(spielerConnection.isSpinning())
		{
			rotationSwitch.setImageResource(R.drawable.switch_1);
		}
		else
		{
			rotationSwitch.setImageResource(R.drawable.switch_0);
		}
	}

	//Variables
	private SharedPreferences commonPreferences;
	private SpielerConnection spielerConnection;
	private MainActivityReceiver receiver;
	private View curtain;
	private TurntableGLSurface turntableGLSurface;
	private ImageButton rotationSwitch;
	private SeekBar rotationSpeedSetter;
	private ProgressBar progressGear;
	private MainDrawer leftDrawer;
	private SpeedBarListener speedBarListener;
	private boolean firstCreation;
}
