package lsh;

import lsh.families.DistanceComparator;
import lsh.families.DistanceMeasure;
import lsh.families.HashFamily;
import lsh.Vector;
import topkmips.lsh.Hash;

import java.util.*;
import java.util.concurrent.*;

/*
1，new ALSH
2, read dataset
3, buildIndex
4，read query
5, calculate precision and recall
 */


public class BashLSH {
    private String[] arguments;

    private int numberOfHashTables;
    private int numberOfHashes;
    private int numberOfNeighbours;
    private double radius;

    private List<Vector> dataset;
    private String datasetPath;
    private List<Vector> queries;
    private String queriesPath;

    private int dimensions;
    private DistanceMeasure measure;
    private int timeout = 40; //seconds timeout for radius search.
//    private HashFamily family;

    private boolean benchmark;
    private boolean printHelp;
    private boolean linear;

    private Index index;
    private HashFamily hashFamily;

    private int neighboursSize; //numberOfNeighbours
    private int W;




    public BashLSH(List<Vector> dataset, HashFamily hashFamily) {

        this.dataset = dataset;
        this.hashFamily = hashFamily;
    }
    public BashLSH() {
    }

    public void readDataset(boolean firstColumnIsKey){
        dataset =readDataset(datasetPath,Integer.MAX_VALUE,firstColumnIsKey,dimensions);
    }

    public void readQueries(boolean firstColumnIsKey){
        queries = readDataset(queriesPath,Integer.MAX_VALUE,firstColumnIsKey,dimensions);
    }


    public void buildIndex(int numberOfHashes, int numberOfHashTables){
        // Do we want to deserialize or build a new index???
        // index = new Index(hashFamily, numberOfHashes, numberOfHashTables);
        // Deserialization can cause duplicates?
        //index = Index.deserialize(hashFamily, numberOfHashes, numberOfHashTables);
        index = new Index(hashFamily, numberOfHashes, numberOfHashTables);
        if(dataset != null){
            for(Vector vector : dataset){
                index.index(vector);
            }
            Index.serialize(index);
        }
    }

    /**
     * Benchmark the current LSH construction.
     //* @param neighboursSize the expected size of the neighbourhood.
     //* @param measure  The measure to use to check for correctness.
     */
    public void benchmark(){
        long startTime = 0;
        double linearSearchTime = 0;
        double lshSearchTime = 0;
        int numbercorrect = 0;
        int falsePositives = 0;
        int truePositives = 0;
        int falseNegatives = 0;
        //int intersectionSize = 0;
        for(int i = 0 ; i < queries.size() ; i++){
            Vector query = queries.get(i);
            startTime = System.currentTimeMillis();
            List<Vector> lshResult = index.query(query,neighboursSize);
            lshSearchTime += System.currentTimeMillis() - startTime;

            startTime = System.currentTimeMillis();
            List<Vector> linearResult = linearSearch(dataset,query,neighboursSize,measure);
            linearSearchTime += System.currentTimeMillis() - startTime;

            Set<Vector> set = new HashSet<Vector>();
            set.addAll(lshResult);
            set.addAll(linearResult);
            //intersectionSize += set.size();
            //In the best case, LSH result and linear result contain the exact same elements.
            //The number of false positives is the number of vectors that exceed the number of linear results.
            falsePositives += set.size() - linearResult.size();
            //The number of true positives is Union of results - intersection.
            truePositives += lshResult.size() + linearResult.size() - set.size();
            //The number of false Negatives the number of vectors that exceed the number of lsh results .
            falseNegatives +=  set.size() - lshResult.size();

            //result is only correct if all nearest neighbours are the same (rather strict).
            boolean correct = true;
            for(int j = 0 ; j < Math.min(lshResult.size(),linearResult.size()); j++){
                correct = correct && lshResult.get(j)== linearResult.get(j);
            }
            if(correct){
                numbercorrect++;
            }
        }
        double numberOfqueries = dataset.size();
        double dataSetSize = dataset.size();
        double precision = truePositives / Double.valueOf(truePositives+falsePositives) * 100;
        double recall = truePositives / Double.valueOf(truePositives+falseNegatives) * 100;
        double percentageCorrect = numbercorrect / dataSetSize * 100;
        double percentageTouched = index.getTouched()/numberOfqueries/dataSetSize*100;
        linearSearchTime/=1000.0;
        lshSearchTime/=1000.0;
        int hashes = index.getNumberOfHashes();
        int hashTables = index.getNumberOfHashTables();

        //System.out.printf("%10s%15s%10s%10s%10s%10s%10s%10s\n","#hashes","#hashTables","Correct","Touched","linear","lsh","Precision","Recall");
        System.out.printf("%10d%15d%9.2f%%%9.2f%%%15.4fs%9.4fs%9.2f%%%9.2f%%\n",
                hashes,hashTables,percentageCorrect,percentageTouched,linearSearchTime,lshSearchTime,precision,recall);
    }



    public void benchmarkWithFile(List<Vector> groundTruth){
        long startTime = 0;
        double linearSearchTime = 0;
        double lshSearchTime = 0;
        int numbercorrect = 0;
        int falsePositives = 0;
        int truePositives = 0;
        int falseNegatives = 0;
        //int intersectionSize = 0;

        for (Vector query : queries){
            Set<Integer> set = new HashSet<>();
            int key = Integer.valueOf(query.getKey());
            Vector linearResultKeyVector = groundTruth.get(key-1);

            List<Integer> linearResultKey = new ArrayList<>();
            for (int i = 0;i< linearResultKeyVector.getDimensions();i++){
                linearResultKey.add((int) linearResultKeyVector.get(i));
            }
            System.out.println(key);
            List<Vector> lshResult = index.query(query,neighboursSize);

            List<Integer> lshResultKey = new ArrayList<>();
            for (Vector item : lshResult){
                System.out.println(item.getKey());
                lshResultKey.add(Integer.valueOf(item.getKey()));
            }

            set.addAll(linearResultKey);
            set.addAll(lshResultKey);

            for (Integer integer: linearResultKey){
                System.out.print(integer+" ");
            }
            System.out.print("\n");
            for (Integer integer: lshResultKey){
                System.out.print(integer+" ");
            }
            System.out.print("\n");

            //intersectionSize += set.size();
            //In the best case, LSH result and linear result contain the exact same elements.
            //The number of false positives is the number of vectors that exceed the number of linear results.
            falsePositives += set.size() - linearResultKey.size();
            //The number of true positives is Union of results - intersection.
            truePositives += lshResultKey.size() + linearResultKey.size() - set.size();
            //The number of false Negatives the number of vectors that exceed the number of lsh results .
            falseNegatives +=  set.size() - lshResultKey.size();

            //result is only correct if all nearest neighbours are the same (rather strict).
            boolean correct = true;
            for(int j = 0 ; j < Math.min(lshResult.size(),linearResultKey.size()); j++){
                correct = correct && lshResultKey.get(j)== linearResultKey.get(j);
            }
            if(correct){
                numbercorrect++;
            }


            double precision = truePositives / Double.valueOf(truePositives+falsePositives) * 100;
            double recall = truePositives / Double.valueOf(truePositives+falseNegatives) * 100;

            System.out.printf("%9.2f%%%9.2f%%\n",precision,recall);

        }
        double numberOfqueries = dataset.size();
        double dataSetSize = dataset.size();
        double precision = truePositives / Double.valueOf(truePositives+falsePositives) * 100;
        double recall = truePositives / Double.valueOf(truePositives+falseNegatives) * 100;
        double percentageCorrect = numbercorrect / dataSetSize * 100;
        double percentageTouched = index.getTouched()/numberOfqueries/dataSetSize*100;

        int hashes = index.getNumberOfHashes();
        int hashTables = index.getNumberOfHashTables();

        //System.out.printf("%10s%15s%10s%10s%10s%10s%10s%10s\n","#hashes","#hashTables","Correct","Touched","linear","lsh","Precision","Recall");
        System.out.printf("%10d%15d%9.2f%%%9.2f%%%15.4fs%9.4fs%9.2f%%%9.2f%%\n",
                hashes,hashTables,percentageCorrect,percentageTouched,linearSearchTime,lshSearchTime,precision,recall);


    }


    /**
     * Find the nearest neighbours for a query in the index.
     *
     * @param query
     *            The query vector.
     * @param neighboursSize
     *            The size of the neighbourhood. The returned list length
     *            contains the maximum number of elements, or less. Zero
     *            elements are possible.
     * @return A list of nearest neigbours, according to the index. The returned
     *         list length contains the maximum number of elements, or less.
     *         Zero elements are possible.
     */
    public List<Vector> query(final Vector query,int neighboursSize){
        return index.query(query,neighboursSize);
    }



    /**
     * Search for the actual nearest neighbours for a query vector using an
     * exhaustive linear search. For each vector a priority queue is created,
     * the distance between the query and other vectors is used to sort the
     * priority queue. The closest k neighbours show up at the head of the
     * priority queue.
     *
     * @param dataset
     *            The data set with a bunch of vectors.
     * @param query
     *            The query vector.
     * @param resultSize
     *            The k nearest neighbours to find. Returns k vectors if the
     *            data set size is larger than k.
     * @param measure
     *            The distance measure used to sort the priority queue with.
     * @return The list of k nearest neighbours to the query vector, according
     *         to the given distance measure.
     */
    public static List<Vector> linearSearch(List<Vector> dataset,final Vector query,int resultSize,DistanceMeasure measure){
        DistanceComparator dc = new DistanceComparator(query, measure);
        PriorityQueue<Vector> pq = new PriorityQueue<Vector>(dataset.size(),dc);
        pq.addAll(dataset);
        List<Vector> vectors = new ArrayList<Vector>();
        for(int i = 0 ; i < resultSize;i++){
            vectors.add(pq.poll());
        }
        return vectors;
    }

    /**
     * Read a data set from a text file. The file has the following contents,
     * with identifier being an optional string identifying the vector and a
     * list of N coordinates (which should be doubles). This results in an
     * N-dimensional vector.
     *
     * <pre>
     * [Identifier] coord1 coord2 ... coordN
     * [Identifier] coord1 coord2 ... coordN
     * </pre>
     *
     * For example a data set with two elements with 4 dimensions looks like
     * this:
     *
     * <pre>
     * Hans 12 24 18.5 -45.6
     * Jane 13 19 -12.0 49.8
     * </pre>
     *
     *
     * @param file
     *            The file to read.
     * @param maxSize
     *            The maximum number of elements in the data set (even if the
     *            file defines more points).
     * @return a list of vectors, the data set.
     */



    public static List<Vector> readDataset(String file,int maxSize,boolean firstColumnIsKey,int dimensions) {
        List<Vector> ret = new ArrayList<Vector>();
        List<String[]> data = FileUtils.readCSVFile(file, " ", -1,dimensions+1);
        if(data.size() > maxSize){
            data = data.subList(0, maxSize);
        }
//		firstColumnIsKey = false;
//		try{
//			Double.parseDouble(data.get(0)[0]);
//		}catch(Exception e){
//			firstColumnIsKey = true;
//		}

        System.out.println(firstColumnIsKey);
        System.out.println(firstColumnIsKey);
        System.out.println(firstColumnIsKey);
        System.out.println(firstColumnIsKey);


        int datadimensions = firstColumnIsKey ? data.get(0).length - 1 : data.get(0).length;

        int startIndex = firstColumnIsKey ? 1 : 0;
        for(String[] row : data){
            Vector item = new Vector(dimensions);
            if(firstColumnIsKey){
                item.setKey(row[0]);
            }

            for (int d = startIndex; d < row.length; d++) {
                double value = Double.parseDouble(row[d]);
                item.set(d - startIndex, value);
            }
            ret.add(item);
        }
        return ret;
    }


    public static List<Vector> readGroundTruth(String file,int maxSize,boolean firstColumnIsKey,int dimensions) {

        List<Vector> ret = new ArrayList<Vector>();

        int count = 0;
        List<String[]> data = FileUtils.readCSVFile(file, " ", -1,dimensions*2);
        if(data.size() > maxSize){
            data = data.subList(0, maxSize);
        }
//		firstColumnIsKey = false;
//		try{
//			Double.parseDouble(data.get(0)[0]);
//		}catch(Exception e){
//			firstColumnIsKey = true;
//		}

        System.out.println(firstColumnIsKey);
        System.out.println(firstColumnIsKey);
        System.out.println(firstColumnIsKey);
        System.out.println(firstColumnIsKey);

        int m = Integer.valueOf(data.get(0)[0]);
        int n = Integer.valueOf(data.get(0)[1]);



        int datadimensions = firstColumnIsKey ? data.get(0).length - 1 : data.get(0).length;

        int startIndex = firstColumnIsKey ? 1 : 0;
        for(String[] row : data){
            if (count==0){
                count++;
                continue;
            }
            Vector item = new Vector(dimensions);
            if(firstColumnIsKey){
                item.setKey(row[0]);
            }else {
                item.setKey(String.valueOf(count));
            }

            for (int d = startIndex; d < row.length; d++) {
                System.out.println(d);
                if (d%2==0) {
                    double value = Double.parseDouble(row[d]);
                    item.set((d - startIndex)/2, value);
                }
            }
            count++;
            ret.add(item);
        }
        return ret;
    }




    public static double determineRadius(List<Vector> dataset,DistanceMeasure measure,int timeout){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        double radius = 0.0;
        LSH.DetermineRadiusTask drt = new LSH.DetermineRadiusTask(dataset,measure);
        Future<Double> future = executor.submit(drt);
        try {
            System.out.println("Determine radius..");
            radius = 0.90 * future.get(timeout, TimeUnit.SECONDS);
            System.out.println("Determined radius: " + radius);
        } catch (TimeoutException e) {
            System.err.println("Terminated!");
            radius = 0.90 * drt.getRadius();
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted!" + e.getMessage());
            radius = 0.90 * drt.getRadius();
        } catch (ExecutionException e) {
            radius = 0.90 * drt.getRadius();
        }
        executor.shutdownNow();
        return radius;
    }


    public static class DetermineRadiusTask implements Callable<Double> {
        private double queriesDone = 0;
        private double radiusSum = 0.0;
        private final List<Vector> dataset;
        private final Random rand;
        private final DistanceMeasure measure;

        public DetermineRadiusTask(List<Vector> dataset,DistanceMeasure measure){
            this.dataset = dataset;
            this.rand = new Random();
            this.measure=measure;
        }

        @Override
        public Double call() throws Exception {
            for(int i = 0 ; i < 30; i ++){
                Vector query = dataset.get(rand.nextInt(dataset.size()));
                List<Vector> result = linearSearch(dataset, query, 2, measure);
                //the first vector is the query self, the second the closest.
                radiusSum += measure.distance(query, result.get(1));
                queriesDone++;
            }
            return radiusSum/queriesDone;
        }

        public double getRadius(){
            return radiusSum/queriesDone;
        }
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public int getNumberOfHashTables() {
        return numberOfHashTables;
    }

    public void setNumberOfHashTables(int numberOfHashTables) {
        this.numberOfHashTables = numberOfHashTables;
    }

    public int getNumberOfHashes() {
        return numberOfHashes;
    }

    public void setNumberOfHashes(int numberOfHashes) {
        this.numberOfHashes = numberOfHashes;
    }

    public int getNumberOfNeighbours() {
        return numberOfNeighbours;
    }

    public void setNumberOfNeighbours(int numberOfNeighbours) {
        this.numberOfNeighbours = numberOfNeighbours;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Vector> getDataset() {
        return dataset;
    }

    public void setDataset(List<Vector> dataset) {
        this.dataset = dataset;
    }

    public List<Vector> getQueries() {
        return queries;
    }

    public void setQueries(List<Vector> queries) {
        this.queries = queries;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public void setDimensions(){
        dimensions = dataset.get(0).getDimensions();
    }


    public DistanceMeasure getMeasure() {
        return measure;
    }

    public void setMeasure(DistanceMeasure measure) {
        this.measure = measure;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }



    public boolean isBenchmark() {
        return benchmark;
    }

    public void setBenchmark(boolean benchmark) {
        this.benchmark = benchmark;
    }

    public boolean isPrintHelp() {
        return printHelp;
    }

    public void setPrintHelp(boolean printHelp) {
        this.printHelp = printHelp;
    }

    public boolean isLinear() {
        return linear;
    }

    public void setLinear(boolean linear) {
        this.linear = linear;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public HashFamily getHashFamily() {
        return hashFamily;
    }

    public String getDatasetPath() {
        return datasetPath;
    }

    public void setDatasetPath(String datasetPath) {
        this.datasetPath = datasetPath;
    }

    public String getQueriesPath() {
        return queriesPath;
    }

    public void setQueriesPath(String queriesPath) {
        this.queriesPath = queriesPath;
    }

    public void setHashFamily(HashFamily hashFamily) {
        this.hashFamily = hashFamily;
    }

    public int getW() {
        return W;
    }

    public void setW(int w) {
        W = w;
    }

    public int getNeighboursSize() {
        return neighboursSize;
    }

    public void setNeighboursSize(int neighboursSize) {
        this.neighboursSize = neighboursSize;
    }
}
