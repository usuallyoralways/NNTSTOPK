package topkmips.lsh;

import topkmips.data.Data;

import java.util.ArrayList;

public abstract class Hash {
	
	public abstract  int[] getHash(double[]  data);
	public abstract int[][] dataToHash(ArrayList<Data> data);
	public abstract int[][] queryToHash(double[][]  queries);

}
