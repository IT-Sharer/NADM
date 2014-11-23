package data;

import java.io.IOException;
import java.util.HashMap;

import core.FileReadAndWriter;

/**
 * @author benfenghua
 *�����ļ��ľ�̬������
 *��ȡָ����ǩ�µ��ļ�
 */
public class DataFileManager {
	public static HashMap<String, String[]> dataFiles=new HashMap<>();
	
	public static String path="E:/dataProcess/logFile.txt";
	/**
	 * д���ļ���Ϣ
	 * ��������###�Ÿ�����  �ļ����ƣ�·�����ļ���·���͵�ǰ�ļ�·��һ�£���
	 * ʱ������,�������������зָ�����
	 * ��ɢ�����Ե���ţ����Ÿ�����,�ļ�����
	 * @param strings
	 * @throws IOException 
	 */
	public static void Write(String [] strings){
		String lineString="";
		for(int i=0;i<strings.length-1;i++){
			lineString+=strings[i]+"###";
		}
		lineString+=strings[strings.length-1];
		if(!IsExist(strings[0])){
			FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path, false,true);
			fileReadAndWriter.WriteLine(lineString);
			fileReadAndWriter.EndWrite();
			dataFiles.put(strings[0], strings);			
		}
		else {
			try {
				throw new IOException("�ļ��Ѵ��ڲ���д�룡����֤����д��");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void Load(){
		FileReadAndWriter fileReadAndWriter=new FileReadAndWriter(path, true);
		while(fileReadAndWriter.EndRead()){			
			String [] strings=fileReadAndWriter.ReadLine().split("###");
			dataFiles.put(strings[0], strings);
		}
	}
	/**����Ƿ�����ļ���¼
	 * @param filename
	 * @return
	 */
	private static boolean IsExist(String filename) {
		if(dataFiles.size()<1)Load();
		return dataFiles.containsKey(filename);
	}
}
