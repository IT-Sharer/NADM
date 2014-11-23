package knn;

import java.util.ArrayList;

import core.Classifier;
import core.Instance;
import core.Instances;
import core.KNNClassifier;

public class TCM_KNN extends KNNClassifier{
	private boolean isCal=false;//�Ƿ����ѵ������ÿ�������� ����ֵ��
	private double [] singularValues;//ÿ������������ֵ
	private ArrayList<String> classeStrings=new ArrayList<>();//������𼯺ϡ�
	public TCM_KNN(int k, DistanceMethod distanceMethod,
			GetKNNMethod getKNNMethod, KNNDecisionMethod knnDecision,Instances trainInstances, Instances testInstances,
			String classifierName) {
		// TODO Auto-generated constructor stub
		super(k, distanceMethod, getKNNMethod, knnDecision,trainInstances,testInstances,classifierName);
		isCal=false;
		singularValues=new double[trainInstances.getCount()];
		
	}
	@Override
	public void setTrainInstances(Instances instances){
		TrainInstances=instances;
		setSingularValues(new double[TrainInstances.getCount()]);
	}
	/**
	 * ����ѵ��������������ֵ�����ͳ��
	 */
	private void CalDis(){
		if(isCal) return;
		double [] KSameAndDiff=new double[2*k];//2k���������ͬ�ĺͲ�ͬ�������ľ��룬ǰK��Ϊ��ͬ�ģ�
		for(int i=0;i<TrainInstances.getCount();i++){
			if(!classeStrings.contains(TrainInstances.getInstance(i).getLabel()))
				classeStrings.add(TrainInstances.getInstance(i).getLabel());
			getKNNMethod.getKNN(TrainInstances.getInstance(i), KSameAndDiff);
			double Dy=0.0,Ddy=0.0;
			for(int j=0;j<k;j++){
				Dy+=KSameAndDiff[j];
				Ddy+=KSameAndDiff[j+k];
			}
			singularValues[i]=Dy/Ddy;					
		}
		isCal=true;
	}
	/**
	 * ͳ��K���ڣ��������
	 * @param instance
	 * @return
	 */
	public String Classify2(Instance instance){
		CalDis();
		ArrayList<String> kNNStrings=new ArrayList<>();
		
		return "";
	}
	/* (non-Javadoc)
	 * @see core.Classifier#Classify(core.Instance)
	 * ������Ƿ����"���,����"�����Ŷȡ�
	 * 
	 */
	@Override
	public String Classify(Instance instance) {
		// TODO Auto-generated method stub
		CalDis();//������������ֵ�����ͳ��
		//ÿһ���ģ�ֵ,������ֵ
		double [] P=new double[classeStrings.size()],singularValue2=new double[classeStrings.size()];
		Instance instance2=instance.clone();
		for (int i=0;i<classeStrings.size();i++) {
			String label=classeStrings.get(i);
			instance2.setLabel(label);
			double [] KSameAndDiff=new double[2*k];
			getKNNMethod.getKNN(instance2, KSameAndDiff);
			double Dy=0.0,Ddy=0.0;
			for(int j=0;j<k;j++){
				Dy+=KSameAndDiff[j];
				Ddy+=KSameAndDiff[j+k];
			}
			singularValue2[i]=Dy/Ddy;			
		}
		//ͳ��ÿһ�����������Լ�����ֵ���ڴ������������ֵ�ĸ���
		int [] countAll=new int[classeStrings.size()],count2=new int[classeStrings.size()];
		for(int i=0;i<TrainInstances.getCount();i++){
			int loc=classeStrings.indexOf(TrainInstances.getInstance(i).getLabel());//���λ��
			countAll[loc]++;
			if(singularValues[i]>singularValue2[loc])count2[loc]++;
		}	
		double max=0.0,secMax=0.0;
		int loc=0;//���P��Ӧ�����λ��
		//��������Pֵ,�������Pֵ������Ϊ���࣬���Ŷ�Ϊ�ڶ���Pֵ��֮�Ĳ�
		for(int i=0;i<classeStrings.size();i++){
			P[i]=count2[i]*1.0/(countAll[i]*1.0);
			if(max<P[i]){
				max=P[i];
				loc=i;
//				secMax=max;
			}else if(secMax<P[i]) secMax=P[i];			
		}
		return classeStrings.get(loc)+","+(max-secMax);
	}
	public double[] getSingularValues() {
		return singularValues;
	}
	public void setSingularValues(double[] singularValues) {
		this.singularValues = singularValues;
	}
	public ArrayList<String> getClasseStrings() {
		return classeStrings;
	}
	public void setClasseStrings(ArrayList<String> classeStrings) {
		this.classeStrings = classeStrings;
	}
}
