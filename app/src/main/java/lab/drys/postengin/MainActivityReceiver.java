package lab.drys.postengin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by lykanthrop on 1507/19/.
 */
public class MainActivityReceiver extends BroadcastReceiver
{
	public MainActivityReceiver(MainActivity act)
	{
		super();

		this.activity = act;

		this.intentFilter = new IntentFilter(INIT_TABLE);
		this.intentFilter.addAction(OPEN_DRAWER);
		this.intentFilter.addAction(SPIELER_READY);
		this.intentFilter.addAction(ARM_TOGGLE);
	}

	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();

		switch(action)
		{
			case INIT_TABLE :

				activity.setBusy(false);

				break;

			case OPEN_DRAWER :

				activity.openDrawer();

				break;

			case SPIELER_READY :

				activity.init();

				break;

			case ARM_TOGGLE :

				activity.armToggle();

				break;

			case SET_VINYL :

				boolean on = intent.getBooleanExtra(PostEngin.ready,false);
				String str = intent.getStringExtra(PostEngin.disco);

				if(on)
				{
					activity.loadVinyl(str);
				}
				else
				{
					activity.unloadVinyl();
				}

				break;
		}
	}

	protected IntentFilter getIntentFilter()
	{
		return intentFilter;
	}

	//Variables
	private MainActivity activity;
	private IntentFilter intentFilter;
	public static final String INIT_TABLE = "lab.drys.postengineeredplayer.InitTable";
	public static final String OPEN_DRAWER = "lab.drys.postengineeredplayer.OpenDrawer";
	public static final String SPIELER_READY = "lab.drys.postengineeredplayer.SpielerReady";
	public static final String ARM_TOGGLE = "lab.drys.postengineeredplayer.ArmToggle";
	public static final String SET_VINYL = "lab.drys.postengineeredplayer.SetVilyn";
}
