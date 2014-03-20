package metrocar.internet;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import metrocar.database.DatabaseController;
import metrocar.database.Reservation;
import metrocar.labels.ModLabels;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Thread for getting reservations that use GetMetrocar class.
 * 
 * @author Roman Kubù
 * 
 */
public class GetReservations extends Thread {

	private GetMetrocar getmetrocar;
	private DatabaseController datasource;
	private ArrayList<Reservation> reservations;

	private static final String TAG = ModLabels.TAG_GET;
	private boolean doLog = false;
	private long end_advance;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Unit id
	 * @param key
	 *            Unit secret key
	 * @param datasource
	 *            DatabeseController controller that is open() already.
	 * @param context
	 */
	public GetReservations(String id, String key,
			DatabaseController datasource, Context context,
			CheckInternetConnection cm, Handler m, boolean doLog,
			long end_advance, String address) {
		this.doLog = doLog;
		if (doLog)
			Log.d(TAG, "constructor");
		this.end_advance = end_advance;
		getmetrocar = new GetMetrocar(Long.valueOf(id), key, cm, m, doLog,
				address, new DefaultHttpClient());
		this.datasource = datasource;
	}

	/* 
	 * Call request on GetMetrocar to get reservations from server.
	 * Then insert reservation into database. On end delete old reservation from database.
	 * 
	 */
	@Override
	public void run() {
		if (doLog)
			Log.d(TAG, "Get reservations");

		reservations = getmetrocar.request();
		if (reservations != null) {
			if (doLog)
				Log.d(TAG, "Create reservations");

			datasource.createReservation(reservations);
		}

		if (doLog)
			Log.d(TAG, "delete old reservation " + System.currentTimeMillis());
		datasource
				.deleteOldReservation(System.currentTimeMillis(), end_advance);

	}

}
