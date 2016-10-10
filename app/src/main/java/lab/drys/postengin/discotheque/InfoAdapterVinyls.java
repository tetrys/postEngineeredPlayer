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

import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/26/.
 */
public class InfoAdapterVinyls extends CursorAdapter
{
	public InfoAdapterVinyls(Context context, Cursor cursor)
	{
		super(context,cursor,0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		TextView artist = (TextView)view.findViewById(R.id.title);
		TextView albums = (TextView)view.findViewById(R.id.subtitle);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);

		artist.setText(cursor.getString(0));
		albums.setText(cursor.getString(1));

		File image = new File(context.getApplicationInfo().dataDir+"/artists/"+cursor.getString(0)+".jpg");

		if(image.exists())
		{
			Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
			icon.setImageBitmap(bitmap);
		}
	}
}
