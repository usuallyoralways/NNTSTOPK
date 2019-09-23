package Stream;

import Constant.Constant;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Stream {

    public static  ArrayList<Data> streams;
    private  double maxnorm = 0.0;//保存最大的|x|2
    public  Stream() {
    	streams = new ArrayList();
    }

    public int getData(String filename) {
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)));
            String line = "";
            int time = 1;//毫秒      
            ArrayList<double[]> minMax = new ArrayList<>();
            try {
                while ((line = bfr.readLine()) != null) {
                    String[] atts = line.split(",| ");
                    double [] d = new double[atts.length-1];//第一位是key，后面是属性值
                   
                    double norm =0;
                    
                    //最大最小值
                    for (int i = 1; i <= d.length; i++) {
                    	double att=Double.valueOf(atts[i]);
                    	/**最大最小标准化
                    	double[] mm;
                    	if(minMax.size()<d.length) {
                    		mm = new double[2];
                			mm[0]=att>0?att:-att;
                			mm[1]=att>0?att:-att;
                			minMax.add(mm);
                    	}else {
	                    	mm =minMax.get(i-1);
	            			mm[0]=Math.min(mm[0], Math.abs(att));
	            			mm[1]=Math.max(mm[1], Math.abs(att));
                    	}
                    	if(mm[1]==mm[0])
                    		d[i-1]=(att>0?1:-1)*Math.random();
                    	else 
	                        d[i-1] = (att>0?1:-1)*((Math.abs(att)-mm[0])/(mm[1]-mm[0]));
	                     */
                    	//d[i-1] =att/255;//最大值255，最小值0
                    	d[i-1] =att;
                    	norm+=d[i-1]*d[i-1];
                    }
                   
                    Data data = new Data(Integer.valueOf(atts[0]),d,time);
                    streams.add(data);
                    time++;
                    
                    maxnorm = Math.max(maxnorm,norm);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return streams.get(0).values.length;
    }
    
    public LinkedList<Data> norm(ArrayList<Data> streams2 , double U, int m){
    	//论文第七页最下面的规范化实现
    	double cons = Math.sqrt(maxnorm)/U;
    	Constant.cons=cons;
    	LinkedList<Data> res  = new LinkedList<>();
    	int index=0;
    	while(index<streams2.size()) {
    		Data tmp = streams2.get(index);
    		index++;
    		double mm[] = new double[tmp.values.length+m];//增加额外的维度
    		double norm = 0;
    		for(int i=0;i<mm.length-m;i++) {
    			mm[i]=tmp.values[i]/cons;
    			norm+=mm[i]*mm[i];
    		}
    		norm=Math.sqrt(norm);
    		for(int i=mm.length-m,j=1;j<=m;i++,j++) {
    			mm[i]=Math.pow(norm, 1<<j);   //norm^(a^m)
    		}
    		Data data = new Data(tmp.key,mm,tmp.arrivalTime);
    		res.addLast(data);
    	}
    	return res;
    }
    
    public double[] transQ(double[] q,int m) {
    	//先标准化
		double norm=0;
		double res[] = new double[q.length+m];
		for(int i=0;i<q.length;i++) {
			norm+=q[i]*q[i];
		}//求长度
		norm = Math.sqrt(norm);
		for(int i=0;i<q.length;i++) {
			res[i]=q[i]/norm;
		}
		for(int i=q.length;i<q.length+m;i++) {
			res[i]=0.5;
		}
		return res;
    }
}
