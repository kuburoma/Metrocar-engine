package metrocar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Tables for database.
 * 
 * @author Roman Kubù
 *
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	//Names of tables
	public static final String TABLE_RECORD = "record";
	public static final String TABLE_RESERVATION = "reservation";
	
	//Columns for saved records
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGTITUDE = "longtitude";
	public static final String COLUMN_ALTITUDE = "altitude";
	public static final String COLUMN_ACCURACE = "accurace";
	public static final String COLUMN_M0105 = "m0105";
	public static final String COLUMN_M010C = "m010C";
	public static final String COLUMN_M010D = "m010D";
	public static final String COLUMN_M0111 = "m0111";
	
	//Columns for reservations gets from server
	public static final String COLUMN_USER = "user";
	public static final String COLUMN_USER_PASSWORD = "password";	
	public static final String COLUMN_RESERVATION_ID = "reservationId";
	public static final String COLUMN_RESERVATION_START = "start";
	public static final String COLUMN_RESERVATION_END = "end";
	
	//Column used in both tables
	public static final String COLUMN_USER_ID = "userId";

	
	private static final String DATABASE_NAME = "main.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_RESERVATION = "create table "
			+ TABLE_RESERVATION + "(" + COLUMN_RESERVATION_ID
			+ " integer primary key," + COLUMN_USER + " TEXT,"
			+ COLUMN_USER_PASSWORD + " TEXT," + COLUMN_RESERVATION_START
			+ " integer," + COLUMN_RESERVATION_END + " integer,"
			+ COLUMN_USER_ID + " integer" + ");";

	private static final String DATABASE_CREATE_RECORD = "create table "
			+ TABLE_RECORD + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_USER_ID
			+ " TEXT," + COLUMN_TIME + " TEXT," + COLUMN_LATITUDE + " TEXT,"
			+ COLUMN_LONGTITUDE + " TEXT," + COLUMN_ALTITUDE + " TEXT," + COLUMN_ACCURACE + " TEXT,"
			+ COLUMN_M0105 + " TEXT," + COLUMN_M010C + " TEXT," + COLUMN_M010D
			+ " TEXT," + COLUMN_M0111 + " TEXT" + ");";

	public MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_RECORD);
		database.execSQL(DATABASE_CREATE_RESERVATION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_RECORD);
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_RESERVATION);
		onCreate(db);
	}

}
