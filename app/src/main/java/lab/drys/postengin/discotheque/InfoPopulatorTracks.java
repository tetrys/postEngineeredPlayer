package lab.drys.postengin.discotheque;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/29/.
 */
public class InfoPopulatorTracks extends AsyncTask<InfoViewHolder, Void, Bitmap>
{
	public InfoPopulatorTracks(String fp)
	{
		filePath = fp;
	}

	@Override
	protected Bitmap doInBackground(InfoViewHolder... params)
	{
		Bitmap out = null;
		holder = params[0];
		if(filePath!=null && (!filePath.isEmpty()))
		{
			out = BitmapFactory.decodeFile(filePath);
		}

		return out;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		if(bitmap!=null)
		{
			holder.icon.setImageBitmap(bitmap);
		}
		else
		{
			holder.icon.setImageResource(R.drawable.gear);
		}

		super.onPostExecute(bitmap);
	}

	//Variables
	String filePath;
	InfoViewHolder holder;
}
