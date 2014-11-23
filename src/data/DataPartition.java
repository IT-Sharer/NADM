package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.FileReadAndWriter;
import core.Instance;
import core.Instances;
import core.LoadCSVData1;
import core.LoadSqlData;
import core.Save2CSV1;

/**
 * @author benfenghua
 *���û�ϵ����ݻ����㷨�������ݽ��л��֡�
 */
public class DataPartition {
	String msgFromMain="";//�����̴߳������Ϣ�����ڿ��������߳����г���������
	String logId="";
	boolean debug=true;
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
	public DataPartition(int N,int min,int max) {
		// TODO Auto-generated constructor stub
		initN=N;
		Vmin=min;
		Vmax=max;
	}
	public DataPartition(int N,int min,int max,String path) {
		// TODO Auto-generated constructor stub
		initN=N;
		Vmin=min;
		Vmax=max;
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
			Cluster();
		}
		System.out.println("load data complete!");
		logId=System.currentTimeMillis()+"";
	}
	
	public void recieveMsg(String msg){
		msgFromMain=msg;
	}
	
	
	/**
	 * 1.�������������ľۼ���
	 * 2.����ۼ����ļ���
	 * 3.��¼�����ۼ�����ʱ�䣬�����Ľ��㷨ʹ��ʱ����̡�
	 * �����ԣ��ڴ���ȫ���õ������
	 * ע������������
	 * @param initN ��ʼ�ۼ�����
	 * @param Vmin �ۼ���С����
	 * @param Vmax �ۼ��������
	 */
 	public void partition(){		
		PrintDebug(""+System.currentTimeMillis());
		//��ʼ���ۼ�����,ѡ�����е����ɸ���
		PrintDebug("��ʼ���ۼ����ģ�");
		String sqlString="select * from (select top "+initN/2+" * from "+viewName+" union select top "+initN/2+" * from "+viewName+" order by no desc) as b";
		PrintDebug(sqlString);
		clusterCenters=loadSqlData.loadInstances3(sqlString);		
		//��ʼ��clusterMembers��maxR4Clusters
		PrintDebug("��ʼ���ۼ���");
		instances=loadSqlData.loadInstances3("select * from "+viewName);
		for(int i=0;i<clusterCenters.getCount();i++){
			Instances instances2=new Instances(""+i);
			clusterMembers.add(instances2);
			maxR4Clusters.add(0.0);
		}
		PrintDebug("����������ɣ�");
		PrintDebug(""+instances.getCount());
		for(int i=0;i<instances.getCount();i++){
			Instance instance=instances.getInstance(i);
			int loc=0;
			double dis=Double.MAX_VALUE;
			for(int j=0;j<clusterCenters.getCount();j++){//�ҳ�����ľۼ�����
				double tmp=instance.Distance(distanceMethod, clusterCenters.getInstance(j));
				if(tmp<=dis){
					loc=j;
					dis=tmp;
				}
			}
			clusterMembers.get(loc).AddInstance(instance);
			if(maxR4Clusters.get(loc)<dis){
				maxR4Clusters.remove(loc);
				maxR4Clusters.add(loc, dis);
			}			
		}
		PrintDebug("�ۼ���ʼ����ɣ�");
		for(int i=maxR4Clusters.size()-1;i>=0;i--)
		PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
		//���ݾۼ����Ľ��оۼ�
		boolean stop=false;//ֹͣ����
		while(!stop){
			PrintDebug("���¼���ۼ�����");
			for(int i=0;i<clusterMembers.size();i++){
				Instances instances2=clusterMembers.get(i);
				Instance instance=instances2.CalCenter();
				clusterCenters.RemoveInstance(i);
				clusterCenters.AddInstance(i, instance);
				maxR4Clusters.remove(i);
				maxR4Clusters.add(i,instances2.getMaxDis(instance, distanceMethod));
			}
			//���ݾۼ����ģ����������µľۼ���������
			
			
			//����ÿ���ۼ�����,������Ҫ�����Ľ��д�����������stop=false,���������û����Ҫ���д���ʱ,stop=TRUE
			stop=true;
			for (int i = 0; i < clusterMembers.size();i++) {
				Instances instances2=clusterMembers.get(i);
				//????�������ǲ��У����ײ�����ѭ�����������ķ���Ҫ�ı䣬����������������߳��ļ��룬�����µľۼ������߲����µľۼ�����������
				if(instances2.getCount()<Vmin){//����������
					PrintDebug("�����������㣡");
					stop=false;
//					clusterMembers.remove(i);
//					clusterCenters.RemoveInstance(i);
//					//ѡ����������ļ���֮
//					for(int j=0;j<instances2.getCount();j++){
//						Instance instance=instances2.getInstance(j);
//						int newloc=clusterCenters.getNearestInstance(instance, distanceMethod);
//						clusterMembers.get(newloc).AddInstance(instance);
//						//Ҫ��Ҫ�ٴμ������ľۼ��ľۼ����ģ�����
//					}
//					i--;
				}
				else if(instances2.getCount()>Vmax){//��������������ѡ��vmax/count * maxR4Clusters 
					PrintDebug("����������");
					stop=false;
//					double fil=maxR4Clusters.get(i)*Vmax/instances2.getCount();
//					while(instances2.getCount()<Vmax){
//						for(int j=0;j<instances2.getCount();){
//							Instance instance=instances2.getInstance(j);
//							double tmp=instance.Distance(distanceMethod, clusterCenters.getInstance(i));
//							if(tmp>fil){
//								instances2.RemoveInstance(j);
//								//ѡ�������һ���ۼ�������֮���������ԭ�ۼ���������һ���ۼ����ġ�����ۼ��������Ҫ�ı䣬ֱ������¯��
//								int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
//								if(loc==i){
//									clusterCenters.AddInstance(instance);
//									Instances instances3=new Instances("");
//									instances3.AddInstance(instance);
//									clusterMembers.add(instances3);
//									maxR4Clusters.add(0.0);
//								}
//								else {
//									clusterMembers.get(loc).AddInstance(instance);
//									//Ҫ��Ҫ�ٴμ������ľۼ��ľۼ����ģ�����
//								}
//							}
//							else {
//								j++;
//							}
//						}
//						fil*=0.9;
//					}
				}
				else {//�����ϸ񣬵���Ҫ�����ۼ����Ծۼ��е�Ԫ������ѡ������ľۼ���
					PrintDebug("�����ۼ����ģ�");
					for(int j=0;j<instances2.getCount();){
						Instance instance=instances2.getInstance(j);
						//ѡ�������һ���ۼ�������֮���������ԭ�ۼ���������һ���ۼ����ġ�
						int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
						if(loc!=i){
							stop=false;
							clusterMembers.get(loc).AddInstance(instance);
							instances2.RemoveInstance(j);
						}	
						else j++;
					}
				}
			}
			
		}
		PrintDebug(""+System.currentTimeMillis());
		//����ۼ����ģ���Ӧ�����������Ϣ
		Save2CSV1 save2csv1=new Save2CSV1();
		//����ۼ�����
		clusterCenters.SaveInstances(pathString+viewName+"_centers.csv", save2csv1);
		for(int i=0;i<clusterMembers.size();i++){
			Instances insm=clusterMembers.get(i);
			insm.SaveInstances(pathString+viewName+"_"+i+".csv", save2csv1);
		}		
	}
	
	public void Cluster(){		
		PrintDebug(""+System.currentTimeMillis());
		//��ʼ���ۼ�����,ѡ�����е����ɸ���
		PrintDebug("��ʼ���ۼ����ģ�");
		String sqlString="select * from (select top "+initN/2+" * from "+viewName+" union select top "+initN/2+" * from "+viewName+" order by no desc) as b";
		PrintDebug(sqlString);
		clusterCenters=loadSqlData.loadInstances3(sqlString);		
		//��ʼ��clusterMembers��maxR4Clusters
		PrintDebug("��ʼ���ۼ���");
		instances=loadSqlData.loadInstances3("select * from "+viewName);
		for(int i=0;i<clusterCenters.getCount();i++){
			Instances instances2=new Instances(""+i);
			clusterMembers.add(instances2);
			maxR4Clusters.add(0.0);
		}
		PrintDebug("����������ɣ�");
		PrintDebug(""+instances.getCount());
		for(int i=0;i<instances.getCount();i++){
			Instance instance=instances.getInstance(i);
			int loc=0;
			double dis=Double.MAX_VALUE;
			for(int j=0;j<clusterCenters.getCount();j++){//�ҳ�����ľۼ�����
				double tmp=instance.Distance(distanceMethod, clusterCenters.getInstance(j));
				if(tmp<=dis){
					loc=j;
					dis=tmp;
				}
			}
			clusterMembers.get(loc).AddInstance(instance);
			if(maxR4Clusters.get(loc)<dis){
				maxR4Clusters.remove(loc);
				maxR4Clusters.add(loc, dis);
			}			
		}
		PrintDebug("�ۼ���ʼ����ɣ�");
		for(int i=maxR4Clusters.size()-1;i>=0;i--)
		PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
		//���ݾۼ����Ľ��оۼ�
		boolean stop=false;//ֹͣ����
		int num=0;
		while(!stop){
			PrintDebug("���¼���ۼ�����");
			for(int i=0;i<clusterMembers.size();i++){
				Instances instances2=clusterMembers.get(i);
				Instance instance=instances2.CalCenter();
				clusterCenters.RemoveInstance(i);
				clusterCenters.AddInstance(i, instance);
				maxR4Clusters.remove(i);
				maxR4Clusters.add(i,instances2.getMaxDis(instance, distanceMethod));
			}
			//����ÿ���ۼ�����,������Ҫ�����Ľ��д�����������stop=false,���������û����Ҫ���д���ʱ,stop=TRUE
			
			stop=true;
			for (int i = 0; i < clusterMembers.size();i++) {
				Instances instances2=clusterMembers.get(i);
				for(int j=0;j<instances2.getCount();){
					Instance instance=instances2.getInstance(j);
					//ѡ�������һ���ۼ�������֮���������ԭ�ۼ���������һ���ۼ����ġ�
					int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
					if(loc!=i){
						stop=false;
						clusterMembers.get(loc).AddInstance(instance);
						instances2.RemoveInstance(j);
					}	
					else j++;
				}				
			}
			if(num%100==0)for(int i=maxR4Clusters.size()-1;i>=0;i--)
				PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
			num++;
		}
		PrintDebug(""+System.currentTimeMillis());
		//����ۼ����ģ���Ӧ�����������Ϣ
		Save2CSV1 save2csv1=new Save2CSV1();
		//����ۼ�����
		clusterCenters.SaveInstances(pathString+viewName+"_centers.csv", save2csv1);
		for(int i=0;i<clusterMembers.size();i++){
			Instances insm=clusterMembers.get(i);
			insm.SaveInstances(pathString+viewName+"_"+i+".csv", save2csv1);
		}		
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
		msgFromMain="saved";
	}
	public void print(){
		int count=clusterCenters.getCount();
		System.out.println("�ۼ�������"+count);
		int all=0;
		for(int i=0;i<count;i++){
			all+=clusterMembers.get(i).getCount();
			System.out.println(i+":"+clusterMembers.get(i).getCount());
		}
		System.out.println("����������"+all);
		msgFromMain="printed";
	}
	/**
	 * ��ε�����ʹ�ۼ��ﵽƽ��
	 */
 	public void makeBalance(){
		boolean stop=false;//ֹͣ����
		int num=0;
		while(!msgFromMain.equals("stop")&&!stop){
			while(!stop&&!stop()){
				PrintDebug("���¼���ۼ�����");
				maxR4Clusters.clear();
				for(int i=0;i<clusterMembers.size()&&!stop();i++){
					Instances instances2=clusterMembers.get(i);
					Instance instance=instances2.CalCenter();
					clusterCenters.RemoveInstance(i);
					clusterCenters.AddInstance(i, instance);
					maxR4Clusters.add(instances2.getMaxDis(instance, distanceMethod));
				}
				stop=true;
				for (int i = 0; i < clusterMembers.size()&&!stop();i++) {
					Instances instances2=clusterMembers.get(i);
					//��������Vmin/15�ľۼ� ��ɾ��֮��Ȼ����뵽����ľۼ��С���������
//					if(instances2.getCount()<=Vmin/15){
//						clusterCenters.RemoveInstance(i);
//						clusterMembers.remove(i);
//						maxR4Clusters.remove(i);
//					}
					for(int j=0;j<instances2.getCount();){
						Instance instance=instances2.getInstance(j);
						int loc=clusterCenters.getNearestInstance(instance, distanceMethod);
						if(loc!=i){
							stop=false;
							clusterMembers.get(loc).AddInstance(instance);
							instances2.RemoveInstance(j);
						}
						else j++;
					}				
				}
				if(num%100==0)for(int i=clusterMembers.size()-1;i>=0;i--)
					PrintDebug(clusterMembers.get(i).getCount()+","+maxR4Clusters.get(i));
				num++;
			}
			PrintDebug("�ۼ�������");
			if (msgFromMain.equals("save")||msgFromMain.equals("stop")) {
				PrintDebug("���������С�����");
				saveData();
				stop=false;
				msgFromMain="";
			}
			if (msgFromMain.equals("print")) {
				print();
				stop=false;
				msgFromMain="";
			}
			
		}		
	}
	
	/**
	 * �Ծۼ����л���,�����µľۼ����ĺ;ۼ�
	 * @param center�ۼ����� 
	 * @param samples ����
	 * @param newCenters �µ����ɸ��ۼ�����
	 * @param newSamples �µľۼ�����
	 */
	public boolean PartitionCluster(Instance center,Instances samples,Instances newCenters,ArrayList<Instances> newSamples){
		boolean re=true;
		int farIndex=samples.getFarestInstance(center, distanceMethod);
		Instance inst=samples.getInstance(farIndex);//��Զ������
		Instance midlleInstance=center.getMiddleInstance(inst);//������Զ��������������֮�������
		midlleInstance=samples.getInstance(samples.getNearestInstance(midlleInstance,distanceMethod));
		Instance otherSide=samples.getInstance(samples.getNearestInstance(center,distanceMethod));//.getOthersideInstance(midlleInstance);//middle ���� �������������ԳƵ�����
		int count=samples.getCount();
		//��������
		Instances instances1=new Instances("1");
		Instances instances2=new Instances("2");		
		for(int i=0;i<count;i++){
			Instance tmpInstance=samples.getInstance(i);
			double dis1=tmpInstance.Distance(distanceMethod, midlleInstance);
			double dis2=tmpInstance.Distance(distanceMethod, otherSide);
			if(dis1<dis2)instances1.AddInstance(tmpInstance);
			else {
				instances2.AddInstance(tmpInstance);
			}
		}
		boolean stop=false;
		while(!stop&&!msgFromMain.equals("stop")&&!msgFromMain.equals("save")&&!msgFromMain.equals("print")){
			stop=true;
			//���¼�������ֱ���ȶ�
			midlleInstance=instances1.CalCenter();
			otherSide=instances2.CalCenter();
			int num2=instances2.getCount();
			int num1=instances1.getCount();
			for(int i=0;i<num1;){
				Instance tmpInstance=instances1.getInstance(i);
				double dis1=tmpInstance.Distance(distanceMethod, midlleInstance);
				double dis2=tmpInstance.Distance(distanceMethod, otherSide);
				if(dis1>dis2){
					stop=false;
					instances1.RemoveInstance(i);
					instances2.AddInstance(tmpInstance);
					num1--;
				}
				else i++;
				if (stop()) {
					return false;
				}
			}
			for(int i=0;i<num2;){
				Instance tmpInstance=instances2.getInstance(i);
				double dis1=tmpInstance.Distance(distanceMethod, midlleInstance);
				double dis2=tmpInstance.Distance(distanceMethod, otherSide);
				if(dis1<dis2){
					stop=false;
					instances2.RemoveInstance(i);
					instances1.AddInstance(tmpInstance);
					num2--;
				}
				else i++;
				if (stop()) {
					return false;
				}
			}
		}
		//�ﵽƽ��
		if(instances1.getCount()<=Vmax&&instances1.getCount()>0){
			newCenters.AddInstance(midlleInstance);
			newSamples.add(instances1);
		}else {
			Instances newCenters2=new Instances("");
			ArrayList<Instances> newArrayList=new ArrayList<>();
			if(PartitionCluster(midlleInstance, instances1, newCenters2, newArrayList)){
				for (int i = 0; i < newCenters2.getCount(); i++) {
					newCenters.AddInstance(newCenters2.getInstance(i));
					newSamples.add(newArrayList.get(i));
				}
			}
			else return false;
		}
		if(instances2.getCount()<=Vmax&&instances2.getCount()>0){
			newCenters.AddInstance(otherSide);
			newSamples.add(instances2);
		}else {
			Instances newCenters2=new Instances("");
			ArrayList<Instances> newArrayList=new ArrayList<>();
			if(PartitionCluster(otherSide, instances2, newCenters2, newArrayList)){
				for (int i = 0; i < newCenters2.getCount(); i++) {
					newCenters.AddInstance(newCenters2.getInstance(i));
					newSamples.add(newArrayList.get(i));
				}
			}
			else return false;
		}
		return re;
	}
	private boolean stop(){
		return msgFromMain.equals("stop")||msgFromMain.equals("save")||msgFromMain.equals("print");
	}
	/**
	 * ���ƾۼ�������������ٴﵽƽ��
	 */
	public void limitVol(){
		PrintDebug("���ƾۼ�������");
		if(!dataOK){
			System.out.println("the data is not OK!");
			Cluster();
		}
		boolean stop=false;
		while (!msgFromMain.equals("stop")&&!stop) {
			while(!stop&&!stop()){
				makeBalance();//���ۼ��Ƿ�ﵽƽ��״̬
				stop=true;
				for(int i=0;i<clusterCenters.getCount()&&!stop();){
					Instances tmpInstances=clusterMembers.get(i);
					if(tmpInstances.getCount()>Vmax){
						System.out.println(i+","+tmpInstances.getCount());
						stop=false;
						Instance centerInstance=clusterCenters.getInstance(i);
						Instances newCenters=new Instances("");
						ArrayList<Instances> newSamples=new  ArrayList<>();
						if(PartitionCluster(centerInstance, tmpInstances, newCenters, newSamples)){
							for (int j = 0; j < newCenters.getCount(); j++) {
								clusterCenters.AddInstance(newCenters.getInstance(j));
								clusterMembers.add(newSamples.get(j));
							}
							clusterCenters.RemoveInstance(i);
							clusterMembers.remove(i);
						}
					}
					else i++;					
				}
			}
			if(stop&&!stop()){
				saveData();
				msgFromMain="stop";
				stop=false;
			}
			if (msgFromMain.equals("save")||msgFromMain.equals("stop")) {
				saveData();
				stop=false;
			}
			if (msgFromMain.equals("print")) {
				print();
				stop=false;
			}
		}
		
	}
	public void limitVol(int i){
		
	}
 	public void PrintDebug(String msg){
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(pathString+viewName+logId+"_log.txt", false,true);
		if(debug){
//			System.out.println(msg);
			fileReadAndWriter.WriteLine(msg);
			fileReadAndWriter.Flush();
		}
		fileReadAndWriter.EndWrite();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="E:/dataProcess/newAlgorithm/";
		//��newThread�߳����м�������
		System.out.println(new Date().toLocaleString());
		DataPartition dataPartition=new DataPartition(90, 5000, 10000,path);
		NewThread newThread=new NewThread(dataPartition);
		Thread thread=new Thread(newThread);
		thread.start();
		UIOperation uiOperation=new UIOperation(dataPartition);
		uiOperation.Interaction();
//		dataPartition.debug=false;
//		dataPartition.Cluster();
		
//		dataPartition.limitVol();
		System.out.println(new Date().toLocaleString());
	}

}
