package topkmips.tests;

import topkmips.data.Data;
import topkmips.data.Stream;
import topkmips.dw.topk.LSH_topK;
import topkmips.lsh.Hash;
import topkmips.lsh.L2LSH;
import topkmips.lsh.SignRandomProjection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Constant.Constant;


public class LSH_Test {

	public static Stream dataStream;
	
	public static void main(String[] args) {
		Constant.flag=1;
		if(Constant.flag==1) {
			findMIPS(Constant.topKcoll,Constant.queryFile, 60000, Constant.K, 512, Constant.topK,3);
		}
	}
	
	public static void findMIPS(String dataFile, String queryFile,int num, int K,int L,int topk,int r) {
//		ArrayList<Integer> topK = new ArrayList<Integer>();//存储topK候选
		dataStream = new Stream();
		int dim = dataStream.getData(dataFile);//待选数据流准备好
		LSH_topK lshi = new LSH_topK();
		///////规范化并转换P！
		ArrayList<Data> datas = dataStream.normTransP(Stream.streams, 0.85,3);//U=0.85和m=3
		dim+=3;//dim+=m;

		Hash hash=null;//hash functions
		int htype = 1;//hash type
		if(htype==0)
			hash = new L2LSH(dim, K, L,r);
		else if(htype==1)
			hash =new SignRandomProjection(dim, K, L);
		else 
			hash =new SignRandomProjection(dim, K, L);
		int[][]  hashes = hash.dataToHash(datas);
		int[] truth = {25719,39268,53029,1208,6112,14693,5655,10006,1202,29307};
		
		//	Q转换，Q(x)=[x;1/2;1/2;...;1/2]	
		ArrayList<Double[]> queries = dataStream.getQueries(queryFile);
		double[][] topkbase = dataStream.transQ(queries,3);//m=3是添加的维度
		int[][] qhash=hash.queryToHash(topkbase);
		
		lshi.PR(10, truth, hashes, qhash);
		
	
	}
	
	public static void write(String filename, List<Integer> outLiers) {
		try {
			File file = new File(filename);//结果文件
			FileOutputStream out = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(out,"UTF-8");
			BufferedWriter bf = new BufferedWriter(osw);
			for (Integer key : outLiers) {
				bf.append(key.toString()).append("\n");
			}
			bf.close();
			osw.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
