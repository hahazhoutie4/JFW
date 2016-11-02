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
	private Map<String,MainInformation> map_information;	//表单信息
	private String[] names; // 包含表单信息
	private Sheet[] sheets;	//表单们
	private Workbook workbook;	//表格文件
	private String realPath;	//文件真正路径
	private String schema;
	// 提取的工程量文件名称创建
	public String createFileName() {
		return realPath + schema + "提量.xls";
	}
	// 创建工作簿
	private void createWorkbook(String name) throws BiffException, IOException {
		File file = new File(name);
		realPath = file.getAbsolutePath();
		String file_name = file.getName();
		String schema1 = file_name.split("\\.")[0];
		schema = "h_" + schema1.split("\\#")[0] + schema1.split("\\#")[1];
		Workbook workbook = Workbook.getWorkbook(file);
		this.workbook = workbook;
	}
	// 获取所有表单
	private void getSheets() {
		if (this.workbook != null) {
			this.sheets = workbook.getSheets();
		} else {
			System.out.println("表单对象为空!");
		}
	}
	// 获取表单名字
	private void getSheetNames() {
		if (this.sheets != null) {
			int length = sheets.length;
			System.out.println("表格数" + length);
			names = new String[length];
			for (int i = 0; i < length; i++) {
				names[i] = sheets[i].getName();
			}
		} else {
			System.out.println("表单对象为空!");
		}
	}
	// 获取表单内容,获取楼层信息--此处重复获取
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
		int rows = sheet.getRows(); // 表格行数
		int caculate=2;			//楼梯等构件的计数器，获取表头信息用，表头map的key值固定为2
						//楼梯工程单独提取
						if(sheet_name.contains("楼梯")){
							// 取数据，从第二列，第三行开始;
							int i_column_lt = 2;
							for (int i = i_column_lt; i < rows; i++) {
								Cell[]	cells=sheet.getRow(i);
								System.out.println(cells[2].getContents().toString()+cells[3].getContents().toString());
								if((cells[2].getContents().toString().equals("小计")&&cells[3].getContents().toString().equals("小计"))||(i==2)){
									System.out.println("cells字段长度"+cells.length);
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
										list.add(information);	//获取到楼梯的楼层信息
									}
								}
								mainInformation.setFloor_information(list); // 获取到了楼层信息
								mainInformation(sheet_name,map,mainInformation);
								map_information.put(sheet_name, mainInformation);
							return ;
						}
		
		//墙面构件单独提取
		if(sheet_name.equals("墙面")){
			
			return ;
		}
		
		//桩承台构件单独提取
		if(sheet_name.equals("桩承台")){
			
			return;
		}
		// 取数据，从第二列，第三行开始;
		int i_column = 2;
		for (int i = i_column; i < rows; i++) {
			Cell[] cells = sheet.getRow(i);
			if(!cells[3].equals("小计")&&!cells[3].equals("合计")){
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
		mainInformation.setFloor_information(list); // 获取到了楼层信息
		this.mainInformation(sheet_name,map,mainInformation);
		map_information.put(sheet_name, mainInformation);
		return;
	}

	// 获取到了map值,然后获取关键信息
	private void mainInformation(String sheet_name,Map<Integer,Cell[]>  map,MainInformation mainInformation) {
			Cell[] cell_information = map.get(2);
		int length = cell_information.length;
		List<String> set = new ArrayList<String>();
		for (int c = 1; c < length; c++) {
			if(sheet_name.equals("楼梯")){
				//楼梯构件的第二行的第一列和第二列是
				String information="";
				if(c==1){
					 information="楼层";
				}else if(c==2){
					 information="构件名称";
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
		mainInformation.setNames(set); // 获取到了表头信息
	}

	public void Initialized(String url) throws BiffException, IOException {
		this.createWorkbook(url);
		this.getSheets();
		this.getSheetNames();
	}

	/**
	 * @return 返回所有表单名称
	 */
	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}
	/**
	 * @return 返回当前文件名称
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