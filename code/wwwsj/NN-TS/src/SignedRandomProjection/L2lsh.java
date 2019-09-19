package SignedRandomProjection;

/**
 * @author zhihui
 *
 */
public class L2lsh {
	
	private int  samSize,K,L;
	private double[][] a;
	private double[] b;
	private int r;
	
	public L2lsh(int dim, int K, int L) {
		this.K = K;
		this.L = L;
		samSize = dim;
		a=new double[L][samSize];
		b = new double[L];
		r=2;
		for(int i=0;i<L;i++) {//a~N(0,1)
			for(int j=0;j<samSize;j++)
			{
				a[i][j]=Math.random();
			}
		}
		for(int i=0;i<L;i++) {
			b[i]=Math.random()*K*r;
		}	
	}
	
	public int[] getHash(double[]  data) {

		int[] hashes = new int[L];
		for (int i = 0; i < L; i++) {
			double h1 = 0;
			for (int j = 0; j < data.length; j++) {
				h1 += a[i][j] * data[j];
			}
			hashes[i] = ((int) (h1 + b[i]) / r) % K;
		}
		return hashes;
	}

	public void printHash(){
		for(int i=0;i<L;i++) {//a~N(0,1)
			for(int j=0;j<K;j++)
			{
				System.out.print(a[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}

	public static void main(String args[]){
		L2lsh l2lsh = new L2lsh(3,3,4);


		l2lsh.a[0][0]=0.0;
		l2lsh.a[0][1]=5.0;

		l2lsh.printHash();

		int[] a = l2lsh.getHash(new double[]{1.1, 3.0});
		for (int item: a){
			System.out.println(item+" ");
		}
	}
}
