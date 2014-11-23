/**
 * 
 */
package psoKmeans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import core.Instance;
import core.Instances;

/**
 * @author benfenghua
 *�۴ؼ�����
 *�۴ؼ��غͱ���
 *������������
 */
public class Clusters {
	protected ArrayList<Instances> cArrayList;//�۴�
	public ArrayList<Instances> getcArrayList() {
		return cArrayList;
	}
	protected Instances cCenters;//�۴����ģ���۴���Ŷ�Ӧ
	protected ArrayList<Double> maxDist;//�۴ص����뾶
//	protected ArrayList<Double> minDist;//�۴ص���С�뾶
//	protected ArrayList<Double> avgDist;//�۴ص�ƽ���뾶
	double [][] centersdist;//�ۼ����ļ�ľ��룬��Ŷ�Ӧ
	String dirString;//�۴ر����·��
	
	DistanceMethod distanceMethod=new DistanceMethod1();
	
	/**
	 * ͨ�����ݻ����㷨�����ۼ���
	 */
	public void generate(){
		
	}
	/**���ļ��м��ؾۼ�
	 * @param dir
	 */
	public void loadFromCSV(String dir) {
		
	}
	/**���浽�ļ�����
	 * @param dir
	 */
	public void save2CSV(String dir){
		
	}
	/**�Ӿ۴��л�ȡ����
	 * p:-1 ��ͬ�����ڣ�1 ��ͬ�����ڣ�0��Ͻ���;2 ��ͬ�Ͳ�ͬ��Ҫ���ء�
	 * K������������
	 * @return
	 */
	public Instances getNNInstances(final Instance inst,int p,int k){
		ArrayList<Integer> canSet=new ArrayList<>();//��ѡ�۴����
		ArrayList<Double> dist=new ArrayList<>();//��ÿ���۴����ĵľ��룬�����ѡ�����ó�double�����ֵ��
		Instances instances=new Instances("result");//���յĽ��
		//ѡ���ѡ���ݼ�
		double min=Double.MAX_VALUE;
		int mloc=0;
		for(int i=0;i<cCenters.getCount();i++){
			double distance=cCenters.getInstance(i).Distance(distanceMethod, inst);
			if (distance<min) {
				mloc=i;
				min=distance;
			}
			dist.add(i, distance);
		}
		//����� һ��
		dist.set(mloc, Double.MAX_VALUE);
		canSet.add(mloc);
		for(int i=0;i<dist.size();i++){
			if(dist.get(i)<=maxDist.get(i)||
					dist.get(i)<=0.5*centersdist[mloc][i]||dist.get(i)<=0.5*centersdist[i][mloc])	{
				dist.set(i, Double.MAX_VALUE);
				canSet.add(i);
			}else if (dist.get(i)-maxDist.get(i)>=0&&dist.get(i)-maxDist.get(i)<=min-maxDist.get(mloc)) {
				dist.set(i, Double.MAX_VALUE);
				canSet.add(i);
			}			
		}
		//�Ӻ�ѡ�۴���ѡ���Ӧ��ǰK�����ڣ�������
		TreeSet<Instance> kNNInstances=new TreeSet<>(new Comparator<Instance>() {			
			@Override
			public int compare(Instance arg0, Instance arg1) {
				// TODO Auto-generated method stub
				if (arg0.Distance(distanceMethod, inst)<arg1.Distance(distanceMethod, inst)) {
					return -1;
				}
				if (arg0.Distance(distanceMethod, inst)>arg1.Distance(distanceMethod, inst)) {
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < canSet.size(); i++) {
			Instances insts=cArrayList.get(canSet.get(i));
			for(int j=0;j<insts.getCount();j++){
				Instance instance=insts.getInstance(j);
				if((p==1&&instance.getLabel().equals(inst.getLabel()))||
						(p==-1&&!instance.getLabel().equals(inst.getLabel()))
						||p==0){
					kNNInstances.add(instance);
					if (kNNInstances.size()>k) {
						kNNInstances.pollLast();
					}
				}
			}
		}
		//�ж��Ƿ���K�����ڣ����������Ҫ��ӣ�������������K��������ӣ�����Ϊ��ֵ��
		int loc=0;
		while (kNNInstances.size()<k&&loc>=0) {
			double mindist=Double.MAX_VALUE/2;
			loc=-1;
			for(int i=0;i<dist.size();i++){
				if (dist.get(i)<mindist) {
					mindist=dist.get(i);
					loc=i;
					dist.set(i, Double.MAX_VALUE);
				}
			}
			Instances insts=cArrayList.get(loc);
			for(int j=0;j<insts.getCount();j++){
				Instance instance=insts.getInstance(j);
				if((p==1&&instance.getLabel().equals(inst.getLabel()))||
						(p==-1&&!instance.getLabel().equals(inst.getLabel()))
						||p==0){
					kNNInstances.add(instance);
					if (kNNInstances.size()>k) {
						kNNInstances.pollLast();
					}
				}
			}
		}	
		Iterator< Instance>iterator=kNNInstances.iterator();
		while(iterator.hasNext()){
			instances.AddInstance(iterator.next());
		}
		return instances;
	}	
}
