package psoKmeans;

import java.util.ArrayList;

import core.Instance;
import core.Instances;

public class PSOInstances extends Instances {

	public PSOInstances(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	/**��ԭ����ת��������Ⱥ��
	 * @param instances
	 */
	public PSOInstances(Instances instances) {
		super(instances.getDataSetNameString());
		super.instances=instances.getInstances();
		initPSO();
	}
	protected ArrayList<Instance> velocity=null;//��ʾ���ӵ��ٶ�����
	protected ArrayList<Instance> xPosition=null;//��ʾ���ӵ�λ������
	/**
	 * ��ʼ��PSO����Ⱥ����������ʼ������һ������Ⱥ
	 * 
	 */
	public void initPSO(){
		velocity=(ArrayList<Instance>) instances.clone();
		xPosition=(ArrayList<Instance>) instances.clone();
	}
}
