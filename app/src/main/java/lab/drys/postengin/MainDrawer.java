package lab.drys.postengin;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import lab.drys.postengin.discotheque.Discotheque;
import lab.drys.postengin.vinylcamera.VinylCamera;

/**
 * Created by lykanthrop on 6/3/15.
 */
public class MainDrawer implements ListView.OnItemClickListener, DrawerLayout.DrawerListener
{
	public MainDrawer(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;

		drawerItems = new ArrayList<>();
		drawerItems.add(new DrawerItem("Settings",R.drawable.ic_settings_applications_black_48dp));
		drawerItems.add(new DrawerItem("Library",R.drawable.ic_library_music_black_48dp));
		drawerItems.add(new DrawerItem("Vinyl Camera",R.drawable.ic_photo_camera_black_48dp));
		drawerItems.add(new DrawerItem("Oldschool",R.drawable.ic_album_black_48dp));
		drawerItems.add(new DrawerItem("About",R.drawable.ic_info_outline_black_48dp));

		drawerAdapter = new DrawerAdapter(mainActivity, drawerItems);

		this.drawerLayout = (DrawerLayout)mainActivity.findViewById(R.id.drawer_layout);
		this.drawerList = (ListView)mainActivity.findViewById(R.id.left_drawer_list);

		this.drawerList.setAdapter(drawerAdapter);
		this.drawerList.setOnItemClickListener(this);
		this.drawerLayout.setDrawerListener(this);
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset)
	{

	}

	@Override
	public void onDrawerOpened(View drawerView)
	{
	}

	@Override
	public void onDrawerClosed(View drawerView)
	{
	}

	@Override
	public void onDrawerStateChanged(int newState)
	{

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
	{
		drawerLayout.closeDrawer(Gravity.LEFT);
		Intent intent;

		switch(i)
		{
			//Settings
			case 0 :
				break;
			//Camera
			case 1 :
				mainActivity.setBusy(true);
				intent = new Intent(mainActivity, Discotheque.class);
				mainActivity.startActivity(intent);
				break;

			//Camera
			case 2 :
				mainActivity.setBusy(true);
				intent = new Intent(mainActivity, VinylCamera.class);
				mainActivity.startActivity(intent);

				break;

			case 3:
				break;
			case 4 :

				mainActivity.setBusy(true);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://post-engineering.blogspot.gr"));
				mainActivity.startActivity(browserIntent);

				break;
		}
	}

	public void openDrawer()
	{
		drawerLayout.openDrawer(Gravity.LEFT);
	}

	public void closeDrawer()
	{
		drawerLayout.closeDrawer(Gravity.LEFT);
	}

	public boolean isDrawerOpen()
	{
		return drawerLayout.isDrawerOpen(Gravity.LEFT);
	}

	public void updateList()
	{
		drawerAdapter.notifyDataSetChanged();
	}

	//Variables
	private MainActivity mainActivity;
	private static ArrayList<DrawerItem> drawerItems;
	private static DrawerAdapter drawerAdapter;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
}
