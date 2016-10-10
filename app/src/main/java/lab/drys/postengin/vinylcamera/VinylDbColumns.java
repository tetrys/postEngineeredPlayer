package lab.drys.postengin.vinylcamera;

import android.provider.BaseColumns;

/**
 * Created by lykanthrop on 1508/05/.
 */
public abstract class VinylDbColumns implements BaseColumns
{
	public static final String TABLE_NAME = "vinyl";
	public static final String COLUMN_NAME_VINYL_ID = "vinyl_id";
	public static final String COLUMN_NAME_ALBUM_ID = "album_id";
	public static final String COLUMN_NAME_SIDE = "side";
	public static final String COLUMN_NAME_DATA = "data";
}
