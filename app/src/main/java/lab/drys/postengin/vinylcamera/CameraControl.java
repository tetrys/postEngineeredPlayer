package lab.drys.postengin.vinylcamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lykanthrop on 1507/26/.
 */
public class CameraControl extends SurfaceView implements SurfaceHolder.Callback
{
	public CameraControl(Context cnt)
	{
		super(cnt);

		this.getHolder().addCallback(this);
		this.getHolder().setFormat(PixelFormat.TRANSPARENT);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2.0f);

		float radius = Math.min(width,height)/2.0f-5.0f;

		Canvas canvas = holder.lockCanvas();
		canvas.drawCircle(width/2.0f,height/2.0f,radius,paint);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{

	}
}
