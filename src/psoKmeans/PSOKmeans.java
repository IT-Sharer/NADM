package psoKmeans;

import java.util.ArrayList;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.Instances;
import core.LoadCSVData1;
import core.LoadSqlData;

public class PSOKmeans {
	//�ο�DataPartition��˫�߳�ʵ��
	int k;
	String logId="";
	boolean debug=true;
	LoadSqlData loadSqlData=new LoadSqlData();
	LoadCSVData1 loadCSVData1;
	DistanceMethod distanceMethod=new DistanceMethod1();
	Instances instances;//��ʼ���ݼ�
	String viewName="KDDCup99_0_train";
	Instances clusterCenters=new Instances("DataPartition");//�ۼ�����
	ArrayList<Instances> clusterMembers=new ArrayList<>();//��Ӧÿһ���ۼ����ĵ���������
	ArrayList<Double> maxR4Clusters=new ArrayList<>();//��Ӧÿ���ۼ��������İ뾶��
	int Vmin,Vmax,initN;
	
	String pathString="E:/dataProcess/newAlgorithm/";
	boolean dataOK=false;
	protected Clusters clusters;//�۴���
	
	/**
	 * PSO-Kmeans�����ݻ����㷨
	 */
	public void PSOKPartition(){
		//
	}
	
	
	
}
