package metrocar.commands;

public class MessageATDPN extends CommandMessage {

	protected String description = "Describe protocol by number";

	@Override
	public String getRequest() {
		return "ATDPN";
	}
	
	@Override
	protected void noticeListener() {
		if (listener == null) {
		} else {
			if(valueResponce.length() == 1){
				if(valueResponce.charAt(0) >= 51 && valueResponce.charAt(0) <= 53){
					done = STATE_ATDPN;
				}
			}
			if(valueResponce.length() == 2){
				if(valueResponce.charAt(1) >= 51 && valueResponce.charAt(1) <= 53){
					done = STATE_ATDPN;
				}
			}
			listener.obtainMessage(target, identifier, -1, valueResponce)
					.sendToTarget();
		}
	}
}
