package metrocar.commands;

import java.io.InputStream;
import java.io.OutputStream;

import metrocar.labels.HandlerLabels;



/**
 * @author Roman Kubù
 * 
 */
public abstract class CommandMessage extends ObdMessage {
	
	@Override
	protected boolean control(String value){
		if(value == null){
			done = STATE_FAILED;
			return false;
		}
		if(value.length() == 0){
			done = STATE_FAILED;
			return false;
		}
		if(value.contains("OK")){
			target = HandlerLabels.OBD_INFORMATION;
			valueResponce = value;
			return false;			
		}
		if(errorControl(value)){
			target = HandlerLabels.OBD_INFORMATION;
			valueResponce = value;
			return true;			
		} 		
		return false;
	}

	@Override
	public int run(InputStream inStream, OutputStream outStream) {
		done = STATE_GOOD;
		if (done == STATE_GOOD)
			send(outStream);

		if (done == STATE_GOOD)
			read(inStream);

		control(responce);
			
		if (done == STATE_GOOD || done == STATE_NO_DATA || done == STATE_UNABLE_TO_CONNECT)
			noticeListener();

		return done;
	}

}
