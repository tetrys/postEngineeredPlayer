package lab.drys.postengin.dj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/29/.
 */
public class PlayQueueArrayAdapter extends ArrayAdapter<TrackItem>
{
	public PlayQueueArrayAdapter(Context context, ArrayList<TrackItem> trk)
	{
		super(context, R.layout.list_item,trk);

		this.tracks = trk;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if(convertView==null)
		{
			holder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.subtitle = (TextView)convertView.findViewById(R.id.subtitle);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		}

		holder = (ViewHolder)convertView.getTag();

		holder.title.setText(tracks.get(position).title);
		holder.subtitle.setText(tracks.get(position).album);

		if(tracks.get(position).isCurrent())
		{
			holder.icon.setImageResource(R.drawable.ic_music_note_black_48dp);
		}
		else
		{
			holder.icon.setImageResource(R.drawable.ic_album_black_48dp);
		}

		return convertView;
	}

	private static class ViewHolder
	{
		protected TextView title;
		protected TextView subtitle;
		protected ImageView icon;
	}

	//Variables
	Context context;
	ArrayList<TrackItem> tracks;
}
