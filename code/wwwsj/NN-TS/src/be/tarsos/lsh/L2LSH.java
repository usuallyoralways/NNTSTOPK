package be.tarsos.lsh;

/*
1，new ALSH
2, read dataset
3, buildIndex
4，read query
5, calculate precision and recall
 */


import be.tarsos.lsh.families.EuclideanDistance;
import be.tarsos.lsh.families.EuclidianHashFamily;
import be.tarsos.lsh.families.HashFamily;

import java.util.List;
import Constant.*;

public class L2LSH extends BashLSH {


    public L2LSH(List<Vector> dataset, HashFamily hashFamily) {
        super(dataset, hashFamily);
    }
    public L2LSH() {
        super();

    }

    public void initL2LSH(double radius,int dimensions,String datasetPath){
        setRadius(radius);
        setDimensions(dimensions);
        setDatasetPath(datasetPath);

        setFamily();
        setMeasure();
        readDataset(true);


    }


    public static void main(String[] args){
        String filePate= Constant.basePath+"Mnist/Mnist.ds";
        String queryPate= Constant.basePath+"Mnist/Mnist.q";
        L2LSH lsh = new L2LSH();
        lsh.setW(51);
        lsh.initL2LSH(-10,50,filePate);
        System.out.println();
        lsh.setNumberOfHashes(2);
        lsh.setNumberOfHashTables(5);
        lsh.setQueriesPath(queryPate);
        lsh.readQueries(true);
        lsh.setNumberOfNeighbours(10);
        lsh.setNeighboursSize(10);


        lsh.buildIndex(lsh.getNumberOfHashes(),lsh.getNumberOfHashTables());

        System.out.println(123);

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
        setMeasure(new EuclideanDistance());
        setFamily();
    }
    public void setFamily() {
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