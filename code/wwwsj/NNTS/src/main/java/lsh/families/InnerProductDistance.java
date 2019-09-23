package lsh.families;

import lsh.Vector;

public class InnerProductDistance implements DistanceMeasure{

    @Override
    public double distance(Vector one, Vector other) {
        double value = one.dot(other);
        if (value==0){
            return Double.MAX_VALUE;
        }else{
            return 100.0/value;
        }
    }
}
