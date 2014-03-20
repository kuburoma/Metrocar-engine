package metrocar.utils;

import java.util.ArrayList;

import metrocar.database.Record;


public class DistanceFromGPS {

	ArrayList<Record> main;
	double[] data;
	static final double _eQuatorialEarthRadius = 6378.1370D;
	double dist;
	int position = -1;

	double dLat;
	double dLng;
	double sinLat;
	double sinLng;
	double a;
	double c;

	public DistanceFromGPS(ArrayList<Record> list) {
		main = list;
	}
	
	public ArrayList<Record> getMain() {
		return main;
	}

	public void setMain(ArrayList<Record> main) {
		this.main = main;
	}
	
	private Record getNext() {
		return main.get(position);
	}

	private boolean hasNext() {
		position++;
		Record r;
		while (main.size() > position) {
			r = main.get(position);
			if (Double.valueOf(r.getLatitude()) != 0
					&& Double.valueOf(r.getLongtituted()) != 0) {
				return true;
			}
			position++;
		}
		return false;
	}

	public double computeDistance() {
		dist = 0;
		Record first;
		Record second;
		if (hasNext()) {
			first = getNext();
		} else {
			return 0;
		}
		while (hasNext()) {
			second = getNext();
			dist += distFrom(Double.valueOf(first.getLatitude()),
					Double.valueOf(first.getLongtituted()),
					Double.valueOf(second.getLatitude()),
					Double.valueOf(second.getLongtituted()));
			first = second;
		}
		return dist;
	}

	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
		dLat = Math.toRadians(lat2 - lat1);
		dLng = Math.toRadians(lng2 - lng1);
		sinLat = Math.sin(dLat / 2);
		sinLng = Math.sin(dLng / 2);
		a = sinLat * sinLat + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * sinLng * sinLng;
		c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return _eQuatorialEarthRadius * c;
	}

	/*
	 * private double distFrom(double lat1, double lng1, double lat2, double
	 * lng2) { double earthRadius = 3958.75; double dLat = Math.toRadians(lat2 -
	 * lat1); double dLng = Math.toRadians(lng2 - lng1); double a =
	 * Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
	 * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) Math.sin(dLng / 2);
	 * double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); return
	 * earthRadius * c; }
	 */
}
