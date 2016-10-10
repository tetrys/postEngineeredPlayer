package lab.drys.postengin.dj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import lab.drys.postengin.R;
import lab.drys.postengin.spieler.FragmentSpielerRow;
import lab.drys.postengin.spieler.SpielerService;
import lab.drys.postengin.spieler.SpielerConnection;

/**
 * Created by lykanthrop on 1507/25/.
 */
public class DJActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_dj);

		Toolbar toolbar = (Toolbar)this.findViewById(R.id.main_toolbar);
		toolbar.showOverflowMenu();
		toolbar.setNavigationIcon(R.mipmap.ic_launcher);
		toolbar.setTitle("Playqueue");
		this.setSupportActionBar(toolbar);

		Intent intent = new Intent(this,SpielerService.class);

		this.spielerConnection = new SpielerConnection();

		this.bindService(intent,spielerConnection, Context.BIND_AUTO_CREATE);

		this.djActivityReceiver = new DJActivityReceiver(this);

		fragmentQueue = new FragmentListQueue();
		fragmentSpieler = new FragmentSpielerRow();

		FragmentManager fragmentManager = this.getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.main_frame,fragmentQueue).commit();
		fragmentManager.beginTransaction().replace(R.id.spieler_frame,fragmentSpieler).commit();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		LocalBroadcastManager.getInstance(this).registerReceiver(djActivityReceiver,djActivityReceiver.getIntentFilter());

		this.togglePlayIcon();
	}

	@Override
	protected void onPause()
	{
		LocalBroadcastManager.getInstance(this).unregisterReceiver(djActivityReceiver);

		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		this.unbindService(spielerConnection);

		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int caseId = item.getItemId();

		if(caseId == android.R.id.home)
		{
			this.finish();
		}

		return true;
	}

	public void onClick(View vw)
	{
		switch(vw.getId())
		{
			case R.id.dj_play:
				spielerConnection.togglePlay();
				break;
			case R.id.dj_next:
				spielerConnection.skipNext();
				break;
			case R.id.dj_previous:
				spielerConnection.skipPrevious();
				break;
			case R.id.dj_stop:
				spielerConnection.stop();
				break;
		}
	}

	protected void updateListView()
	{
		fragmentQueue.updateListView();
	}

	protected void togglePlayIcon()
	{
		fragmentSpieler.togglePlayIcon(spielerConnection.isPlaying());
	}

	//Variables
	SpielerConnection spielerConnection;
	ImageButton playIcon;
	FragmentListQueue fragmentQueue;
	FragmentSpielerRow fragmentSpieler;
	DJActivityReceiver djActivityReceiver;
}
