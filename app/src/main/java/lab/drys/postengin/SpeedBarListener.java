package lab.drys.postengin;

import android.widget.SeekBar;

import lab.drys.postengin.spieler.SpielerConnection;

/**
 * Created by lykanthrop on 1507/21/.
 */
public class SpeedBarListener implements SeekBar.OnSeekBarChangeListener
{
	public SpeedBarListener(SpielerConnection scn)
	{
		spielerConnection = scn;
		speed = spielerConnection.getSpeed();
		if(speed==0)
		{
			speed = 0.025f;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		speed = 0.025f+progress*0.00125f;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		spielerConnection.setSpeed(speed);
	}

	public int getProgress()
	{
		return (int)((speed-0.025f)/0.00125f);
	}

	//Variables
	private float speed;
	private SpielerConnection spielerConnection;
}
