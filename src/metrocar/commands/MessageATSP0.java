package metrocar.commands;

public class MessageATSP0 extends CommandMessage {

	protected String description = "Selection of automatic protocol";
	
	@Override
	public String getRequest() {
		return "ATSP0";
	}

}
