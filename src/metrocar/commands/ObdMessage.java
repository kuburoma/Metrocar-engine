package metrocar.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import metrocar.labels.HandlerLabels;
import metrocar.labels.ModLabels;
import android.os.Handler;
import android.util.Log;

/**
 * @author Roman Kubù
 * 
 */
public abstract class ObdMessage {

	protected static final String TAG = ModLabels.TAG_OBD_MESSAGE;
	protected boolean doLog = false;

	protected String responce;
	protected String valueResponce;
	protected int done = STATE_GOOD;
	protected Handler listener;
	protected int identifier;
	protected int target;
	protected String description;

	public static final int STATE_GOOD = 0;
	public static final int STATE_FAILED = 1;
	public static final int STATE_ATDPN = 2;
	public static final int STATE_NO_DATA = 3;
	public static final int STATE_UNABLE_TO_CONNECT = 4;

	/**
	 * This method parse String getRequest() into int identifier. Target is set
	 * to OBD_DATA. This is settings where will message be sent on handler.
	 * 
	 */
	protected void preparation() {
		identifier = Integer.parseInt(this.getRequest(), 16);
		target = HandlerLabels.OBD_DATA;
		if (doLog)
			Log.d(TAG, getDescription() + " preparation");
	}

	public String getValueResponce() {
		return valueResponce;
	}

	public int getIdentifier() {
		return identifier;
	}

	public int getTarget() {
		return target;
	}

	/**
	 * This will sent String "getRequest + '\r'". When exception is caught when
	 * outStream.write then done = STATE_FAILED
	 * 
	 * @param outStream
	 */
	protected void send(OutputStream outStream) {
		if (doLog)
			Log.d(TAG, getDescription() + " sending request");
		String mes = this.getRequest() + '\r';
		byte[] send = mes.getBytes();
		try {
			outStream.write(send);
		} catch (Exception e) {
			done = STATE_FAILED;
			if (doLog)
				Log.d(TAG, getDescription() + " exception when outStream.write");
		}
	}

	/**
	 * Will read input until get '>' or 62 int. Every byte is get one by one and
	 * saved into String. Chars line feed and carriage return are not saved.
	 * When exception is caught done = STATE_FAILED.
	 * 
	 * @param inStream
	 */
	protected void read(InputStream inStream) {
		try {
			int data = inStream.read();
			String output = "";
			while (data != 62) {
				if (data != 10) {
					if (data != 13) {
						output += (char) data;
					}
				}
				data = inStream.read();
			}
			responce = output;
			if (doLog)
				Log.d(TAG, getDescription() + " read from intput - " + responce);

		} catch (IOException e) {
			done = STATE_FAILED;
			if (doLog)
				Log.d(TAG, getDescription() + " exception when inStream.read");
		}
	}

	/**
	 * This method will send message on handler.
	 */
	protected void noticeListener() {
		if (listener == null) {
			if (doLog)
				Log.d(TAG, getDescription() + " listener is null");
			return;
		} else {
			if (doLog)
				Log.d(TAG, getDescription()
						+ " send message on handler - target: " + target
						+ ", identifier:" + identifier + ", responce:"
						+ valueResponce);
			listener.obtainMessage(HandlerLabels.OBD, target, identifier,
					valueResponce).sendToTarget();

		}
	}

	/**
	 * String value is controlled if consist some error states that can be
	 * returned by unit.
	 * 
	 * @param value
	 * @return false when message contains something else then data responce.
	 */
	protected boolean errorControl(String value) {
		value = value.toUpperCase();
		if (value.contains("UNABLE TO CONNECT")) {
			target = HandlerLabels.OBD_ERROR;
			valueResponce = value;
			done = STATE_UNABLE_TO_CONNECT;
			if (doLog)
				Log.d(TAG, getDescription() + " unable to connect");
			return false;
		}

		if (value.contains("?")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " unknown responce get from OBD2 unit");
			return false;
		}

		if (value.contains("ACT ALERT")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " ACT ALERT");
			return false;
		}
		if (value.contains("BUFFER FULL")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " BUFFER FULL");
			return false;
		}
		if (value.contains("BUS BUSY")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " BUS BUSY");
			return false;
		}
		if (value.contains("ERR")) {
			target = HandlerLabels.OBD_ERROR;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " some error recieved from OD2 unit");
			return false;
		}
		if (value.contains("LP ALERT")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " LP ALERT");
			return false;
		}
		if (value.contains("LV RESET")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " LV RESET");
			return false;
		}
		if (value.contains("NO DATA")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			done = STATE_NO_DATA;
			if (doLog)
				Log.d(TAG, getDescription() + " NO DATA");
			return false;
		}
		if (value.contains("STOPPED")) {
			target = HandlerLabels.OBD_WARNING;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " STOPPED");
			return false;
		}
		if (value.contains("SEARCHING")) {
			target = HandlerLabels.OBD_INFORMATION;
			valueResponce = value;
			if (doLog)
				Log.d(TAG, getDescription() + " SEARCHING");
			return false;
		}
		return true;
	}

	/**
	 * this will return request on OBD2 unit that will be send.
	 * 
	 * @return
	 */
	abstract public String getRequest();

	/**
	 * Method that will send, received, control response, 
	 * processes response, send response on handler and return if method run good.
	 * 
	 * @param inStream
	 * @param outStream
	 * @return
	 */
	abstract public int run(InputStream inStream, OutputStream outStream);

	/**
	 * Controls if some response was received and then processed it by errorControl 
	 * @param Responce get by unit.
	 * @return True if message was received and doesn't consist of some error.
	 */
	abstract protected boolean control(String value);

	public String getDescription() {
		return description;
	}

	public int isDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

	public String getResponce() {
		return responce;
	}

	public void setResponce(String responce) {
		this.responce = responce;
	}

}
