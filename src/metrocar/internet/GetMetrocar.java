package metrocar.internet;

import java.net.URI;
import java.util.ArrayList;

import metrocar.database.Reservation;
import metrocar.labels.HandlerLabels;
import metrocar.labels.ModLabels;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Handler;
import android.util.Log;

/**
 * Gets Reservation from server.
 * 
 * @author Roman Kubù
 *
 */
public class GetMetrocar {

	private long unitId;
	private String secretKey;
	private HttpResponse response;
	private HttpClient httpclient;
	private CheckInternetConnection cm;
	private Handler mHandler;
	private ArrayList<Reservation> res;

	private static final String TAG = ModLabels.TAG_GET;
	private boolean doLog = false;
	private String address;

	/**
	 * Constructor
	 * 
	 * @param id Unit ID
	 * @param key Secret Key
	 * @param cm CheckInternetConnection
	 * @param m	Handler where all main messages will be sent
	 * @param doLog Log class
	 * @param address Address of server GET api
	 * @param httpclient HttpClient
	 */
	public GetMetrocar(long id, String key, CheckInternetConnection cm,
			Handler m, boolean doLog, String address, HttpClient httpclient) {
		this.doLog = doLog;
		secretKey = key;
		unitId = id;
		this.cm = cm;
		mHandler = m;
		this.doLog = doLog;
		this.address = address;
		this.httpclient = httpclient;
	}

	public void handlerMessage(int identification, String value) {
		mHandler.obtainMessage(HandlerLabels.GET, identification, -1, value)
				.sendToTarget();
	}

	/**
	 * @return Unit ID
	 */
	public long getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	/**
	 * @return Secret Key
	 */
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * If Internet connection is available request Get operation from server. If
	 * no reservation is received from server return is null.
	 * 
	 * @return Reservations or null if there is no Internet connection or when
	 *         no Reservation is received from server.
	 */
	public ArrayList<Reservation> request() {
		try {
			if (!cm.haveNetworkConnection()) {
				handlerMessage(HandlerLabels.GET_NO_INTERNET_CONNECTION,
						"No internet connection");
				if (doLog)
					Log.d(TAG, "No internet connection");
				return null;
			}
			response = makeRequest(unitId, secretKey);

			StatusLine line = response.getStatusLine();
			int responseCode = line.getStatusCode();
			if (responseCode != 200) {
				if (responseCode == 400) {
					handlerMessage(HandlerLabels.GET_AUTHENTICATION_FAILED,
							"Authentication Failed");
					if (doLog)
						Log.d(TAG, "Authentication Failed");
				} else {
					handlerMessage(HandlerLabels.GET_WARNING, "Responce code: "
							+ responseCode);
					if (doLog)
						Log.d(TAG, "Responce code: " + responseCode);
				}
				return null;
			} else {
				handlerMessage(HandlerLabels.GET_OK, "ok");
				String html = EntityUtils.toString(response.getEntity(),
						"utf-8");
				if (doLog)
					Log.d(TAG, html);
				res = JSONtoReservation(html);
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			handlerMessage(HandlerLabels.GET_ERROR, "Error occured");
			if (doLog)
				Log.d(TAG, "Error occured");
			return null;
		}

	}

	private ArrayList<Reservation> JSONtoReservation(String json) {
		ArrayList<Reservation> cc = new ArrayList<Reservation>();
		JSONObject object;
		try {
			object = new JSONObject(new JSONTokener(json));
			JSONArray reservations = object.getJSONArray("reservations");
			String start;
			String end;
			JSONObject userJSON;
			String user;
			String password;
			String reservationId;
			long userId;
			Reservation res;

			for (int i = 0; i < reservations.length(); i++) {
				object = reservations.getJSONObject(i);

				start = object.getString("start");
				end = object.getString("end");
				reservationId = object.getString("reservationId");
				userJSON = object.getJSONObject("user");
				user = userJSON.getString("username");
				password = userJSON.getString("password");
				userId = userJSON.getLong("id");

				res = new Reservation();
				res.setReservationId(Long.valueOf(reservationId));
				res.setStart(start);
				res.setEnd(end);
				res.setPassword(password);
				res.setUser(user);
				res.setUserId(Long.valueOf(userId));
				cc.add(res);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return cc;
	}

	private HttpResponse makeRequest(long id, String secretKey) throws Exception {

		BasicHttpParams params = new BasicHttpParams();
		params.setParameter("unit_id", id); // The access token I am getting
											// after
		// the Login
		params.setParameter("secret_key", secretKey);

		JSONObject a = new JSONObject();
		try {
			a.put("unit_id", id);
			a.put("secret_key", secretKey);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringEntity se = new StringEntity(a.toString(), "utf-8");

		// sets the post request as the resulting string

		HttpGetWithEntity e = new HttpGetWithEntity();
		URI absolute = new URI(address);

		e.setEntity(se);
		e.setURI(absolute);

		e.setHeader("Accept", "application/json"); // or
													// application/jsonrequest
		e.setParams(params);
		e.setHeader("Content-type", "application/json");

		// Handles what is returned from the page
		return httpclient.execute(e);
	}
}
