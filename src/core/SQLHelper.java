package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLHelper {
	protected Connection connection;
	protected String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //����JDBC����  
	protected String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=NTDM";   //���ӷ����������ݿ�sample  
	protected String userName = "ntdmreader";   //Ĭ���û���  
	protected String userPwd = "ntdmntdm";   //����  
	public SQLHelper() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName(driverName);
			connection=DriverManager.getConnection(dbURL, userName, userPwd);
			System.out.println("Connect to DB successfully!");			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public Connection getConnection(){
		return connection;
	}
	public boolean executeSQL(String sqlString){
		try {
			Statement statement=connection.createStatement();
			statement.execute(sqlString);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	public ResultSet executeSql(String sqlString){
		ResultSet resultSet;
		try {
			Statement statement=connection.createStatement();
			resultSet=statement.executeQuery(sqlString);
			return resultSet;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
