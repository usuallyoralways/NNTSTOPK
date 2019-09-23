package topkmips.para;


public class Constant {

	public static String basePath="F:/lijun/code/wwwsj/NN-TS/data/";

	public static long curTime = 0;
	public static int flag = 1;//0是异常检测；1是MIPS
	//DW
	public static double relativeError = 0.1;//DW相对误差
	public static int expire = 60000; //窗口大小
	public static int K = 13;//数组每行几位，即哈希值的位数
	public static int L = 6;//数组有多少行，即哈希值的个数
	
	//阈�??
	public static double alpha = 0.2;//阈�?�水平，小于窗口内数量的这个比例即为异常
	public static double mean =0;//平均�?
	public static int n = 0;
    public static int DW_nums = 0;

	
	//文件路径
	public static String dataPath = "F:/lijun/code/wwwsj/NN-TS/data/dataset/tao.txt";
	public static String outputPath = "F:/lijun/code/wwwsj/NN-TS/data/result/tao_outliers_190731.txt";
	
	//topK
	public static double cons =0;
	public static int topK=10;
	public static double[] topkBase = {0,81,253,192,0,0,0,0,0,0,0,30,0,254,253,0,0,203,253,0,0,253,244,0,0,252,122,0,0,0,0,0,0,0,113,253,253,252,253,252,253,254,253,254,253,171,151,151,50,50};
	public static String topKPath = "D:/tao_topk_190922.txt";
	public static String topKcoll = "E:/1111111111111111dataset/TopK/Mnist/Mnist.ds";
	public static String queryFile = "E:/1111111111111111dataset/TopK/Mnist/q.ds";
	
//	public static Data base ;//基准向量
	public static int timeRange=60000;//哪个时间段的topk
}
