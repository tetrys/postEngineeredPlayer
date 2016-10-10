package lab.drys.postengin.dj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.PriorityQueue;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;
import lab.drys.postengin.spieler.SpielerConnection;
import lab.drys.postengin.spieler.SpielerService;
import lab.drys.postengin.spieler.TimeDelayThread;

/**
 * Created by lykanthrop on 1507/25/.
 */
public class FragmentListQueue extends ListFragment implements AbsListView.MultiChoiceModeListener, TimeSpanInterface
{

	@Override
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);

		playQueue = PostEngin.playQueue;
		selected = new PriorityQueue<>(22,new TrackItemComparator());

		clickedPosition = -1;

		timeSpanThread = new TimeSpanThread(500,this);
		timeSpanThread.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View vw = inflater.inflate(R.layout.fragment_playqueue, container, false);
		textEmpty = (TextView)vw.findViewById(R.id.text_empty);

		queueAdapter = new PlayQueueArrayAdapter(this.getActivity(),playQueue.queue);
		this.setListAdapter(queueAdapter);

		return vw;
	}

	@Override
	public void onActivityCreated(Bundle savedInstance)
	{
		super.onActivityCreated(savedInstance);

		this.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		this.getListView().setMultiChoiceModeListener(this);
		this.getListView().setSelector(R.drawable.list_selector);
		this.getListView().setItemsCanFocus(true);

		this.spielerConnection = new SpielerConnection();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		Intent intent = new Intent(this.getActivity(),SpielerService.class);
		this.getActivity().bindService(intent, spielerConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onPause()
	{
		this.getActivity().unbindService(spielerConnection);

		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		timeSpanThread.halt();
		super.onDestroy();
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
	{
		if(checked)
		{
			selected.add(position);
		}
		else
		{
			selected.remove(position);
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		actionMenu = menu;

		mode.getMenuInflater().inflate(R.menu.action_menu_playqueue,actionMenu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		if(menu!=null)
		{

		}
		return true;
	}
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.item_info :


				break;

			case R.id.item_remove :

				playQueue.remove(selected);

				break;
		}

		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		selected.clear();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if(clickedPosition==-1)
		{
			clickedPosition = position;
			timeSpanThread.hit();
		}
		else if(clickedPosition==position)
		{
			timeSpanThread.hit();
		}
		else
		{
			clickedPosition=position;
			timeSpanThread.rehit();
		}
	}

	@Override
	public void action()
	{
		spielerConnection.leap(clickedPosition);
		clickedPosition = -1;
	}

	@Override
	public void cancelAction()
	{
		clickedPosition = -1;
	}

	public void updateListView()
	{
		queueAdapter.notifyDataSetChanged();
	}

	//Variables
	PlayQueue playQueue;
	TextView textEmpty;
	PlayQueueArrayAdapter queueAdapter;
	Menu actionMenu;

	PriorityQueue<Integer> selected;
	int clickedPosition;
	TimeSpanThread timeSpanThread;
	SpielerConnection spielerConnection;
}
