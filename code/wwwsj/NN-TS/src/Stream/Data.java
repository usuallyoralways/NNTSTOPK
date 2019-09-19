package Stream;

public class Data {
	
	public double[] values;
	public int key;
	public int arrivalTime;
	
	public Data() {
	}
	
	public Data(int key,double[] values,  int arrivalTime) {
		this.key = key;
		this.values = values;
		this.arrivalTime = arrivalTime;
	}	
}