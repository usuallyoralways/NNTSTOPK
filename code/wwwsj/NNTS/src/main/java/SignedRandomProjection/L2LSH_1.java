package SignedRandomProjection;

import lsh.*;
import lsh.families.DistanceMeasure;
import lsh.families.HashFamily;

import java.util.List;

public class L2LSH_1 {
    private String[] arguments;

    private int numberOfHashTables;
    private int numberOfHashes;
    private int numberOfNeighbours;
    private double radius;

    private List<Vector> dataset;
    private List<Vector> queries;

    private int dimensions;
    private DistanceMeasure measure;
    private int timeout = 40; //seconds timeout for radius search.
    private HashFamily family;

    private boolean benchmark;
    private boolean printHelp;
    private boolean linear;



}
