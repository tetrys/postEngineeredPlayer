package lab.drys.postengin.turntable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Created by lykanthrop on 1508/09/.
 */
public class VinylLoaderString extends AsyncTask<String,Void,Bitmap>
{
	public VinylLoaderString(TurntableRenderer tblr)
	{
		turntableRenderer = tblr;
	}

	@Override
	protected Bitmap doInBackground(String... params)
	{
		Bitmap out;

		out = BitmapFactory.decodeFile(params[0]);

		return out;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		if(bitmap!=null)
		{
			turntableRenderer.loadVinyl(bitmap);
		}
	}

	//Variables
	TurntableRenderer turntableRenderer;
}
