package lab.drys.postengin.discotheque;


import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayDeque;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;
import lab.drys.postengin.dj.PlayQueue;

/**
 * Created by lykanthrop on 1507/25/.
 */
public class FragmentListTracks extends ListFragment implements AbsListView.MultiChoiceModeListener, LoaderCallbacks<Cursor>
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		trackAdapter = new InfoAdapterTracks(this.getActivity(),cursor);

		playQueue = PostEngin.playQueue;
		selected = new ArrayDeque<>();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View vw = inflater.inflate(R.layout.fragment_discotheque_list, container, false);
		spin = (ProgressBar)vw.findViewById(R.id.progress_spin);
		spin.setVisibility(View.INVISIBLE);
		emptyText = (TextView)vw.findViewById(R.id.text_empty);

		return vw;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		this.getListView().setAdapter(trackAdapter);

		this.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		this.getListView().setMultiChoiceModeListener(this);
		this.getListView().setSelector(R.drawable.list_selector);
		this.getListView().setItemsCanFocus(true);

		this.getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onDestroy()
	{
		cursor.close();
		super.onDestroy();
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
	{
		int selectedNumber = selected.size();

		if(checked)
		{
			selected.addLast(position);
		}
		else
		{
			selected.remove(position);
		}

		if(((selectedNumber==1) && (selected.size()>1)) || ((selectedNumber>1) && (selected.size()==1)))
		{
			onPrepareActionMode(mode, actionMenu);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		actionMenu = menu;

		mode.getMenuInflater().inflate(R.menu.action_menu_tracks,actionMenu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		if(menu!=null)
		{
			if(selected.size()<2)
			{
				menu.findItem(R.id.track_info).setVisible(true);
			}
			else
			{
				menu.findItem(R.id.track_info).setVisible(true);
			}
		}
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.track_info :
				break;

			case R.id.track_add :

				playQueue.add(cursor,selected);

				break;

			case R.id.track_add_play :
				break;
		}

		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		selected.clear();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		if(spin!=null)
		{
			spin.setVisibility(View.VISIBLE);
		}

		String[] projection = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
		                       MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
		                       MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA};

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != ?";
		String[] selectionArgs = {"0"};

		String ordering = MediaStore.Audio.Media.ARTIST+", "+MediaStore.Audio.Media.ALBUM+", "+MediaStore.Audio.Media.TRACK;
		return new CursorLoader(this.getActivity(),MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,
				selection,selectionArgs,ordering);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		cursor = data;

		if(cursor!=null && cursor.getCount()>0)
		{
			emptyText.setVisibility(View.GONE);
		}

		spin.setVisibility(View.GONE);

		trackAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		trackAdapter.swapCursor(null);
	}

	//Variables
	private Cursor cursor;
	private InfoAdapterTracks trackAdapter;
	ProgressBar spin;
	TextView emptyText;
	Menu actionMenu;

	PlayQueue playQueue;
	ArrayDeque<Integer> selected;
}
