package metrocar.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation {

	public String user;
	public String password;
	public long start;
	public long end;
	public long userId;
	public long reservationId;

	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	private Date date;

	public long convertTime(String time) {
		try {
			date = df.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}

	public String convertTime(long time) {
		Date resultdate = new Date(time);
		return df.format(resultdate);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getStart() {
		return start;
	}

	public String getStartString() {
		return convertTime(start);
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setStart(String start) {
		this.start = convertTime(start);
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public void setEnd(String end) {
		this.end = convertTime(end);
	}

}
