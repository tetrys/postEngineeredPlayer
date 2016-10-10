package lab.drys.postengin.vinylcamera;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lykanthrop on 1508/05/.
 */
public class VinylDbHelper extends SQLiteOpenHelper
{
	public VinylDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
	{
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(SQL_DELETE_ENTRIES);
		this.onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		this.onUpgrade(db, oldVersion, newVersion);
	}

	//Variables
	private static final String DB_NAME = "Vinyl.db";
	private static final int DB_VERSION = 1;

	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE "+VinylDbColumns.TABLE_NAME+" ( "+
			VinylDbColumns._ID+" INTEGER PRIMARY KEY,"+
			VinylDbColumns.COLUMN_NAME_VINYL_ID+"TEXT,"+
			VinylDbColumns.COLUMN_NAME_ALBUM_ID+"INTEGER"+
			VinylDbColumns.COLUMN_NAME_SIDE+"INTEGER,"+
			VinylDbColumns.COLUMN_NAME_DATA+"TEXT"+" )";

	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + VinylDbColumns.TABLE_NAME;
}
