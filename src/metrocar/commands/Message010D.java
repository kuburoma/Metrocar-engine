package metrocar.commands;

import android.os.Handler;
import android.util.Log;

public class Message010D extends PidMessage {	
	
	public Message010D(Handler handler) {
		super(handler);
	}

	public Message010D(Handler handler, boolean log) {
		super(handler, log);
	}
	
	@Override
	protected String calculateValue(String value) {
		String[] arr = value.split(" ");
	    int A = Integer.parseInt(arr[2], 16);
		if (doLog)
			Log.d(TAG, getDescription()+" calculateValue("+arr[2]+") result = "+A);
	    return Integer.toString(A);	
	}

	@Override
	public String getRequest() {
		return "010D1";
	}
	
	@Override
	public String getDescription() {
		return "Speed";
	}
}
