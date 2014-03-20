package metrocar.utils;

public class UnitSettings {

	// If tag shloud be write out
	private boolean doLogGPSTracker = true;
	private boolean doLogGet = true;
	private boolean doLogPost = true;
	private boolean doLogUnitEngine = true;
	private boolean doLogMainCore = true;
	private boolean doLogCheckInternetConnection = true;
	private boolean doLogLockEngine = true;
	private boolean doLogDatabaseController = true;
	private boolean doLogObdMessage = true;
	
	
    private long authorizedPostPeriod = 1;
    private long unauthorizedPostPeriod = 1;
    private long authorizedGetPeriod = 1;
    private long unauthorizedGetPeriod = 1;
    
	// Name that will be broadcast by bluetooth
	private String name = "Metrocar";

	private String get_request = "http://admin.autonapul.cz/api/reservations/";

	// public static String get_request =
	// "http://0101.apiary.io/api/reservations/";

	private String post = "http://admin.autonapul.cz/api/log/";

	// public static String post = "http://0101.apiary.io/api/log/";

	// This is time in which user can be authorized before reservation is open
	// in milliseconds
	private long open_advance = 7000000000L;

	// This is time in which user can be authorized after reservation ends in
	// milliseconds
	private long end_advance = 0;

	// The minimum time between updates in milliseconds
	// Value is set on average time that it takes to prepare one record
	private long min_time_bw_updates = 100;

	// The minimum distance to change Updates in meters
	private long min_distance_change_for_server = 1;

	// Number of entries that will me send on server
	private String post_limit = "100";

	public long getAuthorizedPostPeriod() {
		return authorizedPostPeriod;
	}

	public void setAuthorizedPostPeriod(long authorizedPostPeriod) {
		this.authorizedPostPeriod = authorizedPostPeriod;
	}

	public long getUnauthorizedPostPeriod() {
		return unauthorizedPostPeriod;
	}

	public void setUnauthorizedPostPeriod(long unauthorizedPostPeriod) {
		this.unauthorizedPostPeriod = unauthorizedPostPeriod;
	}

	public long getAuthorizedGetPeriod() {
		return authorizedGetPeriod;
	}

	public void setAuthorizedGetPeriod(long authorizedGetPeriod) {
		this.authorizedGetPeriod = authorizedGetPeriod;
	}

	public long getUnauthorizedGetPeriod() {
		return unauthorizedGetPeriod;
	}

	public void setUnauthorizedGetPeriod(long unauthorizedGetPeriod) {
		this.unauthorizedGetPeriod = unauthorizedGetPeriod;
	}
	
	public boolean isDoLogGPSTracker() {
		return doLogGPSTracker;
	}

	public void setDoLogGPSTracker(boolean doLogGPSTracker) {
		this.doLogGPSTracker = doLogGPSTracker;
	}

	public boolean isDoLogGet() {
		return doLogGet;
	}

	public void setDoLogGet(boolean doLogGet) {
		this.doLogGet = doLogGet;
	}

	public boolean isDoLogPost() {
		return doLogPost;
	}

	public void setDoLogPost(boolean doLogPost) {
		this.doLogPost = doLogPost;
	}

	public boolean isDoLogUnitEngine() {
		return doLogUnitEngine;
	}

	public void setDoLogUnitEngine(boolean doLogUnitEngine) {
		this.doLogUnitEngine = doLogUnitEngine;
	}

	public boolean isDoLogMainCore() {
		return doLogMainCore;
	}

	public void setDoLogMainCore(boolean doLogMainCore) {
		this.doLogMainCore = doLogMainCore;
	}

	public boolean isDoLogCheckInternetConnection() {
		return doLogCheckInternetConnection;
	}

	public void setDoLogCheckInternetConnection(
			boolean doLogCheckInternetConnection) {
		this.doLogCheckInternetConnection = doLogCheckInternetConnection;
	}

	public boolean isDoLogLockEngine() {
		return doLogLockEngine;
	}

	public void setDoLogLockEngine(boolean doLogLockEngine) {
		this.doLogLockEngine = doLogLockEngine;
	}

	public boolean isDoLogDatabaseController() {
		return doLogDatabaseController;
	}

	public void setDoLogDatabaseController(boolean doLogDatabaseController) {
		this.doLogDatabaseController = doLogDatabaseController;
	}

	public boolean isDoLogObdMessage() {
		return doLogObdMessage;
	}

	public void setDoLogObdMessage(boolean doLogObdMessage) {
		this.doLogObdMessage = doLogObdMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGet_request() {
		return get_request;
	}

	public void setGet_request(String get_request) {
		this.get_request = get_request;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public long getOpen_advance() {
		return open_advance;
	}

	public void setOpen_advance(long open_advance) {
		this.open_advance = open_advance;
	}

	public long getEnd_advance() {
		return end_advance;
	}

	public void setEnd_advance(long end_advance) {
		this.end_advance = end_advance;
	}

	public long getMin_time_bw_updates() {
		return min_time_bw_updates;
	}

	public void setMin_time_bw_updates(long min_time_bw_updates) {
		this.min_time_bw_updates = min_time_bw_updates;
	}

	public long getMin_distance_change_for_server() {
		return min_distance_change_for_server;
	}

	public void setMin_distance_change_for_server(
			long min_distance_change_for_server) {
		this.min_distance_change_for_server = min_distance_change_for_server;
	}

	public String getPost_limit() {
		return post_limit;
	}

	public void setPost_limit(String post_limit) {
		this.post_limit = post_limit;
	}

}
