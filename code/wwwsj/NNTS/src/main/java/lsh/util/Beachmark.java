package lsh.util;

import java.util.Arrays;
import java.util.HashMap;

public class Beachmark {



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
}
