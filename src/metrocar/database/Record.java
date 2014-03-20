package metrocar.database;

/**
 * Holds all information that shloud be sent on server Metrocar.
 * 
 * @author Roman Kubù
 *
 */
public class Record {

	private long id;

	private String userId = "0";
	private String time = "0";
	private String latitude = "0";
	private String longtituted = "0";
	private String altitude = "0";
	private String accurace = "0";
	private String m0105 = "0";
	private String m010C = "0";
	private String m010D = "0";
	private String m0111 = "0";

	/**
	 * Default constructor that sets all on 0
	 * 
	 */
	public Record() {

	}

	/**
	 * Constructor
	 * 
	 * @param userId	Id of user
	 * @param time		Time when record was taken at same time 	
	 * @param latitude	GPS latitude
	 * @param longtitude	GPS longtitude
	 * @param altitude	GPS altitude
	 * @param accurace	GPS accuracy
	 * @param m0105		Engine coolant temperature
	 * @param m010C		Engine RPM
	 * @param m010D		Vehicle speed
	 * @param m0111		Throttle position
	 */
	public Record(String userId, String time, String latitude,
			String longtitude, String altitude, String accurace, String m0105, String m010C,
			String m010D, String m0111) {
		this.userId = userId;
		this.time = time;
		this.latitude = latitude;
		this.longtituted = longtitude;
		this.altitude = altitude;
		this.accurace = accurace;
		this.m0105 = m0105;
		this.m010C = m010C;
		this.m010D = m010D;
		this.m0111 = m0111;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongtituted() {
		return longtituted;
	}

	public void setLongtituted(String longtituted) {
		this.longtituted = longtituted;
	}

	public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	
	public String getAccurace() {
		return accurace;
	}

	public void setAccurace(String accurace) {
		this.accurace = accurace;
	}

	public String getM0105() {
		return m0105;
	}

	public void setM0105(String m0105) {
		this.m0105 = m0105;
	}

	public String getM010C() {
		return m010C;
	}

	public void setM010C(String m010c) {
		m010C = m010c;
	}

	public String getM010D() {
		return m010D;
	}

	public void setM010D(String m010d) {
		m010D = m010d;
	}

	public String getM0111() {
		return m0111;
	}

	public void setM0111(String m0111) {
		this.m0111 = m0111;
	}
}
