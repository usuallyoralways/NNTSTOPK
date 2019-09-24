package lsh;


import Constant.Constant;
import lsh.families.EuclideanDistance;
import lsh.families.EuclidianHashFamily;
import lsh.families.HashFamily;

import java.util.ArrayList;
import java.util.List;
import lsh.Vector;
import lsh.families.InnerProductDistance;

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
    private double U;
    private double _M;

    public double get_M() {
        return _M;
    }

    public void set_M(double _M) {
        this._M = _M;
    }

    public ALSH(List<Vector> dataset, HashFamily hashFamily) {
        super(dataset, hashFamily);
    }
    public ALSH() {
        super();

    }

    public void initALSH(double radius,String datasetPath,int M,double U){
        setU(U);
        setM(M);
        set_M(M);
        setRadius(radius);
        setDatasetPath(datasetPath);

        setMeasure();
        setDimensions(50);
        readDataset(true);
        setDimensions();
        int radiusEuclidean = (int) LSH.determineRadius(getDataset(), new EuclideanDistance(), 20);
        setRadius(radiusEuclidean);
    }






    public static void main(String[] args){
//        String filePate= Constant.basePath+"Mnist/Mnist.ds";
//        String queryPate= Constant.basePath+"Mnist/Mnist.q";
//        String basePath=args[0];
        String basePath;
        if (args.length>1){
            basePath=args[1];
        }else {
            basePath= Constant.basePath;
        }
        System.out.println(basePath);
        String filePate= basePath+"Mnist/Mnist.ds";
        String queryPate= basePath+"Mnist/Mnist.q";
        ALSH lsh = new ALSH();

        lsh.initALSH(-10,filePate,3,0.85);




        System.out.println();
        lsh.setNumberOfHashes(4);
        lsh.setNumberOfHashTables(3);
        lsh.setQueriesPath(queryPate);
        lsh.setW(50);
        lsh.readQueries(true);

        lsh.setM(0);
        lsh.setDatasetTrans();
        lsh.setQueriesTrans();

        lsh.changeVector();
        lsh.setHashFamily();

//
//        for (Vector item : lsh.getDataset()){
//            System.out.println(item.getDimensions());
//            System.out.println(item);
//        }

        lsh.setNumberOfNeighbours(10);
        lsh.setNeighboursSize(10);

        lsh.buildIndex(lsh.getNumberOfHashes(),lsh.getNumberOfHashTables());


//        if(lsh.getQueries() != null){
//            for(Vector query:lsh.getQueries()){
//                List<Vector> neighbours = lsh.query(query, lsh.getNumberOfNeighbours());
//                System.out.print(query.getKey()+";");
//                for(Vector neighbour:neighbours){
//                    System.out.print(neighbour.getKey() + ";");
//                }
//                System.out.print("\n");
//            }
//        }


        lsh.changeVectorAgain();


        List<Vector> vectorList= lsh.readGroundTruth(Constant.basePath+"Mnist/Mnist.mip",Integer.MAX_VALUE,false,10);
        for (int i=0;i<5;i++){
            System.out.println(vectorList.get(i).toString());
        }

        lsh.benchmarkWithFile(vectorList);
        lsh.benchmark();
    }


    public void changeVector(){
        List<Vector> temp = datasetTrans;
        datasetTrans = getDataset();
        setDataset(temp);

        List<Vector> tempQuery = queriesTrans;
        queriesTrans = getQueries();
        setQueries(tempQuery);
        setDimensions(getDimensions()+getM());
    }

    public void changeVectorAgain(){
        List<Vector> temp = datasetTrans;
        datasetTrans = getDataset();
        setDataset(temp);

        List<Vector> tempQuery = queriesTrans;
        queriesTrans = getQueries();
        setQueries(tempQuery);
        setDimensions(getDimensions()-getM());
    }

    public List<Double> norm(){
        List<Double> normList = new ArrayList<>();
        for (Vector item : getDataset()){
            double value = Math.sqrt(item.dot(item));
            normList.add(value);
            if (value>_M){
                _M=value;
            }
        }
        return normList;
    }

    public void setDatasetTrans() {

        List<Double> normList = norm();
        double scale = getU()/get_M();
        datasetTrans = new ArrayList<>();
        int count=0;
        for (Vector item : getDataset()) {
            String key= item.getKey();
            Vector vectorTrans = new Vector(getDimensions()+M);
            vectorTrans.setKey(key);
            for (int i=0;i<getDimensions();i++ ){
                vectorTrans.set(i,item.get(i)*scale);
            }
            for (int i=0;i<M;i++){
                double temp = Math.pow(normList.get(count),2*(i+1));
                vectorTrans.set(i+getDimensions(),temp);
            }
            count++;
            datasetTrans.add(vectorTrans);
        }
    }


    public void setQueriesTrans() {
        queriesTrans = new ArrayList<>();
        for (Vector item : getQueries()) {
            String key= item.getKey();
            Vector vectorTrans = new Vector(getDimensions()+M);
            vectorTrans.setKey(key);
            for (int i=0;i<getDimensions();i++ ){
                vectorTrans.set(i,item.get(i));
            }
            for (int i=0;i<M;i++){
                vectorTrans.set(i+getDimensions(),0.5);
            }
            queriesTrans.add(vectorTrans);
        }
    }

    public double getNormOfData() {
        return normOfData;
    }

    public void setNormOfData(double normOfData) {
        this.normOfData = normOfData;
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

    public double getU() {
        return U;
    }

    public void setU(double u) {
        U = u;
    }

    public static double normVector(Vector vector){
        double sum=0;
        for (int i=0;i<vector.getDimensions();i++) {
            sum += vector.get(i) * vector.get(i);
        }
        return Math.sqrt(sum);
    }
}
