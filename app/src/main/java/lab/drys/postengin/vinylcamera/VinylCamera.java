package lab.drys.postengin.vinylcamera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/21/.
 */
public class VinylCamera extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_vinylcamera);

		cameraFrame = (FrameLayout)this.findViewById(R.id.camera_preview);

		cameraControl = new CameraControl(this);

		imageCaptureCallback = new ImageCaptureCallback();
		imageAutofocusCallback = new ImageAutofocusCallback();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		cameraFrame.addView(cameraControl);

		this.openCameraView();
	}

	@Override
	public void onPause()
	{
		cameraFrame.removeView(cameraControl);

		this.releaseCameraView();

		super.onPause();
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.capture :

				this.captureImage();

				break;
		}
	}

	private void captureImage()
	{
		camera.takePicture(null,null,imageCaptureCallback);
	}

	private boolean checkCameraHardware()
	{
		if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void openCameraView()
	{
		if(this.checkCameraHardware())
		{
			try
			{
				if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
				{

				}
				else
				{
					this.releaseCameraView();
					camera = Camera.open();
					//camera.autoFocus(imageAutofocusCallback);
					Camera.Parameters parameters =  camera.getParameters();


					if(parameters.getMaxNumMeteringAreas()>0)
					{
						ArrayList<Camera.Area> areas = new ArrayList<>();
						Rect centerRect = new Rect(-100,-100,100,100);
						areas.add(new Camera.Area(centerRect,1000));
						parameters.setMeteringAreas(areas);
					}

					if(parameters.getMaxNumFocusAreas()>0)
					{
						ArrayList<Camera.Area> areas = new ArrayList<>();
						Rect centerRect = new Rect(-300,-300,300,300);
						areas.add(new Camera.Area(centerRect,1000));
						parameters.setFocusAreas(areas);
					}
					parameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
					parameters.setJpegQuality(100);
					parameters.setJpegThumbnailQuality(100);
					parameters.setJpegThumbnailSize(100, 100);
					parameters.setPictureFormat(ImageFormat.JPEG);

					camera.setParameters(parameters);

					cameraPreview = new VinylCameraPreview(this,camera);
					cameraFrame.addView(cameraPreview);
				}
			}
			catch(Exception e)
			{
				PostEngin.logDebug("Failed to open Camera");
				e.printStackTrace();
			}
		}
		else
		{
			PostEngin.logDebug("No Camera Support");
		}
	}

	private void releaseCameraView()
	{
		if (camera!=null)
		{
			camera.release();
			camera = null;
		}
		cameraFrame.removeView(cameraPreview);
		cameraPreview = null;
	}

	//Variables
	FrameLayout cameraFrame;
	Camera camera;
	CameraDevice camera2;
	VinylCameraPreview cameraPreview;
	CameraControl cameraControl;

	ImageCaptureCallback imageCaptureCallback;
	ImageAutofocusCallback imageAutofocusCallback;
}
