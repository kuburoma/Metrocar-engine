package metrocar.internet;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetConnection {
	
	ConnectivityManager cm;
	
	public CheckInternetConnection(ConnectivityManager cm){
			this.cm = cm; 
	}
	
	/**
	 * Test if network connection is set up and running.
	 * 
	 * @return True if network connection is set up.
	 */
	public boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	
	
	/**
	 * Try is network connection over WIFI or MOBILE is available.
	 * It means that WIFI or MOBILE connection is on. Does not need to be connected.
	 * 
	 * @return True if network connection is available.
	 */
	public boolean isNetworkAvailable() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isAvailable())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isAvailable())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
}
