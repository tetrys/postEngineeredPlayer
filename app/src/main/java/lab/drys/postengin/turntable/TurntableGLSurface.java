package lab.drys.postengin.turntable;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import lab.drys.postengin.MainActivityReceiver;
import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;
import lab.drys.postengin.dj.DJActivity;

/**
 * Created by lykanthrop on 1507/19/.
 */
public class TurntableGLSurface extends GLSurfaceView
{
	public TurntableGLSurface(Context cnt, AttributeSet attr)
	{
		super(cnt, attr);
		this.setEGLContextClientVersion(2);

		context = cnt;
	}

	public void initRenderer(Turntable tbl, TurntableUpdater tblupd)
	{
		if(turntable==null || turntableUpdater==null)
		{
			turntable = tbl;
			turntableUpdater = tblupd;
		}

		renderer.set(turntable);
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		if(renderer==null)
		{
			renderer = new TurntableRenderer(context);
		}

		this.setRenderer(renderer);

		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		renderer = null;

		super.onDetachedFromWindow();
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt)
	{
		float evtX = evt.getX()-turntable.width/2;
		float evtY = turntable.height/2-evt.getY();

		switch(evt.getAction())
		{
			case MotionEvent.ACTION_DOWN :

				if(evtX<-turntable.width*0.425 && evtY>turntable.height*0.425)
				{
					Intent intent = new Intent(MainActivityReceiver.OPEN_DRAWER);
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				}
				else if(evtX>turntable.width*0.425 && evtY>turntable.height*0.425)
				{
					Intent intent = new Intent(this.context, DJActivity.class);
					context.startActivity(intent);
				}
				else
				{
					this.checkObjectTouch(evtX,evtY);
				}

				break;

			case MotionEvent.ACTION_MOVE :

				if(turntable.needleTouch)
				{
					if(turntable.isInsideZone(evtX,evtY))
					{
						turntable.armAngle(evtX, evtY);
					}
					else
					{
						turntable.armNeedleOff();
						turntableUpdater.serve(false);
					}
				}
				else if(turntable.discTouch)
				{
					if(turntable.checkDiscTouch(evtX,evtY))
					{
						turntable.discAngle(evtX,evtY);
					}
					else
					{
						turntable.discTouchOff();
						turntableUpdater.serve(false);
					}
				}
				else
				{
					this.checkObjectTouch(evtX,evtY);
				}

				break;

			case MotionEvent.ACTION_UP :

				if(turntable.needleTouch)
				{
					turntable.armNeedleOff();
					turntableUpdater.serve(false);
				}

				if(turntable.discTouch)
				{
					turntable.discTouchOff();
					turntableUpdater.serve(false);
				}

				break;
		}

		return true;
	}

	public void checkObjectTouch(float X, float Y)
	{
		if(turntable.checkNeedleTouch(X,Y))
		{
			turntable.armNeedleTouch(X, Y);
			turntableUpdater.serve(true);
		}
		else if(turntable.checkDiscTouch(X,Y))
		{
			turntable.discTouch(X, Y);
			turntableUpdater.serve(true);
		}
		else if(turntable.checkArmTouch(X,Y))
		{
			Intent intent = new Intent(MainActivityReceiver.ARM_TOGGLE);
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		}

	}

	public void loadVinyl(String str)
	{
		if(str!=null)
		{
			renderer.loadVinyl(str);
		}
		else
		{
			renderer.loadVinyl(R.drawable.schallplatte);
		}
	}

	public void unloadVinyl()
	{
		renderer.unloadVinyl();
	}

	//Variables
	private TurntableRenderer renderer;
	private Context context;

	Turntable turntable;
	TurntableUpdater turntableUpdater;
}
