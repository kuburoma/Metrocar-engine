package metrocar.utils;

import java.util.ArrayList;

import metrocar.database.Record;


public class DistanceFromSpeed {

	ArrayList<Record> main;
	int position = -1;
	double dist;
	

	public DistanceFromSpeed(ArrayList<Record> list) {
		main = list;
	}

	private Record getNext() {
		return main.get(position);
	}

	private boolean hasNext() {
		position++;
		while (main.size() > position) {
			return true;
		}
		return false;
	}

	public double computeDistance() {
		dist = 0;
		Record first;
		Record second;
		double everageSpeed;
		double time;
		if (hasNext()) {
			first = getNext();
		} else {
			return 0;
		}
		while (hasNext()) {
			second = getNext();			
			everageSpeed = (Double.valueOf(first.getM010D()) + Double.valueOf(second.getM010D()))/2;
			time = ((Long.valueOf(second.getTime()) - Long.valueOf(first.getTime()))*1.0)/1000;
			dist += time * everageSpeed *1.0 /3600;
			first = second;
		}
		return dist;
	}
}
