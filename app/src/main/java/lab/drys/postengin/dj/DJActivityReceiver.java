package lab.drys.postengin.dj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1508/02/.
 */
public class DJActivityReceiver extends BroadcastReceiver
{
	public DJActivityReceiver(DJActivity djact)
	{
		this.djActivity = djact;

		this.intentFilter = new IntentFilter(UPDATE_LIST);
		this.intentFilter.addAction(UPDATE_ICONS);
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();

		switch(action)
		{
			case UPDATE_LIST:
				djActivity.updateListView();
				break;
			case UPDATE_ICONS:
				djActivity.togglePlayIcon();
				break;
		}
	}

	protected IntentFilter getIntentFilter()
	{
		return intentFilter;
	}

	//Variables
	private DJActivity djActivity;
	private IntentFilter intentFilter;
	public static final String UPDATE_LIST = "lab.drys.postengineeredplayer.UpdatePlayQueue";
	public static final String UPDATE_ICONS = "lab.drys.postengineeredplayer.UpdateIcons";
}
