package lab.drys.postengin.discotheque;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/25/.
 */
public class Discotheque extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_discotheque);

		toolbar = (Toolbar)this.findViewById(R.id.unit_toolbar);
		toolbar.setNavigationIcon(R.mipmap.ic_launcher);

		this.setSupportActionBar(toolbar);

		DiscothequePagerAdapter pagerAdapter = new DiscothequePagerAdapter(this.getSupportFragmentManager());

		pager = (ViewPager) this.findViewById(R.id.pager);
		pager.setAdapter(pagerAdapter);

		TabLayout slidingTabs = (TabLayout)this.findViewById(R.id.sliding_tabs);
		slidingTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
		slidingTabs.setupWithViewPager(pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu mn)
	{
		menu = mn;

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu mn)
	{
		if(menu!=null)
		{
			menu = mn;
		}

		return true;
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

	//Variables
	Toolbar toolbar;
	ViewPager pager;
	Menu menu;
}
