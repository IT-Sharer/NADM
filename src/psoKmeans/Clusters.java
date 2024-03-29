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
 *聚簇集合类
 *聚簇加载和保存
 *近邻搜索方法
 */
public class Clusters {
	protected ArrayList<Instances> cArrayList;//聚簇
	public ArrayList<Instances> getcArrayList() {
		return cArrayList;
	}
	protected Instances cCenters;//聚簇中心，与聚簇序号对应
	protected ArrayList<Double> maxDist;//聚簇的最大半径
//	protected ArrayList<Double> minDist;//聚簇的最小半径
//	protected ArrayList<Double> avgDist;//聚簇的平均半径
	double [][] centersdist;//聚集中心间的距离，序号对应
	String dirString;//聚簇保存的路径
	
	DistanceMethod distanceMethod=new DistanceMethod1();
	
	/**
	 * 通过数据划分算法产生聚集。
	 */
	public void generate(){
		
	}
	/**从文件夹加载聚集
	 * @param dir
	 */
	public void loadFromCSV(String dir) {
		
	}
	/**保存到文件夹中
	 * @param dir
	 */
	public void save2CSV(String dir){
		
	}
	/**从聚簇中获取近邻
	 * p:-1 不同类别近邻；1 相同类别近邻；0混合近邻;2 相同和不同都要返回。
	 * K：近邻数量；
	 * @return
	 */
	public Instances getNNInstances(final Instance inst,int p,int k){
		ArrayList<Integer> canSet=new ArrayList<>();//候选聚簇序号
		ArrayList<Double> dist=new ArrayList<>();//与每个聚簇中心的距离，如果候选后设置成double的最大值。
		Instances instances=new Instances("result");//最终的结果
		//选择候选数据集
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
		//最近的 一个
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
		//从候选聚簇中选择对应的前K个近邻，及距离
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
		//判断是否是K个近邻，如果不是则要添加，如果到最后还少于K个，则不添加，设置为空值。
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
