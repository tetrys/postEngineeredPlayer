package lab.drys.postengin.turntable;

/**
 * Created by lykanthrop on 1507/21/.
 */
public class Turntable
{
	public Turntable()
	{
		discAngleCos = 1.0f;
		discAngleSin = 0.0f;
		discAngle = 0.0f;

		discTouchInitial_local = new float[2];

		armAngleCos = 1.0f;
		armAngleSin = 0.0f;

		armCenter_local = new float[2];
		armNeedle_local = new float[2];
		armNeedleInitial_local = new float[2];

		discMatrix = new float[4];
		discVector = new float[2];

		armMatrix = new float[4];
		armVector = new float[2];

		discMatrix[0] = 1.0f;
		discMatrix[1] = 0.0f;
		discMatrix[2] = 0.0f;
		discMatrix[3] = 1.0f;

		discVector[0] = 0.0f;
		discVector[1] = 0.0f;

		discTransY = 0.3f;

		armMatrix[0] = 1.0f;
		armMatrix[1] = 0.0f;
		armMatrix[2] = 0.0f;
		armMatrix[3] = 1.0f;

		armVector[0] = 0.0f;
		armVector[1] = 0.0f;

		armTransY = -0.4f;

		needleTouch = false;
		discTouch = false;

		vinylPath = null;
		vinylOn = false;
	}

	public void setDiscVariables()
	{
		discRadius = Math.min(axisX, axisY)-10.0f;
		discRadius2 = (float)Math.pow(discRadius,2);

		discTranslationY = discTransY*axisY;

		discCenterX = 0;
		discCenterY = discTranslationY;
	}

	public void setArmVariables()
	{
		armTranslationY = armTransY*axisY;
		
		armSize = Math.min(axisX, axisY)-20.0f;

		armCenter_local[0] = armSize*0.49152542372f;
		armCenter_local[1] = -armSize*armRatio*0.07843137254f;

		float nx = -armSize*0.92f;
		float ny = armSize*armRatio*0.5417f;

		armNeedle_local[0] = nx - armCenter_local[0];
		armNeedle_local[1] = ny - armCenter_local[1];

		armNeedleInitial_local[0] = armNeedle_local[0];
		armNeedleInitial_local[1] = armNeedle_local[1];

		armLength2 = (float)(Math.pow(armNeedle_local[0],2)+Math.pow(armNeedle_local[1],2));

		armCenterX = armCenter_local[0];
		armCenterY = armTranslationY + armCenter_local[1];

		armNeedleX = nx;
		armNeedleY = armTranslationY+ny;

		armRadius = (float)Math.hypot((armNeedleX-armCenterX), (armNeedleY-armCenterY));

		needleZoneMin2 = (float)Math.pow((armRadius-2*thumbRadius),2);
		needleZoneMax2 = (float)Math.pow((armRadius+2*thumbRadius), 2);
	}

	public void setThumb(float th)
	{
		thumbRadius = (th);
		thumbRadius2 = (float)Math.pow(thumbRadius,2);
	}

	public void rotateDisc()
	{
		discMatrix[0] = discAngleCos;
		discMatrix[1] = discAngleSin;
		discMatrix[2] = -discAngleSin;
		discMatrix[3] = discAngleCos;
	}

	public void rotateArm()
	{
		armMatrix[0] = armAngleCos;
		armMatrix[1] = armAngleSin;
		armMatrix[2] = -armAngleSin;
		armMatrix[3] = armAngleCos;

		armVector[0] = (armCenter_local[0]*(1-armAngleCos)+armCenter_local[1]*armAngleSin);
		armVector[1] = (armCenter_local[1]*(1-armAngleCos)-armCenter_local[0]*armAngleSin);
	}

	public void discTouch(float X, float Y)
	{
		discTouchInitial_local[0] = X;
		discTouchInitial_local[1] = Y-discTranslationY;

		discTouchRadius2 = (float)(Math.pow(discTouchInitial_local[0],2)+Math.pow(discTouchInitial_local[1],2));

		initDiscAngleCos = discAngleCos;
		initDiscAngleSin = discAngleSin;
	}

	public void armNeedleTouch(float X, float Y)
	{
		armNeedle_local[0] = X-armCenter_local[0];
		armNeedle_local[1] = Y-(armCenter_local[1]+armTranslationY);

		armLength2 = (float)(Math.pow(armNeedle_local[0],2)+Math.pow(armNeedle_local[1],2));

		initArmAngleCos = armAngleCos;
		initArmAngleSin = armAngleSin;
	}

	public void armAngle(float X, float Y)
	{
		float coordX = X-armCenter_local[0];
		float coordY = Y-(armCenter_local[1]+armTranslationY);

		float armCenter = (float)(Math.pow(coordX,2)+Math.pow(coordY,2));
		armCenter = armCenter*armLength2;
		armCenter = (float)Math.sqrt(armCenter);

		float bcos = (coordX*armNeedle_local[0]+coordY*armNeedle_local[1])/armCenter;
		float bsin = -(float)(Math.sqrt(1-bcos*bcos));

		if(coordX*armNeedle_local[1]<coordY*armNeedle_local[0])
		{
			bsin = -bsin;
		}

		armAngleCos = initArmAngleCos*bcos-initArmAngleSin*bsin;
		armAngleSin = initArmAngleSin*bcos+initArmAngleCos*bsin;
	}

	public void discAngle(float X, float Y)
	{
		float coordY = Y-discTranslationY;

		float distCenter = (float)(Math.pow(X,2)+Math.pow(coordY,2));
		distCenter = (float)Math.sqrt(distCenter*discTouchRadius2);


		float bcos = (X*discTouchInitial_local[0]+coordY*discTouchInitial_local[1])/distCenter;
		float bsin = -(float)(Math.sqrt(1-bcos*bcos));

		if(X*discTouchInitial_local[1]<coordY*discTouchInitial_local[0])
		{
			bsin = -bsin;
		}

		discAngleCos = initDiscAngleCos*bcos-initDiscAngleSin*bsin;
		discAngleSin = initDiscAngleSin*bcos+initDiscAngleCos*bsin;
	}

	public void updateDisc(float A)
	{
		discAngle+=A;
		this.discAngle();
	}

	public void discAngle()
	{

		discAngleCos = (float)Math.cos(discAngle);
		discAngleSin = (float)Math.sin(discAngle);
	}

	public void discAngle(float ang)
	{

		discAngleCos = (float)Math.cos(ang);
		discAngleSin = (float)Math.sin(ang);
	}

	public void updateArm(float A)
	{
		armAngle+=A;
		this.armAngle();
	}

	public void armAngle()
	{

		armAngleCos = (float)Math.cos(armAngle);
		armAngleSin = (float)Math.sin(armAngle);
	}

	public void armAngle(float ang)
	{

		armAngleCos = (float)Math.cos(ang);
		armAngleSin = (float)Math.sin(ang);
	}

	public void discTouchOff()
	{
		discTouch = false;

		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				discAngle = (float)Math.acos(discAngleCos);

				if(discAngleSin<0)
				{
					discAngle = (float)(2*Math.PI-discAngle);
				}
			}
		});

		thread.start();
		try
		{
			thread.join();
		}
		catch(InterruptedException xcpt)
		{

		}
	}

	public void armNeedleOff()
	{
		needleTouch = false;

		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				armNeedle_local[0] = armAngleCos*armNeedleInitial_local[0]-armAngleSin*armNeedleInitial_local[1];
				armNeedle_local[1] = armAngleSin*armNeedleInitial_local[0]+armAngleCos*armNeedleInitial_local[1];

				armNeedleX = armNeedle_local[0]+armCenter_local[0];
				armNeedleY = armNeedle_local[1]+armCenter_local[1]+armTranslationY;

				armAngle = (float)Math.acos(armAngleCos);

				if(armAngleSin<0)
				{
					armAngle = (float)(2*Math.PI-armAngle);
				}
			}
		});

		thread.start();
		try
		{
			thread.join();
		}
		catch(InterruptedException xcpt)
		{

		}
	}

	public void setVinyl(String filepath)
	{
		vinylPath = filepath;
	}

	public void setVinyl(boolean bl)
	{
		vinylOn = bl;
	}

	public boolean checkNeedleTouch(float X, float Y)
	{
		return needleTouch = isInsideNeedle(X,Y);
	}

	public boolean checkDiscTouch(float X, float Y)
	{
		return discTouch = this.isInsideDisc(X, Y);
	}

	public boolean checkArmTouch(float X, float Y)
	{
		float armDistance = (float)(Math.pow((X-armCenterX),2)+Math.pow((Y-armCenterY),2));

		return armDistance<thumbRadius2;
	}

	public boolean isInsideDisc(float X, float Y)
	{
		float discDistance = (float)(Math.pow((X-discCenterX),2)+Math.pow((Y-discCenterY),2));

		return discDistance<discRadius2;
	}

	public boolean isInsideNeedle(float X, float Y)
	{
		float needleDistance = (float)(Math.pow((X-armNeedleX),2)+Math.pow((Y-armNeedleY), 2));

		return needleDistance<thumbRadius2;
	}

	public boolean isInsideZone(float X, float Y)
	{
		float needleDistance = (float)(Math.pow((X-armCenterX),2)+Math.pow((Y-armCenterY), 2));

		return needleDistance<needleZoneMax2 && needleDistance>needleZoneMin2;
	}

	//Variables
	protected float height;
	protected float width;

	protected float axisX;
	protected float axisY;

	protected float armNeedleX;
	protected float armNeedleY;

	protected float armCenterX;
	protected float armCenterY;

	protected float armRadius;
	protected float armTranslationY;

	protected float[] armCenter_local;
	protected float[] armNeedleInitial_local;
	protected float[] armNeedle_local;

	protected float armSize;
	protected float armRatio;
	protected float armLength2;
	protected float initArmAngleCos;
	protected float initArmAngleSin;
	protected float armAngle;
	protected float armAngleCos;
	protected float armAngleSin;

	//Disc
	protected float discCenterX;
	protected float discCenterY;

	protected float discRadius;
	protected float discRadius2;
	protected float discTranslationY;

	protected float[] discTouchInitial_local;
	protected float discTouchRadius2;

	protected float initDiscAngleCos;
	protected float initDiscAngleSin;
	protected float discAngle;
	protected float discAngleCos;
	protected float discAngleSin;

	protected float[] discMatrix;
	protected float[] discVector;
	protected float discTransY;

	protected float[] armMatrix;
	protected float[] armVector;
	protected float armTransY;

	protected float thumbRadius;
	protected float thumbRadius2;
	protected float needleZoneMin2;
	protected float needleZoneMax2;

	protected boolean needleTouch;
	protected boolean discTouch;

	protected String vinylPath;
	protected boolean vinylOn;
}
