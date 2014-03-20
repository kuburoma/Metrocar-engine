package metrocar.commands;

public class MessageATSW20 extends CommandMessage {

	protected String description = "Set wake-up message";
	
	@Override
	public String getRequest() {
		return "ATSW20";
	}

}
