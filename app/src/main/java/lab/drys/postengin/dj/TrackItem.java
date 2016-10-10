package lab.drys.postengin.dj;

import android.database.Cursor;

import lab.drys.postengin.PostEngin;

/**
 * Created by lykanthrop on 1507/29/.
 */
public class TrackItem
{
	public TrackItem(Cursor cursor)
	{
		title = cursor.getString(1);
		album = cursor.getString(2);
		artist = cursor.getString(3);
		duration = cursor.getLong(4);
		filepath = cursor.getString(5);
		artpath = null;
	}

	public void setArt(Cursor cursor)
	{
		artpath = cursor.getString(3);
	}

	public void setCurrent()
	{
		current = this;
	}

	public void setNext()
	{
		next = this;
	}

	public void setPrevious()
	{
		previous = this;
	}

	public boolean isCurrent()
	{
		return this==current;
	}

	public boolean isNext()
	{
		return this==next;
	}

	public boolean isPrevious()
	{
		return this==previous;
	}

	public static TrackItem getCurrent()
	{
		return current;
	}

	public static TrackItem getNext()
	{
		return next;
	}

	public static TrackItem getPrevious()
	{
		return previous;
	}

	public static void reset()
	{
		current = null;
		next = null;
		previous = null;
	}

	public static void resetCurrent()
	{
		current = null;
	}

	public static void resetNext()
	{
		next = null;
	}

	public static void resetPrevious()
	{
		previous = null;
	}

	public static void forward(TrackItem trm)
	{
		if(next!=null)
		{
			previous = current;
			current = next;
			next = trm;
		}
	}

	public static void backward(TrackItem trm)
	{
		if(previous!=null)
		{
			next = current;
			current = previous;
			previous = trm;
		}
	}

	public static boolean hasNext()
	{
		return next!=null;
	}

	public static boolean hasPrevious()
	{
		return previous!=null;
	}

	//Variables
	protected String title;
	protected String album;
	protected String artist;
	protected long duration;
	protected String filepath;
	protected String artpath;

	protected static TrackItem current = null;
	protected static TrackItem next = null;
	protected static TrackItem previous = null;
}
