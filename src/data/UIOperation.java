/**
 * 
 */
package data;

import java.util.Scanner;

/**
 * ���û�������ִ��
 * @author benfenghua
 *
 */
public class UIOperation {
	private DataPartition dataPartition=null;
	public UIOperation(DataPartition dataPartition){
		this.dataPartition=dataPartition;
	}
	public void Interaction() {
		while(true){
			System.out.println("please Input print stop save:");
			Scanner input=new Scanner(System.in);
			String string=input.next();
			dataPartition.recieveMsg(string);
			if(string.equals("stop"))break;
		}
	}
}
