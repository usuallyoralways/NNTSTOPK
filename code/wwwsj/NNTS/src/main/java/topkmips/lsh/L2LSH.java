package topkmips.lsh;

import topkmips.data.Data;

import java.util.ArrayList;

public class L2LSH  extends Hash {
	private int dim=0,L=0,K=0;
	private double[][] a=null;
	private double[] b=null;
	private int r;
	
	public L2LSH(int dim, int L, int r,int K) {
		this.dim = dim;
		this.L = L;
		this.K=K;
		this.r=r;
		a=new double[L][dim];
		b=new double[L];
		
		 for (int i = 0; i < L; i++) {
			 for (int j = 0; j < dim; j++) {
				 a[i][j]=Math.random();
			 }
			 b[i]=Math.random()*r;
		}
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
	
	public int[] getHash(double[]  data) {
		int[] hashes = new int [L];
		for (int i = 0; i < L; i++) {		
			hashes[i]=dot(data, a[i])%K;
		}
		return hashes;
	}
	
	public  int dot(double[] q,double[] x) {
		double sum=0;
		for(int i=0;i<x.length;i++) {
			sum+=q[i]*x[i];
		}
		return (int) Math.floor(sum);
	}
}
