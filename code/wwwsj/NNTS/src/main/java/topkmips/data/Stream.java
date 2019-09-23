package topkmips.data;

import java.io.*;
import java.util.ArrayList;
import Constant.Constant;

public class Stream {

    public static  ArrayList<Data> streams;
    private  double maxnorm = 0.0;//保存最大的|x|2
    public  Stream() {
    	streams = new ArrayList<Data>();
    }

    
    //读数据，存成一个arraylist
    public int getData(String filename) {
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)));
            String line = "";
            int time = 1;//毫秒      
            try {
                while ((line = bfr.readLine()) != null) {
                    String[] atts = line.split(",| ");
                    double [] d = new double[atts.length-1];//第一位是key，后面是属性值
                    double norm =0;
                    for (int i = 1; i <= d.length; i++) {
                    	d[i-1] =Double.valueOf(atts[i]);
                    	norm+=d[i-1]*d[i-1];
                    }
                   
                    Data data = new Data(Integer.valueOf(atts[0]),d,time);
                    streams.add(data);
                    time++;
                    
                    //最大的二范数
                    maxnorm = Math.max(maxnorm,norm);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return streams.get(0).values.length;//返回维度
    }
    
    public ArrayList<Double[]> getQueries(String filename) {
        ArrayList<Double[]> queries = new ArrayList<>();
    	try {
            BufferedReader bfr = new BufferedReader(new FileReader(new File(filename)));
            String line = "";
            try {
                while ((line = bfr.readLine()) != null) {
                    String[] atts = line.split(",| ");
                    Double[] d = new Double[atts.length-1];//第一位是key，后面是属性值
                    for (int i = 1; i <= d.length; i++) {
                    	d[i-1] =Double.valueOf(atts[i]);
                    }
                   queries.add(d);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return queries;
	}
    
    //处理数据集
    public ArrayList<Data> normTransP(ArrayList<Data> streams2 , double U, int m){
    	//论文第七页最下面的规范化实现
    	double cons = Math.sqrt(maxnorm)/U;
    	Constant.cons=cons;
    	ArrayList<Data> res  = new ArrayList<>();
    	for(Data tmp:streams2) {
    		double mm[] = new double[tmp.values.length+m];//增加额外的维度
    		double norm = 0;
    		for(int i=0;i<mm.length-m;i++) {
    			mm[i]=tmp.values[i]/cons; //第七页的规范化处理
    			norm+=mm[i]*mm[i];
    		}
    		norm=Math.sqrt(norm);
    		//增加m维度
    		for(int i=mm.length-m,j=1;j<=m;i++,j++) {
    			mm[i]=Math.pow(norm, 1<<j);   //norm^(a^m)
    		}
    		Data data = new Data(tmp.key,mm,tmp.arrivalTime);
    		res.add(data);
    	}
    	return res;
    }
    
    //处理查询
    public double[][] transQ(ArrayList<Double[]> q,int m) {
    	//先标准化
    	int dim = q.get(0).length;
    	double res[][] = new double[q.size()][dim+m];
    	for(int iq=0;iq<q.size();iq++) {
			double norm=0;
			for(int i=0;i<dim;i++) {
				norm+=q.get(iq)[i]*q.get(iq)[i];
			}//求长度
			norm = Math.sqrt(norm);
			for(int i=0;i<dim;i++) {
				res[iq][i]=q.get(iq)[i]/norm;
			}
			for(int i=dim;i<dim+m;i++) {
				res[iq][i]=0.5;
			}
    	}
		return res;
    }
}
