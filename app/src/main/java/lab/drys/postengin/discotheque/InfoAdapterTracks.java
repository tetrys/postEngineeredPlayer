package lab.drys.postengin.discotheque;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/27/.
 */
public class InfoAdapterTracks extends CursorAdapter
{
	public InfoAdapterTracks(Context context, Cursor cursor)
	{
		super(context,cursor,0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		ViewHolder vh = new ViewHolder();

		View view = LayoutInflater.from(context).inflate(R.layout.list_item,null);

		vh.title = (TextView)view.findViewById(R.id.title);
		vh.subtitle = (TextView)view.findViewById(R.id.subtitle);
		vh.icon = (ImageView)view.findViewById(R.id.icon);

		view.setTag(vh);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		ViewHolder holder = (ViewHolder)view.getTag();

		holder.title.setText(cursor.getString(1));
		holder.subtitle.setText(cursor.getString(2));
	}

	@Override
	public int getCount()
	{
		int out = 0;
		if(this.getCursor()!=null)
		{
			out = this.getCursor().getCount();
		}
		return out;
	}

	private static class ViewHolder
	{
		protected TextView title;
		protected TextView subtitle;
		protected ImageView icon;
	}
}
