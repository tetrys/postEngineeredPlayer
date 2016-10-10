package lab.drys.postengin.discotheque;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by lykanthrop on 1507/27/.
 */
public class DiscothequePagerAdapter extends FragmentPagerAdapter
{
	public DiscothequePagerAdapter(FragmentManager fm)
	{
		super(fm);

		fragments = new Fragment[4];

		fragments[0] = new FragmentGridArtists();
		fragments[1] = new FragmentGridAlbums();
		fragments[2] = new FragmentListTracks();
		fragments[3] = new FragmentGridPlaylists();
	}

	@Override
	public int getCount()
	{
		return 4;
	}

	@Override
	public Fragment getItem(int position)
	{
		return fragments[position];
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		String out = "Tab";

		switch (position)
		{
			case 0 :
				out = "Artists";
				break;
			case 1 :
				out = "Albums";
				break;
			case 2 :
				out = "Songs";
				break;
			case 3 :
				out = "Playlists";
				break;
		}

		return out;
	}

	//Variables
	private Fragment[] fragments;
}
