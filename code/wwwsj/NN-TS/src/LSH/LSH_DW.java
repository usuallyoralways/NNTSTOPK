package LSH;

import Constant.Constant;
import DW.DeterministicWave;
import DW.Element;
import DW.OverElement;
/**
 * @author zhihui
 * Work out all hash values and mean count;
 */
public class LSH_DW {
	private int K;
	private int L;
	private double relativeError;
	private int expire;
    public DeterministicWave bucket[][];
    
	public LSH_DW(int K, int L,double relativeError,int expire) {
		this.K = K;
		this.L = L;
		this.relativeError = relativeError;
		this.expire = expire;
		bucket = new DeterministicWave[L][1<<K];
	}
	
	public void add(int[][] hashes, int time,int key) {
//		System.out.println("current time: "+time);
		int index = 0;
		double meanCount = 0;
		for (int i = 0; i < L; i++) {
			index = 0;
			for (int j = 0; j < K; j++) {
				index = index << 1;
				index = index + hashes[i][j];//得到K位哈希值
			}


			if(bucket[i][index]==null) {
				bucket[i][index]=new DeterministicWave();
				Constant.DW_nums++;
				bucket[i][index].Initialized(relativeError,expire);
			}
			bucket[i][index].input(key,1, time);		
//			System.out.print("row: "+i+"  col: "+index+"  ");
			meanCount += bucket[i][index].Query();
//			System.out.print(i+" "+index+" "+meanCount+",");
		}
//		System.out.println();
		meanCount = meanCount/(double)L;		
		//##################
		Constant.mean = Constant.n/Constant.DW_nums;
		if(meanCount<Constant.mean*Constant.alpha) {
			for (OverElement e : bucket[L-1][index].getOverflow()) {
				LSH_Outlier.outliers.add(e.getKey());
			}
			for (Element e : bucket[L-1][index].getAllInWindow()) {
				LSH_Outlier.outliers.add(e.getKey());
			}
		}
		//##################
	}
	
}
