package core;

public abstract class Classifier {
	protected Instances TrainInstances;//ѵ����
	protected Instances testInstances;//���Լ�
	protected String [] resultStrings;//���Խ��
	protected String classifierName;//�������ı�ʶ��
	public void buildClassifier(Instances instances){
		setTrainInstances(instances);
	}
	public Instances getTrainInstances() {
		return TrainInstances;
	}
	public Classifier(Instances trainInstances, Instances testInstances,
			String classifierName) {
		super();
		TrainInstances = trainInstances;
		this.testInstances = testInstances;
		this.classifierName = classifierName;
	}
	public void setTrainInstances(Instances trainInstances) {
		TrainInstances = trainInstances;
	}
	public Instances getTestInstances() {
		return testInstances;
	}
	public void setTestInstances(Instances testInstances) {
		this.testInstances = testInstances;
	}
	public String[] getResultStrings() {
		return resultStrings;
	}
	public void setResultStrings(String[] resultStrings) {
		this.resultStrings = resultStrings;
	}
	public String getClassifierName() {
		return classifierName;
	}
	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}
	public void Classify(){
		resultStrings=new String[testInstances.getCount()];
		for(int i=0;i<testInstances.getCount();i++){
			resultStrings[i]=Classify(testInstances.getInstance(i));
		}		
	}
	public abstract String Classify(Instance instance);//���෽��
	public String [] Classify(Instance [] instances){
		String [] resultsStrings=new String[instances.length];
		for(int i=0;i<instances.length;i++){
			resultsStrings[i]=Classify(instances[i]);
		}		
		return resultsStrings;
	}
	public String [] Classify(Instances instances){
		String [] resultsStrings=new String[instances.getCount()];
		for(int i=0;i<instances.getCount();i++){
			resultsStrings[i]=Classify(instances.getInstance(i));
		}		
		return resultsStrings;
	}
	public abstract void Train();//ѵ������
}
