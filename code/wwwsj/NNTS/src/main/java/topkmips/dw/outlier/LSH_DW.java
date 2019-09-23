package topkmips.dw.outlier;

import topkmips.dw.DeterministicWave;
import topkmips.dw.Element;
import topkmips.dw.OverElement;
import Constant.Constant;
import java.util.HashSet;

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
    public HashSet<Integer> outliers;
    
	public LSH_DW(int K, int L,double relativeError,int expire) {
		this.K = K;
		this.L = L;
		this.relativeError = relativeError;
		this.expire = expire;
		bucket = new DeterministicWave[L][1<<K];
	}
	
	public void add(int[] hashes, int time,int key) {
//		System.out.println("current time: "+time);
		double meanCount = 0;
		int index=0;
		for (int i = 0; i < L; i++) {
			index = hashes[i];
			if(bucket[i][index]==null) {
				bucket[i][index]=new DeterministicWave();
				Constant.DW_nums++;//用于求阈值
				bucket[i][index].Initialized(relativeError,expire);
			}
			bucket[i][index].input(key,1, time);		
			meanCount += bucket[i][index].Query();
		}
		
		
		meanCount = meanCount/(double)L;		
		//##################
		Constant.mean = Constant.n/Constant.DW_nums;//求阈值
		if(meanCount<Constant.mean*Constant.alpha) {
			for (OverElement e : bucket[L-1][index].getOverflow()) {
				outliers.add(e.getKey());
			}
			for (Element e : bucket[L-1][index].getAllInWindow()) {
				outliers.add(e.getKey());
			}
		}
		//##################
	}
	
}
