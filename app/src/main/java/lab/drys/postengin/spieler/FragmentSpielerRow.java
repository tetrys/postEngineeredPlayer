package lab.drys.postengin.spieler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1508/10/.
 */
public class FragmentSpielerRow extends Fragment
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View vw = inflater.inflate(R.layout.fragment_spieler_0, container, false);
		playIcon = (ImageButton)vw.findViewById(R.id.dj_play);
		return vw;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	public void togglePlayIcon(boolean bln)
	{
		if(bln)
		{
			playIcon.setImageResource(R.drawable.ic_pause_black_48dp);
		}
		else
		{
			playIcon.setImageResource(R.drawable.ic_play_arrow_black_48dp);
		}
	}

	//Variables
	SpielerConnection spielerConnection;
	ImageButton playIcon;
}
