package com.zhoutong.jxl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
public class OutputExcel {
	/**
	 *@author hahazhoutie4
	 *@website cnblogs.com/hahazhoutie4-blogs/
	 */
	private  static OutputExcel outputExcel;	//����ģʽ
	private File file;
	private  static int sheet_i=-1;				//��ǰ�����
	private WritableWorkbook workbook;
	/**
	 * @return	singleton ����ģʽ
	 */
	public static OutputExcel getOutputExcel(){
		if(null==outputExcel){
			outputExcel=new OutputExcel();
		}
		return outputExcel;
	}				
	private OutputExcel(){
	}			
	public void CreateTable(Map<String,String> map,String directory){
		System.out.println("��������"+map.size());
		sheet_i++;
		File file=this.getFile(directory);
		if(null!=file){
			try {
				if(null==workbook){
				 workbook=Workbook.createWorkbook(file);
				 }
				workbook.createSheet(String.valueOf(sheet_i),sheet_i);
				WritableSheet sheet=workbook.getSheet(sheet_i);
				Set<String> set=map.keySet();
				try {
					sheet.addCell(new Label(0,0,"��������"));
					sheet.addCell(new Label(1,0,"������"));
				int i=1;
				for(String s:set){
					String value=map.get(s);
					Label label=new Label(0,i,s);
					Label label_001=new Label(1,i,value);
					i++;
					sheet.addCell(label_001);
					sheet.addCell(label);
				}
				workbook.write();
				workbook.close();
				}catch (WriteException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private File getFile(String directory){
		File file=new File(directory);
		if(file.exists()){
			return file;		//�ļ�������ѡ��ǰ�ļ�
		}else{
			this.createNewFile(file);
			return file;			//�ļ������ڴ������ļ�
		}
	}
	private void createNewFile(File file){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}