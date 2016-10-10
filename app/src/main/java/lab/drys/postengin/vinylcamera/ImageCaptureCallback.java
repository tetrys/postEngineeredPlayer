package lab.drys.postengin.vinylcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/27/.
 */
public class ImageCaptureCallback implements Camera.PictureCallback
{
	@Override
	public void onPictureTaken(byte[] data, Camera camera)
	{
		File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		if (pictureFile == null)
		{
			return;
		}

		try
		{
			FileOutputStream fos = new FileOutputStream(pictureFile);

			Camera.Parameters parameters = camera.getParameters();
			float radius = Math.min(parameters.getPreviewSize().height/2.0f,parameters.getPreviewSize().width/2.0f)-5.0f;
			float ratio = parameters.getPictureSize().height/parameters.getPreviewSize().height;

			radius*=ratio;

			PostEngin.logDebug("Camera W"+camera.getParameters().getPictureSize().width);
			PostEngin.logDebug("Camera H"+camera.getParameters().getPictureSize().height);
			PostEngin.logDebug("Camera P W"+camera.getParameters().getPreviewSize().width);
			PostEngin.logDebug("Camera P H"+camera.getParameters().getPreviewSize().height);
			Bitmap out = getCroppedBitmap(data,radius);
			out.compress(Bitmap.CompressFormat.JPEG,100,fos);
			fos.close();
		}
		catch (FileNotFoundException xcpt)
		{
		}
		catch (IOException e)
		{
		}

		camera.startPreview();
	}

	private static Uri getOutputMediaFileUri(int type)
	{
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type)
	{

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Vinyls");

		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
		File mediaFile;
		if (type==MEDIA_TYPE_IMAGE)
		{
			mediaFile = new File(mediaStorageDir.getPath()+File.separator+"IMG_"+timeStamp+".jpg");
		}
		else
		{
			return null;
		}

		return mediaFile;
	}

	public Bitmap getCroppedBitmap(byte[] data, float radius)
	{
		Bitmap input = BitmapFactory.decodeByteArray(data,0,data.length);

		float width = input.getWidth();
		float height = input.getHeight();

		PostEngin.logDebug("Picture H "+height);
		PostEngin.logDebug("Picture W "+width);

		Bitmap output = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, (int)width, (int)height);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawCircle(width/2.0f, height/2.0f,radius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(input, rect, rect, paint);

		return output;
	}

	//Variables
	public static final int MEDIA_TYPE_IMAGE = 1;
}
