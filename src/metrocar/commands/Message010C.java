package metrocar.commands;

import android.os.Handler;
import android.util.Log;

public class Message010C extends PidMessage {
	
	
	public Message010C(Handler handler) {
		super(handler);
	}

	public Message010C(Handler handler, boolean log) {
		super(handler, log);
	}

	@Override
	protected String calculateValue(String value) {
		String[] arr = value.split(" ");
	    int A = Integer.parseInt(arr[2], 16);
	    int B = Integer.parseInt(arr[3], 16);
	    int V = ((A*256)+B)/4;
		if (doLog)
			Log.d(TAG, getDescription()+" calculateValue("+arr[2]+" "+arr[3]+") result = "+V);
	    return Integer.toString(V);	
	}

	@Override
	public String getRequest() {
		return "010C2";
	}
	
	@Override
	public String getDescription() {
		return "Engine RPM";
	}
}
