package topkmips.lsh;

import topkmips.data.Data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author zhihui
 *
 */
public class SignRandomProjection extends Hash {
	
	private int numOfHashes, samSize,K,L;
	private double[][] randBits;
	private int[][] indices;
	
	public SignRandomProjection(int dim, int K, int L) {
		this.K = K;
		this.L = L;
		this.numOfHashes = K*L;//一共又L行，每行需要K个哈希，因为每一位需要一次哈希
		if(dim>20)
			samSize = (int) Math.ceil(dim/3);
		else if(dim>5)
			samSize = 5;
		else if(dim>2)
			samSize =dim-1;
		else 
			samSize = dim;
		ArrayList<Integer> aList = new ArrayList<Integer>();
		for (int i = 0; i < dim; i++) {
			aList.add(i);
		}
		
		//workout all hash bits
		//randBits中保存加或者减的标志，这一维是加还是减
		randBits = new double[numOfHashes][samSize];
		//indices保存每次参与计算的维坐标，因为并不是所有维度都参与运算
		indices  = new int[numOfHashes][samSize];
		
		 for (int i = 0; i < numOfHashes; i++) {
			 Collections.shuffle(aList);
			 for (int j = 0; j < samSize; j++) {
				indices[i][j] = aList.get(j);
				int curr = (int) (Math.random()*1000);
				if(curr%2==0) randBits[i][j] = 1;
				else randBits[i][j] = -1;
			}
		}
	}
	
	public int[] getHash(double[]  data) {
		int[] hashes = new int [L];
		for (int i = 0; i < L; i++) {		
			int tmp=0;
			for (int j = 0; j < K; j++) {
				double s = 0;
				int index = i*K+j;
				for (int q = 0; q < samSize; q++) {
					double v = data[indices[index][q]];
					if(randBits[index][q]>0) {
						s=s+v;
					}else {
						s=s-v;
					}
				}
				tmp=tmp<<1;
				tmp=tmp+(s >= 0 ? 1 : 0);
			}
			hashes[i]=tmp;
		}
		return hashes;
	}
	
	public int[][] dataToHash(ArrayList<Data> data) {
		int[][] hashes = new int[data.size()][L];
		for(int i=0;i<data.size();i++) {
			hashes[i]=getHash(data.get(i).values);
		}
		return hashes;
	}
	
	public int[][] queryToHash(double[][]  queries) {
		int[][] hashes = new int [queries.length][L];
		for(int q=0;q<queries.length;q++) {
			hashes[q]=getHash(queries[q]);
		}
		return hashes;
	}
	
}
