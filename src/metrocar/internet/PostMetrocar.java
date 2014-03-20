package metrocar.internet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import metrocar.database.DatabaseController;
import metrocar.database.Record;
import metrocar.labels.HandlerLabels;
import metrocar.labels.ModLabels;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

/**
 * Post records on server
 * 
 * @author Roman Kubù
 *
 */
public class PostMetrocar extends Thread {

	private static final String TAG = ModLabels.TAG_POST;
	private Handler mHandler;
	private String unitId;
	private String secretKey;
	private HttpResponse response;
	private HttpClient httpclient;
	private HttpPost httpost;
	private JSONObject holder;
	private JSONObject entry;
	private JSONArray entries;
	private CheckInternetConnection cic;
	private ArrayList<Record> rec;
	private DatabaseController dc;
	private String limitOfSendIntems;
	private boolean doLog;

	public PostMetrocar(CheckInternetConnection cic, Handler h, String unitId,
			String secretKey, DatabaseController dc, String address, String limitOfSendIntems, boolean doLog) {
		this.unitId = unitId;
		this.secretKey = secretKey;
		mHandler = h;
		httpost = new HttpPost(address);
		this.cic = cic;
		this.dc = dc;
		this.doLog = doLog;
		this.limitOfSendIntems = limitOfSendIntems;
	}

	public void addRecord(String userId, String time, String latitude,
			String longtitude, String altitude, String accurace, String m0105,
			String m010C, String m010D, String m0111) {
		dc.createRecord(userId, time, longtitude, latitude, altitude, accurace,
				m0105, m010C, m010D, m0111);
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void handlerMessage(int identification, String value) {
		mHandler.obtainMessage(HandlerLabels.POST, identification, -1, value)
				.sendToTarget();
	}

	@Override
	public synchronized void run() {
		try {
			request(new DefaultHttpClient());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void request(HttpClient httpClient) throws Exception {
		if(doLog)
			Log.d(TAG, "starting");
		httpclient = httpClient;
		try {
			while (cic.haveNetworkConnection()) {
				rec = dc.getLimitedRecords(limitOfSendIntems);
				if(doLog)
					Log.d(TAG, "records returned: "+rec.size());
				if (rec == null) {
					return;
				}
				if (rec.size() == 0) {
					return;
				}
				response = makeRequest(rec);
				if(response == null){
					if(doLog)
						Log.d(TAG, "responce is null");
				}

				int responseCode = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				String html = "";
				try {
					html = EntityUtils.toString(response.getEntity(), "utf-8");
				} catch (Exception e) {
					handlerMessage(HandlerLabels.POST_ERROR,
							"Cannot parse responce from post request");
					return;
				}

				if (responseCode != 200) {
					if (responseCode == 400) {
						handlerMessage(HandlerLabels.POST_AUTHENTICATION_FAILED,
								"Authentication Failed");
					}
					if (entity != null) {
						String rc = String.valueOf(responseCode);
						String responseBody = rc + " - " + html;
						handlerMessage(HandlerLabels.POST_WARNING,
								"Responce code: " + rc);
						if(doLog)
							Log.d(TAG, responseBody);
					}
					return;
				} else {
					if(doLog)
						Log.d(TAG, "send on post: "+rec.size());
					handlerMessage(HandlerLabels.POST_OK, "ok");
					dc.deleteRecords(rec);
					if (rec.size() < 10) {
						if(doLog)
							Log.d(TAG, "ended");
						return;
					}
				}
			}
			handlerMessage(HandlerLabels.POST_NO_INTERNET_CONNECTION,
					"No internet connection");
		} catch (Exception e) {
			handlerMessage(HandlerLabels.POST_ERROR, "Error occured");
			e.printStackTrace();
		}
	}

	private JSONObject RecordToJSONString(ArrayList<Record> record) {
		try {
			holder = new JSONObject();
			entry = new JSONObject();
			entries = new JSONArray();

			holder.put("unit_id", unitId);
			holder.put("secret_key", secretKey);

			Iterator<Record> it = record.iterator();

			Record help;
			JSONArray position;
			position = new JSONArray();

			// ----------------------------------------------------

			double lat;
			double lon;

			long yourmilliseconds;
			SimpleDateFormat sdf;
			Date resultdate;
			String date;

			int velocity;
			int engineRpm;
			int throttle;
			int engineTmp;
			int userId;
			double altitude;
			double accurace;

			while (it.hasNext()) {
				entry = new JSONObject();
				position = new JSONArray();

				help = it.next();

				lat = Double.valueOf(help.getLatitude());
				lon = Double.valueOf(help.getLongtituted());
				position.put(lat);
				position.put(lon);
				entry.put("location", position);

				yourmilliseconds = Long.valueOf(help.getTime());
				sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				resultdate = new Date(yourmilliseconds);
				date = sdf.format(resultdate);
				entry.put("timestamp", date);

				velocity = Integer.valueOf(help.getM010D());
				engineRpm = Integer.valueOf(help.getM010C());
				throttle = Integer.valueOf(help.getM0111());
				engineTmp = Integer.valueOf(help.getM0105());
				userId = Integer.valueOf(help.getUserId());
				altitude = Double.valueOf(help.getAltitude());
				accurace = Double.valueOf(help.getAccurace());

				entry.put("user_id", userId);
				entry.put("velocity", velocity);
				entry.put("engine_temp", engineTmp);
				entry.put("engine_rpm", engineRpm);
				entry.put("throttle", throttle);
				entry.put("altitude", altitude);
				entry.put("gps_accuracy", accurace);
				entries.put(entry);
			}
			holder.put("entries", entries);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

	private HttpResponse makeRequest(ArrayList<Record> record)
			throws ClientProtocolException, IOException {

		JSONObject json = RecordToJSONString(record);
		
		StringEntity se;
		se = new StringEntity(json.toString(), "utf-8");

		// sets the post request as the resulting string
		httpost.setEntity(se);

		httpost.setHeader("Content-type", "application/json");
		// Handles what is returned from the page
		return httpclient.execute(httpost);
	}

}
