package core;

import knn.DistanceMethod;
import knn.GetKNNMethod;
import knn.KNNDecisionMethod;

public abstract class KNNClassifier extends Classifier {
	protected int k;//k���ڵ���
	protected DistanceMethod distanceMethod;//��������ķ���
	protected GetKNNMethod getKNNMethod;//��ȡKNN�ķ���
	protected KNNDecisionMethod knnDecision;//����K���ڻ�ȡ���վ��ߵķ���
	
	public KNNClassifier(int k, DistanceMethod distanceMethod,
			GetKNNMethod getKNNMethod, KNNDecisionMethod knnDecision,Instances trainInstances, Instances testInstances,
			String classifierName) {
		super(trainInstances,  testInstances,
			  classifierName);
		this.k = k;
		this.distanceMethod = distanceMethod;
		this.getKNNMethod = getKNNMethod;
		this.knnDecision = knnDecision;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public GetKNNMethod getGetKNNMethod() {
		return getKNNMethod;
	}

	public void setGetKNNMethod(GetKNNMethod getKNNMethod) {
		this.getKNNMethod = getKNNMethod;
	}

	public KNNDecisionMethod getKnnDecision() {
		return knnDecision;
	}

	public void setKnnDecision(KNNDecisionMethod knnDecision) {
		this.knnDecision = knnDecision;
	}
	public DistanceMethod getDistanceMethod() {
		return distanceMethod;
	}
	public void setDistanceMethod(DistanceMethod distanceMethod) {
		this.distanceMethod = distanceMethod;
	}
	@Override
	public void Train() {
		// TODO Auto-generated method stub
		System.out.println("KNN classifiers need not to be trained!");
	}

}
