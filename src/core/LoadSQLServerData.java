package core;

import java.sql.Connection;

public class LoadSQLServerData extends LoadData {
	protected Connection connection;
	protected String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //����JDBC����  
	protected String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=NTDM";   //���ӷ����������ݿ�sample  
	protected String userName = "ntdmreader";   //Ĭ���û���  
	protected String userPwd = "ntdmntdm";   //����  
	
	@Override
	public Instances loadInstances(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
