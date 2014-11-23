package wekaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import knn.DistanceMethod1;
import core.FileReadAndWriter;
import core.Instance;
import core.Instances;
import core.LoadSqlData;
import core.SQLHelper;
import core.Save2CSV1;
import data.KDDCUP99Process;

public class ClassifierTester {
	String select="[no],[duration] ,[protocol_type],[service],[flag],[src_bytes] ,[dst_bytes],[land],[wrong_fragment],[urgent],"
			+ "[hot],[num_failed_logins],[logged_in],[num_compromised],[root_shell],[su_attempted],[num_root],[num_file_creations],"
			+ "[num_shells],[num_access_files],[num_outbound_cmds],[is_hot_login],[is_guest_login],[countlink] ,[srv_count]"
			+ ",[serror_rate],[srv_serror_rate] ,[rerror_rate] ,[srv_rerror_rate] ,[same_srv_rate],[diff_srv_rate]"
			+ ",[srv_diff_host_rate] ,[dst_host_count],[dst_host_srv_count],[dst_host_same_srv_rate]"
			+ " ,[dst_host_diff_srv_rate],[dst_host_same_src_port_rate] ,[dst_host_srv_diff_host_rate]"
			+ ",[dst_host_serror_rate] ,[dst_host_srv_serror_rate] ,[dst_host_rerror_rate],[dst_host_srv_rerror_rate] ,[classlabel]";
	String select1="top 100 [no],[duration] ,[protocol_type],[service],[flag],[src_bytes] ,[dst_bytes],[land],[wrong_fragment],[urgent],"
			+ "[hot],[num_failed_logins],[logged_in],[num_compromised],[root_shell],[su_attempted],[num_root],[num_file_creations],"
			+ "[num_shells],[num_access_files],[num_outbound_cmds],[is_hot_login],[is_guest_login],[countlink] ,[srv_count]"
			+ ",[serror_rate],[srv_serror_rate] ,[rerror_rate] ,[srv_rerror_rate] ,[same_srv_rate],[diff_srv_rate]"
			+ ",[srv_diff_host_rate] ,[dst_host_count],[dst_host_srv_count],[dst_host_same_srv_rate]"
			+ " ,[dst_host_diff_srv_rate],[dst_host_same_src_port_rate] ,[dst_host_srv_diff_host_rate]"
			+ ",[dst_host_serror_rate] ,[dst_host_srv_serror_rate] ,[dst_host_rerror_rate],[dst_host_srv_rerror_rate] ,[classlabel]";
	
	String tableString="[NTDM].[dbo].[Kddcup99_no2]";
	String conditon="[no]%489=0";//�������ݼ�
	SQLHelper sqlHelper=new SQLHelper();
	LoadSqlData loadSqlData=new LoadSqlData();
	DistanceMethod1 distanceMethod1=new DistanceMethod1();
	/**
	 * �������ľ���
	 */
	public void CalMaxDistance(String no,double disFilter){
//		double [] dis=new double[KDDCUP99Process.allnum];
		String condition2="[no]='"+no+"'";
		Instances instances1=loadSqlData.loadInstances2(tableString, select, condition2);
		if(instances1==null){
			System.out.println("can not find the instances! program exited!");
			return;
		}
		Instance instance=instances1.getInstance(0);
		int numOnetime=10000;//һ�λ�ȡ������
		int cursor=1;//��־
		String filepathString="E:/dataProcess/dis_"+no+"_"+disFilter+".csv";
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
		while(cursor<KDDCUP99Process.allnum){
			condition2="[no]>='"+cursor+"' and [no]<'"+(cursor+numOnetime)+"' ";
			Instances instances=loadSqlData.loadInstances2(tableString, select, condition2);
			if(instances==null){
				System.out.println("can not find the instances, loop continues!");
				continue;
			}
			int count=instances.getCount();
			for(int i=0;i<count;i++) {
				Instance instance2=instances.getInstance(i);
//				dis[Integer.parseInt(instance2.getInstanceTagString())]=
//						instance.Distance(distanceMethod1, instance2);
				double dis=instance.Distance(distanceMethod1, instance2);
				if(dis<disFilter)
				fileReadAndWriter.WriteLine(instance2.getInstanceTagString()+","+dis+","+instance2.getLabel());
			}
			fileReadAndWriter.Flush();
			cursor+=numOnetime;
		}	
		
	}
	
	/**
	 * @param disFilter ������ֵ
	 * @param maxNum ���ۼ���
	 * @param minNum ��С�ۼ�����
	 */
	public void GenerateCluster(double disFilter,int maxNum,int minNum){
		StringBuffer failSet=new StringBuffer();//ʧ�ܼ�
		int failCount=0;//ʧ�ܼ�������
		ArrayList<StringBuffer> clusters=new ArrayList<>();//�ۼ�����
		Instances clusterCenters=new Instances("ClusterCenters");
		int clusterCount=0;
		//�ۼ���������		
		while(true){
			StringBuffer condition2=new StringBuffer();
			if(failSet.length()>0){
				condition2.append("[no] not in ( ");
				condition2.append(failSet);	
				if(clusters.size()>0){
					int sizeOfClusters=clusters.size();
					for(int i=0;i<clusters.size();i++){
						condition2.append(",");
						condition2.append(clusters.get(i));
					}
				}
				condition2.append(" )");
			}
			else {
				if(clusters.size()>0){
					condition2.append("[no] not in ( ");
					int sizeOfClusters=clusters.size();
					for(int i=0;i<clusters.size()-1;i++){
						condition2.append(clusters.get(i));
						condition2.append(",");
					}
					condition2.append(clusters.get(sizeOfClusters-1));
					condition2.append(" )");
				}
			}
			Instances instances1=loadSqlData.loadInstances2(tableString, select1, condition2.toString());
			if(instances1==null){
				System.out.println("can not find the instances! program exited!");
				return;
			}
			Instance instance=instances1.getInstance(0);
			int numOnetime=10000;//һ�λ�ȡ������
			int cursor=1;//��־
			
			//ͳ��ǰmaxnum -minNum�������Ƿ��������С��disfilter/2
//			String [] noStrings=new String[maxNum];//�洢����
//			double [] dises=new double[maxNum];//���뼯��
//			int point=0;
			HashMap<String, Double> nodisHashMap=new HashMap<>();
			System.out.println("��ʼ��ȡ�ۼ���������");
			while(cursor<KDDCUP99Process.allnum){
				String condictionString="[no]>='"+cursor+"' and [no]<'"+(cursor+numOnetime)+"' "+(condition2.length()>0?"and ":" ")+condition2.toString();
				Instances instances=loadSqlData.loadInstances2(tableString, select, condictionString);
				if(instances==null){
					System.out.println("can not find the instances, loop continues!");
					continue;
				}
				int count=instances.getCount();
				for(int i=0;i<count;i++) {
					Instance instance2=instances.getInstance(i);
//					dis[Integer.parseInt(instance2.getInstanceTagString())]=
//							instance.Distance(distanceMethod1, instance2);
					double dis=instance.Distance(distanceMethod1, instance2);
					if(dis<disFilter){
//						fileReadAndWriter.WriteLine(instance2.getInstanceTagString()+","+dis+","+instance2.getLabel());
						//�����ݲ��뵽�洢�����У�����	
						//http://www.oschina.net/code/snippet_12_546?from=rss �ο��˷�����������
						nodisHashMap.put(instance2.getInstanceTagString(), dis);
					}
				}
				cursor+=numOnetime;
			}
			System.out.println("�ۼ���ȡ��ϣ���ʼ��֤���ϡ�����");
			//�����������Լ��Ͻ�����֤
			if(nodisHashMap.size()<minNum){//�ۼ������ﲻ����СҪ��
				if(failCount>0)failSet.append(","+instance.getInstanceTagString());
				else {
					failSet.append(instance.getInstanceTagString());
				}
				failCount++;
				System.out.println("�ۼ�����꣬����ʧ�ܼ��ϣ�");
			}
			else if(nodisHashMap.size()<maxNum){//ֱ�����ɾۼ�
				StringBuffer nosBuffer=new StringBuffer();
				Set<String> notmp=nodisHashMap.keySet();
				for(String string:notmp){
					nosBuffer.append(","+string);
				}				
				clusters.add(new StringBuffer(nosBuffer.substring(1)));
				clusterCount+=nodisHashMap.size();
				//д���ļ���
				String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
				fileReadAndWriter.EndWrite();
				System.out.println("�ۼ�������ɾۼ���");
			}
			else{//��ȡǰMaxnum��Ԫ��
				ByValueComparator bvc = new ByValueComparator(nodisHashMap);
				TreeMap< String, Double> sorTreeMap=new TreeMap<>(bvc);
				sorTreeMap.putAll(nodisHashMap);
				StringBuffer nosBuffer=new StringBuffer();
				int i=0;
				for(String string:sorTreeMap.keySet()){
					if(i<maxNum){
						nosBuffer.append(","+string);
						i++;
					}
					else {
						break;
					}
				}
				clusters.add(new StringBuffer(nosBuffer.substring(1)));	
				clusterCount+=maxNum;
				//д���ļ�
				String filepathString="E:/dataProcess/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
				fileReadAndWriter.EndWrite();
				System.out.println("�ۼ�������ɾۼ���");
			}
			if(KDDCUP99Process.allnum<=failCount+clusterCount){
				System.out.println("����ʧ�ܼ��ϡ�����");
				//�Ѿ�������ɣ�����ʧ�ܵ�����
				condition2=new StringBuffer(" [no] in (");
				condition2.append(failSet);
				condition2.append(" )");
				String filepathString="E:/dataProcess/dis_FailSet_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(failSet.toString());
				fileReadAndWriter.EndWrite();
				for(int i=0;i<clusters.size();i++){//����ÿ���ۼ��ľۼ�����
					clusterCenters.AddInstance(getCenter(clusters.get(i)));
				}
				System.gc();
				Instances instances=loadSqlData.loadInstances2(tableString, select, condition2.toString());
				for(int i=0;i<failCount;i++){
					//Ѱ������ľۼ����ģ�����֮
					Instance instance2=instances.getInstance(i);
					int loc=0;
					double min=Double.MAX_VALUE;
					for(int j=0;j<clusters.size();j++){
						double tmp=instance2.Distance(distanceMethod1, clusterCenters.getInstance(j));
						if(tmp<min){
							loc=j;
							min=tmp;
						}						
					}
					clusters.get(loc).append(","+instance2.getInstanceTagString());
				}	
				//���¼���ۼ�����
				System.out.println("���¼���ۼ����ģ��ۼ���ɣ��������ۼ�����"+clusters.size());
				clusterCenters=new Instances("NewCenter");				
				for(int i=0;i<clusters.size();i++){//����ÿ���ۼ��ľۼ�����
					clusterCenters.AddInstance(getCenter(clusters.get(i)));
					filepathString="E:/dataProcess/dis_NewCluster_"+disFilter+"_"+(i+1)+".txt";
					fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
					fileReadAndWriter.WriteLine(clusters.get(i).toString());
					fileReadAndWriter.EndWrite();
				}
				filepathString="E:/dataProcess/dis_NewCluster_"+disFilter+"_Centers.txt";
				Save2CSV1 save2csv1=new Save2CSV1();
				save2csv1.SaveInstances(clusterCenters, filepathString);				
				break;
			}
		}
	}
	
	/**
	 * �·���������ʱ�����ʽ��
	 * @param disFilter
	 * @param maxNum
	 * @param minNum
	 */
	public void GenerateCluster2(double disFilter,int maxNum,int minNum){
		String insTableString="Kddcup99_no2_temp";
		String failTableString="Kddcup99_Cluster_Fail";		
		
		StringBuffer failSet=new StringBuffer();//ʧ�ܼ�
		int failCount=0;//ʧ�ܼ�������
		ArrayList<StringBuffer> clusters=new ArrayList<>();//�ۼ�����
		ArrayList<Double>maxDisArrayList=new ArrayList<>();//�ۼ���������ֵ
		Instances clusterCenters=new Instances("ClusterCenters");
		int clusterCount=0;
		int topnum=1;
		//�ۼ���������		
		while(true){
			StringBuffer condition2=new StringBuffer();
			select1=" top "+topnum+select;
			Instances instances1=loadSqlData.loadInstances2(insTableString, select1, condition2.toString());
			if(instances1==null){
				System.out.println("can not find the instances! program exited!");
				return;
			}
			
			Instance instance=instances1.getInstance(instances1.getCount()-1);
			int numOnetime=15000;//һ�λ�ȡ������
			int cursor=1;//��־
			HashMap<String, Double> nodisHashMap=new HashMap<>();
			System.out.println("��ʼ��ȡ�ۼ���������"+instance.getInstanceTagString());	
			//��ԭ�����
			while(cursor<KDDCUP99Process.allnum){
				String condictionString="[no]>='"+cursor+"' and [no]<'"+(cursor+numOnetime)+"' ";//+(condition2.length()>0?"and ":" ")+condition2.toString();
				Instances instances=loadSqlData.loadInstances2(insTableString, select, condictionString);
				if(instances==null){
					System.out.println("can not find the instances, loop continues!");
					continue;
				}
				int count=instances.getCount();
				for(int i=0;i<count;i++) {
					Instance instance2=instances.getInstance(i);
					double dis=instance.Distance(distanceMethod1, instance2);
					if(dis<disFilter){
						//�����ݲ��뵽�洢������
						nodisHashMap.put(instance2.getInstanceTagString(), dis);
					}
				}
				cursor+=numOnetime;
			}
//			��ʧ�ܼ��ϱ���
//			if(failCount>0){
//				Instances instances=loadSqlData.loadInstances2(failTableString, select, "");
//				if(instances==null){
//					System.out.println("can not find the instances");
//				}
//				else {
//					int count=instances.getCount();
//					for(int i=0;i<count;i++) {
//						Instance instance2=instances.getInstance(i);
//						double dis=instance.Distance(distanceMethod1, instance2);
//						if(dis<disFilter){
//							//�����ݲ��뵽�洢������
//							nodisHashMap.put(instance2.getInstanceTagString(), dis);
//						}
//					}
//				}				
//			}
			System.out.println("�ۼ���ȡ��ϣ���ʼ��֤���ϡ�����"+nodisHashMap.size());
			//�����������Լ��Ͻ�����֤
			if(nodisHashMap.size()<minNum){
				//�ۼ������ﲻ����СҪ��
				//��ʧ���������뵽ʧ�ܼ�����,��ԭ��ɾ��
				String sqlString="insert into "+failTableString+" select * from "+insTableString+" where [no]='"+instance.getInstanceTagString()+"'";
				sqlHelper.executeSQL(sqlString);
				sqlString="delete from "+insTableString+" where [no]='"+instance.getInstanceTagString()+"'";
				sqlHelper.executeSQL(sqlString);
				failCount++;
				int count=nodisHashMap.size();
				System.out.println("�ۼ�����꣬�ۼ�ֻ�У�"+count+"����������С��"+0.6*disFilter+"����������ʧ�ܼ��ϣ�");
				//topnum+=5;
				int i=0;
				StringBuffer sqlbBuffer=new StringBuffer();
				for(java.util.Map.Entry<String, Double> entry:nodisHashMap.entrySet()){
					if (entry.getValue()<=0.8*disFilter) {
						sqlbBuffer.append(" insert into "+failTableString+" select * from "+insTableString+" where [no]='"+entry.getKey()+"' ");
						sqlbBuffer.append(" delete from "+insTableString+" where [no]='"+entry.getKey()+"' ");
						i++;
						if(i%300==0){
							i=0;
							sqlHelper.executeSQL(sqlbBuffer.toString());
							sqlbBuffer=new StringBuffer();
						}
					}
				}
				if(sqlbBuffer.length()>0){
					sqlHelper.executeSQL(sqlbBuffer.toString());
				}
			}
			else if(nodisHashMap.size()<maxNum){//ֱ�����ɾۼ�
				StringBuffer nosBuffer=new StringBuffer();
				Set<String> notmp=nodisHashMap.keySet();
				for(String string:notmp){
					nosBuffer.append(","+string);
				}
				StringBuffer noTmp=new StringBuffer(nosBuffer.substring(1));
				clusters.add(noTmp);
				clusterCount+=nodisHashMap.size();
				deleteInstances(noTmp);
				//д���ļ���
				System.out.println("�ۼ����,���ɾۼ���д���ļ�����");
				String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
				fileReadAndWriter.EndWrite();
			}
			else{//��ȡǰMaxnum��Ԫ��
				ByValueComparator bvc = new ByValueComparator(nodisHashMap);
				TreeMap< String, Double> sorTreeMap=new TreeMap<>(bvc);
				sorTreeMap.putAll(nodisHashMap);
				StringBuffer nosBuffer=new StringBuffer();
				int i=0;
				double maxd=0.0;
				for(String string:sorTreeMap.keySet()){
					if(i<maxNum){
						nosBuffer.append(","+string);
						i++;
					}
					else {
						try {
							maxd=sorTreeMap.get(string);
						} catch (Exception e) {
							// TODO: handle exception
							maxd=disFilter;
						}
						break;
					}
				}
				StringBuffer noTmp=new StringBuffer(nosBuffer.substring(1));
				clusters.add(noTmp);	
				clusterCount+=maxNum;
				deleteInstances(noTmp);
				//д���ļ�
				System.out.println("�ۼ����,���ɾۼ���д�뵽�ļ��С���"+maxd);
				String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
				fileReadAndWriter.EndWrite();
			}
			System.out.println(new java.util.Date(System.currentTimeMillis()).toLocaleString());
			if(KDDCUP99Process.allnum<=failCount+clusterCount){
				System.out.println("����ʧ�ܼ��ϡ�����");
				String filepathString="";
				FileReadAndWriter fileReadAndWriter;
				//�Ѿ�������ɣ�����ʧ�ܵ�����
				for(int i=0;i<clusters.size();i++){//����ÿ���ۼ��ľۼ�����
					clusterCenters.AddInstance(getCenter(clusters.get(i)));
				}
				System.gc();
				Instances instances=loadSqlData.loadInstances2(failTableString, select, "");
				if(instances==null)break;
				int count=instances.getCount();
				for(int i=0;i<count;i++){
					//Ѱ������ľۼ����ģ�����֮
					Instance instance2=instances.getInstance(i);
					int loc=0;
					double min=Double.MAX_VALUE;
					for(int j=0;j<clusters.size();j++){
						double tmp=instance2.Distance(distanceMethod1, clusterCenters.getInstance(j));
						if(tmp<min){
							loc=j;
							min=tmp;
						}						
					}
					clusters.get(loc).append(","+instance2.getInstanceTagString());
				}	
				//���¼���ۼ�����
				System.out.println("���¼���ۼ����ģ��ۼ���ɣ��������ۼ�����"+clusters.size());
				clusterCenters=new Instances("NewCenter");				
				for(int i=0;i<clusters.size();i++){//����ÿ���ۼ��ľۼ�����
					clusterCenters.AddInstance(getCenter(clusters.get(i)));
					filepathString="E:/dataProcess/cluster/dis_NewCluster_"+disFilter+"_"+(i+1)+".txt";
					fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
					fileReadAndWriter.WriteLine(clusters.get(i).toString());
					fileReadAndWriter.EndWrite();
				}
				filepathString="E:/dataProcess/cluster/dis_NewCluster_"+disFilter+"_Centers.txt";
				Save2CSV1 save2csv1=new Save2CSV1();
				save2csv1.SaveInstances(clusterCenters, filepathString);				
				break;
			}
		}
	}
	
	
	/**�зŻصز����ۼ����ۼ���������ظ���������
	 * @param disFilter
	 * @param maxNum
	 * @param minNum
	 */
	public void GenerateClusters3(double disFilter,int maxNum,int minNum){
		String insTableString="Kddcup99_no2_temp";
		String failTableString="Kddcup99_Cluster_Fail";		
		String insString="Kddcup99_no2_distinct";
		StringBuffer failSet=new StringBuffer();//ʧ�ܼ�
		int failCount=0;//ʧ�ܼ�������
		ArrayList<StringBuffer> clusters=new ArrayList<>();//�ۼ�����
		ArrayList<Double>maxDisArrayList=new ArrayList<>();//�ۼ���������ֵ
		Instances clusterCenters=new Instances("ClusterCenters");
		int clusterCount=0;
//		int topnum=1;
		//�ۼ���������		
		while(true){
			StringBuffer condition2=new StringBuffer();
//			select1=" top "+topnum+select;
			Instances instances1=loadSqlData.loadInstances2(insTableString, select1, condition2.toString());
			if(instances1==null){
				System.out.println("can not find the instances! program exited!");
				return;
			}
			
			Instance instance=instances1.getInstance(instances1.getCount()-1);
			int numOnetime=15000;//һ�λ�ȡ������
			int cursor=1;//��־
			HashMap<String, Double> nodisHashMap=new HashMap<>();
			System.out.println("��ʼ��ȡ�ۼ���������"+instance.getInstanceTagString());	
			//��ԭ�����
			while(cursor<KDDCUP99Process.allnum){
				String condictionString="[no]>='"+cursor+"' and [no]<'"+(cursor+numOnetime)+"' ";//+(condition2.length()>0?"and ":" ")+condition2.toString();
				Instances instances=loadSqlData.loadInstances2(insString, select, condictionString);
				if(instances==null){
					System.out.println("can not find the instances, loop continues!");
					continue;
				}
				int count=instances.getCount();
				for(int i=0;i<count;i++) {
					Instance instance2=instances.getInstance(i);
					double dis=instance.Distance(distanceMethod1, instance2);
					if(dis<disFilter){
						//�����ݲ��뵽�洢������
						nodisHashMap.put(instance2.getInstanceTagString(), dis);
					}
				}
				cursor+=numOnetime;
			}
			System.out.println("�ۼ���ȡ��ϣ���ʼ��֤���ϡ�����"+nodisHashMap.size());
			//�����������Լ��Ͻ�����֤
			if(nodisHashMap.size()<minNum){
				//�ۼ������ﲻ����СҪ��
				//��ʧ���������뵽ʧ�ܼ�����,��ԭ��ɾ��
				String sqlString="insert into "+failTableString+" select * from "+insTableString+" where [no]='"+instance.getInstanceTagString()+"'";
				sqlHelper.executeSQL(sqlString);
				sqlString="delete from "+insTableString+" where [no]='"+instance.getInstanceTagString()+"'";
				sqlHelper.executeSQL(sqlString);
				failCount++;
				int count=nodisHashMap.size();
				System.out.println("�ۼ�����꣬�ۼ�ֻ�У�"+count+"����������С��"+0.6*disFilter+"����������ʧ�ܼ��ϣ�");
//				topnum+=50;
				int i=0;
				StringBuffer sqlbBuffer=new StringBuffer();
				for(java.util.Map.Entry<String, Double> entry:nodisHashMap.entrySet()){
					if (entry.getValue()<=0.8*disFilter) {
						sqlbBuffer.append(" insert into "+failTableString+" select * from "+insTableString+" where [no]='"+entry.getKey()+"' ");
						sqlbBuffer.append(" delete from "+insTableString+" where [no]='"+entry.getKey()+"' ");
						i++;
						if(i%300==0){
							i=0;
							sqlHelper.executeSQL(sqlbBuffer.toString());
							sqlbBuffer=new StringBuffer();
						}
					}
				}
				if(sqlbBuffer.length()>0){
					sqlHelper.executeSQL(sqlbBuffer.toString());
				}
			}
			else if(nodisHashMap.size()<maxNum){//ֱ�����ɾۼ�
				StringBuffer nosBuffer=new StringBuffer();
				Set<String> notmp=nodisHashMap.keySet();
				for(String string:notmp){
					nosBuffer.append(","+string);
				}
				StringBuffer noTmp=new StringBuffer(nosBuffer.substring(1));
				clusters.add(noTmp);
				clusterCount+=nodisHashMap.size();
				deleteInstances(noTmp);
				//д���ļ���
				System.out.println("�ۼ����,���ɾۼ���д���ļ�����");
				String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
				fileReadAndWriter.EndWrite();
			}
			else{//��ȡǰMaxnum��Ԫ��
				ByValueComparator bvc = new ByValueComparator(nodisHashMap);
				TreeMap< String, Double> sorTreeMap=new TreeMap<>(bvc);
				sorTreeMap.putAll(nodisHashMap);
				StringBuffer nosBuffer=new StringBuffer();
				int i=0;
				double maxd=0.0;
				for(String string:sorTreeMap.keySet()){
					if(i<maxNum){
						nosBuffer.append(","+string);
						i++;
					}
					else {
						try {
							maxd=sorTreeMap.get(string);
						} catch (Exception e) {
							// TODO: handle exception
							maxd=disFilter;
						}
						break;
					}
				}
				StringBuffer noTmp=new StringBuffer(nosBuffer.substring(1));
				clusters.add(noTmp);	
				clusterCount+=maxNum;
				deleteInstances(noTmp);
				//д���ļ�
				System.out.println("�ۼ����,���ɾۼ���д�뵽�ļ��С���"+maxd);
				String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
				FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
				fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
				fileReadAndWriter.EndWrite();
			}
			System.out.println(new java.util.Date(System.currentTimeMillis()).toLocaleString());
			if(KDDCUP99Process.allnum<=failCount+clusterCount){
				System.out.println("����ʧ�ܼ��ϡ�����");
				String filepathString="";
				FileReadAndWriter fileReadAndWriter;
				//�Ѿ�������ɣ�����ʧ�ܵ�����
				for(int i=0;i<clusters.size();i++){//����ÿ���ۼ��ľۼ�����
					clusterCenters.AddInstance(getCenter(clusters.get(i)));
				}
				System.gc();
				Instances instances=loadSqlData.loadInstances2(failTableString, select, "");
				if(instances==null)break;
				int count=instances.getCount();
				for(int i=0;i<count;i++){
					//Ѱ������ľۼ����ģ�����֮
					Instance instance2=instances.getInstance(i);
					int loc=0;
					double min=Double.MAX_VALUE;
					for(int j=0;j<clusters.size();j++){
						double tmp=instance2.Distance(distanceMethod1, clusterCenters.getInstance(j));
						if(tmp<min){
							loc=j;
							min=tmp;
						}						
					}
					clusters.get(loc).append(","+instance2.getInstanceTagString());
				}	
				//���¼���ۼ�����
				System.out.println("���¼���ۼ����ģ��ۼ���ɣ��������ۼ�����"+clusters.size());
				clusterCenters=new Instances("NewCenter");				
				for(int i=0;i<clusters.size();i++){//����ÿ���ۼ��ľۼ�����
					clusterCenters.AddInstance(getCenter(clusters.get(i)));
					filepathString="E:/dataProcess/cluster/dis_NewCluster_"+disFilter+"_"+(i+1)+".txt";
					fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
					fileReadAndWriter.WriteLine(clusters.get(i).toString());
					fileReadAndWriter.EndWrite();
				}
				filepathString="E:/dataProcess/cluster/dis_NewCluster_"+disFilter+"_Centers.txt";
				Save2CSV1 save2csv1=new Save2CSV1();
				save2csv1.SaveInstances(clusterCenters, filepathString);				
				break;
			}
		}
	}
	
	/**��פ�ڴ��
	 * @param disFilter
	 * @param maxNum
	 * @param minNum
	 */
	public void GenerateClusters4(double disFilter,int maxNum,int minNum){
		String insTableString="Kddcup99_no2_temp";
		String failTableString="Kddcup99_Cluster_Fail";		
		String insString="Kddcup99_no2_distinct";
		StringBuffer failSet=new StringBuffer();//ʧ�ܼ�
		int failCount=0;//ʧ�ܼ�������
		ArrayList<StringBuffer> clusters=new ArrayList<>();//�ۼ�����
		ArrayList<Double>maxDisArrayList=new ArrayList<>();//�ۼ���������ֵ
		Instances clusterCenters=new Instances("ClusterCenters");
		int clusterCount=0;
		int topnum=1;
		Instances instances=loadSqlData.loadInstances2(insString, select, "");
		int count=instances.getCount();		
		//�ۼ���������		
		while(true){
			StringBuffer condition2=new StringBuffer();
			select1=" top "+topnum+select;
			Instances instances1=loadSqlData.loadInstances2(insTableString, select1, condition2.toString());
			if(instances1==null){
				System.out.println("can not find the instances! program exited!");
				return;
			}
			for(int i=0;i<instances1.getCount();i++){
				Instance instance=instances1.getInstance(i);
				HashMap<String, Double> nodisHashMap=new HashMap<>();
				System.out.println("��ʼ��ȡ�ۼ���������"+instance.getInstanceTagString());	
				for(int j=0;j<count;j++) {
					Instance instance2=instances.getInstance(j);
					double dis=instance.Distance(distanceMethod1, instance2);
					if(dis<disFilter){
						//�����ݲ��뵽�洢������
						nodisHashMap.put(instance2.getInstanceTagString(), dis);
					}
				}
				System.out.println("�ۼ���ȡ��ϣ���ʼ��֤���ϡ�����"+nodisHashMap.size());
				if(nodisHashMap.size()<minNum){
					//�ۼ������ﲻ����СҪ��
					//��ʧ���������뵽ʧ�ܼ�����,��ԭ��ɾ��
					String sqlString="insert into "+failTableString+" select * from "+insTableString+" where [no]='"+instance.getInstanceTagString()+"'";
					sqlHelper.executeSQL(sqlString);
					sqlString="delete from "+insTableString+" where [no]='"+instance.getInstanceTagString()+"'";
					sqlHelper.executeSQL(sqlString);
					failCount++;
					int count1=nodisHashMap.size();
					System.out.println("�ۼ�����꣬�ۼ�ֻ�У�"+count1+"����������С��"+0.6*disFilter+"����������ʧ�ܼ��ϣ�");
					topnum+=50;
					int m=0;
					StringBuffer sqlbBuffer=new StringBuffer();
					for(java.util.Map.Entry<String, Double> entry:nodisHashMap.entrySet()){
						if (entry.getValue()<=0.8*disFilter) {
							sqlbBuffer.append(" insert into "+failTableString+" select * from "+insTableString+" where [no]='"+entry.getKey()+"' ");
							sqlbBuffer.append(" delete from "+insTableString+" where [no]='"+entry.getKey()+"' ");
							m++;
							if(m%300==0){
								m=0;
								sqlHelper.executeSQL(sqlbBuffer.toString());
								sqlbBuffer=new StringBuffer();
							}
						}
					}
					if(sqlbBuffer.length()>0){
						sqlHelper.executeSQL(sqlbBuffer.toString());
					}
				}
				else if(nodisHashMap.size()<maxNum){//ֱ�����ɾۼ�
					StringBuffer nosBuffer=new StringBuffer();
					Set<String> notmp=nodisHashMap.keySet();
					for(String string:notmp){
						nosBuffer.append(","+string);
					}
					StringBuffer noTmp=new StringBuffer(nosBuffer.substring(1));
					clusters.add(noTmp);
					clusterCount+=nodisHashMap.size();
					deleteInstances(noTmp);
					//д���ļ���
					System.out.println("�ۼ����,���ɾۼ���д���ļ�����");
					String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
					FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
					fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
					fileReadAndWriter.EndWrite();
				}
				else{//��ȡǰMaxnum��Ԫ��
					ByValueComparator bvc = new ByValueComparator(nodisHashMap);
					TreeMap< String, Double> sorTreeMap=new TreeMap<>(bvc);
					sorTreeMap.putAll(nodisHashMap);
					StringBuffer nosBuffer=new StringBuffer();
					int k=0;
					double maxd=0.0;
					for(String string:sorTreeMap.keySet()){
						if(k<maxNum){
							nosBuffer.append(","+string);
							k++;
						}
						else {
							try {
								maxd=sorTreeMap.get(string);
							} catch (Exception e) {
								// TODO: handle exception
								maxd=disFilter;
							}
							break;
						}
					}
					StringBuffer noTmp=new StringBuffer(nosBuffer.substring(1));
					clusters.add(noTmp);	
					clusterCount+=maxNum;
					deleteInstances(noTmp);
					//д���ļ�
					System.out.println("�ۼ����,���ɾۼ���д�뵽�ļ��С���"+maxd);
					String filepathString="E:/dataProcess/cluster/dis_"+instance.getInstanceTagString()+"_"+disFilter+".txt";
					FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(filepathString, false,true);
					fileReadAndWriter.WriteLine(instance.getInstanceTagString()+nosBuffer.toString());
					fileReadAndWriter.EndWrite();
				}
				System.out.println(new java.util.Date(System.currentTimeMillis()).toLocaleString());
			}
		}
	}
	
	
	private void deleteInstances(StringBuffer stringBuffer){
		String insTableString="Kddcup99_no2_temp";
		String failTableString="Kddcup99_Cluster_Fail";
		String [] nos2delStrings=stringBuffer.toString().split(",");
		StringBuffer sqlString=new StringBuffer();
		for(int i=0;i<nos2delStrings.length;i++){
			sqlString.append(" delete from "+insTableString+" where [no]='"+nos2delStrings[i]+"'  delete from "+
		failTableString+" where [no]='"+nos2delStrings[i]+"' ");
			if(i%300==0||i==nos2delStrings.length-1){
				sqlHelper.executeSQL(sqlString.toString());
				sqlString=new StringBuffer();
			}
		}
	}
 	
	private Instance getCenter(StringBuffer clusterBuffer){
		if(clusterBuffer.length()<1)return null;
		Instance instance=new Instance("center");
		StringBuffer condition2=new StringBuffer(" [no] in (");
		condition2.append(clusterBuffer);
		condition2.append(" )");
		Instances instances=loadSqlData.loadInstances2(tableString, select, condition2.toString());
		if(instances==null)return null;
		int count=instances.getCount();
		double [] conall=new double[instances.getInstance(0).getContinuousAttributes().length];
		instance=instances.getInstance(0).clone();
		for(int i=0;i<count;i++){
			Instance instance2=instances.getInstance(i);
			double [] temp=instance2.getContinuousAttributes();
			for(int j=0;j<conall.length;j++){
				conall[j]+=temp[j];
			}
		}
		for(int i=0;i<conall.length;i++){
			conall[i]/=count;
		}		
		instance.setContinuousAttributes(conall);
		return instance;
	}
	
 	/**���ļ���ȡ�ۼ�������ۼ��ľۼ����ı��浽�ļ�����
 	 * @param pathString
 	 */
 	public void generateCentersFromFiles(String pathString){
 		String insTableString="Kddcup99_no2_distinct";
		String failTableString="Kddcup99_Cluster_Fail";
 		File file=new File(pathString);
 		if(!file.isDirectory()){
 			System.out.println("the path is not a directory!");
 			return;
 		}
 		String save=pathString+"/centers.txt";
 		File [] files=file.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name.contains("dis_")) {
					return true;
				}
				return false;
			}
		});
 		for (File file2 : files) {
 			System.out.println("�������ģ�"+file2.getName());
//			FileReader fileReader=new FileReader(file);
			try {
				FileReader fileReader=new FileReader(file2);
				BufferedReader bufferedReader=new BufferedReader(fileReader);
				String nosString=bufferedReader.readLine();
				fileReader.close();
				bufferedReader.close();
				String [] noStrings=nosString.split(",");
				Instances instances=new Instances(noStrings[0]);
				StringBuffer cdt=new StringBuffer();
				for(int i=1;i<noStrings.length;i++){					
					if(i%100==0||i==noStrings.length-1){
						cdt.append(" no='"+noStrings[i]+"' ");
						Instances instances2=loadSqlData.loadInstances2(insTableString, select, cdt.toString());
						for(int j=0;j<instances2.getCount();j++){
							instances.AddInstance(instances2.getInstance(j));
						}
						cdt=new StringBuffer();
					}
					cdt.append(" no='"+noStrings[i]+"' or ");
				}
				//��������
				int count=instances.getCount();
				double [] cont=new double[instances.getInstance(0).getContinuousAttributes().length];
				for(int i=0;i<count;i++){
					Instance instance=instances.getInstance(i);
					double [] tmp=instance.getContinuousAttributes();
					for(int j=0;j<cont.length;j++){
						cont[j]+=tmp[j];
					}
				}
				FileWriter fileWriter=new FileWriter(save,true);
				fileWriter.write(noStrings[0]+",");
				for(int i=0;i<cont.length-1;i++){
					cont[i]/=count;
					fileWriter.write(cont[i]+",");
				}
				fileWriter.write(cont[cont.length-1]+"\r\n");
				fileWriter.close();
				System.out.println(new Date(System.currentTimeMillis()).toLocaleString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
 	}
 	
 	/**���ݾۼ������ٴξۼ�ʣ���ʧ�ܰ���
 	 * @param pathString
 	 */
 	public void ClusterAgain(String pathString){
 		
 	}
 	
 	class ByValueComparator implements Comparator<String> {
        HashMap<String, Double> base_map;
 
        public ByValueComparator(HashMap<String, Double> base_map) {
            this.base_map = base_map;
        }
 
        public int compare(String arg0, String arg1) {
            if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
                return 0;
            }
 
            if (base_map.get(arg0) < base_map.get(arg1)) {
                return -1;
            } else if (base_map.get(arg0) == base_map.get(arg1)) {
                return 0;
            } else {
                return 1;
            }
        }
    }
	
 	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassifierTester classifierTester=new ClassifierTester();
//		classifierTester.CalMaxDistance("1",2.2);
//		classifierTester.GenerateCluster2(1.0, 12000, 6000);//1.2.2 8000 40000 2.1.0 12000 6000
		classifierTester.GenerateClusters3(0.1, 100000, 5000);//3. 0.5 40000 6000
//		classifierTester.generateCentersFromFiles("E:/dataProcess/cluster/1_0");
	}

}
