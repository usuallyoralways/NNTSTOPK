package be.tarsos.lsh;

/*
1，new ALSH
2, read dataset
3, buildIndex
4，read query
5, calculate precision and recall
 */


import be.tarsos.lsh.families.HashFamily;

import java.util.List;

public class L2LSH extends BashLSH {
    public L2LSH(List<Vector> dataset, HashFamily hashFamily) {
        super(dataset, hashFamily);
    }
    public static void main(String[] args){

    }
}