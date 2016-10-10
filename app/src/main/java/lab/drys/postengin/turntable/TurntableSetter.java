package lab.drys.postengin.turntable;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/21/.
 */
public class TurntableSetter extends TurntableThread
{
	public TurntableSetter(Turntable tbl, TurntableUpdater tblupd)
	{
		super(tbl,tblupd);

		this.targetAngle = 0.0f;
		this.discAngle = -30.0f/360.0f*((float)Math.PI);
		this.maxAngle = -60.0f/360.0f*((float)Math.PI);
		this.step = 0.5f/360.0f*((float)Math.PI);
	}

	@Override
	protected void updateLoop()
	{
		turntable.updateArm(step);

		super.updateLoop();

		if((step==0) || ((step>0) && turntable.armAngle>targetAngle) || ((step<0) && turntable.armAngle<targetAngle))
		{
			step = 0.0f;
			updating = false;
			turntable.armNeedleOff();
		}
	}

	public void toggle()
	{
		updating = false;

		if(step>0.0f)
		{
			step = -0.5f/360.0f*((float)Math.PI);
			targetAngle = discAngle;
		}
		else if(step<0.0f)
		{
			step = 0.5f/360.0f*((float)Math.PI);
			targetAngle = 0.0f;
		}
		else
		{

		}

		this.awake();
	}

	//Variables
	private float targetAngle;
	private float discAngle;
	private float maxAngle;
	private float step;
}
