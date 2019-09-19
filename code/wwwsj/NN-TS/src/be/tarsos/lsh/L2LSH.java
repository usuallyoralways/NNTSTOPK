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
        readDataset(false);
    }


    public static void main(String[] args){
        String filePate= Constant.basePath+"Mnist/Mnist.ds";
        String queryPate= Constant.basePath+"Mnist/Mnist.q";
        L2LSH lsh = new L2LSH();
        lsh.initL2LSH(10,50,filePate);
        lsh.setQueriesPath(queryPate);
        lsh.readQueries(false);

    }

    public void setMeasure(){
        setMeasure(new EuclideanDistance());
        setFamily();
    }
    public void setFamily(){
        int w = (int) (10 * getRadius());
        w = w == 0 ? 1 : w;
        HashFamily hashFamily = new EuclidianHashFamily(w,getDimensions());
        setFamily(hashFamily);
    }
}