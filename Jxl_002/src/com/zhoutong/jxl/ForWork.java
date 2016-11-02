package com.zhoutong.jxl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ForWork {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	public ForWork() {
	};
	private Map<String,MainInformation> map_information;	//����Ϣ
	private String[] names; // ��������Ϣ
	private Sheet[] sheets;	//����
	private Workbook workbook;	//����ļ�
	private String realPath;	//�ļ�����·��
	private String schema;
	// ��ȡ�Ĺ������ļ����ƴ���
	public String createFileName() {
		return realPath + schema + "����.xls";
	}
	// ����������
	private void createWorkbook(String name) throws BiffException, IOException {
		File file = new File(name);
		realPath = file.getAbsolutePath();
		String file_name = file.getName();
		String schema1 = file_name.split("\\.")[0];
		schema = "h_" + schema1.split("\\#")[0] + schema1.split("\\#")[1];
		Workbook workbook = Workbook.getWorkbook(file);
		this.workbook = workbook;
	}
	// ��ȡ���б�
	private void getSheets() {
		if (this.workbook != null) {
			this.sheets = workbook.getSheets();
		} else {
			System.out.println("������Ϊ��!");
		}
	}
	// ��ȡ������
	private void getSheetNames() {
		if (this.sheets != null) {
			int length = sheets.length;
			System.out.println("�����" + length);
			names = new String[length];
			for (int i = 0; i < length; i++) {
				names[i] = sheets[i].getName();
			}
		} else {
			System.out.println("������Ϊ��!");
		}
	}
	// ��ȡ������,��ȡ¥����Ϣ--�˴��ظ���ȡ
	public void getSheetContent(String sheet_name) {
		if(null==map_information){
			map_information=new HashMap<String, MainInformation>();
		}
		MainInformation mainInformation = new MainInformation();
		Map<Integer,Cell[]>map = new HashMap<Integer, Cell[]>();
		int length = sheets.length;
		Sheet sheet = null;
		for (int j = 0; j < length; j++) {
			if (sheets[j].getName().equals(sheet_name)) {
				sheet = sheets[j];
				break;
			}
		}
		int rows = sheet.getRows(); // �������
		int caculate=2;			//¥�ݵȹ����ļ���������ȡ��ͷ��Ϣ�ã���ͷmap��keyֵ�̶�Ϊ2
						//¥�ݹ��̵�����ȡ
						if(sheet_name.contains("¥��")){
							// ȡ���ݣ��ӵڶ��У������п�ʼ;
							int i_column_lt = 2;
							for (int i = i_column_lt; i < rows; i++) {
								Cell[]	cells=sheet.getRow(i);
								System.out.println(cells[2].getContents().toString()+cells[3].getContents().toString());
								if((cells[2].getContents().toString().equals("С��")&&cells[3].getContents().toString().equals("С��"))||(i==2)){
									System.out.println("cells�ֶγ���"+cells.length);
									map.put(caculate, cells);
									caculate++;
								}
							}
							mainInformation.setMap(map);
								Cell[] cell_information=sheet.getColumn(1);
								List<String> list=new ArrayList<String>();
								for(int i=0;i<cell_information.length;i++){
									String information=cell_information[i].getContents().toString();
									if(!list.contains(information)){
										list.add(information);	//��ȡ��¥�ݵ�¥����Ϣ
									}
								}
								mainInformation.setFloor_information(list); // ��ȡ����¥����Ϣ
								mainInformation(sheet_name,map,mainInformation);
								map_information.put(sheet_name, mainInformation);
							return ;
						}
		
		//ǽ�湹��������ȡ
		if(sheet_name.equals("ǽ��")){
			
			return ;
		}
		
		//׮��̨����������ȡ
		if(sheet_name.equals("׮��̨")){
			
			return;
		}
		// ȡ���ݣ��ӵڶ��У������п�ʼ;
		int i_column = 2;
		for (int i = i_column; i < rows; i++) {
			Cell[] cells = sheet.getRow(i);
			if(!cells[3].equals("С��")&&!cells[3].equals("�ϼ�")){
			map.put(i, cells);
			}
		}
		Cell[] cell_floor_information = sheet.getColumn(1);
		int information_length = cell_floor_information.length;
		List<String> list = new ArrayList<String>();
		for (int j = 4; j < information_length; j++) {
			String information = cell_floor_information[j].toString();
			if (!list.contains(information)) {
				list.add(information);
			}
		}
		mainInformation.setFloor_information(list); // ��ȡ����¥����Ϣ
		this.mainInformation(sheet_name,map,mainInformation);
		map_information.put(sheet_name, mainInformation);
		return;
	}

	// ��ȡ����mapֵ,Ȼ���ȡ�ؼ���Ϣ
	private void mainInformation(String sheet_name,Map<Integer,Cell[]>  map,MainInformation mainInformation) {
			Cell[] cell_information = map.get(2);
		int length = cell_information.length;
		List<String> set = new ArrayList<String>();
		for (int c = 1; c < length; c++) {
			if(sheet_name.equals("¥��")){
				//¥�ݹ����ĵڶ��еĵ�һ�к͵ڶ�����
				String information="";
				if(c==1){
					 information="¥��";
				}else if(c==2){
					 information="��������";
				}else{
					information=cell_information[c].getContents().toString().replace("(", "_").replace(")", "_");	
				}
				set.add(information);
			}else{
			String information = cell_information[c].getContents()
					.replace("(", "_").replace(")", "_");
			set.add(information);
			}
		}
		mainInformation.setNames(set); // ��ȡ���˱�ͷ��Ϣ
	}

	public void Initialized(String url) throws BiffException, IOException {
		this.createWorkbook(url);
		this.getSheets();
		this.getSheetNames();
	}

	/**
	 * @return �������б�����
	 */
	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}
	/**
	 * @return ���ص�ǰ�ļ�����
	 */
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getRealPath() {
		return realPath;
	}
	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	public Map<String, MainInformation> getMap_information() {
		return map_information;
	}
	public void setMap_information(Map<String, MainInformation> map_information) {
		this.map_information = map_information;
	}
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public void setSheets(Sheet[] sheets) {
		this.sheets = sheets;
	}
}