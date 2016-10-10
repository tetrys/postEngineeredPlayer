package lab.drys.postengin.discotheque;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/27/.
 */
public class FragmentGridVinyls extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		vinylAdapter = new InfoAdapterVinyls(this.getActivity(),cursor);
		this.getLoaderManager().initLoader(0, null, this);
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

		gridView.setAdapter(vinylAdapter);
	}

	@Override
	public void onDestroy()
	{
		cursor.close();
		super.onDestroy();
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

		vinylAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		vinylAdapter.swapCursor(null);
	}

	//Variables
	Cursor cursor;
	InfoAdapterVinyls vinylAdapter;
	GridView gridView;
	ProgressBar spin;
	TextView emptyText;
}
