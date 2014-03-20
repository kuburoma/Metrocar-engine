package metrocar.database;

import java.security.Key;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Iterator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


import android.util.Base64;
import android.util.Log;

/**
 * 
 * Used for authorization of user against reservations from database.
 * 
 * 
 * @author Roman Kubù 
 *
 */
public class ControlPassword {

	private ArrayList<Reservation> res;
	private DatabaseController dc;
	private String username;
	private String pass;
	private long openAdvanced;
	private long endAdvanced;
	private Reservation reservation;
	private long time;
	private long userId = -1;
	

	/**
	 * Constructor of class
	 * 
	 * @param dc DatabseController that must be open
	 * @param username User name
	 * @param pass Password of user
	 * @param openAdvanced Time in milliseconds that user can be authorized before reservation starts
	 * @param endAdvaced Time in milliseconds that user can be authorized after reservation ends
	 * 
	 */
	public ControlPassword(DatabaseController dc, String username, String pass,
			long openAdvanced, long endAdvaced) {
		this.pass = pass;
		this.username = username;
		this.dc = dc;
		this.openAdvanced = openAdvanced;
		this.endAdvanced = endAdvaced;
	}

	/**
	 *
	 * @return user id
	 */
	public long userId() {
		return userId;
	}

	/**
	 * Gets all available reservations for User name. Finds first reservation that is in current time.
	 * Controls if hash of user password is same with hash from reservation. 
	 * 
	 * @return
	 */
	public boolean controlPassword() {
		res = dc.getUserReservation(username);
		if (res == null) {
			return false;
		}
		if (res.size() == 0) {
			return false;
		}

		time = System.currentTimeMillis();

		Iterator<Reservation> it = res.iterator();
		while (it.hasNext()) {
			reservation = it.next();
			Log.d("Try", reservation.getStart() - openAdvanced + " - " + time
					+ " - " + (time - reservation.getStart() - openAdvanced));
			if ((reservation.getStart() - openAdvanced) < time
					&& time < reservation.getEnd() + endAdvanced) {
				break;
			}
			reservation = null;
		}

		if (reservation == null) {
			return false;
		}

		String hashPass = reservation.getPassword();

		String[] pars = hashPass.split("\\$");

		String newPass = hashPassword(pass, pars[2]);
		String oldPass = pars[3] + "\n";

		if (newPass.equals(oldPass)) {
			userId = reservation.getUserId();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Hash password with salt and algorithm PBKDF2WithHmacSHA1
	 * 
	 * @param pass Password of user
	 * @param salt Salt
	 * @return Hashed string
	 */
	private String hashPassword(String pass, String salt) {
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keyspec = new PBEKeySpec(pass.toCharArray(),
					salt.getBytes(), 10000, 160);
			Key key = factory.generateSecret(keyspec);
			return new String(Base64.encode(key.getEncoded(), 0));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
