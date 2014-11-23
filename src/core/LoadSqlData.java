package core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import data.KDDCUP99Process;

public class LoadSqlData extends LoadData {
	String tablenameString="";
	String conditionString="";
	/* (non-Javadoc)
	 * @see core.LoadData#loadInstances(java.lang.String)
	 * @param ʵ������
	 */
	@Override
	public Instances loadInstances(String string){
		Instances instances=new Instances(string);
		StringBuffer attributeBuffer=new StringBuffer();
		attributeBuffer.append("no,");
		for(int i=0;i<KDDCUP99Process.discreteAttributes.length;i++){
			attributeBuffer.append(KDDCUP99Process.discreteAttributes[i]+",");
		}
		for(int i=0;i<KDDCUP99Process.continuousAttributes.length;i++){			
			attributeBuffer.append(KDDCUP99Process.continuousAttributes[i]+",");				
		}
		attributeBuffer.append("classLabel ");
		String sqlString="select "+attributeBuffer+" from "+tablenameString+" where "+conditionString;
//		System.out.println(sqlString);
		ResultSet resultSet=KDDCUP99Process.sqlHelper.executeSql(sqlString);
		try {
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			while(resultSet.next()){
				Instance instance=new Instance(resultSet.getString(1));
				String [] dA=new String[KDDCUP99Process.discreteAttributes.length];
				double [] cA=new double[KDDCUP99Process.continuousAttributes.length];
				for(int i=0;i<KDDCUP99Process.discreteAttributes.length;i++){
					dA[i]=resultSet.getString(i+2);
				}
				for(int i=0;i<cA.length;i++){
					cA[i]=resultSet.getDouble(i+2+dA.length);
				}
				String [] attrNameStrings=new String[dA.length+cA.length];
				for(int i=0;i<attrNameStrings.length;i++){
					attrNameStrings[i]="["+resultSetMetaData.getColumnName(i+2)+"]";
				}
				instance.setAttrNames(attrNameStrings);
				instance.setContinuousAttributes(cA);
				instance.setDisperseAttributes(dA);
				instance.setLabel(resultSet.getString(dA.length+cA.length+2));
				instances.AddInstance(instance);
			}
			return instances;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
//		return null;
	}
	/**
	 * @param tablenameString ����Դ����
	 * @param conditionString ����
	 */
	public LoadSqlData(String tablenameString, String conditionString) {
		super();
		this.tablenameString = tablenameString;
		this.conditionString = conditionString;
	}
	public LoadSqlData(){
		
	}
	public Instances loadInstances2(String table,String select,String condition){
		Instances instances=new Instances(" ");
		String sqlString;
		if(condition.equals(""))sqlString="select "+select+" from "+table+" ";
		else {
			sqlString="select "+select+" from "+table+" where "+condition;
		}
//		System.out.println(sqlString);
		ResultSet resultSet=KDDCUP99Process.sqlHelper.executeSql(sqlString);
		try {
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			while(resultSet.next()){
				Instance instance=new Instance(resultSet.getString(1));
				double [] cA=new double[KDDCUP99Process.continuousAttributes.length+KDDCUP99Process.discreteAttributes.length];
				for(int i=0;i<cA.length;i++){
					cA[i]=resultSet.getDouble(i+2);
				}
				String [] attrNameStrings=new String[cA.length];
				for(int i=0;i<attrNameStrings.length;i++){
					attrNameStrings[i]="["+resultSetMetaData.getColumnName(i+2)+"]";
				}
				instance.setAttrNames(attrNameStrings);
				instance.setContinuousAttributes(cA);
				instance.setLabel(resultSet.getString(cA.length+2));
				instances.AddInstance(instance);
			}
			return instances;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Instances loadInstances3(String sql){
		Instances instances=new Instances(" ");
		ResultSet resultSet=KDDCUP99Process.sqlHelper.executeSql(sql);
		try {
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			while(resultSet.next()){
				Instance instance=new Instance(resultSet.getString("no"));
				double [] cA=new double[KDDCUP99Process.continuousAttributes.length+KDDCUP99Process.discreteAttributes.length];
				for(int i=0;i<cA.length;i++){
					cA[i]=resultSet.getDouble(i+2);
				}
//				String [] attrNameStrings=new String[cA.length];
//				for(int i=0;i<attrNameStrings.length;i++){
//					attrNameStrings[i]="["+resultSetMetaData.getColumnName(i+2)+"]";
//				}
//				instance.setAttrNames(attrNameStrings);
				instance.setContinuousAttributes(cA);
				instance.setLabel(resultSet.getString(cA.length+2));
				instances.AddInstance(instance);
			}
			return instances;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public String getTablenameString() {
		return tablenameString;
	}
	public void setTablenameString(String tablenameString) {
		this.tablenameString = tablenameString;
	}
	public String getConditionString() {
		return conditionString;
	}
	public void setConditionString(String conditionString) {
		this.conditionString = conditionString;
	}
}
