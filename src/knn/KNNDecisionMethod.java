/**
 * 
 */
package knn;

import core.Instance;

/**
 * @author benfenghua
 *����K�������������ߵĲ���
 */
public abstract class KNNDecisionMethod {
	public abstract String getDecision(Instance [] instances,double []distances);
}
