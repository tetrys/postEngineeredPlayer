package lab.drys.postengin.discotheque;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1508/08/.
 */
public class QueuePopulatorArtists extends AsyncTask<String,Void,Cursor>
{
	public QueuePopulatorArtists(Context cntx)
	{
		context = cntx;
		postExecute = false;
	}

	public QueuePopulatorArtists(Context cntx, boolean bln)
	{
		context = cntx;
		postExecute = bln;
	}

	@Override
	protected Cursor doInBackground(String... params)
	{
		String[] projection = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
		                       MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
		                       MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA};

		String selection = MediaStore.Audio.Media.ALBUM_ID + " == ?";

		for(int i=1; i<params.length; i++)
		{
			selection+= "OR "+MediaStore.Audio.Media.ALBUM_ID+" == ?";
		}

		String ordering = MediaStore.Audio.Media.ARTIST+", "+MediaStore.Audio.Media.ALBUM+", "+MediaStore.Audio.Media.TRACK;

		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,selection,params,ordering);

		if(cursor!=null)
		{
			PostEngin.playQueue.add(cursor);
			cursor.close();
		}

		return cursor;
	}

	@Override
	protected void onPostExecute(Cursor cursor)
	{
		if(postExecute)
		{

		}

		super.onPostExecute(cursor);
	}

	//Variables
	private Context context;
	boolean postExecute;
}

