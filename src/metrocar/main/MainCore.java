package metrocar.main;

import metrocar.commands.*;
import metrocar.utils.*;
import metrocar.internet.*;
import metrocar.labels.*;
import metrocar.gps.*;
import metrocar.engine.*;
import metrocar.database.*;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Main class simulating work of Car Unit
 * 
 * @author Roman Kubù
 * 
 */
@SuppressLint("HandlerLeak")
public class MainCore {

	private DatabaseController datasource;
	private CheckInternetConnection internetCheck;
	private PostMetrocar post;
	private GPSTracker gps;
	private UnitEngine unitEngine;
	private LockEngine lockEngine;

	private ScheduledThreadPoolExecutor executorPost;
	private ScheduledThreadPoolExecutor executorGet;

	private String v0105 = "0";
	private String v010C = "0";
	private String v010D = "0";
	private String v0111 = "0";

	private String latitude = "0";
	private String longtitude = "0";
	private String altitude = "0";
	private String accuracy = "0";

	private Handler viewHandler;

	private long newTime;
	private long oldTime;
	private int resultTime;

	private TimeCount time;
	private String userId;

	private String unitId;
	private String secretKey;
	private String addressObd;
	private String addressLock;
	private String lockKey;

	private GetReservations getReservation;
	private ControlPassword cp;
	private Context context;
	private boolean useLock;

	private BluetoothDevice obdDevice;
	private BluetoothDevice lockDevice;
	private ConnectivityManager cm;
	private UnitSettings set;
	private ArrayList<PidMessage> pidList;

	/**
	 * MainCore is not running
	 */
	public static final int STATE_NONE = 0;

	/**
	 * MainCore is in state waiting for user authorization
	 */
	public static final int STATE_AUTHORIZED_USER = 1;

	/**
	 * MainCore is in state for authorized user
	 */
	public static final int STATE_UNAUTHORIZED_USER = 2;

	public static final int STATE_NO_USER = 3;

	private int state;

	public MainCore(Context context, ConnectivityManager cm, String addressObd,
			Handler mHandler) {
		this.viewHandler = mHandler;
		this.context = context;
		this.addressObd = addressObd;
		this.useLock = false;
		this.cm = cm;
		this.set = new UnitSettings();
		state = STATE_NO_USER;

	}

	/**
	 * Constructor without imobilization and with default settings
	 */
	public MainCore(Context context, ConnectivityManager cm, String unitId,
			String secretKey, String addressObd, Handler mHandler) {
		this(context, cm, addressObd, mHandler);
		this.viewHandler = mHandler;
		this.context = context;
		this.unitId = unitId;
		this.secretKey = secretKey;
		this.addressObd = addressObd;
		this.useLock = false;
		this.cm = cm;
		this.set = new UnitSettings();
		state = STATE_NONE;

	}

	/**
	 * Constructor without imobilization and with UnitSettings
	 */
	public MainCore(Context context, ConnectivityManager cm, String unitId,
			String secretKey, String addressObd, Handler mHandler,
			UnitSettings set) {
		this(context, cm, unitId, secretKey, addressObd, mHandler);
		this.set = set;
	}

	/**
	 * Constructor with imobilization and with default settings
	 */
	public MainCore(Context context, ConnectivityManager cm, String unitId,
			String secretKey, String addressObd, String addressLock,
			String lockKey, Handler mHandler) {
		this(context, cm, unitId, secretKey, addressObd, mHandler);
		this.addressLock = addressLock;
		this.lockKey = lockKey;
		this.useLock = true;
		this.set = new UnitSettings();
	}

	/**
	 * Constructor without imobilization and with UnitSettings
	 */
	public MainCore(Context context, ConnectivityManager cm, String unitId,
			String secretKey, String addressObd, String addressLock,
			String lockKey, Handler mHandler, UnitSettings set) {
		this(context, cm, unitId, secretKey, addressObd, addressLock, lockKey,
				mHandler);
		this.set = set;
		this.useLock = true;
	}

	/**
	 * Runs test of UnitEngine, GetReservation and if Constructor with
	 * Imobilization was used then LockEngine.
	 * 
	 * 
	 * @return False when Unit ID, Secret Key, Address OBD, Address Lock, Lock
	 *         Key wasnt set.
	 */
	public boolean test() {
		if ((!unitId.equals("")) && (!secretKey.equals(""))
				&& (!addressObd.equals(""))) {
			if (useLock) {
				if (!(!addressLock.equals("") && (!lockKey.equals("")))) {
					return false;
				}
			}
			preparation();
			unitEngine = new UnitEngine(context, testHandler, obdDevice,
					pidList, set.isDoLogUnitEngine());
			unitEngine.start();
			getReservation.start();
			if (useLock) {
				lockEngine = new LockEngine(context, testHandler, lockDevice,
						lockKey, set.isDoLogLockEngine());
				lockEngine.start();
				lockEngine.test(lockKey);
			}
			return true;
		}
		return false;
	}

	/**
	 * Starts getting reservations, post records, and lock engine when was set,
	 * for unatuhorized user. Change state to STATE_UNAUTHORIZED_USER. Can be
	 * called only from state STATE_NONE.
	 * 
	 * @return False when Unit ID, Secret Key, Address OBD, Address Lock, Lock
	 *         Key wasnt set.
	 */
	public boolean setUp() {
		if (state == STATE_NO_USER) {
			return false;
		}
		if (state != STATE_NONE) {
			return false;
		}
		if ((!unitId.equals("")) && (!secretKey.equals(""))
				&& (!addressObd.equals(""))) {
			if (useLock) {
				if (!(!addressLock.equals("") && (!lockKey.equals("")))) {
					return false;
				}
			}
			preparation();
			executorGet = new ScheduledThreadPoolExecutor(1);
			executorGet.scheduleAtFixedRate(getReservation, 0,
					set.getUnauthorizedGetPeriod(), TimeUnit.MINUTES);
			executorPost = new ScheduledThreadPoolExecutor(1);
			executorPost.scheduleAtFixedRate(post, 0,
					set.getUnauthorizedPostPeriod(), TimeUnit.MINUTES);
			if (useLock) {
				lockEngine.start();
			}
			state = STATE_UNAUTHORIZED_USER;
			return true;
		}
		return false;
	}

	/**
	 * Stops getting reservations, post records, and lock engine when was set,
	 * for unatuhorized user.
	 * 
	 * Change state to STATE_NONE. Can be called only from state
	 * STATE_UNAUTHORIZED_USER.
	 * 
	 * @return False when MainCore is not in state STATE_UNAUTHORIZED_USER
	 */
	public boolean close() {
		if (state == STATE_NO_USER) {
			return false;
		}
		if (state != STATE_UNAUTHORIZED_USER) {
			return false;
		}
		executorGet.shutdown();
		executorPost.shutdown();
		if (useLock) {
			lockEngine.stop();
		}
		datasource.close();
		state = STATE_NONE;
		return true;

	}

	/**
	 * Controls if User Name and password match active reservation. When active
	 * reservation is found is started getting reservation and posting record
	 * for authorized user also call LockEngine.open() when Imobilization is
	 * set. Starts connection to OBD Unit, getting GPS coordinates.
	 * 
	 * Change state to STATE_AUTHORIZED_USER. Can be called only from state
	 * STATE_UNAUTHORIZED_USER.
	 * 
	 * @param userName
	 *            User Name
	 * @param userPassword
	 *            Password
	 * @return False when MainCore is not in state STATE_UNAUTHORIZED_USER or
	 *         when no active reservation was found for user.
	 */
	public boolean authorizedUser(String userName, String userPassword) {
		if (state == STATE_NO_USER) {
			return false;
		}
		if (state != STATE_UNAUTHORIZED_USER) {
			return false;
		}
		cp = new ControlPassword(datasource, userName, userPassword,
				set.getOpen_advance(), set.getEnd_advance());
		if (cp.controlPassword()) {
			userId = String.valueOf(cp.userId());
			startRecording();
			if (useLock) {
				lockEngine.open();
			}
			state = STATE_AUTHORIZED_USER;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Stops getting reservation, getting GPS coordinates, change period of post
	 * with record on time for unathorized user also made same for getting
	 * reservation. And if Imobilization is set up then call LockEngine.close()
	 * 
	 * Change state to STATE_UNAUTHORIZED_USER. Can be called only from state
	 * STATE_AUTHORIZED_USER.
	 * 
	 * @return false when MainCOre is not in state STATE_AUTHORIZED_USER
	 */
	public boolean unauthorizedUser() {
		if (state == STATE_NO_USER) {
			return false;
		}
		if (state != STATE_AUTHORIZED_USER) {
			return false;
		}
		if (useLock) {
			lockEngine.close();
		}
		unitEngine.stop();
		gps.stop();
		executorPost.shutdownNow();
		executorPost = new ScheduledThreadPoolExecutor(1);
		executorPost.scheduleAtFixedRate(post, 0,
				set.getUnauthorizedGetPeriod(), TimeUnit.MINUTES);
		state = STATE_UNAUTHORIZED_USER;
		return true;
	}

	public int getState() {
		return state;
	}

	private void preparation() {
		datasource = new DatabaseController(context,
				set.isDoLogDatabaseController());
		internetCheck = new CheckInternetConnection(cm);
		datasource.open();
		gps = new GPSTracker(context, set.getMin_time_bw_updates(),
				set.getMin_distance_change_for_server(),
				set.isDoLogGPSTracker());
		pidList = new ArrayList<PidMessage>();
		pidList.add(new Message0105(controlHandler, set.isDoLogObdMessage()));
		pidList.add(new Message010C(controlHandler, set.isDoLogObdMessage()));
		pidList.add(new Message010D(controlHandler, set.isDoLogObdMessage()));
		pidList.add(new Message0111(controlHandler, set.isDoLogObdMessage()));
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		obdDevice = btAdapter.getRemoteDevice(addressObd);
		if (useLock) {
			lockDevice = btAdapter.getRemoteDevice(addressLock);
		}
		getReservation = new GetReservations(unitId, secretKey, datasource,
				context, internetCheck, testHandler, set.isDoLogGet(),
				set.getEnd_advance(), set.getGet_request());
		post = new PostMetrocar(internetCheck, controlHandler, unitId,
				secretKey, datasource, set.getPost(), set.getPost_limit(),
				set.isDoLogPost());
		if (useLock) {
			lockDevice = btAdapter.getRemoteDevice(addressLock);
			lockEngine = new LockEngine(context, controlHandler, lockDevice,
					lockKey, set.isDoLogLockEngine());
		}
		time = new TimeCount();
	}

	public void startRecordingNoUser() {
		gps = new GPSTracker(context, set.getMin_time_bw_updates(),
				set.getMin_distance_change_for_server(),
				set.isDoLogGPSTracker());
		pidList = new ArrayList<PidMessage>();
		pidList.add(new Message0105(controlHandler, set.isDoLogObdMessage()));
		pidList.add(new Message010C(controlHandler, set.isDoLogObdMessage()));
		pidList.add(new Message010D(controlHandler, set.isDoLogObdMessage()));
		pidList.add(new Message0111(controlHandler, set.isDoLogObdMessage()));
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		obdDevice = btAdapter.getRemoteDevice(addressObd);
		time = new TimeCount();

		unitEngine = new UnitEngine(context, controlHandler, obdDevice,
				pidList, set.isDoLogUnitEngine());
		unitEngine.start();
		gps.start();
	}

	public void stopRecordingNoUser() {
		unitEngine.stop();
		gps.stop();
	}

	private ShowValues cycleCompleted() {
		newTime = System.currentTimeMillis();
		if (gps.refresh()) {
			latitude = String.valueOf(gps.getLatitude());
			longtitude = String.valueOf(gps.getLongitude());
			altitude = String.valueOf(gps.getAltitude());
			accuracy = String.valueOf(gps.getAccuracy());
		}
		if (oldTime != 0) {
			resultTime = (int) (newTime - oldTime);
			resultTime = time.proccedeNextTime(resultTime);
			oldTime = newTime;
		} else {
			oldTime = newTime;
		}

		if (state != STATE_NO_USER) {
			post.addRecord(userId, String.valueOf(newTime), latitude,
					longtitude, altitude, accuracy, v0105, v010C, v010D, v0111);
		}
		return new ShowValues(v0105, v010C, v010D, v0111,
				String.valueOf(resultTime), altitude, accuracy, latitude
						+ " - " + longtitude);
	}

	private void startRecording() {
		unitEngine = new UnitEngine(context, controlHandler, obdDevice,
				pidList, set.isDoLogUnitEngine());
		unitEngine.start();
		gps.start();
		executorPost.shutdown();
		executorPost = new ScheduledThreadPoolExecutor(1);
		executorPost.scheduleAtFixedRate(post, 0,
				set.getAuthorizedPostPeriod(), TimeUnit.MINUTES);
	}

	private void addValue(int identificator, String value) {
		if (4177 == identificator) {
			v0105 = value;
		}
		if (4290 == identificator) {
			v010C = value;
		}
		if (4305 == identificator) {
			v010D = value;
		}
		if (4369 == identificator) {
			v0111 = value;
		}
	}

	private void sendOnView(Message msg) {
		viewHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj)
				.sendToTarget();
	}

	private final Handler controlHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerLabels.OBD:
				switch (msg.arg1) {
				case HandlerLabels.OBD_DATA:
					addValue(msg.arg2, (String) msg.obj);
					break;
				case HandlerLabels.OBD_CYCLE_COMPLETED:
					msg = obtainMessage(HandlerLabels.OBD,
							HandlerLabels.OBD_CYCLE_COMPLETED, -1,
							cycleCompleted());
					sendOnView(msg);
					break;
				}
			default:
				sendOnView(msg);
			}

		}
	};

	private final Handler testHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandlerLabels.GET:
				sendOnView(msg);
				break;
			case HandlerLabels.LOCK:
				switch (msg.arg1) {
				case HandlerLabels.LOCK_TEST_OK:
					viewHandler.obtainMessage(msg.what, msg.arg1, msg.arg2,
							"ok").sendToTarget();
					lockEngine.stop();
					break;
				case HandlerLabels.LOCK_CONNECTED:
					break;
				default:
					lockEngine.stop();
					sendOnView(msg);
					break;
				}

				break;
			case HandlerLabels.OBD:
				Log.d("pokus", msg.what + " - " + msg.arg1 + " - "
						+ (String) msg.obj);
				switch (msg.arg1) {
				case HandlerLabels.OBD_STATE_CONNECTED:
					viewHandler.obtainMessage(msg.what, msg.arg1, msg.arg2,
							"ok").sendToTarget();
					unitEngine.stop();
					break;
				case HandlerLabels.OBD_STATE_CLOSED:
					break;
				case HandlerLabels.OBD_WARNING:
					unitEngine.stop();
					viewHandler.obtainMessage(msg.what, msg.arg1, msg.arg2,
							"Failed to connect").sendToTarget();
					break;
				case HandlerLabels.OBD_ERROR:
					unitEngine.stop();
					viewHandler.obtainMessage(msg.what, msg.arg1, msg.arg2,
							"Failed to connect").sendToTarget();
					break;
				case HandlerLabels.OBD_STATE_CONNECTING_FAILED_RECCONECT:
					unitEngine.stop();
					viewHandler.obtainMessage(msg.what, msg.arg1, msg.arg2,
							"Failed to connect").sendToTarget();
					break;
				case HandlerLabels.OBD_DATA:
					break;
				}
			}

		}
	};
}
