package metrocar.utils;

public class Device {
	private int id;
	private String macAddress;
	private String name;
	private String state;
	
	public Device(int id, String macAddress, String name, String state){
		this.id = id;
		this.macAddress = macAddress;
		this.name = name;
		this.state = state;
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}
