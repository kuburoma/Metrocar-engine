package metrocar.database;

import java.util.ArrayList;
import java.util.Iterator;

import metrocar.labels.ModLabels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database controller.
 * 
 * @author Roman Kubù
 * 
 */
public class DatabaseController {

	// Debugging
	private static final String TAG = ModLabels.TAG_DATABASE_CONTROLLER;
	private boolean doLog = false;

	private SQLiteDatabase database;
	private MySQLiteOpenHelper dbHelper;

	private String[] allColumnsRecord = { MySQLiteOpenHelper.COLUMN_ID,
			MySQLiteOpenHelper.COLUMN_USER_ID, MySQLiteOpenHelper.COLUMN_TIME,
			MySQLiteOpenHelper.COLUMN_LATITUDE,
			MySQLiteOpenHelper.COLUMN_LONGTITUDE,
			MySQLiteOpenHelper.COLUMN_ALTITUDE,
			MySQLiteOpenHelper.COLUMN_ACCURACE,
			MySQLiteOpenHelper.COLUMN_M0105, MySQLiteOpenHelper.COLUMN_M010C,
			MySQLiteOpenHelper.COLUMN_M010D, MySQLiteOpenHelper.COLUMN_M0111 };
	private String[] allColumnsReservation = {
			MySQLiteOpenHelper.COLUMN_RESERVATION_ID,
			MySQLiteOpenHelper.COLUMN_USER,
			MySQLiteOpenHelper.COLUMN_USER_PASSWORD,
			MySQLiteOpenHelper.COLUMN_RESERVATION_START,
			MySQLiteOpenHelper.COLUMN_RESERVATION_END,
			MySQLiteOpenHelper.COLUMN_USER_ID };

	public DatabaseController(Context context, boolean doLog) {
		dbHelper = new MySQLiteOpenHelper(context);
		this.doLog = doLog;
		if (doLog)
			Log.d(TAG, "constructor");
	}

	public void open() throws SQLException {
		database = dbHelper.getReadableDatabase();
		if (doLog)
			Log.d(TAG, "open()");
	}

	public void close() {
		if (doLog)
			Log.d(TAG, "close()");
		dbHelper.close();
	}

	/**
	 * Insert record into database.
	 * 
	 * 
	 * @param userId
	 *            Id of user.
	 * @param time
	 *            Time when this record was taken.
	 * @param latitude
	 * @param longtitude
	 * @param altitude
	 * @param m0105
	 *            Engine coolant temperature
	 * @param m010C
	 *            Engine RPM
	 * @param m010D
	 *            Vehicle speed
	 * @param m0111
	 *            Throttle position
	 */
	public void createRecord(String userId, String time, String latitude,
			String longtitude, String altitude, String accurace, String m0105,
			String m010C, String m010D, String m0111) {

		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.COLUMN_USER_ID, userId);
		values.put(MySQLiteOpenHelper.COLUMN_TIME, time);
		values.put(MySQLiteOpenHelper.COLUMN_LATITUDE, latitude);
		values.put(MySQLiteOpenHelper.COLUMN_LONGTITUDE, longtitude);
		values.put(MySQLiteOpenHelper.COLUMN_ALTITUDE, altitude);
		values.put(MySQLiteOpenHelper.COLUMN_ACCURACE, accurace);
		values.put(MySQLiteOpenHelper.COLUMN_M0105, m0105);
		values.put(MySQLiteOpenHelper.COLUMN_M010C, m010C);
		values.put(MySQLiteOpenHelper.COLUMN_M010D, m010D);
		values.put(MySQLiteOpenHelper.COLUMN_M0111, m0111);

		database.insert(MySQLiteOpenHelper.TABLE_RECORD, null, values);

		if (doLog)
			Log.d(TAG, "record created");
	}

	/**
	 * Insert reservation into database.
	 * 
	 * @param res
	 *            Reservation
	 * 
	 */
	public void createReservation(Reservation res) {
		if (doLog)
			Log.d(TAG, "DatabaseController - creating reservation");

		Log.d(TAG,
				res.getReservationId() + " - " + res.getUser() + " - "
						+ res.getStart() + " - " + res.getEnd() + " - "
						+ res.getUserId());
		ContentValues values = new ContentValues();
		values.put(MySQLiteOpenHelper.COLUMN_RESERVATION_ID,
				res.getReservationId());
		values.put(MySQLiteOpenHelper.COLUMN_USER, res.getUser());
		values.put(MySQLiteOpenHelper.COLUMN_USER_PASSWORD, res.getPassword());
		values.put(MySQLiteOpenHelper.COLUMN_RESERVATION_START, res.getStart());
		values.put(MySQLiteOpenHelper.COLUMN_RESERVATION_END, res.getEnd());
		values.put(MySQLiteOpenHelper.COLUMN_USER_ID, res.getUserId());
		database.insert(MySQLiteOpenHelper.TABLE_RESERVATION, null, values);
		if (doLog)
			Log.d(TAG, "reservation created");

	}

	/**
	 * Insert ArrayList<Reservation> inserting Reservation into database. If is
	 * in database Reservation with same reservation id reservation will not be
	 * inserted.
	 * 
	 * @param res
	 *            ArrayList of Reservation
	 */
	public void createReservation(ArrayList<Reservation> res) {
		if (doLog)
			Log.d(TAG, "create reservations");
		Iterator<Reservation> it = res.iterator();
		Reservation help;
		while (it.hasNext()) {
			help = it.next();
			if (!getReservationsById(help.getReservationId())) {
				createReservation(help);
			}
		}
	}

	/**
	 * Return all records in database.
	 * 
	 * @return ArrayList Record
	 */
	public ArrayList<Record> getAllRecords() {
		if (doLog)
			Log.d(TAG, "get all records");
		ArrayList<Record> comments = new ArrayList<Record>();

		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_RECORD,
				allColumnsRecord, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Record record = cursorToRecord(cursor);
			comments.add(record);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		if (doLog)
			Log.d(TAG, comments.size() + "is returned");
		return comments;
	}

	/**
	 * Return limited number of records in database.
	 * 
	 * @return ArrayList Record
	 */
	public ArrayList<Record> getLimitedRecords(String post_limit) {
		if (doLog)
			Log.d(TAG, "get limited records");
		ArrayList<Record> comments = new ArrayList<Record>();

		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_RECORD,
				allColumnsRecord, null, null, null, null, null, post_limit);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Record record = cursorToRecord(cursor);
			comments.add(record);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		if (doLog)
			Log.d(TAG, comments.size() + "is returned");
		return comments;
	}

	/**
	 * Returns all reservations from database
	 * 
	 * @return ArrayList Reservation
	 */
	public ArrayList<Reservation> getAllReservations() {
		if (doLog)
			Log.d(TAG, "get all reservations");
		ArrayList<Reservation> comments = new ArrayList<Reservation>();

		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_RESERVATION,
				allColumnsReservation, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Reservation record = cursorToReservation(cursor);
			comments.add(record);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		if (doLog)
			Log.d(TAG, comments.size() + "is returned");
		return comments;
	}

	/**
	 * Check if reservation with same reservation id is not already in database.
	 * 
	 * @param id
	 *            Reservation id
	 * @return True if exist.
	 */
	public boolean getReservationsById(long id) {

		ArrayList<Reservation> res = getAllReservations();
		Iterator<Reservation> it = res.iterator();
		while (it.hasNext()) {
			Reservation help = it.next();
			Log.d(TAG, help.getUser() + " " + help.getReservationId());
			if (help.getReservationId() == id) {
				if (doLog)
					Log.d(TAG, "reservation with id: " + id + " exist");
				return true;
			}
		}
		if (doLog)
			Log.d(TAG, "reservation with id: " + id + " doesnt exist");
		return false;
	}

	/**
	 * Return all reservations of user define by user name.
	 * 
	 * @param user
	 *            User name
	 * @return ArrayList Reservation
	 */
	public ArrayList<Reservation> getUserReservation(String user) {
		if (doLog)
			Log.d(TAG, "get reservations for user");

		ArrayList<Reservation> comments = new ArrayList<Reservation>();

		Cursor cursor = database.query(MySQLiteOpenHelper.TABLE_RESERVATION,
				allColumnsReservation, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Reservation record = cursorToReservation(cursor);
			if (record.getUser().equals(user)) {
				comments.add(record);
			}
			cursor.moveToNext();
		}
		cursor.close();
		if (doLog)
			Log.d(TAG, comments.size() + "is returned");
		return comments;
	}

	public void deleteRecords(ArrayList<Record> rc) {
		if (doLog)
			Log.d(TAG, "delecte records");
		Iterator<Record> it = rc.iterator();
		while (it.hasNext()) {
			deleteRecord(it.next().getId());
		}
	}

	/**
	 * Delete record with matching id.
	 * 
	 * @param id
	 *            ID of record
	 */
	public void deleteRecord(long id) {
		database.delete(MySQLiteOpenHelper.TABLE_RECORD,
				MySQLiteOpenHelper.COLUMN_ID + " = " + id, null);
	}

	/**
	 * Delete reservation with matching id.
	 * 
	 * @param id
	 *            ID of record
	 */
	public void deleteReservation(long id) {
		database.delete(MySQLiteOpenHelper.TABLE_RESERVATION,
				MySQLiteOpenHelper.COLUMN_ID + " = " + id, null);
	}

	/**
	 * 
	 * Delete all reservations which time end is smaller then inputet time
	 * 
	 * @param end
	 *            Real time
	 */
	public void deleteOldReservation(long end, long end_advance) {
		end += end_advance;
		if (doLog)
			Log.d(TAG, "delecte old reservation: " + end);
		database.delete(MySQLiteOpenHelper.TABLE_RESERVATION,
				MySQLiteOpenHelper.COLUMN_RESERVATION_END + " < " + end, null);

	}

	private Reservation cursorToReservation(Cursor cursor) {
		Reservation comment = new Reservation();
		comment.setReservationId(cursor.getLong(0));
		comment.setUser(cursor.getString(1));
		comment.setPassword(cursor.getString(2));
		comment.setStart(cursor.getLong(3));
		comment.setEnd(cursor.getLong(4));
		comment.setUserId(cursor.getLong(5));
		return comment;
	}

	private Record cursorToRecord(Cursor cursor) {
		Record comment = new Record();
		comment.setId(cursor.getLong(0));
		comment.setUserId(cursor.getString(1));
		comment.setTime(cursor.getString(2));
		comment.setLatitude(cursor.getString(3));
		comment.setLongtituted(cursor.getString(4));
		comment.setAltitude(cursor.getString(5));
		comment.setAccurace(cursor.getString(6));
		comment.setM0105(cursor.getString(7));
		comment.setM010C(cursor.getString(8));
		comment.setM010D(cursor.getString(9));
		comment.setM0111(cursor.getString(10));
		return comment;
	}
}
