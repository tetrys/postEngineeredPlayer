package lab.drys.postengin.discotheque;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayDeque;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/27/.
 */
public class FragmentGridAlbums extends Fragment implements AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks<Cursor>
{
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		albumAdapter = new InfoAdapterAlbums(this.getActivity(),cursor);

		selected = new ArrayDeque<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View vw = inflater.inflate(R.layout.fragment_discotheque_grid,container,false);

		gridView = (GridView)vw.findViewById(R.id.disco_grid);
		spin = (ProgressBar)vw.findViewById(R.id.progress_spin);
		spin.setVisibility(View.INVISIBLE);
		emptyText = (TextView)vw.findViewById(R.id.text_empty);

		return vw;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		gridView.setAdapter(albumAdapter);
		gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		gridView.setMultiChoiceModeListener(this);
		gridView.setSelector(R.drawable.grid_selector);

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
		cursor.moveToPosition(position);
		if(checked)
		{
			selected.add(Integer.toString(cursor.getInt(0)));
		}
		else
		{
			selected.remove(Integer.toString(cursor.getInt(0)));
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		actionMenu = menu;

		mode.getMenuInflater().inflate(R.menu.action_menu_albums,actionMenu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		if(menu!=null)
		{

		}
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.album_add:
				String[] str = new String[selected.size()];
				new QueuePopulatorAlbum(this.getActivity()).execute(selected.toArray(str));

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

		String[] projection = {MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM,
		                       MediaStore.Audio.Albums.ARTIST,MediaStore.Audio.Albums.ALBUM_ART};

		return new CursorLoader(this.getActivity(),MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,projection,null,null,
				MediaStore.Audio.Albums.ARTIST);
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

		albumAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		albumAdapter.swapCursor(null);
	}

	//Variables
	Cursor cursor;
	InfoAdapterAlbums albumAdapter;
	GridView gridView;
	ProgressBar spin;
	TextView emptyText;
	Menu actionMenu;

	ArrayDeque<String> selected;
}
