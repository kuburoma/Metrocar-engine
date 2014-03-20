package metrocar.commands;

public class MessageATZ extends CommandMessage {

	protected String description = "Reset device";

	
	@Override
	public String getRequest() {
		return "ATZ";
	}

}
