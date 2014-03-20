package metrocar.commands;

import android.os.Handler;
import android.util.Log;

public class Message0111 extends PidMessage {	
	
	public Message0111(Handler handler) {
		super(handler);
	}

	public Message0111(Handler handler, boolean log) {
		super(handler, log);
	}
	
	@Override
	protected String calculateValue(String value) {
		String[] arr = value.split(" ");
	    int A = ((Integer.parseInt(arr[2], 16))*100)/255;
		if (doLog)
			Log.d(TAG, getDescription()+" calculateValue("+arr[2]+") result = "+A);
	    return Integer.toString(A);	
	}

	@Override
	public String getRequest() {
		return "01111";
	}
	
	@Override
	public String getDescription() {
		return "Throttle position";
	}
}
