package metrocar.commands;

import android.os.Handler;
import android.util.Log;

public class Message0101 extends PidMessage {

	public Message0101(Handler handler) {
		super(handler);
	}

	public Message0101(Handler handler, boolean log) {
		super(handler, log);
	}
	
	@Override
	protected String calculateValue(String value) {
		String[] arr = value.split(" ");
		int A = (Integer.parseInt(arr[2], 16))-40;
		if (doLog)
			Log.d(TAG, getDescription()+" calculateValue("+arr[2]+") result = "+A);
		return Integer.toString(A);
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	@Override
	public String getRequest() {
		return "01051";
	}

	@Override
	public String getDescription() {
		return "Engine coolant temperature";
	}
}
