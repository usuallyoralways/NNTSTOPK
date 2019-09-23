package lsh;

import Constant.Constant;
import SignedRandomProjection.SignRandomProjection;
import Stream.Data;
import Stream.Stream;

import java.io.*;
import java.util.*;

public class LSH_Outlier {

	public static Stream dataStream;
	//####存储异常数据
	public static HashSet<Integer> outliers = null;
	public static ArrayList<Integer> topK = null;
	//####
	public static void detectOutliers(String dataFile, double relativeError, int expire, int K,int L,double  alpha) {
		outliers = new HashSet<>();
		dataStream = new Stream();
		int dim = dataStream.getData(dataFile);//数据流准备好
		
		LSH_DW lshi = new LSH_DW(K,L,relativeError, expire);
		SignRandomProjection proj = new SignRandomProjection(dim, K,L);//哈希向量准备好
		
//		while(!dataStream.streams.isEmpty()) {
		for (Data data : dataStream.streams) {
//			Data data = new Data();
//			data  = d;
			int[][]  hashes = proj.getHash(data.values);
			lshi.add(hashes, data.arrivalTime,data.key);
		}	
	}
	
	public static void findMIPS(String dataFile, double relativeError, int expire, int K,int L,int topk) {
		topK = new ArrayList<Integer>();//存储topK候选
		dataStream = new Stream();
		
		int dim = dataStream.getData(dataFile);//待选数据流准备好
		
		LSH_topK lshi = new LSH_topK(K,L,relativeError, expire);//sketch建好
		
		///////规范化并转换P！
		LinkedList<Data> datas = dataStream.norm(Stream.streams, 0.85,3);//U=0.85和m=3
		
		dim+=3;//dim+=m;
		
		///////
		SignRandomProjection proj = new SignRandomProjection(dim, K,L);//哈希向量准备好
		//L2lsh proj= new L2lsh(dim, K, L);
		int[][]  hashes;
		//int[] hashes;
//		int count=0;
		while(!datas.isEmpty()) {
			Data data = new Data();
			data  = datas.poll();
			hashes = proj.getHash(data.values);
			lshi.add(hashes, data.arrivalTime,data.key);
//			lshi.addL2(hashes, data.arrivalTime,data.key);
			Constant.curTime = data.arrivalTime;
//			count++;
//			if(count>5000)
//				break;
		}
		
		//	Q转换，Q(x)=[x;1/2;1/2;...;1/2]	
		double[] topkbase = dataStream.transQ(Constant.topkBase,3);//m=3是添加的维度
		hashes = proj.getHash(topkbase);//L行，每行的01情况
		topK=lshi.Query(hashes, Constant.topkBase, Constant.timeRange, Constant.topK);
//		topK=lshi.QueryL2(hashes,Constant.timeRange,Constant.topK);
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
	
	public static void main(String[] args) {
//		int window[] =  {1000,5000,10000,15000,20000}; 
//		for (int i = 0; i < window.length; i++) {
//			detectOutliers(fileName, 0.1, window[i], 6, 2, 30);
//		}
		Constant.flag=0;
		if(Constant.flag==0) {
			detectOutliers(Constant.dataPath, Constant.relativeError, Constant.expire, Constant.K, Constant.L, Constant.alpha);
			List<Integer> outLiers = new ArrayList<>(outliers);
			Collections.sort(outLiers);
			System.out.println(outLiers.size());
			write(Constant.outputPath, outLiers);
			System.out.println("Over!");
		}else {
			findMIPS(Constant.topKcoll, Constant.relativeError, Constant.expire, Constant.K, Constant.L, Constant.topK);
			List<Integer> topk = new ArrayList<>(topK);
			Collections.sort(topk);
			System.out.println(topk.size());
			write(Constant.topKPath, topk);
			System.out.println("Over!");
		}
	}
}
