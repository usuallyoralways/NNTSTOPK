package LSH;

import java.awt.MenuComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import DW.DeterministicWave;
import DW.Element;
import DW.OverElement;
import Stream.Stream;
/**
 * @author zhihui
 * Work out all hash values and mean count;
 */
public class LSH_topK {
	private int K;
	private int L;
	private double relativeError;
	private int expire;
    public DeterministicWave bucket[][];
    
	public LSH_topK(int K, int L,double relativeError,int expire) {
		this.K = K;
		this.L = L;
		this.relativeError = relativeError;
		this.expire = expire;
		bucket = new DeterministicWave[L][1<<K];
	}
	
	public void add(int[][] hashes, int time,int key) {
		int index = 0;
		for (int i = 0; i < L; i++) {
			index = 0;
			for (int j = 0; j < K; j++) {
				index = index << 1;
				index = index + hashes[i][j];//得到K位哈希值
			}
			if(bucket[i][index]==null) {
				bucket[i][index]=new DeterministicWave();
				bucket[i][index].Initialized(relativeError,expire);
			}
			bucket[i][index].input(key,1, time);		
		}
	}
	
	//L2LSH  add
	public void addL2(int[] hashes, int time,int key) {
		for (int i = 0; i < L; i++) {
			if(bucket[i][hashes[i]]==null) {
				bucket[i][hashes[i]]=new DeterministicWave();
				bucket[i][hashes[i]].Initialized(relativeError,expire);
			}
			bucket[i][hashes[i]].input(key,1, time);		
		}
	}
	
	
	public ArrayList<Integer> Query(int[][] hashes,double[] q,int range,int topK) {
		int index;
		HashSet<Integer> res = new HashSet<>();
		ArrayList<Integer>[][] siMap = new ArrayList[L][K+1];
		
		int[] base_lable = new int[L];
		for (int i = 0; i < L; i++) {//获得base的位置
			index = 0;
			for (int j = 0; j < K; j++) {
				index = index << 1;
				index = index + hashes[i][j];//得到K位哈希值
			}
			base_lable[i]=index;
		}
		
		//求similarity
		for(int i=0;i<L;i++) {
			index = base_lable[i];
			for(int j=0;j<1<<K;j++) {
				int tmp = index^j;
				tmp=K-Integer.bitCount(tmp);
				if(bucket[i][j]!=null) {
					if(siMap[i][tmp]==null) {
						siMap[i][tmp]=new ArrayList<>();
					}
					siMap[i][tmp].add(j);
				}
			}
		}
		
		//求topk
	int count  = 0;
//		for(int i=K;i>=1;i--) {//相似度
		for(int i=K;i>=K;i--) {//相似度
			for(int j=0;j<L;j++) {//每一层都有相似度为i的桶
				if(siMap[j][i]!=null && count<topK) {//如果有这个相似度并且数量不达标			
					ArrayList<Integer> tmp= siMap[j][i];//第j层，相似度为i的桶的标号
					for(int ii=0;ii<tmp.size();ii++) {//对于每个桶
						LinkedList<Element> tmpall=bucket[j][tmp.get(ii)].getAllInWindow();
						for(Element e: tmpall) {
//							if(count>=topK)return res;
							res.add(e.getKey());
//							count++;
						}
						LinkedList<OverElement> overall=bucket[j][tmp.get(ii)].getOverflow();
						for(OverElement e: overall) {
//							if(count>=topK)return res;
							res.add(e.getKey());
//							count++;
						}
					}
				}
			}
		}
		
		//计算并排序
		 Map<Integer, Double> dots = new TreeMap<Integer, Double>();
		for (Integer i : res) {
			dots.put(i, dot(q, Stream.streams.get(i).values));
		}
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(
				dots.entrySet());
        //然后通过比较器来实现排序
            //升序排序       
		Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Entry<Integer, Double> o1,
                    Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
		
		ArrayList<Integer> restop=new ArrayList<Integer>();
		int resnum=0;
		 for(Entry<Integer, Double> mapping:list){ 
			 if(resnum>=10)break;
             restop.add(mapping.getKey());
             resnum++;
        }
		 
		
		return restop;
	}
	
	
	public HashSet<Integer> QueryL2(int[] hashes,int range,int topK) {
		int index;
		HashSet<Integer> res = new HashSet<>();
		ArrayList<Integer>[][] siMap = new ArrayList[L][K+1];
		
		int[] base_lable = hashes;
		
		//求similarity
		for(int i=0;i<L;i++) {
			index = base_lable[i];
			for(int j=0;j<1<<K;j++) {
				int tmp = index^j;
				tmp=4-Integer.bitCount(tmp);
				if(bucket[i][j]!=null) {
					if(siMap[i][tmp]==null) {
						siMap[i][tmp]=new ArrayList<>();
					}
					siMap[i][tmp].add(j);
				}
			}
		}
		
		//求topk
		int count  = 0;
		for(int i=K;i>=1;i--) {//相似度
			for(int j=0;j<L;j++) {//每一层都有相似度为i的桶
				if(siMap[j][i]!=null && count<topK) {//如果有这个相似度并且数量不达标
					ArrayList<Integer> tmp= siMap[j][i];//第j层，相似度为i的桶的标号
					for(int ii=0;ii<tmp.size();ii++) {//对于每个桶
						LinkedList<Element> tmpall=bucket[j][tmp.get(ii)].getAllInWindow();
						for(Element e: tmpall) {
							if(count>=topK)return res;
							res.add(e.getKey());
							count++;
						}
						LinkedList<OverElement> overall=bucket[j][tmp.get(ii)].getOverflow();
						for(OverElement e: overall) {
							if(count>=topK)return res;
							res.add(e.getKey());
							count++;
						}
					}
				}
			}
		}
		
		return res;
	}
	
	public static double dot(double[] q,double[] x) {
		double sum=0;
		for(int i=0;i<x.length;i++) {
			sum+=q[i]*x[i];
		}
		return sum;
	}
}
