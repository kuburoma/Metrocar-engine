package metrocar.utils;

public class TimeCount {

	private static final int arrayLength = 20;
	int[] array = new int[arrayLength];
	int numberOfTimes = 0;
	int pointer = 0;
	int countedTime = 0;
	
	public TimeCount() {
		array = new int[20];
		numberOfTimes = 0;
		pointer = 0;
		countedTime = 0;
	}

	public int proccedeNextTime(int currentTime) {
		if(numberOfTimes != 0){		
			if(numberOfTimes < arrayLength){
				array[pointer] = currentTime;
				countedTime += currentTime;
				numberOfTimes++;
			}else{
				countedTime -= array[pointer];
				array[pointer] = currentTime;
				countedTime += currentTime;
			}
		}else{
			countedTime = currentTime;
			array[pointer] = currentTime;
			numberOfTimes++;
		}
		pointer = nextPointer(pointer);
		return countedTime/numberOfTimes;
	}
	
	private int nextPointer(int pointer){
		pointer ++;
		if(pointer == arrayLength){
			pointer = 0;
		}
		return pointer;
	}

	void reset() {

	}

}
