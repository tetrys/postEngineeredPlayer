package lab.drys.postengin.spieler;

import android.os.Binder;

/**
 * Created by lykanthrop on 1507/23/.
 */
public class SpielerBinder extends Binder
{
	public SpielerBinder(SpielerService spl)
	{
		spielerService = spl;
	}

	//Variables
	protected SpielerService spielerService;
}
