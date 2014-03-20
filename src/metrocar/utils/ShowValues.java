package metrocar.utils;

public class ShowValues {

	private String v0105 = "0";
	private String v010D = "0";
	private String v010C = "0";
	private String v0111 = "0";
	private String vSpeedRequest = "0";
	private String vAltitude = "0";
	private String vAccuracy = "0";
	private String vGPS = "0 - 0";
	
	public ShowValues(String v0105, String v010c, String v010d, String v0111,
			String vSpeedRequest, String vAltitude, String vAccuracy, String vGPS) {
		this.v0105 = v0105;
		v010D = v010d;
		v010C = v010c;
		this.v0111 = v0111;
		this.vSpeedRequest = vSpeedRequest;
		this.vAltitude = vAltitude;
		this.vGPS = vGPS;
		this.vAccuracy = vAccuracy;
	}
	
	
	public String getV0105() {
		return v0105;
	}

	public void setV0105(String v0105) {
		this.v0105 = v0105;
	}

	public String getV010D() {
		return v010D;
	}

	public void setV010D(String v010d) {
		v010D = v010d;
	}

	public String getV010C() {
		return v010C;
	}

	public void setV010C(String v010c) {
		v010C = v010c;
	}

	public String getV0111() {
		return v0111;
	}

	public void setV0111(String v0111) {
		this.v0111 = v0111;
	}

	public String getvSpeedRequest() {
		return vSpeedRequest;
	}

	public void setvSpeedRequest(String vSpeedRequest) {
		this.vSpeedRequest = vSpeedRequest;
	}

	public String getvAltitude() {
		return vAltitude;
	}

	public void setvAltitude(String vAltitude) {
		this.vAltitude = vAltitude;
	}
	
	public String getvAccuracy() {
		return vAccuracy;
	}

	public void setvAccuracy(String vAccuracy) {
		this.vAccuracy = vAccuracy;
	}

	public String getvGPS() {
		return vGPS;
	}

	public void setvGPS(String vGPS) {
		this.vGPS = vGPS;
	}
	
}
