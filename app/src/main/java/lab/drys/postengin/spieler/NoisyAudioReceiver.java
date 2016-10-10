package lab.drys.postengin.spieler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by lykanthrop on 1508/01/.
 */
public class NoisyAudioReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
		{
			Intent intentOut = new Intent(context,SpielerService.class);
			intentOut.putExtra(SpielerService.COMMAND, SpielerService.COMS.PAUSE);
		}
	}
}
