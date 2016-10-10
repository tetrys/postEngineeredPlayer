package lab.drys.postengin.turntable;

/**
 * Created by lykanthrop on 1507/21/.
 */
public class TurntableSpinner extends TurntableThread
{

	public TurntableSpinner(Turntable tbl, TurntableUpdater tblupd)
	{
		super(tbl,tblupd);

		this.acceleration = 0.00125f;

		this.currentRPT = 0.0f;
		this.targetRPT = 0.0f;
		this.setRPT = 0.025f;

		this.spinning = false;
	}

	@Override
	protected void updateLoop()
	{
		if(((acceleration>0) && (currentRPT<targetRPT)) || ((acceleration<0) && (currentRPT>targetRPT)))
		{
			currentRPT += acceleration;
		}

		turntable.updateDisc(currentRPT);

		super.updateLoop();

		if(acceleration<0 && currentRPT<0.00125f)
		{
			updating = false;
		}
	}

	public void spinOn()
	{
		targetRPT = setRPT;

		this.setAcceleration();

		this.awake();

		spinning = true;
	}

	public void spinOff()
	{
		targetRPT = 0.0f;

		this.setAcceleration();

		spinning = false;
	}

	public void spinToggle()
	{
		if(spinning)
		{
			this.spinOff();
		}
		else
		{
			this.spinOn();
		}
	}

	public void setSpeed(float f)
	{
		setRPT = f;

		if(updating)
		{
			targetRPT = setRPT;
			this.setAcceleration();
		}
	}

	private void setAcceleration()
	{
		if(targetRPT>currentRPT)
		{
			acceleration = Math.abs(acceleration);
		}
		else
		{
			acceleration = -Math.abs(acceleration);
		}
	}

	public float getSpeed()
	{
		return setRPT;
	}

	public boolean isSpinning()
	{
		return spinning;
	}

	//Variables
	private float currentRPT;
	private float targetRPT;
	private float setRPT;
	private float acceleration;
	private boolean spinning;

}
