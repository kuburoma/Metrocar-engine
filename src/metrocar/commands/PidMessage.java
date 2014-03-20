package metrocar.commands;

import java.io.InputStream;
import java.io.OutputStream;

import android.os.Handler;
import android.util.Log;

/**
 * @author Roman Kubù
 * 
 */
public abstract class PidMessage extends ObdMessage {

	public PidMessage(Handler handler) {
		listener = handler;
		doLog = false;
		if (doLog)
			Log.d(TAG, getDescription()+" constructor");
	}

	public PidMessage(Handler handler, boolean log){
		listener = handler;
		doLog = log; 
		if (doLog)
			Log.d(TAG, getDescription()+" constructor");
	}

	abstract public String getDescription();

	abstract protected String calculateValue(String value);

	@Override
	protected boolean control(String value) {
		if (value.length() == 0) {
			done = STATE_FAILED;
			if (doLog)
				Log.d(TAG, getDescription()+" size of response is zero");
			return false;
		}
		if (errorControl(value)) {
			return true;
		}
		if (doLog)
			Log.d(TAG, getDescription()+" error was found in responce");
		return false;
	}

	@Override
	public int run(InputStream inStream, OutputStream outStream) {
		if (doLog)
			Log.d(TAG, getDescription()+" run started");
		done = STATE_GOOD;
		preparation();

		if (done == STATE_GOOD)
			send(outStream);

		if (done == STATE_GOOD)
			read(inStream);

		if (done == STATE_GOOD) {
			if (control(responce))
				valueResponce = calculateValue(responce);
		}
		if (done == STATE_GOOD)
			noticeListener();
		return done;
	}

}
