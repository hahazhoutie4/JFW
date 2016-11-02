package com.zhoutong.properties;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
public class Information {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	public Information() {
	}
	public Map<String, String> getInformation(File file) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			int rows = sheet.getRows();
			for (int i = 0; i < rows; i++) {
				String floor_information = sheet.getCell(0, i).getContents();
				String concrete_information = sheet.getCell(1, i).getContents();
				map.put(floor_information, concrete_information);
			}
		} catch (BiffException | IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	public Map<String,SortedSet<String>> getConcrete(Map<String,String> map){
		Map<String,SortedSet<String>> map_001=new HashMap<String, SortedSet<String>>();
			SortedSet<String> set_001 = new TreeSet<String>();
			SortedSet<String> set_002=new TreeSet<String>();
			Set<String> key=map.keySet();
			for(String q:key){
				if(!q.equals("������")&&!String.valueOf(q.charAt(1)).equals("-")){
						set_001.add(map.get(q).split("C")[1]);
				}else{
					set_002.add(map.get(q).split("C")[1]);
				}
			}
			map_001.put("���¹�����", set_001);
			map_001.put("���Ϲ�����",set_002);
			return map_001;			//��ȡ��������¥��Ļ�����ǿ�ȵȼ�����,�磺C30-C35-C40-C45,��ȡ����ֵΪ30-35-40-45���˴�Ϊ�Ѿ�����Ļ�����ǿ�ȵȼ�
	}	
}
