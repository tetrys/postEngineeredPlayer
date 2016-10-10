package lab.drys.postengin;

import android.app.Application;
import android.util.Log;

import lab.drys.postengin.dj.PlayQueue;

/**
 * Created by lykanthrop on 1507/19/.
 */
public class PostEngin extends Application
{
	public PostEngin()
	{
		super();
	}

	public static void logError(String msg)
	{
		Log.e("PostEngineeredPlayer",msg);
	}

	public static void logDebug(String msg)
	{
		Log.d("PostEngineeredPlayer",msg);
	}

	public static void logInfo(String msg)
	{
		Log.i("PostEngineeredPlayer",msg);
	}

	//Variables
	public static final PlayQueue playQueue = new PlayQueue();

	public static final String COMMON_PREFERENCES = "CommonPreferences";
	public static final String ready = "ready";
	public static final String disco = "disco";
}
