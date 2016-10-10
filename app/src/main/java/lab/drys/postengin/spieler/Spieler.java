package lab.drys.postengin.spieler;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.dj.DJActivityReceiver;

/**
 * Created by lykanthrop on 1508/03/.
 */
public class Spieler implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener
{
	public Spieler(SpielerService splsv)
	{
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		this.mediaPlayer.setOnCompletionListener(this);
		this.mediaPlayer.setOnErrorListener(this);
		this.mediaPlayer.setOnPreparedListener(this);

		this.spielerService = splsv;

		mediaPlayer.setWakeMode(spielerService, PowerManager.PARTIAL_WAKE_LOCK);

		this.ready = false;
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		ready = true;
		if(!(swapSpieler.mediaPlayer.isPlaying() || spielerService.paused))
		{
			mediaPlayer.start();

			spielerService.currentReady();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		if(swapSpieler!=null && swapSpieler.ready)
		{
			PostEngin.playQueue.forward();
			swapSpieler.start();
			spielerService.swap();
		}
		else
		{
			spielerService.playEnd();
		}

		mediaPlayer.stop();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		return false;
	}

	public void setSwapSpieler(Spieler splr)
	{
		this.swapSpieler = splr;
	}

	public void release()
	{
		mediaPlayer.release();
		mediaPlayer = null;
	}

	public void prepareSwap()
	{
		if(swapSpieler!=null)
		{
			swapSpieler.ready = false;
			String filepath = PostEngin.playQueue.potentialNextData();
			if(filepath!=null)
			{
				swapSpieler.prepare(filepath);
			}
		}
	}

	protected void prepare(String filepath)
	{
		try
		{
			this.ready = false;
			this.mediaPlayer.stop();
			this.mediaPlayer.reset();
			this.mediaPlayer.setDataSource(filepath);
			this.mediaPlayer.prepareAsync();
		}
		catch(IOException xcpt)
		{

		}
	}

	protected void prepare()
	{
		try
		{
			this.ready = false;
			this.mediaPlayer.stop();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(PostEngin.playQueue.getCurrentData());
			mediaPlayer.prepareAsync();
		}
		catch(IOException xcpt)
		{

		}
	}

	protected void start()
	{
		mediaPlayer.start();
	}

	protected void restart()
	{
		if(mediaPlayer.isPlaying())
		{
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}
	}

	protected void stop()
	{
		if(mediaPlayer.isPlaying())
		{
			mediaPlayer.stop();
		}
	}

	protected void pause()
	{
		if(mediaPlayer.isPlaying())
		{
			mediaPlayer.pause();
			spielerService.paused = true;
		}
	}

	protected void setVolume(float rt, float lt)
	{
		mediaPlayer.setVolume(rt,lt);
	}

	protected boolean isPlaying()
	{
		return mediaPlayer.isPlaying();
	}

	//Variables
	private MediaPlayer mediaPlayer;
	private Spieler swapSpieler;
	private SpielerService spielerService;
	private boolean ready;
}
