package lsh;

/*
1，new ALSH
2, read dataset
3, buildIndex
4，read query
5, calculate precision and recall
 */


import Constant.Constant;
import lsh.families.EuclidianHashFamily;
import lsh.families.HashFamily;
import lsh.families.InnerProductDistance;

import java.util.List;

public class L2LSH extends BashLSH {


    public L2LSH(List<Vector> dataset, HashFamily hashFamily) {
        super(dataset, hashFamily);
    }
    public L2LSH() {
        super();

    }

    public void initL2LSH(double radius,String datasetPath){
        setRadius(radius);
        setDatasetPath(datasetPath);


        setMeasure();
        readDataset(true);
        setDimensions();
        setHashFamily();

    }


    public static void main(String[] args){
        String filePate= Constant.basePath+"Mnist/Mnist.ds";
        String queryPate= Constant.basePath+"Mnist/Mnist.q";
        L2LSH lsh = new L2LSH();
        lsh.initL2LSH(10,filePate);
        System.out.println();
        lsh.setNumberOfHashes(2);
        lsh.setNumberOfHashTables(5);
        lsh.setQueriesPath(queryPate);
        lsh.setW(50);
        lsh.readQueries(true);
        lsh.setNumberOfNeighbours(10);
        lsh.setNeighboursSize(10);


        lsh.buildIndex(lsh.getNumberOfHashes(),lsh.getNumberOfHashTables());


        if(lsh.getQueries() != null){
            for(Vector query:lsh.getQueries()){
                List<Vector> neighbours = lsh.query(query, lsh.getNumberOfNeighbours());
                System.out.print(query.getKey()+";");
                for(Vector neighbour:neighbours){
                    System.out.print(neighbour.getKey() + ";");
                }
                System.out.print("\n");
            }
        }
        lsh.benchmark();


    }

    public void setMeasure(){
        setMeasure(new InnerProductDistance());
    }
    public void setHashFamily() {
        if (getRadius() < 0) {
            HashFamily hashFamily = new EuclidianHashFamily(getW(), getDimensions());
            setHashFamily(hashFamily);
        } else {
            int w = (int) (10 * getRadius());
            w = w == 0 ? 1 : w;
            HashFamily hashFamily = new EuclidianHashFamily(w, getDimensions());
            setHashFamily(hashFamily);
        }
    }
}