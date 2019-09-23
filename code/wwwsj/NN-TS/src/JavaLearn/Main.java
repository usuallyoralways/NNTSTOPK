package JavaLearn;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        int hashes=12;
        int hashTables=13;
        double percentageCorrect=1.2;
        double percentageTouched=2.4;
        double linearSearchTime=45.9;
        double lshSearchTime =345.2;
        double precision=23;
        double recall =34;
        System.out.printf("%10d%15d%9.2f%%%9.2f%%%9.4fs%9.4fs%9.2f%%%9.2f%%\n",
                hashes, hashTables, percentageCorrect, percentageTouched, linearSearchTime, lshSearchTime, precision, recall);
    }

}
