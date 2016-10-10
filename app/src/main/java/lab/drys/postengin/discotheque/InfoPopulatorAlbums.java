package lab.drys.postengin.discotheque;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/29/.
 */
public class InfoPopulatorAlbums extends AsyncTask<InfoViewHolder, Void, Bitmap>
{
	public InfoPopulatorAlbums(String fp)
	{
		filePath = fp;
	}

	@Override
	protected Bitmap doInBackground(InfoViewHolder... params)
	{
		Bitmap out = null;
		holder = new WeakReference<>(params[0]);
		if(filePath!=null && (!filePath.isEmpty()))
		{
			out = this.decodeSampledBitmap(300,300);
		}

		return out;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		InfoViewHolder hldr = holder.get();

		if(hldr!=null)
		{
			if(bitmap!=null)
			{
				hldr.icon.setImageBitmap(bitmap);
			}
			else
			{
				hldr.icon.setImageResource(R.drawable.cover);
			}
		}

		super.onPostExecute(bitmap);
	}

	public Bitmap decodeSampledBitmap(int reqWidth, int reqHeight)
	{

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = this.calculateInSampleSize(options, reqWidth, reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if(height>reqHeight || width>reqWidth)
		{

			final int halfHeight = height/2;
			final int halfWidth = width/2;

			while((halfHeight/inSampleSize)>reqHeight && (halfWidth/inSampleSize)>reqWidth)
			{
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	//Variables
	String filePath;
	WeakReference<InfoViewHolder> holder;
}
