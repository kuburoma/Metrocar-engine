package metrocar.labels;

/**
 * @author Roman Kubù
 *
 */
public class HandlerLabels {
	
	//----------- Message what ---------------------
	/**
	 * Message.what for UnitEngine
	 */
	public static final int OBD = 1;
	/**
	 * Message.what for LockEngie
	 */
	public static final int LOCK = 2;
	/**
	 * Message.what for PostMetrocar
	 */
	public static final int POST = 3;
	/**
	 * Message.what for MainCore
	 */
	public static final int MAIN = 4;
	/**
	 * Message.what for GetReservation
	 */
	public static final int GET = 5;
	
	
	//----------- Message arg1 ---------------------
	//------------------------------------------------
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Nothing is happening.
	 */
	public static final int OBD_STATE_NONE = 1;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Connecting to BluetoothDevice.
	 */
	public static final int OBD_STATE_CONNECTING = 2;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Failed to connect to BluetoothDevice and trying to reconnect.
	 */
	public static final int OBD_STATE_CONNECTING_FAILED_RECCONECT = 3;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Connected to BluetoothDevice.
	 */
	public static final int OBD_STATE_CONNECTED = 4;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Lost connection to BluetoothDevice and trying to connect again.
	 */
	public static final int OBD_STATE_CONNECTED_LOST = 5;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Set up of BluetoothDevice.
	 */
	public static final int OBD_STATE_SET_UP = 6;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Connection Closed.
	 */
	public static final int OBD_STATE_CLOSED= 7;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Error occurred.
	 */
	public static final int OBD_ERROR = 8;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Warning occurred.
	 */
	public static final int OBD_WARNING = 9;	
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Information message.
	 */
	public static final int OBD_INFORMATION = 10;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * Message that have data. On Message.arg2 is information which data are sent.
	 */
	public static final int OBD_DATA = 11;
	/**
	 * Message.arg1 for UnitEngine
	 * 
	 * When all PidMessage are sent and new cycle starts.
	 */
	public static final int OBD_CYCLE_COMPLETED = 12;
	
	//-------------------------------------------------
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Lock has been opened.
	 */
	public static final int LOCK_OPENED = 1;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Cannot connect to BluetoothDevice
	 */
	public static final int LOCK_CANNOCT_CONNECT = 2;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Connection to BluetoothDevice has been closed.
	 */
	public static final int LOCK_CLOSED = 3;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Wrong key has been sent.
	 */
	public static final int LOCK_WRONG_KEY = 4;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Reset of password is OK.
	 */
	public static final int LOCK_RESET_OK = 5;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * reset of password failed.
	 */
	public static final int LOCK_RESET_FAILED = 6;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Test of password has failed.
	 */
	public static final int LOCK_TEST_OK = 7;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Test of Lock failed.
	 */
	public static final int LOCK_TEST_FAILED = 8;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Unknown response was get from device.
	 */
	public static final int LOCK_UNKNOWN_RESPONCE = 9; 
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Connection to BluetoothDevice has been lost.
	 */
	public static final int LOCK_CONNECTION_LOST = 10;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Connection to BluetoothDevice has failed.
	 */
	public static final int LOCK_CONNECTION_FAILED = 11;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Connection to BluetoothDevice has been established.
	 */
	public static final int LOCK_CONNECTED = 12; 
	//-------------------------------------------------	
	/**
	 * Message.arg1 for PostMetrocar
	 * 
	 * Post has been done.
	 */
	public static final int POST_OK = 1;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Error occurred when running PostMetrocar.
	 */
	public static final int POST_ERROR = 2;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Warning occurred when running PostMetrocar.
	 */
	public static final int POST_WARNING = 3;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * Authentication by Unit ID and Secret Key has failed.
	 */
	public static final int POST_AUTHENTICATION_FAILED = 4;
	/**
	 * Message.arg1 for LockEngine
	 * 
	 * No Internet connection is online.
	 */
	public static final int POST_NO_INTERNET_CONNECTION = 5;
	//-------------------------------------------------	
	/**
	 * Message.arg1 for GetMetrocar
	 * 
	 * GetMetrocar done well.
	 */
	public static final int GET_OK = 1;
	/**
	 * Message.arg1 for GetMetrocar
	 * 
	 * Error occurred.
	 */
	public static final int GET_ERROR = 2;
	/**
	 * Message.arg1 for GetMetrocar
	 * 
	 * Warning occurred.
	 */
	public static final int GET_WARNING = 3;
	/**
	 * Message.arg1 for GetMetrocar
	 * 
	 * Authentication by Unit ID and Secret Key has failed.
	 */
	public static final int GET_AUTHENTICATION_FAILED = 4;
	/**
	 * Message.arg1 for GetMetrocar
	 * 
	 * No Internet connection is online.
	 */
	public static final int GET_NO_INTERNET_CONNECTION = 5;
	//-------------------------------------------------		
	
	
}
