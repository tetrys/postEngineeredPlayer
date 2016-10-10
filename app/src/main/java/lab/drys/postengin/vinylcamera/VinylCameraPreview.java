package lab.drys.postengin.vinylcamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/21/.
 */
public class VinylCameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
	public VinylCameraPreview(Context context, Camera cmr)
	{
		super(context);

		camera = cmr;

		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);

		previewRunning = false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		if (surfaceHolder.getSurface() == null)
		{
			return;
		}

		if(camera!=null)
		{
			if(previewRunning)
			{
				camera.stopPreview();
				previewRunning = false;
			}

			try
			{
				Display display = ((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

				if(display.getRotation() == Surface.ROTATION_0)
				{
					camera.setDisplayOrientation(90);
				}
				else if(display.getRotation() == Surface.ROTATION_270)
				{
					camera.setDisplayOrientation(180);
				}

				PostEngin.logDebug("Preview H "+height);
				PostEngin.logDebug("Preview W "+width);

				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();

				previewRunning = true;
			}
			catch (IOException xcpt)
			{
				PostEngin.logDebug(xcpt.getMessage());
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{

	}

	//Variables
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private static boolean previewRunning;

}
