package metrocar.utils;

import metrocar.commands.PidMessage;

public class ConnectionClass {

	private PidMessage message;
	
	private boolean selected = false;
	
	private String value = "0";
	
	
	
	public ConnectionClass(PidMessage message){
		this.message = message;		
	}
	
	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}

	public PidMessage getMessage() {
		return message;
	}

	public void setMessage(PidMessage message) {
		this.message = message;
	}
	
	public String getDescription(){
		return message.getDescription();
	}
	
	public String getRequest(){
		return message.getRequest();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	
}
