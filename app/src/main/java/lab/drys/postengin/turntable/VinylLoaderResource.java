package lab.drys.postengin.turntable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Created by lykanthrop on 1508/09/.
 */
public class VinylLoaderResource extends AsyncTask<Integer,Void,Bitmap>
{
	public VinylLoaderResource(TurntableRenderer tblr)
	{
		turntableRenderer = tblr;
	}

	@Override
	protected Bitmap doInBackground(Integer... params)
	{
		Bitmap out;

		out = BitmapFactory.decodeResource(turntableRenderer.context.getResources(), params[0]);

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
