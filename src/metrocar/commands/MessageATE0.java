package metrocar.commands;

public class MessageATE0 extends CommandMessage {

	protected String description = "Echo off";
	
	@Override
	public String getRequest() {
		return "ATE0";
	}

}
