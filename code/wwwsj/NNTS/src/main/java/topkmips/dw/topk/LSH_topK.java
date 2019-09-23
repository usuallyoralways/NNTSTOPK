package topkmips.dw.topk;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author zhihui
 * Work out all hash values and mean count;
 */
public class LSH_topK {
	
	public void PR (int topt, int[] truth, int[][] hashes,int[][] query) {
		for(int q=0;q<query.length;q++) {
			int indexes[] = topIndex(topt, truth, hashes, query[q]);
			Arrays.sort(indexes);
			for(int i=0;i<topt;i++) {
				double recall = (i+1)*1.0/topt;
				double precision = (i+1)*1.0/indexes[i];
				System.out.println(recall+"  "+precision +"  "+ indexes[i]);
			}
			System.out.println("=====================");
		}
	}

	public int[] topIndex(int topt, int[] truth, int[][] hashes, int query[]) {
		HashMap<Integer,Integer> sortIndex = hamm(hashes, query);
		int indexes[] = new int[topt];
		for(int i=0;i<topt;i++) {
			indexes[i]=sortIndex.get(truth[i]);
		}
		return indexes;
	}
	
	public HashMap<Integer,Integer> hamm(int[][] hashes, int query[]) {
		TreeMap<Integer , Integer> dis=new TreeMap<Integer, Integer>();
		for(int i=0;i<hashes.length;i++) {
			dis.put(i, hammdis(hashes[i], query));
		}
		
		List<Entry<Integer , Integer>> list = new ArrayList<Entry<Integer , Integer>>(
				dis.entrySet());
        //然后通过比较器来实现排序
            //升序排序
		Collections.sort(list,new Comparator<Entry<Integer , Integer>>() {
            public int compare(Entry<Integer , Integer> o1,
                    Entry<Integer , Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
		HashMap<Integer,Integer> sortIndex = new HashMap<Integer,Integer>();
		int i=1;
		for(Entry<Integer, Integer> e:list) {
			sortIndex.put(e.getKey(),i);
			i++;
		}
		return sortIndex;
	}
	
	public int hammdis(int[] data,int[] query) {
		int res=0;
		for(int i=0;i<data.length;i++) {
			if(data[i]!=query[i])
				res++;
		}
		return res;
	}
	
}
