package be.tarsos.lsh;

import Constant.Constant;
import be.tarsos.lsh.families.*;
import DW.DeterministicWave;

import java.util.*;
import java.util.concurrent.*;

/*
//Asymmetric LSH (ALSH) for Sublinear Time Maximum Inner Product Search (MIPS)s
1，new ALSH
2, read dataset
3, buildIndex
4，read query
5, calculate precision and recall
 */


public class ALSH extends BashLSH{

    //ALSH Data Transform is asymmetric. add m dims to data \\x\\^i and x .5 values to queries
    private int M;
    private List<Vector> datasetTrans;
    private List<Vector> queriesTrans;
    private double normOfData=-11;

    public ALSH(List<Vector> dataset, HashFamily hashFamily) {
        super(dataset, hashFamily);
    }
    public ALSH() {
        super();

    }

    public void initALSH(double radius,String datasetPath,int M){
        setM(M);
        setDatasetPath(datasetPath);
        setHashFamily();
        setMeasure();
        setDimensions(128);
        readDataset(true);
        setDimensions();


    }

    public void setDatasetTrans() {
        datasetTrans = new ArrayList<>();
        for (Vector item : getDataset()) {
            String key= item.getKey();
            Vector vectorTrans = new Vector(getDimensions()+M);
            for (int i=0;i<getDimensions();i++ ){
                vectorTrans.set(i,item.get(i));
            }
            if (getNormOfData()<0){
                setNormOfData(normListVector(getDataset()));
            }
            for (int i=0;i<M;i++){
                double temp = Math.pow(getNormOfData(),2*(i+1));
                vectorTrans.set(i+getDimensions(),temp);
            }
        }
    }


    public void setQueriesTrans() {
        queriesTrans = new ArrayList<>();
        for (Vector item : getDataset()) {
            String key= item.getKey();
            Vector vectorTrans = new Vector(getDimensions()+M);
            for (int i=0;i<getDimensions();i++ ){
                vectorTrans.set(i,item.get(i));
            }
            if (getNormOfData()<0){
                setNormOfData(normListVector(getDataset()));
            }
            for (int i=0;i<M;i++){
                double temp = Math.pow(getNormOfData(),2*(i+1));
                vectorTrans.set(i+getDimensions(),temp);
            }
        }
    }


    public static void main(String[] args){
//        String filePate= Constant.basePath+"Mnist/Mnist.ds";
//        String queryPate= Constant.basePath+"Mnist/Mnist.q";
        String basePath=args[0];
        String filePate= basePath+"Gist/Gist.ds";
        String queryPate= basePath+"Gist/Gist.q";
        ALSH lsh = new ALSH();

        lsh.initALSH(-10,filePate,3);

        System.out.println("norm==="+normListVector(lsh.getDataset()));


        System.out.println();
        lsh.setNumberOfHashes(2);
        lsh.setNumberOfHashTables(5);
        lsh.setQueriesPath(queryPate);
        lsh.readQueries(true);
        lsh.setNumberOfNeighbours(10);
        lsh.setNeighboursSize(10);
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

    private void setMeasure() {
        setMeasure(new InnerProductDistance());
    }


    public int getM() {
        return M;
    }

    public void setM(int m) {
        M = m;
    }

    public List<Vector> getDatasetTrans() {
        return datasetTrans;
    }

    public void setDatasetTrans(List<Vector> datasetTrans) {
        this.datasetTrans = datasetTrans;
    }

    public List<Vector> getQueriesTrans() {
        return queriesTrans;
    }

    public void setQueriesTrans(List<Vector> queriesTrans) {
        this.queriesTrans = queriesTrans;
    }

    public double getNormOfData() {
        return normOfData;
    }

    public void setNormOfData(double normOfData) {
        this.normOfData = normOfData;
    }

    public static double normListVector(List<Vector> vectorList){
        List<Double> doubleList = new ArrayList<Double>();
        double sum=0;
        double limitValue=100000000;
        int count=0;
        for (Vector vector: vectorList){
            for (int i=0;i<vector.getDimensions();i++){
                sum+=vector.get(i)*vector.get(i);
                if (sum>limitValue){
                    doubleList.add(limitValue);
                    sum-=limitValue;
                }
            }
            count++;
            System.out.println(count);
        }
        doubleList.add(sum);
        double last = sum/limitValue;
        double len= doubleList.size()-1+last;
        return Math.sqrt(len)*Math.sqrt(limitValue);

    }
}
