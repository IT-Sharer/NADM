package data;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.Instance;
import core.Instances;
import core.LoadCSVData1;
import core.LoadSqlData;
import core.Save2CSV1;

public class KNN {
	String logId="";
	LoadSqlData loadSqlData=new LoadSqlData();
	LoadCSVData1 loadCSVData1;
	DistanceMethod distanceMethod=new DistanceMethod1();
	Instances instances;
	String viewName="KDDCup99_0_train";
	Instances clusterCenters=new Instances("DataPartition");//�ۼ�����
	ArrayList<Instances> clusterMembers=new ArrayList<>();//��Ӧÿһ���ۼ����ĵ���������
	ArrayList<Double> maxR4Clusters=new ArrayList<>();//��Ӧÿ���ۼ��������İ뾶��
	int Vmin,Vmax,initN;
	String pathString="E:/dataProcess/newAlgorithm/";
	boolean dataOK=false;
	int k=30;
	public KNN(String path,int k_num) {
		// TODO Auto-generated constructor stub
		k=k_num;
		pathString=path;
		File file=new File(path+viewName+"_centers.csv");
		if (file.exists()) {
			loadCSVData1=new LoadCSVData1();
			clusterCenters=loadCSVData1.loadInstances1(path+viewName+"_centers.csv");
			for(int i=0;i<clusterCenters.getCount();i++){
				Instances inst=loadCSVData1.loadInstances1(path+viewName+"_"+i+".csv");
				clusterMembers.add(inst);
				double maxr=inst.getMaxDis(clusterCenters.getInstance(i), distanceMethod);
				maxR4Clusters.add(maxr);
			}
			dataOK=true;
		}
		else {
//			Cluster();
		}
		System.out.println("���ط�������ɣ�");
		//ɾ������K�������ľۼ�
		deleteClusters();
		System.out.println("load data complete!");
		logId=System.currentTimeMillis()+"";
	}
	public void deleteClusters(){
		for(int i=0;i<clusterCenters.getCount();i++){
			if(clusterMembers.get(i).getCount()<k){
				clusterCenters.RemoveInstance(i);
				clusterMembers.remove(i);
				i--;
			}
		}
		System.out.println("delete clusters");
	}
	public void saveData(){
		Save2CSV1 save2csv1=new Save2CSV1();
		long c=System.currentTimeMillis();
		File file=new File(pathString+c+"/");
		if(!file.exists())
			file.mkdir();
		clusterCenters.SaveInstances(pathString+c+"/"+viewName+"_centers.csv", save2csv1);
		for(int i=0;i<clusterMembers.size();i++){
			Instances insm=clusterMembers.get(i);
			insm.SaveInstances(pathString+c+"/"+viewName+"_"+i+".csv", save2csv1);
		}
	}
	public String classify(Instance instance){
		String result="";
		int i=clusterCenters.getNearestInstance(instance, distanceMethod);
		Instances instances=clusterMembers.get(i);
		Instances kInstances=instances.getNearestInstances(instance, distanceMethod, k);
		HashMap<String, Integer> classHashMap=new HashMap<>();
		for(int j=0;i<kInstances.getCount();i++){
			String clString=kInstances.getInstance(j).getLabel();
			if(classHashMap.containsKey(clString))classHashMap.put(clString, classHashMap.get(clString)+1);
			else classHashMap.put(clString, 1);
		}
		int max=0;
		for(java.util.Map.Entry<String, Integer> entry:classHashMap.entrySet()){
			if(entry.getValue()>max)result=entry.getKey();
		}		
		return result;
	}
	public void Test(){
		System.out.println("���ز������ݼ���");
		String testviewString="KDDCup99_0_test";
		Instances testInstances=loadSqlData.loadInstances3("select * from "+testviewString);
		System.out.println("������ϣ����з��ࣺ");
		k=340;
		deleteClusters();
		for(k=300;k<500;k+=5){
			int count=0;
			int wubao=0;
			int loubao=0;
			deleteClusters();
			System.out.println("����ʱ�䣺"+new Date().toLocaleString());	
			System.out.println("K��ֵ��"+k);
			for(int i=0;i<testInstances.getCount();i++){
				String result=classify(testInstances.getInstance(i));
				if(result.equals(testInstances.getInstance(i).getLabel()))count++;
				else if(result.contains("unnormal"))wubao++;
				else if(result.contains("normal"))loubao++;
				
			}
			System.out.println("�������ȷ���ǣ�"+count*100.0/testInstances.getCount()+"%,������ȷ������"+count+";©�� ������"+loubao+"; �󱨸�����"+wubao);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("����ʱ�䣺"+new Date().toLocaleString());
		String pathString="E:/dataProcess/newAlgorithm/1413349489521/";
		int k=1;
		KNN knn=new KNN(pathString, k);
		knn.Test();		
		System.out.println("����ʱ�䣺"+new Date().toLocaleString());
	}

}
