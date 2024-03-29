package tcmKNN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import knn.DistanceMethod;
import knn.DistanceMethod1;
import psoKmeans.Clusters;
import core.Instance;
import core.Instances;

public class TCMKNN4BigSet {
	protected DistanceMethod distanceMethod=new DistanceMethod1();
	protected Clusters clusters;//聚簇集合
	//保存各类别对应的奇异值序列
	public HashMap<String,TreeSet<Double>> strangeness=new HashMap<>();
	
	int k;
	String normalString="normal              ";//保存和加载的时候一定要trim一下
	String unNormalString="unnormal            ";
	public TCMKNN4BigSet() {
		// TODO Auto-generated constructor stub
		//初始化Clusters
		
	}
	/**
	 * 计算样本奇异值
	 */
	public double CalStrangeness(Instance instance){
		Instances difInstances=clusters.getNNInstances(instance, -1, k);//需要修改，一次计算返回两个实例集
		Instances sameInstances=clusters.getNNInstances(instance, 1, k);
		double difDists=0,sameDists=0;
		for(int i=0;i<difInstances.getCount();i++){
			difDists+=difInstances.getInstance(i).Distance(distanceMethod, instance);
		}for(int i=0;i<sameInstances.getCount();i++){
			sameDists+=sameInstances.getInstance(i).Distance(distanceMethod, instance);
		}
		if(difDists>0.0000001)return sameDists/difDists;
		else return Double.MAX_VALUE;
	}
	/**
	 * 计算所有样本的奇异值
	 */
	public void CalAllStrangeness(){
		ArrayList<Instances> iniArrayList=clusters.getcArrayList();
		for(int i=0;i<iniArrayList.size();i++){
			Instances instances=iniArrayList.get(i);
			for(int j=0;j<instances.getCount();j++){
				Instance instance=instances.getInstance(j);
				if(strangeness.containsKey(instance.getLabel().trim())){
					TreeSet<Double> tmpDoubles=strangeness.get(instance.getLabel().trim());
					tmpDoubles.add(CalStrangeness(instance));
				}
				else {
					TreeSet<Double> tmpDoubles=new TreeSet<>();
					tmpDoubles.add(CalStrangeness(instance));
					strangeness.put(instance.getLabel().trim(), tmpDoubles);
				}
			}			
		}
	}
	/**折半查找元素的排位
	 * @return
	 */
	public int halfSearch(double d){
		//???
		return 0;
	}
	public void SaveStrageness(){
		
	}
	public void LoadStrageness(){
		
	}
	class ClassifyResult{
		String result;//结果类别
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public double getPos() {
			return pos;
		}
		public void setPos(double pos) {
			this.pos = pos;
		}
		double pos;//置信度
	}
	/**分类
	 * @param instance
	 * @return
	 */
	public ClassifyResult classify(Instance instance){
		Instance instance2=instance.clone();
		String [] labelStrings={normalString,unNormalString};
		double [] P=new double[labelStrings.length];
		for(int i=0;i<labelStrings.length;i++){
			instance2.setLabel(labelStrings[i]);
			double strange=CalStrangeness(instance2);
			int c=halfSearch(strange);
			P[i]=c*1.0/strangeness.get(labelStrings[i].trim()).size();			
		}
		//返回结果和置信度。
		return null;
	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
