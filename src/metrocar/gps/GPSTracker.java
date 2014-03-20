package metrocar.gps;

import metrocar.labels.ModLabels;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Class for getting GPS coordinates
 * 
 * @author Roman Kubù
 *
 */
public class GPSTracker extends Service {

	private final Context mContext;

	private static final String TAG = ModLabels.TAG_GPS_TRACKER;

	// flag for GPS status
	private boolean isGPSEnabled = false;
	private boolean isWIFIEnabled = false;

	private double latitude = 0;
	private double longitude = 0;
	private double altitude = 0;
	private double accuracy = 0;

	private Location locWifi;
	private Location locGPS;

	private boolean getWIFI;
	private boolean getGPS;

	// The minimum distance to change Updates in meters
	private long MIN_DISTANCE_CHANGE_FOR_UPDATES;
	// meters
	// The minimum time between updates in milliseconds
	private long MIN_TIME_BW_UPDATES;

	private boolean doLog = false;

	// Declaring a Location Manager
	private LocationManager locationGPS;
	private LocationManager locationWifi;

	/**
	 * Constructor
	 * 
	 * @param context Context of main application
	 * @param minTimeForUpdate Time in which should be GPS updated  
	 * @param minDistanceForUpdate Distance after which should be GPS updated
	 * @param doLog Logging of class
	 */
	public GPSTracker(Context context, long minTimeForUpdate,
			long minDistanceForUpdate, boolean doLog) {
		MIN_DISTANCE_CHANGE_FOR_UPDATES = minDistanceForUpdate;
		MIN_TIME_BW_UPDATES = minTimeForUpdate;
		this.mContext = context;
		locationGPS = (LocationManager) mContext
				.getSystemService(LOCATION_SERVICE);
		locationWifi = (LocationManager) mContext
				.getSystemService(LOCATION_SERVICE);

		// getting GPS status
		isGPSEnabled = locationGPS
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isWIFIEnabled = locationGPS
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		this.doLog = doLog;
		if (doLog)
			Log.d(TAG, "GPSTracker has been set up");

	}

	/**
	 * Starts getting GPS coordinates
	 */
	public void start() {
		locationGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, gps);

		locationWifi.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, wifi);

		if (doLog)
			Log.d(TAG, "GPSTracker is running");
	}

	/**
	 * Stop getting GPS coordinates
	 * 
	 */
	public void stop() {
		locationGPS.removeUpdates(gps);
		locationWifi.removeUpdates(wifi);
	}

	/**
	 * Test if sources of GPS are enabled.
	 * 
	 * @return True if at least one GPS provider is enabled.
	 * 
	 */
	public boolean isEnabled() {
		if (isGPSEnabled || isWIFIEnabled) {
			return true;
		}
		return false;
	}

	/**
	 * Get new GPS information and return if providers are enabled.
	 * 
	 * @return True if at least one of provider (GPS-Network) are enabled.
	 */
	public boolean refresh() {
		if (getGPS && getWIFI) {
			if (locGPS.getAccuracy() <= locWifi.getAccuracy()) {
				if (doLog)
					Log.d(TAG,
							"GPSTracker - GPS is more accurace then network provider");

				latitude = locGPS.getLatitude();
				longitude = locGPS.getLongitude();
				altitude = locGPS.getAltitude();
				accuracy = locGPS.getAccuracy();
			} else {
				if (doLog)
					Log.d(TAG,
							"GPSTracker - Network provider is more accurace then GPS");

				latitude = locWifi.getLatitude();
				longitude = locWifi.getLongitude();
				altitude = locWifi.getAltitude();
				accuracy = locWifi.getAccuracy();
			}
			getGPS = false;
			getWIFI = false;
			return true;
		}
		if (getGPS) {
			if (doLog)
				Log.d(TAG, "GPSTracker - Only GPS is running");
			latitude = locGPS.getLatitude();
			longitude = locGPS.getLongitude();
			altitude = locGPS.getAltitude();
			accuracy = locGPS.getAccuracy();
			getGPS = false;
			return true;
		}
		if (getWIFI) {
			if (doLog)
				Log.d(TAG, "GPSTracker - Only Network provider is running");
			latitude = locWifi.getLatitude();
			longitude = locWifi.getLongitude();
			altitude = locWifi.getAltitude();
			accuracy = locWifi.getAccuracy();
			getWIFI = false;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return GPS Latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return GPS Altitude
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * @return GPS Accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * @return GPS Longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	protected LocationListener gps = new LocationListener() {

		public void onLocationChanged(Location location) {
			if (doLog)
				Log.d(TAG, "GPSTracker - Location changed by GPS provider");

			getGPS = true;

			locGPS = location;
		}

		public void onProviderDisabled(String provider) {
			isGPSEnabled = false;
		}

		public void onProviderEnabled(String provider) {
			isGPSEnabled = true;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	protected LocationListener wifi = new LocationListener() {

		public void onLocationChanged(Location location) {
			if (doLog)
				Log.d(TAG, "GPSTracker - Location changed by network provider");

			getWIFI = true;

			locWifi = location;
		}

		public void onProviderDisabled(String provider) {
			isWIFIEnabled = false;
		}

		public void onProviderEnabled(String provider) {
			isWIFIEnabled = true;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

}
