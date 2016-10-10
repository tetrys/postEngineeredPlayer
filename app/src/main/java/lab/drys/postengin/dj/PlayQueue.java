package lab.drys.postengin.dj;

import android.database.Cursor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/29/.
 */
public class PlayQueue
{
	public PlayQueue()
	{
		queue = new ArrayList<>();
		duration = 0;
	}

	public void add(TrackItem track)
	{
		queue.add(track);

		duration+=track.duration;
	}

	public void add(Cursor cursor)
	{
		cursor.moveToPosition(-1);

		TrackItem trm = null;

		while(cursor.moveToNext())
		{
			trm = new TrackItem(cursor);
			queue.add(trm);

			duration+=trm.duration;
		}
	}

	public void add(Cursor cursor, ArrayDeque<Integer> items)
	{
		TrackItem trm = null;
		ListIterator<TrackItem> iterator = queue.listIterator(queue.size());

		for(Integer i : items)
		{
			cursor.moveToPosition(i);
			trm = new TrackItem(cursor);
			queue.add(trm);
			duration+=trm.duration;
		}
	}

	public void remove(PriorityQueue<Integer> items)
	{
		Iterator<Integer> iterator = items.iterator();
		int index = -1;
		while(iterator.hasNext())
		{
			index = iterator.next();
			duration -= queue.get(index).duration;
			queue.remove(index);

		}
	}

	public void set(int index)
	{
		queue.get(index).setCurrent();

		if(index>0)
		{
			queue.get(index-1).setPrevious();
		}
		else
		{
			TrackItem.resetPrevious();
		}

		if(index<(queue.size()-1))
		{
			queue.get(index+1).setNext();
		}
		else
		{
			TrackItem.resetNext();
		}
	}

	public void reset()
	{
		if(queue.isEmpty())
		{
			TrackItem.reset();
		}
		else
		{
			TrackItem.resetPrevious();
			queue.get(0).setCurrent();

			if(queue.size()>1)
			{
				queue.get(1).setNext();
			}
		}
	}

	public void hardReset()
	{
		TrackItem.reset();
	}

	public TrackItem potentialNext()
	{
		return TrackItem.getNext();
	}

	public String potentialNextData()
	{
		String out = null;

		TrackItem trm = TrackItem.getNext();
		if(trm!=null)
		{
			out = trm.filepath;
		}

		return out;
	}

	public void forward()
	{
		int index = queue.indexOf(TrackItem.getCurrent());

		if(index<queue.size()-3)
		{
			TrackItem.forward(queue.get(index+2));
		}
		else if(index==queue.size()-2)
		{
			TrackItem.forward(null);
		}
	}

	public TrackItem potentialPrevious()
	{
		return TrackItem.getPrevious();
	}

	public String potentialPreviousData()
	{
		String out = null;

		TrackItem trm = TrackItem.getPrevious();
		if(trm!=null)
		{
			out = trm.filepath;
		}

		return out;
	}

	public void backward()
	{
		int index = queue.indexOf(TrackItem.getCurrent());

		if(index>1)
		{
			TrackItem.backward(queue.get(index-2));
		}
		else if(index==1)
		{
			TrackItem.backward(null);
		}
	}

	public String getCurrentData()
	{
		return TrackItem.getCurrent().filepath;
	}

	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	//Variables
	protected ArrayList<TrackItem> queue;
	protected long duration;
}
