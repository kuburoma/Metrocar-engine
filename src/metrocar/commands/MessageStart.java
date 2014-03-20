package metrocar.commands;

public class MessageStart extends CommandMessage {

	protected String description = "To start obd ";
	
	@Override
	public String getRequest() {
		return "0100";
	}

}
