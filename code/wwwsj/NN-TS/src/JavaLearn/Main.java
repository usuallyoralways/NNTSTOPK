package JavaLearn;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args){
        ArrayList<Integer> aList = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            aList.add(i);
        }
        Collections.shuffle(aList);

    }

}
