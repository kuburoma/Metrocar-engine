package metrocar.commands;

public class MessageATWS extends CommandMessage {

	protected String description = "Reset device but not elektricity";

	
	@Override
	public String getRequest() {
		return "ATWS";
	}

}
